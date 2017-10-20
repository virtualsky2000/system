package system.web.faces;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.validation.ConstraintViolation;

import org.apache.commons.beanutils.BeanUtils;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;
import system.utils.ClassUtils;
import system.validator.ValidatorUtils;
import system.web.PageInfo;

public abstract class AbstractManagedBean {

    protected static final Map<String, ArrayList<PageInfo>> mapPageInfo = new HashMap<>();

    protected FacesContext fc;

    protected ExternalContext exContext;

    protected Flash flash;

    protected ArrayList<PageInfo> lstPageInfo;

    protected PageInfo pageInfo;

    protected String pageId;

    protected int pageIndex;

    protected String methodName;

    private String userId;

    private Logger log;

    @PostConstruct
    private void postConstruct() {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        fc = FacesContext.getCurrentInstance();
        exContext = fc.getExternalContext();
        flash = exContext.getFlash();

        String sessionId = exContext.getSessionId(true);
        if (!mapPageInfo.containsKey(sessionId)) {
            lstPageInfo = new ArrayList<>();
            mapPageInfo.put(sessionId, lstPageInfo);
        } else {
            lstPageInfo = mapPageInfo.get(sessionId);
        }

        pageId = getPageId();
        pageInfo = getPageInfo();

        if (!fc.isPostback()) {
            updateHistoryInfo();
        }
    }

    /**
     * 画面ID取得
     * @return 画面ID
     */
    protected String getPageId() {
        String id = this.getClass().getSimpleName();
        return id.substring(0, id.length() - 4);
    }

    /**
     * 画面情報取得
     * @return 画面情報
     */
    protected PageInfo getPageInfo() {
        pageIndex = 0;
        for (PageInfo info : lstPageInfo) {
            if (info.getPageId().equals(pageId)) {
                return info;
            }
            pageIndex++;
        }

        return null;
    }

    protected void updateHistoryInfo() {
        if (pageInfo == null) {
            pageInfo = new PageInfo();
            pageInfo.setPageId(pageId);
            lstPageInfo.add(pageInfo);

            pageIndex = lstPageInfo.size() - 1;

            if (pageIndex > 0) {
                setFormData();
            }
        } else {
            // 子画面から戻る場合
            restoreFormData();

            // 以降の履歴情報を削除する
            for (int i = lstPageInfo.size() - 1; i > pageIndex; i--) {
                PageInfo pageInfo = lstPageInfo.get(i);
                if (pageInfo != null) {
                    Map<String, Object> formData = pageInfo.getFormData();
                    if (formData != null) {
                        formData.clear();
                        formData = null;
                    }
                }
                lstPageInfo.remove(i);
            }
        }
    }

    /**
     * クリア処理
     * @param parentId 親のID
     */
    protected void clear(String parentId) {
        List<UIInput> lstComponent = FacesUtils.getChilds(parentId, UIInput.class);

        for (UIInput component : lstComponent) {
            component.setValue("");
        }
    }

    private void copyMap(Map<String, Object> mapFormData) {
        try {
            BeanUtils.populate(this, mapFormData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApplicationException(e);
        }
    }

    protected void setFormData() {
        // copyMap(lstPageInfo.get(pageIndex - 1).getFormData());
        copyMap(ClassUtils.cast(flash.get("formData")));
    }

    protected void restoreFormData() {
        // Map<String, Object> mapFormData = pageInfo.getFormData();
        Map<String, Object> mapFormData = ClassUtils.cast(flash.get("formData"));
        if (mapFormData != null) {
            copyMap(mapFormData);
        }
    }

    protected Map<String, Object> getFormData() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            Map<String, Object> mapFormData = new HashMap<>(fields.length);

            for (Field field : fields) {
                mapFormData.put(field.getName(), field.get(this));
            }

            return mapFormData;
        } catch (IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * アクション処理
     * @param methodName 処理名
     * @return url
     */
    public String action(String methodName) {
        this.methodName = methodName;

        if (!"clear".equals(methodName) && !validateData()) {
            // チェックエラーの場合
            return null;
        }

        beforeAction();

        String result = doAction();

        afterAction();

        return result;
    }

    /**
     * アクション前処理
     */
    protected void beforeAction() {
        // pageInfo.setFormData(getFormData());

        flash.put("formData", getFormData());
    }

    protected String doAction() {
        String result = null;

        try {
            Method method = this.getClass().getMethod(methodName);
            result = (String) method.invoke(this);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }

        if (result == null) {
            return result;
        } else if (result.indexOf("?") < 0) {
            return result + "?faces-redirect=true";
        } else {
            return result + "&faces-redirect=true";
        }
    }

    /**
     * アクション後処理
     */
    protected void afterAction() {

    }

    /**
     * 戻る処理
     * @return url
     */
    public String back() {
        return lstPageInfo.get(pageIndex - 1).getUrl();
    }

    /**
     * 検証処理
     * @return 検証OK：true、検証NG：false
     */
    protected boolean validateData() {
        ConstraintViolation<?>[] violation = ValidatorUtils.validate(this);

        if (violation.length == 0) {
            // チェックOKの場合
            return true;
        }

        // チェックNGの場合
        validateOnError(violation);

        return false;
    }

    /**
     * 検証エラー時の処理
     * @param violation バイオレーション
     */
    protected void validateOnError(ConstraintViolation<?>[] violation) {
        List<UIComponent> lstComponent = FacesUtils.getChilds(UIComponent.class);

        for (ConstraintViolation<?> v : violation) {
            UIComponent component = FacesUtils.findComponent(lstComponent, v);
            if (component != null) {
                setErrorItem(component);

                fc.addMessage(component.getClientId(), new FacesMessage(v.getMessage()));
            } else {
                fc.addMessage("", new FacesMessage(v.getMessage()));
            }
        }
    }

    /**
     * エラー項目設定
     * @param component エラー項目
     */
    protected void setErrorItem(UIComponent component) {
        FacesUtils.addStyleClass(component, "error");
    }

    protected String getUserId() {
        if (userId == null) {

        }

        return userId;
    }

}

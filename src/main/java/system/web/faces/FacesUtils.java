package system.web.faces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.ArrayUtils;

import com.sun.faces.facelets.compiler.UIInstructions;

import system.utils.ClassUtils;

public class FacesUtils {

    private static final Pattern p = Pattern.compile("\\{\\s*\\w+\\.(\\w+)\\s*\\}");

    public static UIComponent findComponent(String id) {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        if (root.getId().equals(id)) {
            return root;
        }

        return findComponent(root, id);
    }

    public static UIComponent findComponent(String parentId, String id) {
        UIComponent parent = findComponent(parentId);
        return findComponent(parent, id);
    }

    public static UIComponent findComponent(UIComponent parent, String id) {
        for (UIComponent child : parent.getChildren()) {
            if (child instanceof UIInstructions) {
                continue;
            }
            if (child.getId().equals(id)) {
                return child;
            } else if (child.getChildCount() > 0) {
                UIComponent component = findComponent(child, id);
                if (component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    public static <T> List<T> getChilds(String parentId, Class<T> clazz) {
        UIComponent parent = findComponent(parentId);
        return getChilds(parent, clazz);
    }

    public static <T> List<T> getChilds(UIComponent parent, Class<T> clazz) {
        List<T> lstComponent = new ArrayList<>();

        for (UIComponent child : parent.getChildren()) {
            if (child instanceof UIInstructions) {
                continue;
            }
            if (ClassUtils.isExtend(clazz, child.getClass())) {
                lstComponent.add(clazz.cast(child));
            }
            if (child.getChildCount() > 0) {
                List<T> subComponents = getChilds(child, clazz);
                if (!subComponents.isEmpty()) {
                    lstComponent.addAll(subComponents);
                }
            }
        }

        return lstComponent;
    }

    public static <T> List<T> getChilds(Class<T> clazz) {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        return getChilds(root, clazz);
    }

    public static UIComponent findComponent(List<? extends UIComponent> lstComponent,
            ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int pos = path.lastIndexOf(".");
        String fieldName = pos > 0 ? path.substring(pos) : path;

        for (UIComponent component : lstComponent) {
            if (component.getId().equals(path)) {
                return component;
            }
            ValueExpression expression = component.getValueExpression("value");
            if (expression != null) {
                Matcher m = p.matcher(expression.getExpressionString());
                if (m.find() && fieldName.equals(m.group(1))) {
                    return component;
                }
            }

        }

        return null;
    }

    public static String getStyle(UIComponent component) throws NoSuchMethodException {
        Method m = component.getClass().getMethod("getStyle");
        try {
            return (String) m.invoke(component);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 何もしない
        }
        return null;
    }

    public static void setStyle(UIComponent component, String style) throws NoSuchMethodException {
        Method m = component.getClass().getMethod("setStyle", String.class);
        try {
            m.invoke(component, style);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 何もしない
        }
    }

    public static void addStyle(UIComponent component, String style) {
        try {
            String oldStyle = getStyle(component);
            if (oldStyle == null) {
                setStyle(component, style);
            } else if (!ArrayUtils.contains(oldStyle.split(" "), style)) {
                setStyle(component, oldStyle + " " + style);
            }
        } catch (NoSuchMethodException e) {
            // 何もしない
        }
    }

    public static String getStyleClass(UIComponent component) throws NoSuchMethodException {
        Method m = component.getClass().getMethod("getStyleClass");
        try {
            return (String) m.invoke(component);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 何もしない
        }
        return null;
    }

    public static void setStyleClass(UIComponent component, String styleClass) throws NoSuchMethodException {
        Method m = component.getClass().getMethod("setStyleClass", String.class);
        try {
            m.invoke(component, styleClass);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 何もしない
        }
    }

    public static void addStyleClass(UIComponent component, String styleClass) {
        try {
            String oldStyleClass = getStyleClass(component);
            if (oldStyleClass == null) {
                setStyleClass(component, styleClass);
            } else if (!ArrayUtils.contains(oldStyleClass.split(" "), styleClass)) {
                setStyleClass(component, oldStyleClass + " " + styleClass);
            }
        } catch (NoSuchMethodException e) {
            // 何もしない
        }
    }

}

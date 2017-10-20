package system.web;

import java.util.Map;

/**
 * 画面情報
 *
 */
public class PageInfo {

    /**
     * 画面ID
     */
    private String pageId;

    /**
     * タイトル
     */
    private String title;

    /**
     * URL
     */
    private String url;

    /**
     * フォームデータ
     */
    private Map<String, Object> formData;

    /**
     * 画面ID取得
     * @return 画面ID
     */
    public String getPageId() {
        return pageId;
    }

    /**
     * 画面ID設定
     * @param pageId 画面ID
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * タイトル取得
     * @return タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * タイトル設定
     * @param title タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * URL取得
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL設定
     * @param url URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * フォームデータ取得
     * @return フォームデータ
     */
    public Map<String, Object> getFormData() {
        return formData;
    }

    /**
     * フォームデータ設定
     * @param formData フォームデータ
     */
    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

}

package system.xml;

/**
 * XML属性クラス
 *
 */
public class XmlAttribute {

    /** 属性名 */
    private String name;

    /** 属性値 */
    private String value;

    /**
     * 属性初期化
     * @param name 属性名
     * @param value 属性値
     */
    public XmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 属性名取得
     * @return 属性名
     */
    public String getName() {
        return name;
    }

    /**
     * 属性名設定
     * @param name 属性名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 属性値取得
     * @return 属性値
     */
    public String getValue() {
        return value;
    }

    /**
     * 属性値設定
     * @param value 属性値
     */
    public void setValue(String value) {
        this.value = value;
    }

}
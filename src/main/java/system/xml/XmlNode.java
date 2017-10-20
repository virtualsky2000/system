package system.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * XMLノートクラス
 *
 */
public class XmlNode {

    /** 親ノート */
    private XmlNode parent;

    /** ノート名 */
    private String name;

    /** ノート値 */
    private String value;

    /** 属性リスト */
    private List<XmlAttribute> lstAttribute;

    /** 子ノートリスト */
    private List<XmlNode> lstNode;

    /** 行番号 */
    private int lineNumber = -1;

    /** 列番号 */
    private int columnNumber = -1;

    private String systemId = null;

    private String publicId = null;

    private String path;

    /**
     * XmlNode初期化
     * @param parent 親ノート
     * @param name ノート名称
     * @param lstAttribute 属性リスト
     */
    public XmlNode(XmlNode parent, String name, List<XmlAttribute> lstAttribute) {
        this.parent = parent;
        if (parent != null) {
            List<XmlNode> nodes = parent.lstNode;
            if (nodes == null) {
                nodes = new ArrayList<>();
                parent.lstNode = nodes;
            }
            nodes.add(this);
        }
        this.name = name;
        this.lstAttribute = lstAttribute;
    }

    /**
     * 親ノート取得
     * @return 親ノート
     */
    public XmlNode getParent() {
        return parent;
    }

    /**
     * 親ノート設定
     * @param parent 親ノート
     */
    public void setParent(XmlNode parent) {
        this.parent = parent;
    }

    /**
     * ノート名取得
     * @return ノート名
     */
    public String getName() {
        return name;
    }

    /**
     * ノート名設定
     * @param name ノート名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * ノート値取得
     * @return ノート値
     */
    public String getValue() {
        return value;
    }

    /**
     * ノート値設定
     * @param value ノート値
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 属性リスト取得
     * @return 属性リスト
     */
    public List<XmlAttribute> getLstAttribute() {
        return lstAttribute;
    }

    /**
     * 属性リスト設定
     * @param lstAttribute 属性リスト
     */
    public void setLstAttribute(List<XmlAttribute> lstAttribute) {
        this.lstAttribute = lstAttribute;
    }

    /**
     * 子ノートリスト取得
     * @return 子ノートリスト
     */
    public List<XmlNode> getLstNode() {
        return lstNode;
    }

    /**
     * 子ノートリスト設定
     * @param lstNode 子ノートリスト
     */
    public void setLstNode(List<XmlNode> lstNode) {
        this.lstNode = lstNode;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    /**
     * 属性値取得
     * @param attributeName 属性名
     * @return 属性値
     */
    public String getAttributeValue(String attributeName) {
        if (lstAttribute == null) {
            return null;
        }

        for (XmlAttribute attr : lstAttribute) {
            if (attr.getName().equals(attributeName)) {
                return attr.getValue();
            }
        }

        return null;
    }

    /**
     * ルートノート取得
     * @return ルートノート
     */
    public XmlNode getRoot() {
        XmlNode node = this;
        while (node.parent != null) {
            node = node.parent;
        }

        return node;
    }

    /**
     * パス取得
     * @return パス
     */
    public String getPath() {
        if (path != null) {
            return path;
        }
        if (parent == null) {
            path = "/" + name;
            return path;
        } else {
            StringBuilder sb = new StringBuilder();
            path = sb.append(parent.getPath()).append("/").append(getName()).toString();
            return path;
        }
    }

    public static XmlNode getNode(List<XmlNode> lstNode, String path) {
        String[] strPath = path.split("/");
        List<XmlNode> nodes = lstNode;
        XmlNode findNode = null;

        for (String item : strPath) {
            boolean find = false;
            for (XmlNode node : nodes) {
                if (node.name.equals(item)) {
                    nodes = node.lstNode;
                    findNode = node;
                    find = true;
                    break;
                }
            }
            if (!find) {
                return null;
            }
        }

        return findNode;
    }

    public static XmlNode getNode(XmlNode parent, String path) {
        if (path.startsWith("/")) {
            // 絶対パスの場合
            if (path.equals(parent.getPath())) {
                return parent;
            } else if (path.startsWith(parent.getPath() + "/")) {
                return getNode(parent.lstNode, path.substring(parent.getPath().length() + 1));
            } else {
                return null;
            }
        } else {
            // 相対パスの場合
            return getNode(parent.lstNode, path);
        }
    }

    public static List<XmlNode> getSubNodes(XmlNode parent, String nodeName) {
        List<XmlNode> nodes = new ArrayList<>();

        if (parent.lstNode != null) {
            for (XmlNode node : parent.lstNode) {
                if (node.name.equals(nodeName)) {
                    nodes.add(node);
                }
                nodes.addAll(getSubNodes(node, nodeName));
            }
        }

        return nodes;
    }

    public static List<XmlNode> getNodes(List<XmlNode> lstNode, String nodeName) {
        List<XmlNode> nodes = new ArrayList<>();

        for (XmlNode node : lstNode) {
            nodes.addAll(getSubNodes(node, nodeName));
        }

        return nodes;
    }

    public static List<XmlNode> selectNodes(XmlNode parent, String xpath) {

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(name);

        if (lstAttribute != null && lstAttribute.size() > 0) {
            for (XmlAttribute attr : lstAttribute) {
                sb.append(" ").append(attr.getName()).append("=\"").append(attr.getValue()).append("\"");
            }
        }

        if (value == null || value.length() == 0) {
            sb.append(" />");
        } else {
            sb.append(">").append(value).append("</").append(name).append(">");
        }

        return sb.toString();
    }

}
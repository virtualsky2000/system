package system.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import system.exception.ApplicationException;
import system.reader.AbstractReader;
import system.utils.FileUtils;

public class XmlStreamReader extends AbstractReader {

    private static final XMLInputFactory factory = XMLInputFactory.newInstance();

    protected XmlNode root;

    private XmlNode parent;

    private String value;

    private String comment;

    public static XmlStreamReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlStreamReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlStreamReader load(File file, Charset charset) {
        XmlStreamReader reader = new XmlStreamReader(file, charset);
        reader.load();

        return reader;
    }

    protected XmlStreamReader(File file, Charset charset) {
        super(file, charset);
    }

    public void load() {
        load((StreamFilter) null);
    }

    public void load(StreamFilter filter) {
        try {
            InputStreamReader sr = new InputStreamReader(new FileInputStream(file), charset);
            XMLStreamReader reader = factory.createXMLStreamReader(sr);

            if (filter != null) {
                reader = factory.createFilteredReader(reader, filter);
            }

            while (reader.hasNext()) {
                loadStream(reader);
                reader.next();
            }

            reader.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new ApplicationException("Xmlファイル「" + file.getAbsolutePath() + "」読込が失敗しました。", e);
        }
    }

    protected void loadStream(XMLStreamReader reader) {
        switch (reader.getEventType()) {
        case XMLStreamConstants.START_ELEMENT:
            startElement(reader);
            break;
        case XMLStreamConstants.END_ELEMENT:
            endElement(reader);
            break;
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
            processingInstruction(reader);
            break;
        case XMLStreamConstants.CHARACTERS:
            characters(reader);
            break;
        case XMLStreamConstants.COMMENT:
            comment(reader);
            break;
        case XMLStreamConstants.SPACE:
            space(reader);
            break;
        case XMLStreamConstants.START_DOCUMENT:
            startDocument(reader);
            break;
        case XMLStreamConstants.END_DOCUMENT:
            endDocument(reader);
            break;
        case XMLStreamConstants.ENTITY_REFERENCE:
            entityReference(reader);
            break;
        case XMLStreamConstants.ATTRIBUTE:
            attribute(reader);
            break;
        case XMLStreamConstants.DTD:
            dtd(reader);
            break;
        case XMLStreamConstants.CDATA:
            cdata(reader);
            break;
        case XMLStreamConstants.NAMESPACE:
            namespace(reader);
            break;
        case XMLStreamConstants.NOTATION_DECLARATION:
            notationDeclaration(reader);
            break;
        case XMLStreamConstants.ENTITY_DECLARATION:
            entityDeclaration(reader);
            break;
        }
    }

    protected void startDocument(XMLStreamReader reader) {
        parent = null;
        comment = "";
    }

    protected void endDocument(XMLStreamReader reader) {

    }

    protected void startElement(String name, List<XmlAttribute> lstAttribute, Location location) {
        XmlNode node = new XmlNode(parent, name, lstAttribute);
        node.setLineNumber(location.getLineNumber());
        node.setColumnNumber(location.getColumnNumber());
        node.setSystemId(location.getSystemId());
        node.setPublicId(location.getPublicId());

        if (root == null) {
            root = node;
        }

        parent = node;
        value = "";
    }

    protected void startElement(XMLStreamReader reader) {
        List<XmlAttribute> lstAttribute = null;
        int len = reader.getAttributeCount();
        if (len > 0) {
            lstAttribute = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                lstAttribute.add(new XmlAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i)));
            }
        }

        startElement(reader.getLocalName(), lstAttribute, reader.getLocation());
    }

    protected void endElement(XMLStreamReader reader) {
        parent.setValue(value);
        parent = parent.getParent();
        comment = "";
    }

    protected void processingInstruction(XMLStreamReader reader) {

    }

    protected void characters(XMLStreamReader reader) {
        value = new String(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
    }

    protected void comment(XMLStreamReader reader) {
        comment = reader.getText().trim();
        log.debug("comment: {}", comment);
    }

    protected void space(XMLStreamReader reader) {

    }

    protected void entityReference(XMLStreamReader reader) {

    }

    protected void attribute(XMLStreamReader reader) {

    }

    protected void dtd(XMLStreamReader reader) {

    }

    protected void cdata(XMLStreamReader reader) {

    }

    protected void namespace(XMLStreamReader reader) {
        log.info(reader.getNamespaceCount());
    }

    protected void notationDeclaration(XMLStreamReader reader) {

    }

    protected void entityDeclaration(XMLStreamReader reader) {

    }

    public XmlNode getRootNode() {
        return this.root;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
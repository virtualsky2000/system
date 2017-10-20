package system.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import system.exception.ApplicationException;
import system.reader.AbstractReader;
import system.utils.FileUtils;

public class XmlReader extends AbstractReader {

    private static final SAXParserFactory factory = SAXParserFactory.newInstance();

    private XmlNode root;

    private XmlNode parent;

    private String value;

    private String comment;

    private Locator locator;

    public static XmlReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlReader load(File file, Charset charset) {
        XmlReader reader = new XmlReader(file, charset);
        reader.load();

        return reader;
    }

    protected XmlReader(File file, Charset charset) {
        super(file, charset);
    }

    public void load() {
        try {
            SAXParser parser = factory.newSAXParser();
            InputSource is = new InputSource(new InputStreamReader(new FileInputStream(file), charset));
            XmlReaderHandler readerHandler = new XmlReaderHandler();
            parser.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", readerHandler);
            parser.parse(is, readerHandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ApplicationException("Xmlファイル「" + file.getAbsolutePath() + "」読込が失敗しました。", e);
        }
    }

    public XmlNode getNode(String path) {
        return XmlNode.getNode(root, path);
    }

    public XmlNode getRootNode() {
        return this.root;
    }

    protected void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    protected void startDocument() throws SAXException {
        log.debug("startDocument");
        parent = null;
        comment = "";
    }

    protected void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        log.debug("startElement");

        XmlNode node = new XmlNode(parent, qName, getAttributes(attributes));
        node.setLineNumber(locator.getLineNumber());
        node.setColumnNumber(locator.getColumnNumber());
        node.setSystemId(locator.getSystemId());
        node.setPublicId(locator.getPublicId());

        if (root == null) {
            root = node;
        }

        parent = node;
        value = "";
    }

    protected void endElement(String uri, String localName, String qName) throws SAXException {
        log.debug("endElement");
        parent.setValue(value);
        parent = parent.getParent();
        comment = "";
    }

    protected void characters(char[] ch, int start, int length) throws SAXException {
        value += new String(ch, start, length);
    }

    protected void endDocument() throws SAXException {
        log.debug("endDocument");
    }

    protected void startPrefixMapping(String prefix, String uri) throws SAXException {
        log.debug("startPrefixMapping: {} {}", prefix, uri);
    }

    protected void endPrefixMapping(String prefix) throws SAXException {
        log.debug("endPrefixMapping: {}", prefix);
    }

    protected void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        log.debug("ignorableWhitespace: {} {} {}", ch, start, length);
    }

    protected void processingInstruction(String target, String data) throws SAXException {
        log.debug("processingInstruction: {} {}", target, data);
    }

    protected void skippedEntity(String name) throws SAXException {
        log.debug("skippedEntity: {}", name);
    }

    protected void startDTD(String name, String publicId, String systemId) throws SAXException {
        log.debug("startDTD: {} {} {}", name, publicId, systemId);
    }

    protected void endDTD() throws SAXException {
        log.debug("endDTD");
    }

    protected void startEntity(String name) throws SAXException {
        log.debug("startEntity: {}", name);
    }

    protected void endEntity(String name) throws SAXException {
        log.debug("endEntity: {}", name);
    }

    protected void startCDATA() throws SAXException {
        log.debug("startCDATA");
    }

    protected void endCDATA() throws SAXException {
        log.debug("endCDATA");
    }

    protected void comment(char[] ch, int start, int length) throws SAXException {
        comment = new String(ch, start, length).trim();
        log.debug("comment: {}", comment);
    }

    private class XmlReaderHandler extends DefaultHandler implements LexicalHandler {

        private XmlReader self = XmlReader.this;

        @Override
        public void setDocumentLocator(Locator locator) {
            self.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            self.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            self.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            self.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            self.characters(ch, start, length);
        }

        @Override
        public void endDocument() throws SAXException {
            self.endDocument();
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            self.startPrefixMapping(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            self.endPrefixMapping(prefix);
        }

        @Override
        public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
            self.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            self.processingInstruction(target, data);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            self.skippedEntity(name);
        }

        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            self.startDTD(name, publicId, systemId);
        }

        @Override
        public void endDTD() throws SAXException {
            self.endDTD();
        }

        @Override
        public void startEntity(String name) throws SAXException {
            self.startEntity(name);
        }

        @Override
        public void endEntity(String name) throws SAXException {
            self.endEntity(name);
        }

        @Override
        public void startCDATA() throws SAXException {
            self.startCDATA();
        }

        @Override
        public void endCDATA() throws SAXException {
            self.endCDATA();
        }

        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            self.comment(ch, start, length);
        }

    }

    private static List<XmlAttribute> getAttributes(Attributes attributes) {
        int len = attributes.getLength();
        if (len == 0) {
            return null;
        }

        List<XmlAttribute> lstAttributes = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            lstAttributes.add(new XmlAttribute(attributes.getQName(i), attributes.getValue(i)));
        }

        return lstAttributes;
    }

}
package system.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import system.exception.ApplicationException;
import system.reader.AbstractReader;
import system.utils.FileUtils;

public class XmlEventReader extends AbstractReader {

    private static final XMLInputFactory factory = XMLInputFactory.newInstance();

    protected XmlNode root;

    private XmlNode parent;

    private String value;

    private String comment;

    public static XmlEventReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlEventReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlEventReader load(File file, Charset charset) {
        XmlEventReader reader = new XmlEventReader(file, charset);
        reader.load();

        return reader;
    }

    protected XmlEventReader(File file, Charset charset) {
        super(file, charset);
    }

    public void load() {
        load((EventFilter) null);
    }

    public void load(EventFilter filter) {
        try {
            InputStreamReader sr = new InputStreamReader(new FileInputStream(file), charset);
            XMLEventReader reader = factory.createXMLEventReader(sr);
            if (filter != null) {
                reader = factory.createFilteredReader(reader, filter);
            }

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                loadEvent(event);
            }

            reader.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new ApplicationException("Xmlファイル「" + file.getAbsolutePath() + "」読込が失敗しました。", e);
        }
    }

    protected void loadEvent(XMLEvent event) {
        switch (event.getEventType()) {
        case XMLEvent.START_ELEMENT:
            startElement((StartElement) event);
            break;
        case XMLEvent.END_ELEMENT:
            endElement((EndElement) event);
            break;
        case XMLEvent.PROCESSING_INSTRUCTION:
            processingInstruction((ProcessingInstruction) event);
            break;
        case XMLEvent.CHARACTERS:
            characters((Characters) event);
            break;
        case XMLEvent.COMMENT:
            comment((Comment) event);
            break;
        case XMLEvent.SPACE:
            space(event);
            break;
        case XMLEvent.START_DOCUMENT:
            startDocument((StartDocument) event);
            break;
        case XMLEvent.END_DOCUMENT:
            endDocument((EndDocument) event);
            break;
        case XMLEvent.ENTITY_REFERENCE:
            entityReference((EntityReference) event);
            break;
        case XMLEvent.ATTRIBUTE:
            attribute((Attribute) event);
            break;
        case XMLEvent.DTD:
            dtd((DTD) event);
            break;
        case XMLEvent.CDATA:
            cdata(event);
            break;
        case XMLEvent.NAMESPACE:
            namespace((Namespace) event);
            break;
        case XMLEvent.NOTATION_DECLARATION:
            notationDeclaration((NotationDeclaration) event);
            break;
        case XMLEvent.ENTITY_DECLARATION:
            entityDeclaration((EntityDeclaration) event);
            break;
        }
    }

    protected void startDocument(StartDocument element) {
        parent = null;
        comment = "";
    }

    protected void endDocument(EndDocument event) {

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

    protected void startElement(StartElement element) {
        @SuppressWarnings("unchecked")
        Iterator<Attribute> iterator = (Iterator<Attribute>) element.getAttributes();
        List<XmlAttribute> lstAttribute = null;

        if (iterator.hasNext()) {
            lstAttribute = new ArrayList<>();
        }

        while (iterator.hasNext()) {
            Attribute attr = iterator.next();
            lstAttribute.add(new XmlAttribute(attr.getName().getLocalPart(), attr.getValue()));
        }

        startElement(element.getName().getLocalPart(), lstAttribute, element.getLocation());
    }

    protected void endElement(EndElement event) {
        parent.setValue(value);
        parent = parent.getParent();
        comment = "";
    }

    protected void processingInstruction(ProcessingInstruction event) {

    }

    protected void characters(Characters event) {
        value = event.getData();
    }

    protected void comment(Comment event) {
        comment = event.getText().trim();
        log.debug("comment: {}", comment);
    }

    protected void space(XMLEvent event) {

    }

    protected void cdata(XMLEvent event) {

    }

    protected void entityReference(EntityReference event) {

    }

    protected void attribute(Attribute event) {

    }

    protected void dtd(DTD event) {

    }

    protected void namespace(Namespace event) {

    }

    protected void notationDeclaration(NotationDeclaration event) {

    }

    protected void entityDeclaration(EntityDeclaration event) {

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
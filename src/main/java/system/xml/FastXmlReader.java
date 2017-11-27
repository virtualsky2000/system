package system.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import system.exception.ApplicationException;
import system.reader.AbstractReader;
import system.utils.FileUtils;

public class FastXmlReader extends AbstractReader {

    private final static int DEFAULT_BUF_SIZE = 8192;

    private char[] buf;

    private int bufSize;

    private int curRow = 1;

    private int curCol = 1;

    private int curDepth = 0;

    private int offset = 0;

    private int bytes = 0;

    private int endComment = 0;

    private boolean bInDoubleQuote = false;

    private boolean bInSigleQuote = false;

    private boolean first = false;

    private boolean change = false;

    private boolean skip = false;

    private List<String> lstTagName = new ArrayList<>();

    private List<String> lstPath = new ArrayList<>();

    public static FastXmlReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static FastXmlReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static FastXmlReader load(File file, Charset charset) {
        FastXmlReader reader = new FastXmlReader(file, charset);
        reader.load();

        return reader;
    }

    public static FastXmlReader load(InputStreamReader sr) {
        FastXmlReader reader = new FastXmlReader(sr);
        reader.load();

        return reader;
    }

    protected FastXmlReader(File file, Charset charset) {
        super(file, charset);
    }

    protected FastXmlReader(InputStreamReader sr) {
        super(sr);
    }

    @Override
    public void load() {
        try {
            setBufSize();
            buf = new char[bufSize];

            setFilter();

            startDocument();

            processDocument();

            while (processTag() != -1)
                ;

            endDocument();
        } catch (Exception e) {
            throwException("Xml", e);
        }
    }

    public int getBufSize() {
        return this.bufSize;
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public void setBufSize() {
        setBufSize(DEFAULT_BUF_SIZE);
    }

    public void setFilter() {
        addFilter("/Configuration/Appenders/");
    }

    public void addFilter(String path) {
        lstPath.add(path);
    }

    private void skipSpace() {
        skipText((start, c) -> {
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                return 0;
            }
            return -1;
        });
    }

    private void read() {
        try {
            bytes = sr.read(buf);
            if (bytes > -1) {
                offset = 0;
                change = true;
            }
        } catch (IOException e) {
            throw new ParseXmlException(e);
        }
    }

    private void moveCursor() {
        offset++;
        if (offset == bufSize) {
            read();
        }
    }

    private void skipText(skipTextFunc func) {
        while (true) {
            int start = offset;
            for (int len = bytes; offset < len; offset++) {
                char c = buf[offset];
                if (c == '\n') {
                    curRow++;
                    curCol = 1;
                }
                curCol++;
                if (func.skip(start, c) == 0) {
                    return;
                }
            }
            if (bytes < bufSize) {
                // EOF
                throw new ParseXmlException("invalid xml file.");
            }
            if (offset == bufSize) {
                read();
                if (bytes == 0) {
                    // EOF
                    throw new ParseXmlException("invalid xml file.");
                }
            }
        }
    }

    private String getText(getTextFunc func) {
        StringBuilder sbText = new StringBuilder();

        while (true) {
            int start = offset;
            for (int len = bytes; offset < len; offset++) {
                char c = buf[offset];
                if (c == '\n') {
                    curRow++;
                    curCol = 1;
                }
                curCol++;
                if (func.get(sbText, start, c) == 0) {
                    return sbText.toString();
                }
            }
            if (bytes < bufSize) {
                // EOF
                throw new ParseXmlException("invalid xml file.");
            }
            if (offset == bufSize) {
                if (bInDoubleQuote || bInSigleQuote) {
                    if (change == false) {
                        sbText.append(buf, start + 1, bufSize - start - 1);
                    } else {
                        sbText.append(buf, 0, bufSize);
                    }
                } else {
                    sbText.append(buf, start, bufSize - start);
                }
                read();
                if (bytes == 0) {
                    // EOF
                    throw new ParseXmlException("invalid xml file.");
                }
            }
        }
    }

    private String getAttributeName() {
        return getText((sbText, start, c) -> {
            if (c == ' ' || c == '\t' || c == '=') {
                sbText.append(buf, start, offset - start);
                return 0;
            }
            return -1;
        });
    }

    private String getAttributeValue() {
        bInSigleQuote = false;
        bInDoubleQuote = false;
        first = true;
        change = false;

        return getText((sbText, start, c) -> {
            if (first && start == offset) {
                if (c == '"') {
                    bInDoubleQuote = true;
                } else if (c == '\'') {
                    bInSigleQuote = true;
                } else {
                    throw new ParseXmlException("attribute name must be start with \" or '.");
                }
            }
            if (bInDoubleQuote && c == '"' && !first) {
                if (change == false) {
                    sbText.append(buf, start + 1, offset - 1 - start);
                } else {
                    sbText.append(buf, start, offset - start);
                }
                bInDoubleQuote = false;
                return 0;
            } else if (bInSigleQuote && c == '\'' && !first) {
                if (change == false) {
                    sbText.append(buf, start + 1, offset - 1 - start);
                } else {
                    sbText.append(buf, start, offset - start);
                }
                bInSigleQuote = false;
                return 0;
            }
            if (first) {
                first = false;
            }
            return -1;
        });
    }

    private String getTagName() {
        return getText((sbText, start, c) -> {
            if (c == ' ' || c == '\t' || c == '>') {
                sbText.append(buf, start, offset - start);
                return 0;
            }
            return -1;
        });
    }

    private String getTagText() {
        return getText((sbText, start, c) -> {
            if (c == '<') {
                sbText.append(buf, start, offset - start);
                return 0;
            }
            return -1;
        });
    }

    private String getComment() {
        endComment = 0;

        return getText((sbText, start, c) -> {
            if (c == '-') {
                endComment++;
            } else if (c == '>') {
                if (endComment >= 2) {
                    sbText.append(buf, start, offset - start);
                    sbText.setLength(sbText.length() - 2);
                    return 0;
                } else {
                    endComment = 0;
                }
            } else {
                endComment = 0;
            }
            return -1;
        });
    }

    private void processDocument() {
        read();
        skipSpace();
        int ofs = offset;
        char c = buf[offset];
        if (c != '<') {
            throw new ParseXmlException(
                    String.format("document should begin with '<' at %d row %d column.", curRow, curCol));
        }
        moveCursor();
        String tag = getTagName();

        if ("?xml".equalsIgnoreCase(tag)) {
            // <?xml
            getAttributies();
        } else {
            offset = ofs;
        }
    }

    private boolean inPath(String curPath) {
        for (String path : lstPath) {
            if (path.startsWith(curPath) || curPath.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    private int processTag() {
        if (offset == bytes || bytes == -1) {
            return -1;
        }

        skipSpace();
        char c = buf[offset];
        if (curDepth == 0 && c != '<') {
            throw new ParseXmlException(
                    String.format("document should begin with '<' at %d row %d column.", curRow, curCol));
        }

        if (c == '<') {
            moveCursor();
            String tagName = getTagName();

            if (tagName.equalsIgnoreCase("!DOCTYPE")) {
                // DOCTYPE
                moveCursor();
                skipSpace();
                String docType = getTagName();
                log.info("DOCTYPE = {}", docType);

                if (buf[offset] != '>') {
                    throw new ParseXmlException("DOCTYPE can not close.");
                }
            } else if (tagName.equalsIgnoreCase("!--")) {
                // コメント
                if (skip) {
                    skipComment();
                } else {
                    String comment = getComment();
                    setComment(comment);
                }
            } else {
                if (!tagName.startsWith("/")) {
                    // start tag
                    lstTagName.add(tagName);
                    curDepth++;
                    String curPath = "/" + StringUtils.join(lstTagName.toArray(), "/") + "/";
                    if (inPath(curPath)) {
                        skip = false;
                        startElement(tagName);
                        getAttributies();
                    } else {
                        skip = true;
                        skipAttributies();
                    }
                } else {
                    // end tag
                    tagName = tagName.substring(1);
                    String lastTagName = lstTagName.get(lstTagName.size() - 1);
                    if (!tagName.equalsIgnoreCase(lastTagName)) {
                        throw new ParseXmlException(
                                String.format("tag can not be close at %d row %d column, start with %s, end with %s.",
                                        curRow, curCol - tagName.length() - 1, lastTagName, tagName));
                    }
                    lstTagName.remove(lstTagName.size() - 1);
                    curDepth--;
                }
            }
            moveCursor();
        } else {
            // tag text
            if (!skip) {
                String tagText = getTagText();
                setTagText(tagText);
            } else {
                skipTagText();
            }
        }

        return 0;
    }

    private void getAttributies() {
        while (true) {
            skipSpace();
            char c = buf[offset];
            if (c == '>') {
                return;
            } else if (c == '/') {
                moveCursor();
                skipSpace();
                if (buf[offset] == '>') {
                    // end tag
                    lstTagName.remove(lstTagName.size() - 1);
                    curDepth--;
                    return;
                } else {
                    throw new ParseXmlException("tag can not close.");
                }
            } else if (c != '?') {
                String attrName = getAttributeName();
                moveCursor();
                skipSpace();
                String attrValue = getAttributeValue();
                moveCursor();
                startAttribute(attrName, attrValue);
            } else {
                moveCursor();
                skipSpace();
                if (buf[offset] == '>') {
                    moveCursor();
                    return;
                } else {
                    throw new ParseXmlException("tag can not close.");
                }
            }
        }
    }

    private void skipAttributeName() {
        skipText((start, c) -> {
            if (c == ' ' || c == '\t' || c == '=') {
                return 0;
            }
            return -1;
        });
    }

    private void skipAttributeValue() {
        bInSigleQuote = false;
        bInDoubleQuote = false;
        first = true;

        skipText((start, c) -> {
            if (first && start == offset) {
                if (c == '"') {
                    bInDoubleQuote = true;
                } else if (c == '\'') {
                    bInSigleQuote = true;
                } else {
                    throw new ParseXmlException("attribute name must be start with \" or '.");
                }
            }
            if (bInDoubleQuote && c == '"' && !first) {
                bInDoubleQuote = false;
                return 0;
            } else if (bInSigleQuote && c == '\'' && !first) {
                bInSigleQuote = false;
                return 0;
            }
            if (first) {
                first = false;
            }
            return -1;
        });
    }

    private void skipTagText() {
        skipText((start, c) -> {
            if (c == '<') {
                return 0;
            }
            return -1;
        });
    }

    private void skipComment() {
        endComment = 0;

        skipText((start, c) -> {
            if (c == '-') {
                endComment++;
            } else if (c == '>') {
                if (endComment >= 2) {
                    return 0;
                } else {
                    endComment = 0;
                }
            } else {
                endComment = 0;
            }
            return -1;
        });
    }

    private void skipAttributies() {
        while (true) {
            skipSpace();
            char c = buf[offset];
            if (c == '>') {
                return;
            } else if (c == '/') {
                moveCursor();
                skipSpace();
                if (buf[offset] == '>') {
                    // end tag
                    lstTagName.remove(lstTagName.size() - 1);
                    curDepth--;
                    return;
                } else {
                    throw new ParseXmlException("tag can not close.");
                }
            } else if (c != '?') {
                skipAttributeName();
                moveCursor();
                skipSpace();
                skipAttributeValue();
                moveCursor();
            } else {
                moveCursor();
                skipSpace();
                if (buf[offset] == '>') {
                    moveCursor();
                    return;
                } else {
                    throw new ParseXmlException("tag can not close.");
                }
            }
        }
    }

    public void startDocument() {
        log.info("startDocument...");

    }

    public void endDocument() {
        log.info("endDocument...");

    }

    public void startElement(String tagName) {
        log.info("tagName = {}", tagName);

    }

    public void startAttribute(String attrName, String attrValue) {
        log.info("attrName = {}, attrValue = {}", attrName, attrValue);

    }

    public void setTagText(String tagText) {
        log.info("tagText = {}", tagText);

    }

    public void setComment(String comment) {
        log.info("comment = {}", comment);

    }

    private interface getTextFunc {

        public int get(StringBuilder sbText, int start, char c);

    }

    private interface skipTextFunc {

        public int skip(int start, char c);

    }

    public static class ParseXmlException extends ApplicationException {

        public ParseXmlException(String msg) {
            super(msg);
        }

        public ParseXmlException(Throwable cause) {
            super(cause);
        }

    }

}

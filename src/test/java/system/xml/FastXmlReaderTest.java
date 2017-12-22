package system.xml;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import system.utils.FileUtils;
import system.xml.FastXmlReader.ParseXmlException;

public class FastXmlReaderTest {

    private Charset charset = Charset.defaultCharset();

    @Test
    public void test0001() {
        FastXmlReader.load("test.xml");
        FastXmlReader.load("test.xml", (List<String>) null);
        FastXmlReader.load("test.xml", charset);
        FastXmlReader.load("test.xml", charset, null, -1);
        FastXmlReader.load("test.xml", charset, null, 1);
        FastXmlReader.load("test.xml", charset, Arrays.asList("/test/a"));
        FastXmlReader.load("test0.xml");

        try {
            InputStreamReader sr = new InputStreamReader(new FileInputStream(FileUtils.getFile("test.xml")));
            FastXmlReader.load(sr);

            sr = new InputStreamReader(new FileInputStream(FileUtils.getFile("test.xml")));
            FastXmlReader.load(sr, null, 4096);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test0002() {
        try {
            FastXmlReader.load("test1.xml");
        } catch (Exception e) {
            verifyException(e, "unexpected end of file.");
        }
    }

    @Test
    public void test0003() {
        try {
            FastXmlReader.load("test1.xml", charset, null, 52);
        } catch (Exception e) {
            verifyException(e, "unexpected end of file.");
        }
    }

    @Test
    public void test0004() {
        try {
            FastXmlReader.load("test2.xml");
        } catch (Exception e) {
            verifyException(e, "unexpected end of file.");
        }
    }

    @Test
    public void test0005() {
        try {
            FastXmlReader.load("test2.xml", charset, null, 53);
        } catch (Exception e) {
            verifyException(e, "unexpected end of file.");
        }
    }

    @Test
    public void test0006() {
        try {
            FastXmlReader.load("test3.xml");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof ParseXmlException);
            assertTrue(cause.getMessage().startsWith("attribute name must be start with \" or '"));
        }
    }

    @Test
    public void test0007() {
        try {
            FastXmlReader.load("test4.xml");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof ParseXmlException);
            System.out.println(e.getCause().getMessage());
        }
    }

    @Test
    public void test0008() {
        try {
            FastXmlReader.load("test5.xml");
        } catch (Exception e) {
            verifyException(e, "tag can not close.");
        }

        try {
            FastXmlReader.load("test5.xml", charset, Arrays.asList("/test/aaa"));
        } catch (Exception e) {
            verifyException(e, "tag can not close.");
        }
    }

    @Test
    public void test0009() {
        try {
            FastXmlReader.load("test6.xml");
        } catch (Exception e) {
            verifyException(e, "attribute has not value.");
        }

        try {
            FastXmlReader.load("test6.xml", charset, Arrays.asList("/test/aaa"));
        } catch (Exception e) {
            verifyException(e, "attribute has not value.");
        }
    }

    @Test
    public void test0010() {
        try {
            FastXmlReader.load("test7.xml");
        } catch (Exception e) {
            verifyException(e, "tag can not close.");
        }
    }

    @Test
    public void test0011() {
        try {
            FastXmlReader.load("test8.xml");
        } catch (Exception e) {
            verifyException(e, "document should begin with '<'.");
        }
    }

    @Test
    public void test0012() {
        try {
            InputStreamReader sr = new InputStreamReader(new FileInputStream(FileUtils.getFile("test1.xml")));
            FastXmlReader.load(sr);
        } catch (Exception e) {
            verifyException(e, "unexpected end of file.");
        }
    }

    private void verifyException(Exception e, String msg) {
        Throwable cause = e.getCause();
        assertTrue(cause instanceof ParseXmlException);
        assertTrue(cause.getMessage().equals(msg));
    }

}

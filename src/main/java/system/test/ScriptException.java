package system.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import system.exception.ApplicationException;
import system.xml.XmlNode;

public class ScriptException extends ApplicationException {

    private WebDriver driver;

    private XmlNode script;

    private static final Pattern pParam = Pattern.compile("\\{\"method\":\"(.+)\",\"selector\":\"(.+)\"\\}");

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, XmlNode script) {
        super(message);
        this.script = script;
    }

    public ScriptException(Exception e, WebDriver driver, XmlNode script) {
        super(e);
        this.driver = driver;
        this.script = script;

        if (e instanceof NoSuchElementException) {
            Matcher m = pParam.matcher(e.getMessage());
            if (m.find()) {
                String msg = "htmlエレメント[" + m.group(1) + "=\"" + m.group(2) + "\"]が見つかりませんでした。";
                //                switch (script.getName()) {
                //                case "setText":
                //                    String js = "$(input[type='text'])";
                //                    msg = (String) ((JavascriptExecutor) driver).executeScript(js);
                //                    break;
                //                }

                setErrorMessage(msg);
            }
        }
    }

    public XmlNode getScript() {
        return script;
    }

    public WebDriver getDriver() {
        return driver;
    }

}

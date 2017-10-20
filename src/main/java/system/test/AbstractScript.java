package system.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;
import system.xml.XmlAttribute;
import system.xml.XmlNode;
import system.xml.XmlStreamReader;

public abstract class AbstractScript {

    protected static final List<String> lstKey = Arrays.asList("setText", "click", "waitFor", "waitForPageLoad",
            "openWindow", "saveScreen", "start");

    private static final Logger log = LogManager.getLogger(AbstractScript.class);

    private static final Pattern pParam = Pattern.compile("\\$\\{(.+)\\}");

    protected static List<XmlNode> lstCommonAction;

    protected List<String> lstWindowId = new ArrayList<>();

    protected List<WebDriver> lstDriver = new ArrayList<>();

    protected WebDriver curDriver;

    private String type;

    private XmlNode curAction;

    public boolean run(String fileName, String type) {
        this.type = type;

        Map<XmlNode, Boolean> mapReplace = new HashMap<>();
        try {
            XmlStreamReader reader = XmlStreamReader.load(fileName);
            XmlNode node = XmlNode.getNode(reader.getRootNode(), "action");
            List<XmlNode> lstNode = node.getLstNode();

            for (XmlNode action : lstNode) {
                List<XmlNode> subNodes = action.getLstNode();
                if (subNodes == null && !lstKey.contains(action.getName())) {
                    action.setLstNode(replaceAction(action));
                    mapReplace.put(action, true);
                } else {
                    mapReplace.put(action, false);
                }
            }

            log.info("start...");

            for (XmlNode action : lstNode) {
                runNode(action);
            }

            log.info("end...");

        } catch (ScriptException e) {
            if (Boolean.TRUE.equals(mapReplace.get(curAction))) {
                fileName = FileUtils.getFile("script/CommonAction.xml").getAbsolutePath();
            }
            log.error("script error at: {} {}行", fileName, e.getScript().getLineNumber());
            //            log.error(e.getErrorMessage());
        }

        return true;
    }

    protected void runNode(XmlNode action) {
        curAction = action;
        if (!action.getName().equals("loop")) {
            log.info("runNode: {}", showNode(action));
            List<XmlNode> lstNode = action.getLstNode();
            if (lstNode != null) {
                for (XmlNode script : lstNode) {
                    runScript(script);
                }
            } else {
                runScript(action);
            }
        } else {
            runLoop(action);
        }
    }

    protected void runLoop(XmlNode loop) {
        String strCount = loop.getAttributeValue("count");
        int count = -1;
        if (strCount != null) {
            count = Integer.parseInt(strCount);
        }

        if (count != -1) {
            for (int i = 1; i <= count; i++) {
                log.info("runLoop: {}", i);
                List<XmlNode> lstNode = loop.getLstNode();
                for (XmlNode script : lstNode) {
                    runScript(script);
                }
            }
        } else {
            for (int i = 1;; i++) {
                log.info("runLoop: {}", i);
                List<XmlNode> lstNode = loop.getLstNode();
                for (XmlNode script : lstNode) {
                    runScript(script);
                }
            }
        }

    }

    private void loadCommonAction() {
        if (lstCommonAction == null) {
            XmlStreamReader reader = XmlStreamReader.load("script/CommonAction.xml");
            lstCommonAction = reader.getRootNode().getLstNode();
        }
    }

    protected List<XmlNode> replaceAction(XmlNode action) {
        loadCommonAction();
        List<XmlNode> subNodes = XmlNode.getNode(lstCommonAction, "action").getLstNode();
        List<XmlNode> lstNode = null;
        String ref = action.getAttributeValue("ref");
        if (ref != null) {
            for (XmlNode node : subNodes) {
                if (ref.equals(node.getAttributeValue("id"))) {
                    lstNode = node.getLstNode();
                    break;
                }
            }

            if (lstNode == null) {
                throw new ScriptException("CommonAction.xmlに「" + ref + "」の定義が見つかりませんでした。");
            }
        } else {
            String actionName = action.getName();
            for (XmlNode node : subNodes) {
                if (node.getName().equals(actionName) && node.getLstAttribute() == null) {
                    lstNode = node.getLstNode();
                    break;
                }
            }

            if (lstNode == null) {
                throw new ScriptException("CommonAction.xmlに「" + actionName + "」の定義が見つかりませんでした。", action);
            }

            // パラメータ置換
            for (XmlNode node : lstNode) {
                replaceParam(node, action);
            }
        }

        return lstNode;
    }

    private void replaceParam(XmlNode node, XmlNode action) {
        List<XmlAttribute> lstAttr = node.getLstAttribute();
        if (lstAttr != null && lstAttr.size() > 0) {
            for (XmlAttribute attr : lstAttr) {
                String attrValue = attr.getValue();
                Matcher m = pParam.matcher(attrValue);
                if (m.find()) {
                    for (int i = 1, len = m.groupCount(); i <= len; i++) {
                        String replacement = action.getAttributeValue(m.group(i));
                        attrValue = attrValue.replaceAll("\\$\\{" + m.group(i) + "\\}", replacement);
                    }
                    attr.setValue(attrValue);
                }
            }
        }
    }

    public String showNode(XmlNode script) {
        StringBuilder sb = new StringBuilder();
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(script.getName());

        List<XmlAttribute> lstAttr = script.getLstAttribute();
        if (lstAttr != null && lstAttr.size() > 0) {
            lstAttr.forEach(attr -> sb.append(" ").append(attr.getName()).append("=\"")
                    .append(attr.getValue()).append("\""));
        }

        return sb.toString();
    }

    protected void runScript(XmlNode script) {
        log.info("runScript: {}", showNode(script));
        switch (script.getName()) {
        case "setText":
            setText(script);
            break;
        case "click":
            click(script);
            break;
        case "waitFor":
            waitFor(script);
            break;
        case "waitForPageLoad":
            waitForPageLoad(script);
            break;
        case "openWindow":
            openWindow(script);
            break;
        case "saveScreen":
            saveScreen(script);
            break;
        case "start":
            start(script);
            break;
        default:
            throw new ScriptException("unsupported action", script);
        }
    }

    protected By getBy(XmlNode script) {
        try {
            String id = script.getAttributeValue("id");
            if (id != null) {
                return By.id(id);
            }

            String name = script.getAttributeValue("name");
            if (name != null) {
                return By.name(name);
            }

            String selector = script.getAttributeValue("selector");
            if (selector != null) {
                return By.cssSelector(selector);
            }

            throw new ScriptException("id or name or selector is must be set");
        } catch (Exception e) {
            throw new ScriptException(e, curDriver, script);
        }
    }

    protected void setText(XmlNode script) {
        By by = getBy(script);
        WebDriverWait wait = new WebDriverWait(curDriver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));

        WebElement element = curDriver.findElement(by);
        element.clear();
        element.sendKeys(script.getAttributeValue("value"));
    }

    protected void click(XmlNode script) {
        By by = getBy(script);
        WebDriverWait wait = new WebDriverWait(curDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(by));

        WebElement element = curDriver.findElement(by);
        wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
    }

    protected void waitFor(XmlNode script) {
        String ms = script.getAttributeValue("ms");
        if (ms == null) {
            throw new ScriptException("waitForタグにmsが必要です。", script);
        }

        try {
            long millis = Long.parseLong(ms);
            Thread.sleep(millis);
        } catch (NumberFormatException e) {
            throw new ScriptException("waitForタグのmsに数字を入力してください。", script);
        } catch (InterruptedException e) {
            // no op
        }
    }

    protected abstract void waitForPageLoad(XmlNode script);

    protected void openWindow(XmlNode script) {
        // ウィンドウ表示までに時間がかかると、seleniumが先走ることがあるのでウィンドウが増えるまで待機
        (new WebDriverWait(curDriver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getWindowHandles().size() > lstDriver.size();
            }
        });

        String newWindowId = null;
        for (String id : curDriver.getWindowHandles()) {
            if (!lstWindowId.contains(id)) {
                newWindowId = id;
                break;
            }
        }

        // ウィンドウ切り替え
        curDriver = curDriver.switchTo().window(newWindowId);
        lstDriver.add(curDriver);
        lstWindowId.add(curDriver.getWindowHandle());
    }

    protected void saveScreen(XmlNode script) {
        if (curDriver instanceof HtmlUnitDriver) {
            log.warn("HtmlUnitDriverはsaveScreenできませんので、無視する。");
            return;
        }

        File img = (File) ((TakesScreenshot) curDriver).getScreenshotAs(OutputType.FILE);
        try {
            File file = new File(script.getAttributeValue("filename"));
            FileUtils.forceMkdirParent(file);
            ImageIO.write(ImageIO.read(img), "jpeg", file);
        } catch (IOException e) {
            throw new ScriptException(e, curDriver, script);
        }
    }

    protected void start(XmlNode script) {
        switch (type) {
        case "ie":
            DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
            ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            curDriver = new InternetExplorerDriver(ieCapabilities);
            //            curDriver = new InternetExplorerDriver();
            break;
        case "edge":
            curDriver = new EdgeDriver();
            break;
        case "chrome":
            curDriver = new ChromeDriver();
            break;
        case "firefox":
            curDriver = new FirefoxDriver();
            break;
        case "safari":
            curDriver = new SafariDriver();
            break;
        case "htmlunit":
            curDriver = new HtmlUnitDriver(true);
            break;
        default:
            throw new ScriptException("unsuporrted type");
        }
        curDriver.get(script.getAttributeValue("url"));
        lstDriver.add(curDriver);
        lstWindowId.add(curDriver.getWindowHandle());
    }

    protected Object getReadyState() {
        return ((JavascriptExecutor) curDriver).executeScript("return document.readyState;");
    }

}

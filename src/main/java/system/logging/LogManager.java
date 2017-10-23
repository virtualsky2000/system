package system.logging;

import java.io.File;

import system.utils.ClassUtils;

/*
 * Log4j version >= 1.2.12
 */
public class LogManager {

    private static int mode = 0;

    private static final String MinLog4jVersion = "1.2.12";

    public static Logger getLogger(Class<?> clazz) {
        if (mode == 1) {
            return new Log4J2Logger(org.apache.logging.log4j.LogManager.getLogger(clazz));
        } else if (mode == 2) {
            return new Slf4JLogger(org.slf4j.LoggerFactory.getLogger(clazz));
        } else if (mode == 3) {
            return new Log4JLogger(org.apache.log4j.LogManager.getLogger(clazz));
        } else if (mode == 4) {
            return new CommonLogger(org.apache.commons.logging.LogFactory.getLog(clazz));
        } else {
            return new SimpleLogger();
        }
    }

    public static Logger getLogger(String name) {
        if (mode == 1) {
            return new Log4J2Logger(org.apache.logging.log4j.LogManager.getLogger(name));
        } else if (mode == 2) {
            return new Slf4JLogger(org.slf4j.LoggerFactory.getLogger(name));
        } else if (mode == 3) {
            return new Log4JLogger(org.apache.log4j.LogManager.getLogger(name));
        } else if (mode == 4) {
            return new CommonLogger(org.apache.commons.logging.LogFactory.getLog(name));
        } else {
            return new SimpleLogger();
        }
    }

    public static boolean isLog4J2Available() {
        try {
            Class.forName("org.apache.logging.log4j.Logger");
            Class.forName("org.apache.logging.log4j.LogManager");
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (ClassUtils.getResource("log4j2.xml") == null) {
            return false;
        }

        System.out.println("Log4j2 will be used.");
        return true;
    }

    public static boolean isSlf4JAvailable() {
        try {
            Class.forName("org.slf4j.Logger");
            Class.forName("ch.qos.logback.core.Context");
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (ClassUtils.getResource("logback.xml") == null) {
            return false;
        }

        System.out.println("Slf4J will be used.");
        return true;
    }

    public static boolean isLog4JAvailable() {
        try {
            Class.forName("org.apache.log4j.Logger");
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (ClassUtils.getResource("log4j.xml") == null && ClassUtils.getResource("log4j.properties") == null) {
            return false;
        }

        File jarFile = ClassUtils.getClassFile("org.apache.log4j.Logger");
        String ver = ClassUtils.getJarFileVersion(jarFile);
        if (ver.compareTo(MinLog4jVersion) < 0) {
            System.out.println("log4j version " + MinLog4jVersion + " or higher be supported, current version " + ver);
            return false;
        }

        System.out.println("Log4j will be used.");
        return true;
    }

    public static boolean isCommonLoggerAvailable() {
        try {
            Class.forName("org.apache.commons.logging.LogFactory");
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (ClassUtils.getResource("commons-logging.properties") == null) {
            return false;
        }

        System.out.println("Commons Logging will be used.");
        return true;
    }

    static {
        if (isLog4J2Available()) {
            mode = 1;
        } else if (isSlf4JAvailable()) {
            mode = 2;
        } else if (isLog4JAvailable()) {
            mode = 3;
        } else if (isCommonLoggerAvailable()) {
            mode = 4;
        }
    }

}

package system.utils;

public class ExceptionUtils extends org.apache.commons.lang.exception.ExceptionUtils {

    public static StackTraceElement getCurrentStackTraceElement() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        for (int i = 0, len = st.length; i < len; i++) {
            if ("<init>".equals(st[i].getMethodName())) {
                return st[i + 1];
            }
        }
        return null;
    }

    public static String getCurrentMethod() {
        StackTraceElement element = getCurrentStackTraceElement();
        return String.format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(),
                element.getFileName(), element.getLineNumber());
    }

}

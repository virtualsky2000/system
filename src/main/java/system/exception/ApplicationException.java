package system.exception;

import system.logging.LogManager;
import system.logging.Logger;

public class ApplicationException extends RuntimeException {

    private Logger log = null;

    private Object[] objects;

    private String errorMessage;

    public ApplicationException() {
        this(getMsg());
    }

    public ApplicationException(String message) {
        super(message);
        getLogger();
        log.error(message);
    }

    public ApplicationException(Throwable cause) {
        this(getMsg(), cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        getLogger();
        log.error(message);
    }

    public ApplicationException(Throwable cause, Object... objects) {
        this(getMsg(), cause);
        this.setObjects(objects);
    }

    private static String getMsg() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        for (int i = 3, len = st.length; i < len; i++) {
            StackTraceElement element = st[i];
            String methodName = element.getMethodName();
            if (!"<init>".equals(methodName)) {
                return element.getClassName() + "." + methodName + "が失敗しました。";
            }
        }
        return "";
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public Logger getLogger() {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        return log;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}

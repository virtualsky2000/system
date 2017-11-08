package system.exception;

import system.utils.ExceptionUtils;

public class MustOverrideException extends ApplicationException {

    public MustOverrideException(Class<?> clazz) {
        super(getMsg(clazz));
    }

    private static String getMsg(Class<?> clazz) {
        StackTraceElement element = ExceptionUtils.getCurrentStackTraceElement();
        return String.format("%s.%s()をオーバーライトしてください。", clazz.getName(), element.getMethodName());
    }

}

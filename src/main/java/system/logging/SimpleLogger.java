package system.logging;

import java.io.PrintStream;

public class SimpleLogger implements Logger {

    private static final String[] strMsg = { "[TRACE] ", "[DEBUG] ", "[INFO] ", "[WARN] ", "[ERROR] ", "[FATAL] " };

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(Object paramObject) {
        log(0, String.valueOf(paramObject));
    }

    @Override
    public void trace(Object paramObject, Throwable paramThrowable) {
        log(0, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void trace(String paramString) {
        log(0, paramString);
    }

    @Override
    public void trace(String paramString, Object... paramVarArgs) {
        log(0, paramString, paramVarArgs);
    }

    @Override
    public void trace(String paramString, Throwable paramThrowable) {
        log(0, paramString, paramThrowable);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(Object paramObject) {
        log(1, String.valueOf(paramObject));
    }

    @Override
    public void debug(Object paramObject, Throwable paramThrowable) {
        log(1, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void debug(String paramString) {
        log(1, paramString);
    }

    @Override
    public void debug(String paramString, Object... paramVarArgs) {
        log(1, paramString, paramVarArgs);
    }

    @Override
    public void debug(String paramString, Throwable paramThrowable) {
        log(1, paramString, paramThrowable);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(Object paramObject) {
        log(2, String.valueOf(paramObject));
    }

    @Override
    public void info(Object paramObject, Throwable paramThrowable) {
        log(2, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void info(String paramString) {
        log(2, paramString);
    }

    @Override
    public void info(String paramString, Object... paramVarArgs) {
        log(2, paramString, paramVarArgs);
    }

    @Override
    public void info(String paramString, Throwable paramThrowable) {
        log(2, paramString, paramThrowable);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(Object paramObject) {
        log(3, String.valueOf(paramObject));
    }

    @Override
    public void warn(Object paramObject, Throwable paramThrowable) {
        log(3, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void warn(String paramString) {
        log(3, paramString);
    }

    @Override
    public void warn(String paramString, Object... paramVarArgs) {
        log(3, paramString, paramVarArgs);
    }

    @Override
    public void warn(String paramString, Throwable paramThrowable) {
        log(3, paramString, paramThrowable);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(Object paramObject) {
        log(4, String.valueOf(paramObject));
    }

    @Override
    public void error(Object paramObject, Throwable paramThrowable) {
        log(4, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void error(String paramString) {
        log(4, paramString);
    }

    @Override
    public void error(String paramString, Object... paramVarArgs) {
        log(4, paramString, paramVarArgs);
    }

    @Override
    public void error(String paramString, Throwable paramThrowable) {
        log(4, paramString, paramThrowable);
    }

    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public void fatal(Object paramObject) {
        log(5, String.valueOf(paramObject));
    }

    @Override
    public void fatal(Object paramObject, Throwable paramThrowable) {
        log(5, String.valueOf(paramObject), paramThrowable);
    }

    @Override
    public void fatal(String paramString) {
        log(5, paramString);
    }

    @Override
    public void fatal(String paramString, Object... paramVarArgs) {
        log(5, paramString, paramVarArgs);
    }

    @Override
    public void fatal(String paramString, Throwable paramThrowable) {
        log(5, paramString, paramThrowable);
    }

    private void log(int level, String paramString) {
        PrintStream ps;

        if (level < 4) {
            ps = System.out;
        } else {
            ps = System.err;
        }

        ps.println(strMsg[level] + paramString);
    }

    private void log(int level, String paramString, Object... paramVarArgs) {
        log(level, MessageFormat.format(paramString, paramVarArgs));
    }

    private void log(int level, String paramString, Throwable paramThrowable) {
        log(level, MessageFormat.format(paramString, paramThrowable));
    }

}

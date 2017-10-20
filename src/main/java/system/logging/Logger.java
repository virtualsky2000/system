package system.logging;

public abstract interface Logger {

    public abstract boolean isTraceEnabled();

    public abstract void trace(Object paramObject);

    public abstract void trace(Object paramObject, Throwable paramThrowable);

    public abstract void trace(String paramString);

    public abstract void trace(String paramString, Object... paramVarArgs);

    public abstract void trace(String paramString, Throwable paramThrowable);

    public abstract boolean isDebugEnabled();

    public abstract void debug(Object paramObject);

    public abstract void debug(Object paramObject, Throwable paramThrowable);

    public abstract void debug(String paramString);

    public abstract void debug(String paramString, Object... paramVarArgs);

    public abstract void debug(String paramString, Throwable paramThrowable);

    public abstract boolean isInfoEnabled();

    public abstract void info(Object paramObject);

    public abstract void info(Object paramObject, Throwable paramThrowable);

    public abstract void info(String paramString);

    public abstract void info(String paramString, Object... paramVarArgs);

    public abstract void info(String paramString, Throwable paramThrowable);

    public abstract boolean isWarnEnabled();

    public abstract void warn(Object paramObject);

    public abstract void warn(Object paramObject, Throwable paramThrowable);

    public abstract void warn(String paramString);

    public abstract void warn(String paramString, Object... paramVarArgs);

    public abstract void warn(String paramString, Throwable paramThrowable);

    public abstract boolean isErrorEnabled();

    public abstract void error(Object paramObject);

    public abstract void error(Object paramObject, Throwable paramThrowable);

    public abstract void error(String paramString);

    public abstract void error(String paramString, Object... paramVarArgs);

    public abstract void error(String paramString, Throwable paramThrowable);

    public abstract boolean isFatalEnabled();

    public abstract void fatal(Object paramObject);

    public abstract void fatal(Object paramObject, Throwable paramThrowable);

    public abstract void fatal(String paramString);

    public abstract void fatal(String paramString, Object... paramVarArgs);

    public abstract void fatal(String paramString, Throwable paramThrowable);

}

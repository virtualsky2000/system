package system.logging;

public class CommonLogger implements Logger {

    private transient org.apache.commons.logging.Log logger = null;

    public CommonLogger(org.apache.commons.logging.Log logger) {
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(Object paramObject) {
        if (logger.isTraceEnabled()) {
            logger.trace(paramObject);
        }
    }

    @Override
    public void trace(Object paramObject, Throwable paramThrowable) {
        if (logger.isTraceEnabled()) {
            logger.trace(paramObject, paramThrowable);
        }
    }

    @Override
    public void trace(String paramString) {
        if (logger.isTraceEnabled()) {
            logger.trace(paramString);
        }
    }

    @Override
    public void trace(String paramString, Object... paramVarArgs) {
        if (logger.isTraceEnabled()) {
            logger.trace(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void trace(String paramString, Throwable paramThrowable) {
        if (logger.isTraceEnabled()) {
            logger.trace(paramString, paramThrowable);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(Object paramObject) {
        if (logger.isDebugEnabled()) {
            logger.debug(paramObject);
        }
    }

    @Override
    public void debug(Object paramObject, Throwable paramThrowable) {
        if (logger.isDebugEnabled()) {
            logger.debug(paramObject, paramThrowable);
        }
    }

    @Override
    public void debug(String paramString) {
        if (logger.isDebugEnabled()) {
            logger.debug(paramString);
        }
    }

    @Override
    public void debug(String paramString, Object... paramVarArgs) {
        if (logger.isDebugEnabled()) {
            logger.debug(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void debug(String paramString, Throwable paramThrowable) {
        if (logger.isDebugEnabled()) {
            logger.debug(paramString, paramThrowable);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(Object paramObject) {
        if (logger.isInfoEnabled()) {
            logger.info(paramObject);
        }
    }

    @Override
    public void info(Object paramObject, Throwable paramThrowable) {
        if (logger.isInfoEnabled()) {
            logger.info(paramObject, paramThrowable);
        }
    }

    @Override
    public void info(String paramString) {
        if (logger.isInfoEnabled()) {
            logger.info(paramString);
        }
    }

    @Override
    public void info(String paramString, Object... paramVarArgs) {
        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void info(String paramString, Throwable paramThrowable) {
        if (logger.isInfoEnabled()) {
            logger.info(paramString, paramThrowable);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(Object paramObject) {
        if (logger.isWarnEnabled()) {
            logger.warn(paramObject);
        }
    }

    @Override
    public void warn(Object paramObject, Throwable paramThrowable) {
        if (logger.isWarnEnabled()) {
            logger.warn(paramObject, paramThrowable);
        }
    }

    @Override
    public void warn(String paramString) {
        if (logger.isWarnEnabled()) {
            logger.warn(paramString);
        }
    }

    @Override
    public void warn(String paramString, Object... paramVarArgs) {
        if (logger.isWarnEnabled()) {
            logger.warn(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void warn(String paramString, Throwable paramThrowable) {
        if (logger.isWarnEnabled()) {
            logger.warn(paramString, paramThrowable);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(Object paramObject) {
        if (logger.isErrorEnabled()) {
            logger.error(paramObject);
        }
    }

    @Override
    public void error(Object paramObject, Throwable paramThrowable) {
        if (logger.isErrorEnabled()) {
            logger.error(paramObject, paramThrowable);
        }
    }

    @Override
    public void error(String paramString) {
        if (logger.isErrorEnabled()) {
            logger.error(paramString);
        }
    }

    @Override
    public void error(String paramString, Object... paramVarArgs) {
        if (logger.isErrorEnabled()) {
            logger.error(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void error(String paramString, Throwable paramThrowable) {
        if (logger.isErrorEnabled()) {
            logger.error(paramString, paramThrowable);
        }
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    @Override
    public void fatal(Object paramObject) {
        if (logger.isFatalEnabled()) {
            logger.fatal(paramObject);
        }
    }

    @Override
    public void fatal(Object paramObject, Throwable paramThrowable) {
        if (logger.isFatalEnabled()) {
            logger.fatal(paramObject, paramThrowable);
        }
    }

    @Override
    public void fatal(String paramString) {
        if (logger.isFatalEnabled()) {
            logger.fatal(paramString);
        }
    }

    @Override
    public void fatal(String paramString, Object... paramVarArgs) {
        if (logger.isFatalEnabled()) {
            logger.fatal(MessageFormat.format(paramString, paramVarArgs));
        }
    }

    @Override
    public void fatal(String paramString, Throwable paramThrowable) {
        if (logger.isFatalEnabled()) {
            logger.fatal(paramString, paramThrowable);
        }
    }

}

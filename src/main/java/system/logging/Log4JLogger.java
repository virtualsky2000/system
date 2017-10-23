package system.logging;

import org.apache.log4j.Level;

public class Log4JLogger implements Logger {

    private transient org.apache.log4j.Logger logger;

    public Log4JLogger(org.apache.log4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(Object paramObject) {
        logger.trace(paramObject);
    }

    @Override
    public void trace(Object paramObject, Throwable paramThrowable) {
        logger.trace(paramObject, paramThrowable);
    }

    @Override
    public void trace(String paramString) {
        logger.trace(paramString);
    }

    @Override
    public void trace(String paramString, Object... paramVarArgs) {
        logger.trace(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void trace(String paramString, Throwable paramThrowable) {
        logger.trace(paramString, paramThrowable);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(Object paramObject) {
        logger.debug(paramObject);
    }

    @Override
    public void debug(Object paramObject, Throwable paramThrowable) {
        logger.debug(paramObject, paramThrowable);
    }

    @Override
    public void debug(String paramString) {
        logger.debug(paramString);
    }

    @Override
    public void debug(String paramString, Object... paramVarArgs) {
        logger.debug(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void debug(String paramString, Throwable paramThrowable) {
        logger.debug(paramString, paramThrowable);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(Object paramObject) {
        logger.info(paramObject);
    }

    @Override
    public void info(Object paramObject, Throwable paramThrowable) {
        logger.info(paramObject, paramThrowable);
    }

    @Override
    public void info(String paramString) {
        logger.info(paramString);
    }

    @Override
    public void info(String paramString, Object... paramVarArgs) {
        logger.info(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void info(String paramString, Throwable paramThrowable) {
        logger.info(paramString, paramThrowable);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public void warn(Object paramObject) {
        logger.warn(paramObject);
    }

    @Override
    public void warn(Object paramObject, Throwable paramThrowable) {
        logger.warn(paramObject, paramThrowable);
    }

    @Override
    public void warn(String paramString) {
        logger.warn(paramString);
    }

    @Override
    public void warn(String paramString, Object... paramVarArgs) {
        logger.warn(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void warn(String paramString, Throwable paramThrowable) {
        logger.warn(paramString, paramThrowable);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

    @Override
    public void error(Object paramObject) {
        logger.error(paramObject);
    }

    @Override
    public void error(Object paramObject, Throwable paramThrowable) {
        logger.error(paramObject, paramThrowable);
    }

    @Override
    public void error(String paramString) {
        logger.error(paramString);
    }

    @Override
    public void error(String paramString, Object... paramVarArgs) {
        logger.error(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void error(String paramString, Throwable paramThrowable) {
        logger.error(paramString, paramThrowable);
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isEnabledFor(Level.FATAL);
    }

    @Override
    public void fatal(Object paramObject) {
        logger.fatal(paramObject);
    }

    @Override
    public void fatal(Object paramObject, Throwable paramThrowable) {
        logger.fatal(paramObject, paramThrowable);
    }

    @Override
    public void fatal(String paramString) {
        logger.fatal(paramString);
    }

    @Override
    public void fatal(String paramString, Object... paramVarArgs) {
        logger.fatal(MessageFormat.format(paramString, paramVarArgs));
    }

    @Override
    public void fatal(String paramString, Throwable paramThrowable) {
        logger.fatal(paramString, paramThrowable);
    }

}

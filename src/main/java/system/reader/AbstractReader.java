package system.reader;

import java.io.File;
import java.nio.charset.Charset;

import system.logging.LogManager;
import system.logging.Logger;

public abstract class AbstractReader {

    protected File file;

    protected Charset charset;

    protected Logger log;

    public abstract void load();

    protected AbstractReader(File file, Charset charset) {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        this.file = file;
        this.charset = charset;
    }

}

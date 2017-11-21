package system.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public abstract class AbstractReader {

    protected Logger log;

    protected File file;

    protected Charset charset;

    protected InputStreamReader sr;

    public abstract void load();

    protected AbstractReader(File file, Charset charset) {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        this.file = file;
        this.charset = charset;
        try {
            this.sr = new InputStreamReader(new FileInputStream(file), charset);
        } catch (FileNotFoundException e) {
            throw new ApplicationException(e);
        }
    }

    protected AbstractReader(InputStreamReader sr) {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        this.sr = sr;
    }

    protected void throwException(String title, Exception e) {
        if (file != null) {
            throw new ApplicationException(title + "ファイル「" + file.getAbsolutePath() + "」の読込が失敗しました。", e);
        } else {
            throw new ApplicationException(title + "インプットストリームリーダーの読込が失敗しました。", e);
        }
    }

}

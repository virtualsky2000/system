package system.utils;

import java.io.File;
import java.net.URL;

import system.exception.ApplicationException;

public class FileUtils extends org.apache.commons.io.FileUtils {

    public static File getFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            URL url = ClassUtils.getResource(fileName);
            if (url == null) {
                throw new ApplicationException("ファイル「" + fileName + "」が見つかりませんでした。");
            }
            fileName = url.getFile();
            file = new File(fileName);
        }

        return file;
    }

}

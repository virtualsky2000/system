package system.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import system.utils.FileUtils;
import system.utils.PropertiesReader;

public class PropertiesReaderCache extends PropertiesReader {

    private static final Map<String, PropertiesReaderCache> mapCache = new ConcurrentHashMap<>();

    private long times = 0;

    public static PropertiesReaderCache load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset(), false);
    }

    public static PropertiesReaderCache load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset, false);
    }

    public static PropertiesReaderCache load(File file, Charset charset) {
        return load(file, charset, false);
    }

    public static PropertiesReaderCache load(String fileName, boolean sort) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset(), sort);
    }

    public static PropertiesReaderCache load(String fileName, Charset charset, boolean sort) {
        return load(FileUtils.getFile(fileName), charset, sort);
    }

    public static PropertiesReaderCache load(File file, Charset charset, boolean sort) {
        String fileName = file.getAbsolutePath();

        if (mapCache.containsKey(fileName)) {
            return mapCache.get(fileName);
        } else {
            PropertiesReaderCache cache = new PropertiesReaderCache(file, charset, sort);
            cache.load();

            mapCache.put(fileName, cache);

            return cache;
        }
    }

    protected PropertiesReaderCache(File file, Charset charset, boolean sort) {
        super(file, charset, sort);
    }

    @Override
    synchronized public void load() {
        long lastTimes = file.lastModified();
        if (times < lastTimes) {
            // 前回読込後、ファイルが更新された場合再読込
            super.load();

            times = lastTimes;
        }
    }

}

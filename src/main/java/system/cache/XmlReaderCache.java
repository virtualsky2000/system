package system.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import system.utils.FileUtils;
import system.xml.XmlNode;
import system.xml.XmlReader;

public class XmlReaderCache extends XmlReader {

    private static final Map<String, XmlReaderCache> mapCache = new ConcurrentHashMap<>();

    private long times = 0;

    public static XmlReaderCache load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlReaderCache load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlReaderCache load(File file, Charset charset) {
        String fileName = file.getAbsolutePath();

        if (mapCache.containsKey(fileName)) {
            return mapCache.get(fileName);
        } else {
            XmlReaderCache cache = new XmlReaderCache(file, charset);
            cache.load();

            mapCache.put(fileName, cache);

            return cache;
        }
    }

    protected XmlReaderCache(File file, Charset charset) {
        super(file, charset);
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

    @Override
    public XmlNode getNode(String path) {
        load();
        return super.getNode(path);
    }

    @Override
    public XmlNode getRootNode() {
        load();
        return super.getRootNode();
    }

}

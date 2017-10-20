package system.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import system.exception.ApplicationException;
import system.reader.AbstractReader;

public class PropertiesReader extends AbstractReader {

    protected boolean sort;

    private Properties props;

    public static PropertiesReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset(), false);
    }

    public static PropertiesReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset, false);
    }

    public static PropertiesReader load(File file, Charset charset) {
        return load(file, charset, false);
    }

    public static PropertiesReader load(String fileName, boolean sort) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset(), sort);
    }

    public static PropertiesReader load(String fileName, Charset charset, boolean sort) {
        return load(FileUtils.getFile(fileName), charset, sort);
    }

    public static PropertiesReader load(File file, Charset charset, boolean sort) {
        PropertiesReader reader = new PropertiesReader(file, charset, sort);
        reader.load();

        return reader;
    }

    protected PropertiesReader(File file, Charset charset, boolean sort) {
        super(file, charset);
        this.sort = sort;

        if (sort) {
            this.props = new SortProperties();
        } else {
            this.props = new Properties();
        }
    }

    public void load() {
        try {
            props.clear();
            props.load(new InputStreamReader(new FileInputStream(file), charset));
        } catch (IOException e) {
            throw new ApplicationException("プロパティファイル「" + file.getAbsolutePath() + "」の読込が失敗しました。", e);
        }
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public int size() {
        return props.size();
    }

    public boolean isEmpty() {
        return props.isEmpty();
    }

    public boolean containsKey(Object key) {
        return props.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return props.containsValue(value);
    }

    public Enumeration<Object> keys() {
        return props.keys();
    }

    public Collection<Object> values() {
        return props.values();
    }

    public Enumeration<Object> elements() {
        return props.elements();
    }

    public Set<Object> keySet() {
        return props.keySet();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return props.entrySet();
    }

    private class SortProperties extends Properties {

        private LinkedHashMap<Object, Object> mapData = new LinkedHashMap<>();

        @Override
        public Object put(Object key, Object value) {
            return mapData.put(key, value);
        }

        @Override
        public Object get(Object key) {
            return mapData.get(key);
        }

        @Override
        public boolean contains(Object value) {
            return mapData.containsValue(value);
        }

        @Override
        public boolean containsKey(Object key) {
            return mapData.containsKey(key);
        }

        @Override
        public Object remove(Object key) {
            return mapData.remove(key);
        }

        @Override
        public void clear() {
            mapData.clear();
        }

        @Override
        public int size() {
            return mapData.size();
        }

        @Override
        public boolean isEmpty() {
            return mapData.isEmpty();
        }

        @Override
        public Collection<Object> values() {
            return mapData.values();
        }

        @Override
        public Set<Object> keySet() {
            return mapData.keySet();
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return mapData.entrySet();
        }

        @Override
        public synchronized Enumeration<Object> keys() {

            return new Enumeration<Object>() {

                int index = 0;
                int size = mapData.size();

                Object[] keys = mapData.keySet().toArray();

                @Override
                public boolean hasMoreElements() {
                    return index < size;
                }

                @Override
                public Object nextElement() {
                    return keys[index++];
                }

            };

        }

        @Override
        public synchronized Enumeration<Object> elements() {

            return new Enumeration<Object>() {

                int index = 0;
                int size = mapData.size();

                Object[] values = mapData.values().toArray();

                @Override
                public boolean hasMoreElements() {
                    return index < size;
                }

                @Override
                public Object nextElement() {
                    return values[index++];
                }

            };

        }

    }

}

package system.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import system.reader.AbstractReader;
import system.utils.ClassUtils;
import system.utils.FileUtils;

public class ReaderCache<T> {

	private static final Map<Object, Object> hashCache = new ConcurrentHashMap<>();

	private long times = 0;

	protected File file;

	protected T reader;

	public static <T> ReaderCache<T> load(String fileName, Class<T> clazz) {
		return load(FileUtils.getFile(fileName), Charset.defaultCharset(), clazz);
	}

	public static <T> ReaderCache<T> load(String fileName, Charset charset, Class<T> clazz) {
		return load(FileUtils.getFile(fileName), charset, clazz);
	}

	public static <T> ReaderCache<T> load(File file, Charset charset, Class<T> clazz) {
		Map<String, ReaderCache<T>> mapCache = getCache(clazz);
		String fileName = file.getAbsolutePath();

		if (mapCache.containsKey(fileName)) {
			return mapCache.get(fileName);
		} else {
			ReaderCache<T> cache = new ReaderCache<>(file, charset, clazz);
			cache.load();

			mapCache.put(fileName, cache);

			return cache;
		}
	}

	private static <T> Map<String, ReaderCache<T>> getCache(Class<T> clazz) {
		if (hashCache.containsKey(clazz)) {
			return ClassUtils.cast(hashCache.get(clazz));
		} else {
			Map<String, ReaderCache<T>> cache = new ConcurrentHashMap<>();
			hashCache.put(clazz, cache);
			return cache;
		}
	}

	protected ReaderCache(File file, Charset charset, Class<T> clazz) {
		assert clazz.isAssignableFrom(AbstractReader.class);
		this.file = file;
		this.reader = null; // TODO
	}

	synchronized public void load() {
		long lastTimes = file.lastModified();
		if (times < lastTimes) {
			// 前回読込後、ファイルが更新された場合再読込
			((AbstractReader) reader).load();

			times = lastTimes;
		}
	}

}

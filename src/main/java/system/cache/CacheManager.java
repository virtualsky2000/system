package system.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import system.logging.LogManager;
import system.logging.Logger;

public final class CacheManager {

    private static final Logger logger = LogManager.getLogger(CacheManager.class);

    private static final Map<Object, Object> mapCache = new ConcurrentHashMap<>();

    private static final Map<Object, Long> mapTime = new ConcurrentHashMap<>();

    private static int delayTime = 10000;

    private static int lostTime = 60000;

    private static Timer timer;

    static {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                checkCache();
            }

        };

        timer = new Timer();
        timer.schedule(task, 0, delayTime);
    }

    synchronized public static void checkCache() {
        if (mapTime.size() == 0) {
            return;
        }

        logger.debug("in checkCache...");
        long curTime = System.currentTimeMillis();

        for (Entry<Object, Long> entry : mapTime.entrySet()) {
            if (curTime - entry.getValue() > lostTime) {
                logger.debug("delete...");
                mapTime.remove(entry.getKey());
                mapCache.remove(entry.getKey());
            }
        }
    }

    synchronized public static Object get(String key) {
        if (mapCache.containsKey(key)) {
            mapTime.put(key, System.currentTimeMillis());
            return mapCache.get(key);
        }

        return null;
    }

    synchronized public static Object put(Object key, Object value) {
        mapTime.put(key, System.currentTimeMillis());
        return mapCache.put(key, value);
    }

    synchronized public static boolean containsKey(Object key) {
        return mapCache.containsKey(key);
    }

    synchronized public static void shutdown() {
        mapTime.clear();
        mapCache.clear();
        timer.cancel();
    }

}

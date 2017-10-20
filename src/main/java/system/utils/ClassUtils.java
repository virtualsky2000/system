package system.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassUtils extends org.apache.commons.lang.ClassUtils {

    private static ClassLoader loader;

    static {
        loader = ClassUtils.class.getClassLoader();
    }

    public static boolean isExtend(Class<?> A, Class<?> B) {
        do {
            if (B.equals(A)) {
                return true;
            }
            B = B.getSuperclass();
        } while (!B.equals(Object.class));

        return false;
    }

    public static URL getResource(String name) {
        return loader.getResource(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static File getClassFile(String className) {
        try {
            String name = className.substring(className.lastIndexOf(".") + 1) + ".class";
            String path = Class.forName(className).getResource(name).getFile();
            path = path.substring(6, path.indexOf("!"));
            return new File(path);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static String getVersionByPom(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String value;
            while ((value = br.readLine()) != null) {
                if (value.startsWith("version=")) {
                    return value.substring(8);
                }
            }
        }

        return null;
    }

    private static String getVersionByManifest(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String value;
            while ((value = br.readLine()) != null) {
                if (value.startsWith("Implementation-Version:")) {
                    return value.substring(23).trim();
                }
            }
        }

        return null;
    }

    public static String getJarFileVersion(File jarFile) {
        try (ZipFile zip = new ZipFile(jarFile)) {
            ZipEntry maven = zip.getEntry("META-INF/maven/");
            if (maven != null) {
                @SuppressWarnings("unchecked")
                Enumeration<ZipEntry> enumEntry = (Enumeration<ZipEntry>) zip.entries();
                while (enumEntry.hasMoreElements()) {
                    ZipEntry entry = enumEntry.nextElement();
                    String name = entry.getName();
                    if (name.startsWith("META-INF/maven/") && name.endsWith("pom.properties")) {
                        try (InputStream in = zip.getInputStream(entry)) {
                            return getVersionByPom(in);
                        }
                    }
                }
            } else {
                ZipEntry manifest = zip.getEntry("META-INF/MANIFEST.MF");
                if (manifest != null) {
                    try (InputStream in = zip.getInputStream(manifest)) {
                        return getVersionByManifest(in);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

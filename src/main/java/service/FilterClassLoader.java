package service;

import filters.Filter;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilterClassLoader {
    private static final FilterClassLoader outInstance = new FilterClassLoader();
    private final Map<String, Class<? extends Filter>> classMap = new HashMap<>();
    private final String packageToScan = "com.gridnine.testing.filters".replace(".", "/");
    private final URL url = Objects.requireNonNull(Thread
            .currentThread()
            .getContextClassLoader()
            .getResource(packageToScan));
    private final File file = new File(String.valueOf(url));

    private FilterClassLoader() {
    }

    public static FilterClassLoader getInstance() {
        return outInstance;
    }

    public Map<String, Class<? extends Filter>> getImplClasses() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (!file.exists()) {
            return classMap;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return classMap;
        }
        for (File file : files) {
            //в другие пакеты уходить ненужно
            if (file.isFile() && file.getName().endsWith("Filter.class") && !file.getName().startsWith("Filter")) {
                classMap.put(file.getName().substring(0, file.getName().indexOf(".")), (Class<? extends Filter>) Class.forName(String.valueOf(file)).newInstance());
            }
        }
        return classMap;

      /*  if (files == null) {
            assert false;
            if (files.length != 0) {
               return classMap;
            }
        }
        return Stream.of(files)
                .filter(file -> file.getName().endsWith("Filter.class") && !file.getName().startsWith("Filter"))
                .collect((Supplier<HashMap<String, Class<? extends Filter>>>) HashMap::new,
                        (stringClassHashMap, file) -> {
                            try {
                                stringClassHashMap.put(file.getName(), (Class<? extends Filter>) Class.forName(String.valueOf(file)));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }, HashMap::putAll
                );*/
    }
}

package ir.org.acm.framework;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * this class used as core of framework
 * we use this class for create instance og injected objects
 */

public class DiContext {
    private final String configFile = "src/config/context.txt";

    public Object getBean(String name) { // factory method

        Object obj = null;
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(configFile));
            Class type = Class.forName(p.getProperty(name));
            obj = type.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;

    }

    public void injectIn(Object client) {

        try {
            Class c = client.getClass();
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                Autowired a = f.getAnnotation(Autowired.class);
                if (a != null) f.set(client, getBean(a.name())); // Injection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
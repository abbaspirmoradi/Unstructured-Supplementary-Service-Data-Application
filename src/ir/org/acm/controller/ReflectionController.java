package ir.org.acm.controller;

import ir.org.acm.utils.UssdMethod;
import ir.org.acm.utils.UssdService;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.tools.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * in this class we inspect all feature of ussd Operations class
 */
public class ReflectionController implements ReflectionControllerInterface {

    /**
     * get all methods of given class
     */
    @Override
    public List<Method> getAllUssdMethodes(String className) {

        Class klass = null;

        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<Method> resultMethods = new ArrayList<Method>();
        if (klass == null)
            return resultMethods;
        Method[] methods = klass.getDeclaredMethods();


        for (Method method : methods) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(UssdMethod.class)) {
                resultMethods.add(method);
            }
        }

        return resultMethods;
    }

    /**
     * get all methods of given class
     */
    @Override
    public List<Class> getAllUssdClasses(String packageName) {

        List<Class> commands = new ArrayList<Class>();
        List<String> classNames = new ArrayList<String>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);

        StandardLocation location = StandardLocation.CLASS_PATH;

        Set<JavaFileObject.Kind> kinds = new HashSet<>();
        kinds.add(JavaFileObject.Kind.CLASS);
        boolean recurse = false;
        Iterable<JavaFileObject> list = new ArrayList<>();
        try {
            list = fileManager.list(location, packageName,
                    kinds, recurse);
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (JavaFileObject javaFileObject : list) {
            String path = javaFileObject.toUri().getPath();
            String className = path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
            classNames.add(className);
        }

        for (String className : classNames) {
            Class klass = null;
            try {
                klass = Class.forName(packageName + "." + className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (klass != null && klass.isAnnotationPresent(UssdService.class)) {
                commands.add(klass);
            }
        }
        return commands;
    }

    @Override
    public Map<Class,ArrayList<Method>> getAllUssdMethodsAndOwnerClasses(String packageName) {

        List<Class> classes = getAllUssdClasses(packageName);
        Map<Class,ArrayList<Method>> methodNamesWithOwnerClasses = new HashMap<Class,ArrayList<Method> > ();


        for (Class klass : classes) {
            List<Method> methodNames = new ArrayList<Method>();
            List<Method> methods = getAllUssdMethodes(klass.getName());
            for (Method m:
                 methods) {
                methodNames.add(m);
            }
            methodNamesWithOwnerClasses.put(klass,(ArrayList<Method>) methodNames);
        }

        return methodNamesWithOwnerClasses;
    }

}

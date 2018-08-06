package ir.org.acm.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface ReflectionControllerInterface {

    public List<Method> getAllUssdMethodes(String className);

    public List<Class> getAllUssdClasses(String packageName);

    public  Map<Class,ArrayList<Method>> getAllUssdMethodsAndOwnerClasses(String packageName);
}

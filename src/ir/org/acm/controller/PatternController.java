package ir.org.acm.controller;

import ir.org.acm.framework.Autowired;
import ir.org.acm.framework.DiContext;
import ir.org.acm.utils.UssdMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 */
public class PatternController implements PatternControllerInterface {

    private final String packageName = "ir.org.acm.controller";

    @Autowired(name = "ReflectionInterface")
    private ReflectionControllerInterface reflectionInterface;

    public PatternController() {
        new DiContext().injectIn(this);
    }

    @Override
    public void doOperationForReceivedPattern(String pattern) {

        Map<Class, Method> matchesUssdMethodWithOwnerClass = getMatchesUssdMethodAndOwnerClassWithInputPattern(pattern);
        Iterator iterator = matchesUssdMethodWithOwnerClass.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry entry = (Map.Entry) iterator.next();
            Class klass = (Class) entry.getKey();
            Method method = (Method) entry.getValue();

            method.setAccessible(true);
            pattern = pattern.replace("*", "■");
            String[] inputs = pattern.split("■|#");
            List<String> variables = new ArrayList<String>();

            boolean b = true;
            for (String str : inputs) {
                //reject first element of pattern
                if (b && !str.isEmpty() && str != null) {
                    b = false;
                } else if (!str.isEmpty() && str != null) {
                    variables.add(str);
                }
            }

            Parameter[] parameters = method.getParameters();
            Object[] invokes = new Object[method.getParameterCount()];

            for (int i = 0; i < parameters.length; i++) {
                String lClassName = parameters[i].getType().getName();
                switch (lClassName) {
                    case "long":
                        invokes[i] =Long.valueOf(variables.get(i)) ;
                        break;
                    case "java.lang.String":
                        invokes[i] = variables.get(i);
                        break;
                    case "int":
                        invokes[i] = Integer.valueOf(variables.get(i));
                        break;
                    case "char":
                        invokes[i] = variables.get(i);
                        break;
                    case "float":
                        invokes[i] = Float.valueOf(variables.get(i));
                        break;
                    case "double":
                        invokes[i] =Double.valueOf(variables.get(i));
                        break;
                    default:
                        try {
                            Class<?> theClass = Class.forName(parameters[i].getType().getName());
                            invokes[i] = theClass.cast(variables.get(i));
                        }catch (ClassNotFoundException e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
            //execute method
            try {
                Object obj = klass.newInstance();
                method.invoke(obj, invokes);

            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }

        }

    }

    private Map<Class, Method> getMatchesUssdMethodAndOwnerClassWithInputPattern(String pattern) { //factory method

        Map<Class, ArrayList<Method>> matchesMethods = reflectionInterface.getAllUssdMethodsAndOwnerClasses(packageName);

        Iterator iterator = matchesMethods.entrySet().iterator();
        Map<Class, Method> map = new HashMap<Class, Method>();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Class klass = (Class) entry.getKey();
            ArrayList<Method> methodNames = (ArrayList<Method>) entry.getValue();

            for (Method method : methodNames) {

                if (method.isAnnotationPresent(UssdMethod.class)) {

                    method.setAccessible(true);
                    UssdMethod annotation = method.getAnnotation(UssdMethod.class);
                    String methodPattern = annotation.expression();
                    pattern = pattern.replace("*", "■");
                    methodPattern = methodPattern.replace("*", "■");
                    if (Pattern.matches(pattern, methodPattern)) {
                        map.put(klass, method);
                        return map;
                    }
                }
            }
            //end of for loop
        }

        return map;
    }
}

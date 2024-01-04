package WebsocketDemo.utils;

import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

@Component
public class HibernateProcessor {
    public <T> T deepUnProxyAndClone(T t) throws InvocationTargetException, IllegalAccessException {
        return deepUnProxyAndClone(t, new HashMap<>(), t.getClass().getName() + "/");
    }

    public <T> T deepUnProxyAndClone(T t, Map<String, T> objectIsUnProxy, String path) throws IllegalAccessException, InvocationTargetException {
        if (t == null) return null;

        objectIsUnProxy.put(t.toString(), t);
        Field[] fields = t.getClass().getDeclaredFields();
        Method[] methods = t.getClass().getDeclaredMethods();
        HashMap<String, Method> methodsMap = new HashMap<>();
        for (Method method : methods) {
            methodsMap.put(method.getName(), method);
        }
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().getDeclaredAnnotation(Entity.class) != null && field.get(t) != null) {

                String className = field.getType().getName();
                String key = field.get(t).toString();

                if (path.contains(className + "/") && objectIsUnProxy.get(key) != null) {
                    try {
                        field.set(t, deepClone(field.get(t)));
                    } catch (Exception e) {
                        field.set(t, null);
                        e.printStackTrace();
                    }
                } else {
                    String newPath = path + className + "/";
                    field.set(t, deepUnProxyAndClone((T) field.get(t), objectIsUnProxy, newPath));
                }
            }

            if (Collection.class.isAssignableFrom(field.getType())) {

                Collection<T> collection = (Collection<T>) field.get(t);

                if (collection != null) {
                    try {
                        Collection<T> newCollection = new ArrayList<>();
                        for (T _t : collection) {
                            String className = _t.getClass().getName();
                            String key = _t.toString();

                            if (path.contains(className + "/") && objectIsUnProxy.get(key) != null) {
                                newCollection.add(deepClone(_t));
                            } else {
                                String newPath = path + className + "/";
                                newCollection.add(deepUnProxyAndClone(_t, objectIsUnProxy, newPath));
                            }

                        }
                        field.set(t, newCollection);

                    } catch (Exception e) {
                        String methodName = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                        Method method = methodsMap.get(methodName);
                        method.invoke(t, new ArrayList<>());
                    }
                }
            }
        }
        return t;
    }

    private <T> T deepClone(T t) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = t.getClass();
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T tClone = (T) constructor.newInstance();
        Field[] fields = Stream.concat(Arrays.stream(clazz.getSuperclass().getDeclaredFields()), Arrays.stream(clazz.getDeclaredFields())).toArray(Field[]::new);
        Method[] methods = t.getClass().getDeclaredMethods();
        Map<String, Method> methodsMap = new HashMap<>();
        for (Method method : methods) {
            methodsMap.put(method.getName(), method);
        }
        for (Field field : fields) {
            field.setAccessible(true);
            if (Collection.class.isAssignableFrom(field.getType())) {
                Collection<?> collection = (Collection<?>) field.get(t);
                try {
                    boolean check = true;
                    for (Object o : collection) {
                        if (o.getClass().getDeclaredAnnotation(Entity.class) == null) {
                            field.set(tClone, collection);
                            check = false;
                        }
                        break;
                    }
                    if (check) {
                        field.set(tClone, null);
                    }
                } catch (Exception e) {
                    String methodName = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    Method method = methodsMap.get(methodName);
                    method.invoke(tClone, new ArrayList<>());
                }

            } else {
                if (field.getType().getDeclaredAnnotation(Entity.class) == null) {
                    field.set(tClone, field.get(t));
                } else {
                    field.set(tClone, null);
                }
            }

        }

        return tClone;
    }

}

package util;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class HttpEncoder {
    public static String getJSON(Object object, Map<Object, String> objectMap) {
        if (object == null) {
            return "null";
        } else {
            Class<?> className = object.getClass();
            if (className == String.class) {
                String var10000 = object.toString();
                return "\"" + var10000.replace("\"", "\\\"") + "\"";
            } else if (className != Integer.class && className != Long.class && className != Double.class && className != Float.class && className != Boolean.class && !className.isPrimitive()) {
                if (objectMap.containsKey(object)) {
                    return (String)objectMap.get(object);
                } else {
                    objectMap.put(object, "\"#ref-" + String.valueOf(object) + "\"");
                    if (object instanceof List) {
                        List<?> list = (List)object;
                        StringBuilder sb = new StringBuilder("[");

                        for(int i = 0; i < list.size(); ++i) {
                            sb.append(getJSON(list.get(i), objectMap));
                            if (i != list.size() - 1) {
                                sb.append(",");
                            }
                        }

                        return sb.append("]").toString();
                    } else if (object instanceof Map) {
                        Map<?, ?> map = (Map)object;
                        StringBuilder sb = new StringBuilder("{");
                        int i = 0;

                        for(Map.Entry<?, ?> entry : map.entrySet()) {
                            sb.append(getJSON(entry.getKey(), objectMap)).append(":").append(getJSON(entry.getValue(), objectMap));
                            if (i != map.size() - 1) {
                                sb.append(",");
                            }

                            ++i;
                        }

                        return sb.append("}").toString();
                    } else {
                        StringBuilder sb = new StringBuilder("{");
                        Field[] fields = className.getDeclaredFields();
                        int count = 0;

                        for(Field field : fields) {
                            field.setAccessible(true);

                            try {
                                Object value = field.get(object);
                                sb.append("\"").append(field.getName()).append("\":").append(getJSON(value, objectMap));
                                ++count;
                                if (count < fields.length) {
                                    sb.append(",");
                                }
                            } catch (IllegalAccessException var11) {
                                System.out.println("Skipped inaccessible field: " + field.getName());
                                ++count;
                            }
                        }

                        return sb.append("}").toString();
                    }
                }
            } else {
                return object.toString();
            }
        }
    }
}

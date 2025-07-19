package util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class HttpEncoder {

    public static String getJSON(Object object, Map<Object,String> objectMap) {

        if (object == null) return "null";

        Class<?> className = object.getClass();


        if (className == String.class) {
            return "\"" + object.toString().replace("\"", "\\\"") + "\"";
        }

        if (className == Integer.class || className == Long.class || className == Double.class
                || className == Float.class || className == Boolean.class || className.isPrimitive()) {
            return object.toString();
        }
        if(objectMap.containsKey(object)) return objectMap.get(object);

        objectMap.put(object,"\"#ref-"+object+"\"");




        if (object instanceof List<?> list) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(getJSON(list.get(i),objectMap));
                if (i != list.size() - 1) sb.append(",");
            }
            return sb.append("]").toString();
        }



        if (object instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            int i = 0;
            for (var entry : map.entrySet()) {
                sb.append(getJSON(entry.getKey(),objectMap))
                        .append(":")
                        .append(getJSON(entry.getValue(),objectMap));
                if (i != map.size() - 1) sb.append(",");
                i++;
            }
            return sb.append("}").toString();
        }


        StringBuilder sb = new StringBuilder("{");
        Field[] fields = className.getDeclaredFields();
        int count = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                sb.append("\"").append(field.getName()).append("\":")
                        .append(getJSON(value,objectMap));
                if (++count < fields.length) sb.append(",");
            } catch (IllegalAccessException e) {
                System.out.println("Skipped inaccessible field: " + field.getName());
                count++;
            }
        }
        return sb.append("}").toString();
    }
}


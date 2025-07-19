package request;

import java.util.List;
import java.util.Map;

public record RequestBody(Map<String, Object> body) {

    public Object get(String key) {
        return body.get(key);
    }

    public Map<String, Object> getMap(String key) {
        return (Map<String, Object>) this.body.get(key);
    }

    public List<Object> getList(String key) {
        return (List<Object>) this.body.get(key);
    }

    public Object getInt(String key) {
        return Integer.parseInt(body.get(key).toString());
    }

    public String getString(String key) {
        return body.get(key).toString();
    }

}

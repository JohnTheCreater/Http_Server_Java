package request;

import java.util.HashMap;
import java.util.Map;
public class Cookie {

    private final Map<String, String> cookie = new HashMap<>();

    public void setCookieMap(Map<String,String> c) {
        cookie.putAll(c);
    }

    public Map<String,String> getCookieMap() {
        return new HashMap<>(cookie);
    }


    public String get(String key) {
        return cookie.get(key);
    }
}

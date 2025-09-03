package request;

import java.security.InvalidParameterException;
import java.util.Map;

public record PathParams(Map<String,String> params) {


    public String get(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Path Parameter Found in This Name: " + name);
        return params.get(name);
    }

    public Integer getInt(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Path Parameter Found in This Name: " + name);

        return Integer.parseInt(params.get(name));
    }

    public Boolean getBoolean(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Path Parameter Found in This Name: " + name);
        return Boolean.parseBoolean(params.get(name));
    }


}

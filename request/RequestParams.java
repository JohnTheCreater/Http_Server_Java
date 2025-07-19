package request;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

public record RequestParams(Map<String, List<String>> params) {

    public List<String> getList(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Parameter Found in This Name: " + name);
        return params.get(name);
    }

    public String getFirst(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Parameter Found in This Name: " + name);

        return params.get(name).getFirst();
    }

    public Integer getFirstInt(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Parameter Found in This Name: " + name);

        return Integer.parseInt(params.get(name).getFirst());
    }

    public Boolean getFirstBoolean(String name) {
        if (!params.containsKey(name)) throw new InvalidParameterException("No Parameter Found in This Name: " + name);

        return Boolean.parseBoolean(params.get(name).getFirst());
    }
}

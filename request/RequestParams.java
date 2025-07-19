package request;


import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

public record RequestParams(Map<String, List<String>> params) {
    public List<String> getList(String name) {
        if (!this.params.containsKey(name)) {
            throw new InvalidParameterException("No Parameter Found in This Name: " + name);
        } else {
            return (List)this.params.get(name);
        }
    }

    public String getFirst(String name) {
        if (!this.params.containsKey(name)) {
            throw new InvalidParameterException("No Parameter Found in This Name: " + name);
        } else {
            return (String)((List)this.params.get(name)).getFirst();
        }
    }

    public Integer getFirstInt(String name) {
        if (!this.params.containsKey(name)) {
            throw new InvalidParameterException("No Parameter Found in This Name: " + name);
        } else {
            return Integer.parseInt((String)((List)this.params.get(name)).getFirst());
        }
    }

    public Boolean getFirstBoolean(String name) {
        if (!this.params.containsKey(name)) {
            throw new InvalidParameterException("No Parameter Found in This Name: " + name);
        } else {
            return Boolean.parseBoolean((String)((List)this.params.get(name)).getFirst());
        }
    }
}

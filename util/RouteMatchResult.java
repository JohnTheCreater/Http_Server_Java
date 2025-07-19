package util;


import java.util.List;
import java.util.Map;

public record RouteMatchResult(String matchedRoute, Map<String, List<String>> params) {
}

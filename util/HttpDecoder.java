package util;

import request.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class HttpDecoder {

    public static Optional<HttpRequest> decode(List<String> message,Set<String> routes) throws URISyntaxException {

        if(message.isEmpty()) return Optional.empty();
        System.out.println("REQUEST:");

        String[] requestParts = message.getFirst().split(" ");

        if(requestParts.length != 3 || !requestParts[2].equals("HTTP/1.1")) return Optional.empty();



        HttpMethod httpMethod = HttpMethod.valueOf(requestParts[0]);

        String uri = requestParts[1];

        RouteMatchResult result = decodeRequestUrl(httpMethod,uri,routes);
        if( result == null )  return Optional.empty();



        PathParams pathParams = new PathParams(result.pathParams());
        QueryParams queryParams = new QueryParams(result.queryParams());
        String matchedRoute = result.matchedRoute();

        System.out.println("ROUTE: "+matchedRoute);
        System.out.println("URI: "+uri);
        System.out.println("PATH PARAMS: "+pathParams.params());
        System.out.println("QUERY PARAMS: "+queryParams.params());



        Map<String,List<String>> requestHeader = new HashMap<>();
        RequestBody body = null;
        Cookie cookie = new Cookie();

        for(int i = 1 ; i < message.size() ; i++)
        {
            String str = message.get(i);
            int colonIndex = str.indexOf(":");
            if(colonIndex == -1) break;
            String name = str.substring(0,colonIndex).trim();
            String valuesStr = str.substring(colonIndex+1).trim();
            System.out.println(name +" :" +valuesStr);
            if(name.equalsIgnoreCase("Body"))
            {
                body = decodeRequestBody(valuesStr);
                continue;
            }
            if(name.equalsIgnoreCase("Cookie"))
            {
                cookie.setCookieMap(getCookies(valuesStr));
                continue;
            }


            String[] values = valuesStr.split(",");
            List<String> headerTypeValues = requestHeader.computeIfAbsent(name, k -> new ArrayList<>());

            for (String value : values) {
                headerTypeValues.add(value.trim());
            }
        }



        HttpRequest.Builder builder = new HttpRequest.Builder()
                .setHttpMethod(httpMethod)
                .setUri(new URI(uri))
                .setRequestHeader(requestHeader)
                .setBody(body)
                .setPathParams(pathParams)
                .setQueryParams(queryParams)
                .setMatchedRoute(matchedRoute)
                .setCookie(cookie);

        HttpRequest request = builder.build();


        return Optional.of(request);


    }

    private static Map<String, String> getCookies(String valuesStr) {

        String[] pairs = valuesStr.split(";");
        Map<String,String> cookies = new HashMap<>();
        for(String pair:pairs)
        {
            pair = pair.trim();
            if (pair.isEmpty()) continue;
            String[] splited = pair.split("=", 2);
            String key = splited[0].trim();
            String value = splited.length > 1 ? splited[1].trim() : "";
            cookies.put(key, value);

        }
        return cookies;
    }

    private static RouteMatchResult decodeRequestUrl(HttpMethod httpMethod,String uri,Set<String> routes) {


        Map<String,List<String>> queryParams = new HashMap<>();
        Map<String,String> pathParams = new HashMap<>();
        String[] uriParts = uri.split("\\?",2);

        String path = uriParts[0];
        String query = uriParts.length < 2 ? "" : uriParts[1];

        String matchedRoute =  parsePath(pathParams,httpMethod,getNormalizedURL(path),routes);
        parseQuery(queryParams,query);

        if(matchedRoute == null) return null;

        return new RouteMatchResult(matchedRoute,queryParams,pathParams);
    }

    private static String parsePath(Map<String, String> params, HttpMethod httpMethod, String path, Set<String> routes) {
        String candidate = httpMethod.name() + "::" + path;

        if (routes.contains(candidate)) {
            return candidate;
        }

        String[] pathParts = path.split("/");

        for (String route : routes) {

            String[] routeParts = route.split("::", 2);
            if (!routeParts[0].equals(httpMethod.name())) continue;

            String[] routePathParts = routeParts[1].split("/");

            if (pathParts.length != routePathParts.length) continue;

            Map<String, String> temp = new HashMap<>();
            boolean matched = true;

            for (int i = 0; i < pathParts.length; i++) {
                String actual = pathParts[i];
                String expected = routePathParts[i];

                if (expected.isEmpty() && actual.isEmpty()) continue;

                if (expected.equals(actual)) {
                } else if (expected.startsWith("{") && expected.endsWith("}")) {
                    String key = expected.substring(1, expected.length() - 1);
                    temp.put(key, actual);
                } else if (expected.equals("*")) {
                    continue;
                } else {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                params.putAll(temp);
                return route;
            }
        }

        return null;
    }

    private static void parseQuery(Map<String, List<String>> params,String query) {

        if(query.isEmpty()) return;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";

            if (!key.isEmpty()) {
                params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }



    }

    private static RequestBody decodeRequestBody(String valuesStr) {

        Map<String,Object> bodyMap = decodeJSON(valuesStr);
        return new RequestBody(bodyMap);

    }

    enum DataType{STRING,OBJECT,ARRAY,BOOLEAN,NUMBER,NULL,UNKNOWN}

    private static Map<String, Object> decodeJSON(String valuesStr) {
        valuesStr = valuesStr.trim();
        Map<String,Object> map = null;
        if(valuesStr.startsWith("{") && valuesStr.endsWith("}"))
        {
            map = new HashMap<>();
            valuesStr = valuesStr.substring(1,valuesStr.length()-1);
            List<String> values = splitTopLevel(valuesStr,',');
            for(String str:values)
            {
                String[] parts = str.split(":",2);
                String key = parts[0].trim().substring(1,parts[0].length()-1);
                parts[1] = parts[1].trim();
                DataType dataType = findDataType(parts[1]);
                Object value = getValue(dataType,parts[1]);
                map.put(key,value);
            }

        }
        return map;

    }

    private static Object getValue(DataType dataType,String value) {
        return switch (dataType){
            case OBJECT -> decodeJSON(value);
            case ARRAY -> getArray(value);
            case NUMBER -> getNumber(value);
            case BOOLEAN -> Boolean.valueOf(value);
            case NULL -> null;
            case STRING -> value.substring(1,value.length()-1);
            default ->  throw new IllegalArgumentException("Unknown data type: " + value);

        };
    }

    private static Object getNumber(String part) {
        return new BigDecimal(part.trim());
    }

    private static Object getArray(String part) {
        List<Object> list = new ArrayList<>();
        part = part.substring(1,part.length()-1);
        List<String> elements = splitTopLevel(part,',');
        for(String element:elements)
        {
            DataType dataType = findDataType(element);
            Object value = getValue(dataType,element);
            list.add(value);
        }
        return list;
    }

    private static DataType findDataType(String part) {

        if(part.equalsIgnoreCase("null")) return DataType.NULL;
        if(part.startsWith("\"") && part.endsWith( "\""))
            return DataType.STRING;
        if(part.startsWith("[") && part.endsWith("]")) return DataType.ARRAY;
        if(part.startsWith("{")&& part.endsWith("}")) return DataType.OBJECT;
        if(part.equalsIgnoreCase("true") || part.equalsIgnoreCase("false")) return DataType.BOOLEAN;

        try {
            Double.parseDouble(part);
            return DataType.NUMBER;
        } catch (NumberFormatException e) {
            return DataType.UNKNOWN;
        }
    }
    public static List<String> splitTopLevel(String input, char delimiter) {


        List<String> result = new ArrayList<>();
        int depth = 0;
        boolean inString = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '\"') {
                inString = !inString;
                current.append(ch);
            } else if (!inString) {
                if (ch == '{' || ch == '[') depth++;
                else if (ch == '}' || ch == ']') depth--;
                else if (ch == delimiter && depth == 0) {
                    result.add(current.toString().trim());
                    current.setLength(0);
                    continue;
                }
                current.append(ch);
            } else {
                current.append(ch);
            }
        }

        if (!current.isEmpty()) result.add(current.toString().trim());

        return result;
    }

    public static String getNormalizedURL(String url)
    {
        StringBuilder normalizedUrl = new StringBuilder();
        int i = 0 , n = url.length();
        boolean flag = true;
        while(i < n)
        {
            if(url.charAt(i) == '/')
            {
                if(flag)
                {
                    normalizedUrl.append( "/");
                    flag = false;
                }

            }
            else {
                flag = true;
                normalizedUrl.append(url.charAt(i));
            }
            i++;
        }
        if(normalizedUrl.charAt(0) != '/')
        {
            normalizedUrl.insert(0,"/");
        }
        if(normalizedUrl.length() > 1 && normalizedUrl.charAt(normalizedUrl.length()-1) == '/')
        {
            normalizedUrl.deleteCharAt(normalizedUrl.length()-1);
        }
        return normalizedUrl.toString();
    }

}

package com.govskill.utils;

import java.lang.reflect.Field;
import java.util.*;

public class JsonUtil {

    /**
     * Serializes any Object, Map, List, or primitive into a JSON string
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escape(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable) {
            Iterable<?> list = (Iterable<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(item));
            }
            sb.append("]");
            return sb.toString();
        }

        // POJO Reflection
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object val = f.get(obj);
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(f.getName()).append("\":");
                sb.append(toJson(val));
            } catch (IllegalAccessException ignored) {
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Parses simple JSON object string into Map<String, Object>
     */
    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return result;

        String s = json.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}")) s = s.substring(0, s.length() - 1);

        int i = 0;
        int len = s.length();

        while (i < len) {
            // Find key
            while (i < len && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',')) i++;
            if (i >= len) break;

            if (s.charAt(i) != '"') {
                i++;
                continue;
            }
            i++; // skip opening quote
            int keyStart = i;
            while (i < len && s.charAt(i) != '"') {
                if (s.charAt(i) == '\\') i++;
                i++;
            }
            String key = s.substring(keyStart, i);
            if (i < len && s.charAt(i) == '"') i++;

            // Find colon
            while (i < len && s.charAt(i) != ':') i++;
            if (i < len && s.charAt(i) == ':') i++;

            // Find value
            while (i < len && Character.isWhitespace(s.charAt(i))) i++;
            if (i >= len) break;

            Object val = null;
            if (s.charAt(i) == '"') {
                // String value
                i++;
                int valStart = i;
                StringBuilder valSb = new StringBuilder();
                while (i < len) {
                    char c = s.charAt(i);
                    if (c == '\\' && i + 1 < len) {
                        valSb.append(s.charAt(i + 1));
                        i += 2;
                    } else if (c == '"') {
                        i++;
                        break;
                    } else {
                        valSb.append(c);
                        i++;
                    }
                }
                val = valSb.toString();
            } else if (s.charAt(i) == '{') {
                // Nested object
                int depth = 1;
                int startObj = i;
                i++;
                while (i < len && depth > 0) {
                    if (s.charAt(i) == '{') depth++;
                    else if (s.charAt(i) == '}') depth--;
                    i++;
                }
                val = parseJsonObject(s.substring(startObj, i));
            } else if (s.charAt(i) == '[') {
                // Array
                int depth = 1;
                int startArr = i;
                i++;
                while (i < len && depth > 0) {
                    if (s.charAt(i) == '[') depth++;
                    else if (s.charAt(i) == ']') depth--;
                    i++;
                }
                val = s.substring(startArr, i);
            } else {
                // Primitive number or boolean
                int startVal = i;
                while (i < len && s.charAt(i) != ',' && s.charAt(i) != '}') i++;
                String raw = s.substring(startVal, i).trim();
                if ("true".equalsIgnoreCase(raw)) val = Boolean.TRUE;
                else if ("false".equalsIgnoreCase(raw)) val = Boolean.FALSE;
                else if ("null".equalsIgnoreCase(raw)) val = null;
                else {
                    try {
                        if (raw.contains(".")) val = Double.parseDouble(raw);
                        else val = Integer.parseInt(raw);
                    } catch (Exception e) {
                        val = raw;
                    }
                }
            }

            result.put(key, val);
        }

        return result;
    }

    public static String getString(Map<String, Object> map, String key, String defaultVal) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultVal;
        return String.valueOf(map.get(key)).trim();
    }

    public static int getInt(Map<String, Object> map, String key, int defaultVal) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultVal;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(String.valueOf(val).trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static double getDouble(Map<String, Object> map, String key, double defaultVal) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultVal;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(val).trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultVal) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultVal;
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        String s = String.valueOf(val).trim();
        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
        return defaultVal;
    }
}

package com.zom.sample.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * jsonson 对：object、json、xml、map、list互转
 */
public class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static XmlMapper xmlMapper = new XmlMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * javaBean,list,array convert to json string
     */
    public static String obj2json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * json string convert to javaBean
     */
    public static <T> T json2pojo(String jsonStr, Class<T> clazz) throws Exception {
        return objectMapper.readValue(jsonStr, clazz);
    }

    public static <T> T json2pojo(String jsonStr, TypeReference<T> typeRef) throws Exception {
        return objectMapper.readValue(jsonStr, typeRef);
    }

    /**
     * json string convert to map
     */
//    public static <T> Map<String, Object> json2map(String jsonStr) throws Exception {
//        return objectMapper.readValue(jsonStr, Map.class);
//    }

    /**
     * json string convert to map
     */
    public static Map<String, Object> json2map(String json) {
        Map<String, Object> convertedMap = null;
        try {
            convertedMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedMap;
    }

    /**
     * json string convert to map with javaBean
     */
    public static <T> Map<String, T> json2map(String jsonStr, Class<T> clazz) throws Exception {
        Map<String, Map<String, Object>> map = objectMapper.readValue(jsonStr, new TypeReference<Map<String, T>>() {
        });
        Map<String, T> result = new HashMap<String, T>();
        for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), map2pojo(entry.getValue(), clazz));
        }
        return result;
    }

    /**
     * json array string convert to list with javaBean
     * TODO 当bean属性为list时转换为空，需要注意
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz) throws Exception {
        List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
        });
        List<T> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            result.add(map2pojo(map, clazz));
        }
        return result;
    }

    /**
     * map convert to javaBean
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * map convert to javaBean
     */
    public static <T> T map2pojo(Map map, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(map, toValueTypeRef);
    }

    public static <T> T pojo2map(Object obj, Class<T> clazz) {
        return objectMapper.convertValue(obj, clazz);
    }

    public static <T> T pojo2map(Object obj, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(obj, toValueTypeRef);
    }

    /**
     * json string convert to map
     */
    public static Map<String, Object> pojo2map(Object json) {
        return objectMapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * json string convert to xml string
     */
    public static String json2xml(String jsonStr) throws Exception {
        JsonNode root = objectMapper.readTree(jsonStr);
        return xmlMapper.writeValueAsString(root);
    }

    /**
     * xml string convert to json string
     */
    public static String xml2json(String xml) throws Exception {
        StringWriter w = new StringWriter();
        JsonParser jp = xmlMapper.getFactory().createParser(xml);
        JsonGenerator jg = objectMapper.getFactory().createGenerator(w);
        while (jp.nextToken() != null) {
            jg.copyCurrentEvent(jp);
        }
        jp.close();
        jg.close();
        return w.toString();
    }
}


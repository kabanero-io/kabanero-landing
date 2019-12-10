package io.kabanero.instance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

import org.apache.commons.io.IOUtils;

public class DiscoveryTools {

    public static Map<String, ?> getDiscoveryTools() throws IOException {

        try {

            InputStream inputStream = DiscoveryTools.class.getClassLoader().getResourceAsStream("tools.json");
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            Object obj = new JsonParser().parse(result);
            JsonArray toolsList = (JsonArray) obj;

            Map<String, Map> map = new HashMap<>();

            for (JsonElement toolsObjects : toolsList) {

                Map<String, String> toolMap = new HashMap<>();

                JsonObject toolObject = (JsonObject) toolsObjects;
                JsonObject tool = (JsonObject) toolObject.get("tool");

                String toolName = tool.get("toolName").getAsString();
                String namespace = tool.get("namespace").getAsString();
                String route = tool.get("route").getAsString();

                toolMap.put("toolName", toolName);
                toolMap.put("namespace", namespace);
                toolMap.put("route", route);

                map.put(toolName, toolMap);
            }

            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

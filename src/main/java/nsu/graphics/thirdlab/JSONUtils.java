package nsu.graphics.thirdlab;

import nsu.graphics.thirdlab.template.Parameters;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class JSONUtils {
    public static void writeJson(File file, Parameters parameters) throws Exception {
        JSONObject sampleObject = new JSONObject();
        sampleObject.put("K", parameters.K());
        sampleObject.put("N", parameters.N());
        sampleObject.put("m", parameters.m());
        sampleObject.put("M", parameters.M());

        JSONArray jsonPoints = new JSONArray();
        for (Point point : parameters.keyPoints()) {
            JSONObject keyPoint = new JSONObject();
            keyPoint.put("x", String.valueOf(point.x));
            keyPoint.put("y", String.valueOf(point.y));
            jsonPoints.put(keyPoint);
        }
        sampleObject.put("points", jsonPoints);
        System.out.println(sampleObject);
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            fOut.write(sampleObject.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public static Parameters readJson(File file) throws Exception {
        byte[] encoded = Files.readAllBytes(file.toPath());
        JSONObject sampleObject = new JSONObject(new String(encoded, StandardCharsets.UTF_8));
        int K = Integer.parseInt(sampleObject.get("K").toString());
        int N = Integer.parseInt(sampleObject.get("N").toString());
        int m = Integer.parseInt(sampleObject.get("m").toString());
        int M = Integer.parseInt(sampleObject.get("M").toString());
        JSONArray messages = sampleObject.getJSONArray("points");
        List<Point> keyPoints = new LinkedList<>();
        for (int i = 0; i < messages.length(); i++) {
            JSONObject point = messages.getJSONObject(i);
            int x = Integer.parseInt(point.get("x").toString());
            int y = Integer.parseInt(point.get("y").toString());
            keyPoints.add(new Point(x, y));
        }
        System.out.println(K + " " + N + " " + m + " " + M);
        System.out.println(keyPoints);
        return new Parameters(K, N, m, M, keyPoints);
    }
}

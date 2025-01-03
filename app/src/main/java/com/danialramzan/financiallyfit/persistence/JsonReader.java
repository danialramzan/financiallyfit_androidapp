package com.danialramzan.financiallyfit.persistence;


import model.Event;
import model.EventLog;
import model.GymMember;
import model.MembersManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

// Represents a reader that reads MembersManager from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads MembersManager from file and returns it;
    // throws IOException if an error occurs reading data from file
    public MembersManager read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseMembersManager(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses MembersManager from JSON object and returns it
    private MembersManager parseMembersManager(JSONObject jsonObject) {
        MembersManager mm = new MembersManager();
        addGymMembers(mm, jsonObject);
        return mm;
    }

    // MODIFIES: mm
    // EFFECTS: parses gym members from JSON object and adds them to MembersManager
    private void addGymMembers(MembersManager mm, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("Gym Members");
        for (Object json : jsonArray) {
            JSONObject nextMember = (JSONObject) json;
            addGymMember(mm, nextMember);
        }
    }

    // MODIFIES: mm
    // EFFECTS: parses gymMember from JSON object and adds it to MembersManager
    private void addGymMember(MembersManager mm, JSONObject jsonObject) {

        Integer numOfDaysLeftInMonth = jsonObject.getInt("numOfDaysLeftInMonth");
        String username = jsonObject.getString("username");
        Double baseMembershipCost = jsonObject.getDouble("baseMembershipCost");
        Double dailyPenalty = jsonObject.getDouble("dailyPenalty");
        Integer allowedMiss = jsonObject.getInt("allowedMiss");
        Integer attendanceCount = jsonObject.getInt("attendanceCount");


        JSONObject attendanceLogJsonObject = jsonObject.getJSONObject("attendanceLog");
        Map<String, Double> attendanceLog = new HashMap<>();

        GymMember gymMember =
                new GymMember(numOfDaysLeftInMonth, username, baseMembershipCost,
                        dailyPenalty, allowedMiss, attendanceCount, attendanceLog);

        mm.addMember(gymMember);

        for (String key : attendanceLogJsonObject.keySet()) {
            attendanceLog.put(key, attendanceLogJsonObject.getDouble(key));
            EventLog.getInstance().logEvent(new Event(
                    "**EVENT** Attendance was loaded in for member: "
                            + username.substring(0, username.length() - 11)
                            + ": " + attendanceLogJsonObject.getDouble(key) + " hours logged on " + key));
        }
    }
}
        
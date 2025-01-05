package com.danrmzn.financiallyfit.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.danrmzn.financiallyfit.persistence.Writable;

import java.util.*;
import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/*
 * Represents a Gym Member (X).
 */
public class GymMember implements Writable {
    private final String username;
    private int attendanceCount;
    private final double baseMembershipCost;
    private final double dailyPenalty;
    private final int allowedMiss;
    private final int numOfDaysLeftInMonth;
    private Map<String, Double> attendanceLog;

    private static final int DAILY_FEE_MULTIPLIER = 1;
    private static final int DAILY_PENALTY = 15;


    // Constructs the GymMember Object (X)
    // REQUIRES: - allowedMiss <= numOfDaysLeftInMonth
    //           - regDate MUST be in format YYYY-MM-DD
    //           - If not the firsts GymMember object to be
    //             constructed, Year and Month of GymMember has to match
    //             Year and Month of last GymMember to be added to members
    // EFFECTS: creates a GymMember with a username, allowed missed days, base
    //          membership cost, and an attendance log, (amongst another things)
    public GymMember(String name, String regDate, Integer allowedMiss) {

        LocalDate registrationDate = LocalDate.parse(regDate);
        this.numOfDaysLeftInMonth = (registrationDate.with(lastDayOfMonth()).getDayOfMonth()
                - registrationDate.getDayOfMonth() + 1);
        this.username = (name + "_" + registrationDate);
        this.baseMembershipCost = numOfDaysLeftInMonth * DAILY_FEE_MULTIPLIER;
        this.dailyPenalty = DAILY_PENALTY;
        this.allowedMiss = allowedMiss;
        this.attendanceCount = 0;
        this.attendanceLog = new HashMap<>();
    }


    // Constructor for data persistence purposes
    // EFFECTS: recreates a GymMember with numOfDaysLeftInMonth,
    // username, baseMembershipCost, dailyPenalty, allowedMiss, attendanceCount, attendanceLog

    public GymMember(Integer numOfDaysLeftInMonth, String username,
                     Double baseMembershipCost, Double dailyPenalty, Integer allowedMiss,
                     Integer attendanceCount, Map<String, Double> attendanceLog) {

        this.numOfDaysLeftInMonth = numOfDaysLeftInMonth;
        this.username = username;
        this.baseMembershipCost = baseMembershipCost;
        this.dailyPenalty = dailyPenalty;
        this.allowedMiss = allowedMiss;
        this.attendanceCount = attendanceCount;
        this.attendanceLog = attendanceLog;
    }


    // Logs Attendance
    // REQUIRES: - logDate should be in format YYYY-MM-DD
    //           - logDate should be on, or after regDate
    //           - logDate should be in the same month and year as regDate
    //           =  hours >= 0
    // MODIFIES: this
    // EFFECTS: logs the attendance of the user for a date
    // - If the date is already populated, it is replaced by the new entry
    // - If not, a new entry is created and the attendance count is incremented by 1
    public void logAttendance(double hours, String logDate) {
        if (attendanceLog.containsKey(String.valueOf(logDate))) {
            attendanceLog.put(String.valueOf(logDate), hours);
            EventLog.getInstance().logEvent(new Event(
                    "**EVENT** Attendance was just updated for member: " + getName()
                            + ": " + hours + " hours logged on " + logDate));
        } else {
            attendanceLog.put(String.valueOf(logDate), hours);
            attendanceCount++;
            EventLog.getInstance().logEvent(new Event(
                    "**EVENT** Attendance was just logged for member " + getName()
                            + ": " + hours + " hours logged on " + logDate + ", changing number of days attended to "
                            + getAttendanceCount()));

        }
    }


    // EFFECTS: returns monthly bill of user by applying the missed day algorithm.
    public double getMonthlyBill() {
        if (attendanceCount >= (numOfDaysLeftInMonth - allowedMiss)) {
            return baseMembershipCost;
        } else {
            return baseMembershipCost + (dailyPenalty * ((numOfDaysLeftInMonth - attendanceCount) - allowedMiss));
        }

    }


    // EFFECTS: returns the name of the user, extracts it from username
    public String getName() {
        return username.substring(0, username.length() - 11);
    }


    // EFFECTS: returns the registration date
    public String getRegDate() {
        return username.substring(username.length() - 10);
    }


    // EFFECTS: returns the number of days the user has logged their attendance
    public int getAttendanceCount() {
        return attendanceCount;
    }

    // EFFECTS: updates the number of days the user has logged their attendance
    public void setAttendanceCount(int attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    // EFFECTS: returns the number of days the user is allowed to miss their attendance
    public int getAllowedMiss() {
        return allowedMiss;
    }


    // EFFECTS: returns the number of days between registration date and the end of the month.
    public int getNumOfDaysLeftInMonth() {
        return numOfDaysLeftInMonth;
    }


    // EFFECTS: returns total number of hours spent by user in the gym since registration.
    public double getTotalHours() {
        double sum = 0;
        for (Double hours : attendanceLog.values()) {
            sum += hours;
        }
        return sum;
    }

    // EFFECTS: returns daily fee multiplier
    public int getDailyFeeMultiplier() {
        return DAILY_FEE_MULTIPLIER;
    }

    // EFFECTS: returns Daily Fee Penalty
    public int getDailyPenalty() {
        return DAILY_PENALTY;
    }

    // EFFECTS: returns Attendance Log
    public Map<String, Double> getAttendanceLog() {
        return attendanceLog;
    }


    // EFFECTS: stores information for each GymMember as a JsonObject for saving.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("numOfDaysLeftInMonth", numOfDaysLeftInMonth);
            json.put("username", username);
            json.put("baseMembershipCost", baseMembershipCost);
            json.put("dailyPenalty", dailyPenalty);
            json.put("allowedMiss", allowedMiss);
            json.put("attendanceCount", attendanceCount);
        } catch (Exception e) {
            // do nothing
        }

        JSONObject attendanceLogJsonObject = new JSONObject(attendanceLog);
        try {
            json.put("attendanceLog", attendanceLogJsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}


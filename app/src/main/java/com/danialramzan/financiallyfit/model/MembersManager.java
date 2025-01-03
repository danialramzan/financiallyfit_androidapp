package com.danialramzan.financiallyfit.model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a GymMembers List (Y).
 */
public class MembersManager implements Writable {

    List<GymMember> members;


    // Constructs the MembersManager Object (Y)
    // EFFECTS: creates a MembersManager object containing a list members.
    public MembersManager() {
        this.members = new ArrayList<>();
    }

    // EFFECTS: returns list of members.
    public List<GymMember> getMembers() {
        return members;
    }


    // MODIFIES: this
    // EFFECTS: adds a GymMember to the MemberManager object. (X's in Y)
    public void addMember(GymMember member) {
        members.add(member);
        EventLog.getInstance().logEvent(new Event(
                "**EVENT** Member: " + member.getName()
                        + " registered on " + member.getRegDate()
                        + " with allowed missed days: " + member.getAllowedMiss()
        ));
    }

    // MODIFIES: this
    // EFFECTS: removes a GymMember from the MemberManager object.
    public void removeMember(GymMember member) {
        members.remove(member);
    }


    // EFFECTS: returns size of members list.
    public int getSize() {
        return members.size();
    }

    // REQUIRES: date input needs to be a String and follow YYYY-MM-DD format
    // EFFECTS: returns a list of all patrons who attended the gym on a certain date.
    public List<String> returnAttendanceDay(String date) {
        String eventLogString = "**EVENT** The attendance record for " + date
                + " was requested, the members who attended are: ";
        List<String> memberList = new ArrayList<>();
        for (GymMember m : members) {
            if (m.getAttendanceLog().containsKey(date)) {
                memberList.add(m.getName());

            }
        }
        if (memberList.size() > 0) {
            for (String m : memberList) {
                eventLogString = eventLogString + "+" + m;
            }
        } else {
            eventLogString = eventLogString + "Nobody!";
        }
        EventLog.getInstance().logEvent(new Event(eventLogString));
        return memberList;
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
    public void logAttendance(double hours, String logDate, GymMember member) {
        if (member.getAttendanceLog().containsKey(String.valueOf(logDate))) {
            member.getAttendanceLog().put(String.valueOf(logDate), hours);
            EventLog.getInstance().logEvent(new Event(
                    "**EVENT** Attendance was just updated for member: " + member.getName()
                            + ": " + hours + " hours logged on " + logDate));
        } else {
            member.getAttendanceLog().put(String.valueOf(logDate), hours);
            member.setAttendanceCount(member.getAttendanceCount() + 1);
            EventLog.getInstance().logEvent(new Event(
                    "**EVENT** Attendance was just logged for member " + member.getName()
                            + ": " + hours + " hours logged on " + logDate + ", changing number of days attended to "
                            + member.getAttendanceCount()));


        }
    }

    // EFFECTS: puts the JSONArray of list of GymMembers into a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("Gym Members", gymMembersToJson());
        return json;
    }

    // EFFECTS: returns things in this MembersManager as a JSON array
    private JSONArray gymMembersToJson() {
        JSONArray jsonArray = new JSONArray();

        for (GymMember m : members) {
            jsonArray.put(m.toJson());
        }

        return jsonArray;
    }

}

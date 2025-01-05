package com.danrmzn.financiallyfit.persistence;

import com.danrmzn.financiallyfit.model.GymMember;

public class JsonTest {
    protected void checkGymMember(String name, String regDate, Integer attendanceCount,
                                  Double totalHours, GymMember gymMember) {
        assertEquals(name, gymMember.getName());
        assertEquals(regDate, gymMember.getRegDate());
        assertEquals(attendanceCount, gymMember.getAttendanceCount());
        assertEquals(totalHours, gymMember.getTotalHours());
    }
}

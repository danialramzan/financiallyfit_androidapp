package com.danialramzan.financiallyfit.persistence;

import model.GymMember;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkGymMember(String name, String regDate, Integer attendanceCount,
                                  Double totalHours, GymMember gymMember) {
        assertEquals(name, gymMember.getName());
        assertEquals(regDate, gymMember.getRegDate());
        assertEquals(attendanceCount, gymMember.getAttendanceCount());
        assertEquals(totalHours, gymMember.getTotalHours());
    }
}

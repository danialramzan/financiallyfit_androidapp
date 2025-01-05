package com.danrmzn.financiallyfit.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

public class GymMemberTest {

    @BeforeEach
    void BeforeEach() {
    }

    @Test
    void testConstructorEmpty() {
        GymMember testGymMember = new GymMember("danial", "2023-01-01", 0);
        assertEquals(((testGymMember.getNumOfDaysLeftInMonth())
                * testGymMember.getDailyFeeMultiplier())
                + (testGymMember.getDailyPenalty() * ((testGymMember.getNumOfDaysLeftInMonth()
                - testGymMember.getAttendanceCount())  - testGymMember.getAllowedMiss())),
                testGymMember.getMonthlyBill());
        assertEquals("danial",testGymMember.getName());
        assertEquals("2023-01-01",testGymMember.getRegDate());
        assertEquals(0, testGymMember.getTotalHours());


    }

    @Test
    void testConstructorPopulated() {
        GymMember testGymMember = new GymMember("danial", "2023-01-01", 0);
        testGymMember.logAttendance(4.5, "2023-01-01");
        assertEquals(4.5, testGymMember.getTotalHours());
        testGymMember.logAttendance(5.5, "2023-01-01");
        assertEquals("2023-01-01",testGymMember.getRegDate());
        assertEquals(5.5, testGymMember.getTotalHours());


    }

    @Test
    void testConstructorAttendanceMoreThanRequirement() {
        GymMember testGymMember = new GymMember("danial", "2020-02-29", 0);
        testGymMember.logAttendance(4.5, "2020-02-29");
        Map<String, Double> testMap = new HashMap<>();
        testMap.put("2020-02-29", 4.5);

        assertEquals(((testGymMember.getNumOfDaysLeftInMonth())
                        * testGymMember.getDailyFeeMultiplier())
                        + (testGymMember.getDailyPenalty() * ((testGymMember.getNumOfDaysLeftInMonth()
                        - testGymMember.getAttendanceCount())  - testGymMember.getAllowedMiss())),
                testGymMember.getMonthlyBill());
        assertEquals(testMap, testGymMember.getAttendanceLog());


    }



}

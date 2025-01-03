package com.danialramzan.financiallyfit.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MembersManagerTest {

    private GymMember testGymMember;
    private GymMember testGymMember2;
    private ArrayList<GymMember> testList;
    private MembersManager testMembersManager;

    @BeforeEach
    void BeforeEach() {
        testList = new ArrayList<>();
        testMembersManager = new MembersManager();
        testGymMember = new GymMember("danial", "2023-01-31", 0);
        testGymMember2 = new GymMember("gregor", "2023-01-02", 0);
    }


    @Test
    void testConstructor() {
        assertEquals(0, testMembersManager.getSize());
    }

    @Test
    void testAddAndRemoveMember() {
        assertEquals(testList, testMembersManager.getMembers());
        testMembersManager.addMember(testGymMember);
        assertEquals(1, testMembersManager.getSize());
        testMembersManager.addMember(testGymMember2);
        assertEquals(2, testMembersManager.getSize());
        testMembersManager.removeMember(testGymMember);
        assertEquals(1, testMembersManager.getSize());
    }

    @Test
    void testReturnAttendanceDay() {
        List<String> testList2  = new ArrayList<>();
        List<String> testList3  = new ArrayList<>();
        testList3.add("gregor");
        testGymMember.logAttendance(3, "2023-01-31");
        assertEquals(testList2, testMembersManager.returnAttendanceDay("2023-01-30"));
        testGymMember2.logAttendance(3, "2023-01-31");
        testGymMember2.logAttendance(3, "2023-01-30");
        testList2.add("danial");
        testList2.add("gregor");
        testMembersManager.addMember(testGymMember);
        testMembersManager.addMember(testGymMember2);
        assertEquals(testList2, testMembersManager.returnAttendanceDay("2023-01-31"));
        assertEquals(testList3, testMembersManager.returnAttendanceDay("2023-01-30"));

    }
}

package com.danrmzn.financiallyfit.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class FinanciallyFitModelTest {

    private GymMember testGymMember;
    private ArrayList<GymMember> testList;
    private FinanciallyFitModel financiallyFitModel;

    @BeforeEach
    void BeforeEach() {
        testList = new ArrayList<>();
        testGymMember = new GymMember("danial", "2023-01-31", 0);
        GymMember testGymMember2 = new GymMember("gregor", "2023-01-02", 0);
        financiallyFitModel = new FinanciallyFitModel();
        testList.add(testGymMember);
        testList.add(testGymMember2);

    }

    @Test
    void testReturnFeeNotInList() {
        assertEquals(-1, financiallyFitModel.calculateMonthlyBillPublic(testList, "biden"));
    }

    @Test
    void testReturnFeeInList() {
        assertEquals(16,
                financiallyFitModel.calculateMonthlyBillPublic(testList, "danial"));
    }

    @Test
    void testReturnMemberNotInList() {
        assertNull(financiallyFitModel.findGymMemberPublic(testList, "biden"));
    }

    @Test
    void testReturnMemberInList() {
        assertEquals(testGymMember, financiallyFitModel.findGymMemberPublic(testList, "danial"));
    }
}

package com.danialramzan.financiallyfit.model;

import java.util.List;

/*
 * Represents the Utility Code for FinanciallyFitConsoleUI
 */
public class FinanciallyFitModel {

    // Constructs a FinanciallyFitModel object.
    public FinanciallyFitModel() {

    }

    // EFFECTS: Calculates the monthly bill for a member in list members.
    private double calculateMonthlyBillModel(List<GymMember> members, String billMemberName) {
        GymMember billedMember = null;

        for (GymMember m : members) {
            if (m.getName().equalsIgnoreCase(billMemberName)) {
                billedMember = m;
                break;
            }
        }

        if (billedMember != null) {
            return billedMember.getMonthlyBill();
        } else {
            return -1;
        }
    }

    // EFFECTS: returns a member if a member with a corresponding name is found
    //          else: returns null
    private GymMember findGymMember(List<GymMember> members, String memberName) {
        GymMember foundMember = null;
        for (GymMember m : members) {
            if (m.getName().equalsIgnoreCase(memberName)) {
                foundMember = m;
                break;
            }
        }
        return foundMember;
    }

    // EFFECTS: public wrapper class for private calculateMonthlyBill method
    public double calculateMonthlyBillPublic(List<GymMember> members, String billMemberName) {
        return calculateMonthlyBillModel(members, billMemberName);
    }

    // EFFECTS: public wrapper class for private findGymMember method
    public GymMember findGymMemberPublic(List<GymMember> members, String memberName) {
        return findGymMember(members, memberName);
    }

}

package com.danrmzn.financiallyfit.ui;

import com.danrmzn.financiallyfit.model.EventLog;
import com.danrmzn.financiallyfit.model.FinanciallyFitModel;
import com.danrmzn.financiallyfit.model.GymMember;
import com.danrmzn.financiallyfit.model.MembersManager;
import com.danrmzn.financiallyfit.persistence.JsonReader;
import com.danrmzn.financiallyfit.persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.danrmzn.financiallyfit.ui.FinanciallyFitConsoleUI;


/*
 * Represents the Gym Interface.
 */
public class FinanciallyFitConsoleUI {


    private static final String JSON_FILEPATH = "./data/membersManager.json";

    private FinanciallyFitModel financiallyFitModel = new FinanciallyFitModel();
    private MembersManager membersManager = new MembersManager();
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: Starts the User Interface
    public FinanciallyFitConsoleUI() throws FileNotFoundException {

        jsonWriter = new JsonWriter(JSON_FILEPATH);
        jsonReader = new JsonReader(JSON_FILEPATH);

        while (true) {
            Scanner scanner = new Scanner(System.in);

            if (membersManager.getSize() == 0) {
                displayMenu1();
                System.out.print("Please enter your choice: ");
                String choice = scanner.nextLine();
                processInput1(choice, scanner);
            } else {
                displayMenu2();
                System.out.print("Please enter your choice: ");
                String choice = scanner.nextLine();
                processInput2(choice, scanner);
            }
        }
    }

    // EFFECTS: processes inputs for the primary menu
    private void processInput1(String choice, Scanner scanner) {
        if (choice.equals("1") || choice.equalsIgnoreCase("r")) {
            registerMember(scanner);

        } else if (choice.equals("2") || choice.equalsIgnoreCase("sa")) {
            saveMembersManager();

        } else if (choice.equals("3") || choice.equalsIgnoreCase("lo")) {
            loadMembersManager();

        } else if (choice.equals("4") || choice.equalsIgnoreCase("e")) {
            exit();

        } else {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    // EFFECTS: processes inputs for the secondary menu
    private void processInput2(String choice, Scanner scanner) {

        if (choice.equals("1") || choice.equalsIgnoreCase("r")) {
            registerMember(scanner);

        } else if (choice.equals("2") || choice.equalsIgnoreCase("d")) {
            deregisterMember(scanner);

        } else if (choice.equals("3") || choice.equalsIgnoreCase("l")) {
            logMemberAttendance(scanner);

        } else if (choice.equals("4") || choice.equalsIgnoreCase("c")) {
            calculateMonthlyBillUI(scanner);

        } else if (choice.equals("5") || choice.equalsIgnoreCase("v")) {
            viewMembers();

        } else if (choice.equals("6") || choice.equalsIgnoreCase("a")) {
            attendanceChecker(scanner);

        } else if (choice.equals("7") || choice.equalsIgnoreCase("sa")) {
            saveMembersManager();

        } else if (choice.equals("8") || choice.equalsIgnoreCase("lo")) {
            loadMembersManager();

        } else if (choice.equals("9") || choice.equalsIgnoreCase("e")) {
            exit();

        } else {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    // EFFECTS: Displays the starting Menu
    public void displayMenu1() {
        System.out.println("__________________________");
        System.out.println("~FinanciallyFit Terminal~");
        System.out.println("__________________________");
        System.out.println("1. (r)egister member");
        System.out.println("2. (sa)ve");
        System.out.println("3. (lo)ad");
        System.out.println("4. (e)xit");
    }

    // EFFECTS: Displays the secondary menu (when MembersManager is not empty)
    public void displayMenu2() {
        System.out.println("__________________________");
        System.out.println("~FinanciallyFit Terminal~");
        System.out.println("__________________________");
        System.out.println("1. (r)egister member");
        System.out.println("2. (d)eregister member");
        System.out.println("3. (l)og member attendance");
        System.out.println("4. (c)alculate monthly bill");
        System.out.println("5. (v)iew members");
        System.out.println("6. (a)ttendance of members for day");
        System.out.println("7. (sa)ve");
        System.out.println("8. (lo)ad");
        System.out.println("9. (e)xit");
    }

    // EFFECTS: Exits the Program
    private void exit() {
        System.out.println("Exiting the FinanciallyFit terminal. Goodbye!");
//        printLog(EventLog.getInstance());
        System.exit(0);
    }

    // EFFECTS: Uses the returnAttendanceDay method to print a list of people who attended on a certain day
    private void attendanceChecker(Scanner scanner) {
        System.out.print("Enter date to check attendance for (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        if (membersManager.returnAttendanceDay(date).isEmpty()) {
            System.out.println("Nobody attended that day :(");
        } else {
            System.out.println("List of people who attended that day: " + membersManager.returnAttendanceDay(date));
        }
    }

    // EFFECTS: prints out a list of members along with their regDate, Total Hours, and Days Attended
    private void viewMembers() {

        if (membersManager.getMembers().isEmpty()) {
            System.out.println("No members are registered!");
        } else {
            System.out.println("_______________________________________________________________________");
            System.out.println("Member Name     Registration Date    Total Hours    Days Attended");
            for (GymMember m : membersManager.getMembers()) {
                System.out.println(m.getName() + "       " + m.getRegDate() + "                  "
                        + m.getTotalHours() + "           " + m.getAttendanceCount() + "/"
                        + m.getNumOfDaysLeftInMonth());

            }
        }
    }



    // EFFECTS: Uses the calculateMonthlyBillPublic method to print a monthly bill for the user.
    private void calculateMonthlyBillUI(Scanner scanner) {
        System.out.print("Enter member name: ");
        String billMemberName = scanner.nextLine();
        double result = financiallyFitModel.calculateMonthlyBillPublic(membersManager.getMembers(), billMemberName);
        if (result != -1) {
            System.out.println("Monthly Bill for " + billMemberName + ": $" + result);
            System.out.println("Note that as you attend the gym more often your total amount due will go down");
        } else {
            System.out.println("Member not found.");
        }
    }



    // REQUIRES: inputs must respect REQUIRES of logAttendance in GymMember
    // MODIFIES: GymMember
    // EFFECTS: logs the attendance of the Member
    private void logMemberAttendance(Scanner scanner) {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        System.out.println("Enter date to log attendance for member " + memberName + " (YYYY-mm-dd):");
        String logDate = scanner.nextLine();

        GymMember foundMember = financiallyFitModel.findGymMemberPublic(membersManager.getMembers(), memberName);

        if (foundMember != null) {
            System.out.print("Enter time spent at the gym (hours): ");
            double hours = scanner.nextDouble();
            membersManager.logAttendance(hours, logDate, foundMember);
            System.out.println(hours + " hours logged for " + memberName);
        } else {
            System.out.println("Member not found.");
        }
    }


    // Registers a member by constructing a GymMember object and adding it to membersManager.
    // REQUIRES: Inputs need to respect REQUIRES of GymMember in GymMember.
    // MODIFIES: GymMember
    // EFFECTS: creates a GymMember with a username, allowed missed days,
    // base membership cost, and an attendance log, (amongst another things)
    private void registerMember(Scanner scanner) {
        System.out.print("Enter member name: ");
        String name = scanner.nextLine();
        System.out.print("Enter date of registration (YYYY-mm-dd): ");
        String regDate = scanner.next();
        System.out.print("Enter number of days allowed missed: ");
        Integer allowedMiss = scanner.nextInt();


        GymMember gymMember = new GymMember(name, regDate, allowedMiss);
        membersManager.addMember(gymMember);
        System.out.println(name + " has been registered.");
    }


    // MODIFIES: membersManager
    // EFFECTS: removes a member from the membersManager
    // object using the removeMember method.
    private void deregisterMember(Scanner scanner) {
        System.out.print("Enter member name: ");
        String name = scanner.nextLine();
        GymMember foundMember = financiallyFitModel.findGymMemberPublic(membersManager.getMembers(), name);

        if (foundMember != null) {
            membersManager.removeMember(foundMember);
            System.out.println(name + " has been deregistered.");
        } else {
            System.out.println("Member not found.");
        }

    }

    // EFFECTS: saves the current MembersManager instance to file
    private void saveMembersManager() {
        try {
            jsonWriter.open();
//            jsonWriter.write(membersManager);
            jsonWriter.close();
            System.out.println("Successfully saved instance to " + JSON_FILEPATH);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_FILEPATH);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads MembersManager instance from file
    private void loadMembersManager() {
        try {
            membersManager = jsonReader.read();
            System.out.println("Successfully loaded instance from " + JSON_FILEPATH);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_FILEPATH);
        }
    }

}
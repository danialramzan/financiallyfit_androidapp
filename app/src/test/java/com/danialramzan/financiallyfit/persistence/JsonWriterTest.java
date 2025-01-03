package com.danialramzan.financiallyfit.persistence;

import model.GymMember;
import model.MembersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {

    Map<String, Double> testAttendanceLog1;
    Map<String, Double> testAttendanceLog2;
    GymMember testGymMember1;
    GymMember testGymMember2;


    @BeforeEach
    void setUp() {
        testAttendanceLog1 = new HashMap<>();
        testAttendanceLog1.put("2023-10-22", 3.0);
        testGymMember1 = new GymMember(10, "paul_2023-10-22",
                10.0, 15.0, 4, 1, testAttendanceLog1);
        testAttendanceLog2 = new HashMap<>();
        testAttendanceLog2.put("2023-10-24", 4.0);
        testAttendanceLog2.put("2023-10-25", 3.0);
        testGymMember2 = new GymMember(9, "obama_2023-10-23",
                10.0, 15.0, 3, 2, testAttendanceLog2);
    }

    @Test
    void testWriterInvalidFile() {
        try {
            MembersManager mm = new MembersManager();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWorkroom() {
        try {
            MembersManager mm = new MembersManager();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyMembersManager.json");
            writer.open();
            writer.write(mm);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyMembersManager.json");
            mm = reader.read();
            assertEquals(0, mm.getSize());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            MembersManager mm = new MembersManager();
            mm.addMember(testGymMember1);
            mm.addMember(testGymMember2);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralMembersManager.json");
            writer.open();
            writer.write(mm);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralMembersManager.json");
            mm = reader.read();
            List<GymMember> gymMembers = mm.getMembers();
            assertEquals(2, gymMembers.size());
            checkGymMember("paul","2023-10-22", 1,
                    3.0, gymMembers.get(0));
            checkGymMember("obama","2023-10-23", 2,
                    7.0, gymMembers.get(1));

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}

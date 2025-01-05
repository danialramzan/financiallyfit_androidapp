package com.danrmzn.financiallyfit.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.danrmzn.financiallyfit.model.GymMember;
import com.danrmzn.financiallyfit.model.MembersManager;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            MembersManager mm = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyMembersManager.json");
        try {
            MembersManager mm = reader.read();
            assertEquals(0, mm.getSize());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }


    @Test
    void testReaderGeneralWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralMembersManager.json");
        try {
            MembersManager mm = reader.read();
            List<GymMember> gymMembers = mm.getMembers();
            assertEquals(2, gymMembers.size());
            checkGymMember("danial","2023-10-22", 1,
                    3.0, gymMembers.get(0));
            checkGymMember("gregor","2023-10-23", 2,
                    7.0, gymMembers.get(1));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
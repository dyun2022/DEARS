package com.example.dears;

import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JournalActivityTest extends TestCase {
    // testing variables
    LocalDate ld = LocalDate.now();
    DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String testDate = dateformatter.format(ld);


    // check response formatting
    @Test
    public void testEntryFormatting() {
        int timesChatted = 3;
        int timesFed = 2;
        int timesSleep = 1;

        String response = testDate + "\n";
        if (timesChatted != 0) response += " You chatted with me " + timesChatted + " time" + ((timesChatted == 1) ? "!" : "s!");
        if (timesFed != 0) response += " You fed me " + timesFed + " time" + ((timesFed == 1) ? "!" : "s!");
        if (timesSleep != 0) response += " I napped " + timesSleep + " time" + ((timesSleep == 1) ? "!" : "s!");

        assertTrue(response.contains(testDate));
        assertTrue(response.contains("You chatted with me 3 times!"));
        assertTrue(response.contains("You fed me 2 times!"));
        assertTrue(response.contains("I napped 1 time!"));
    }

    // check response formatting if nothing occurred that day (should say nothing but the date)
    @Test
    public void testEntryFormattingWithNoActivities() {
        int timesChatted = 0;
        int timesFed = 0;
        int timesSleep = 0;

        String response = testDate + "\n";
        if (timesChatted != 0) response += " You chatted with me " + timesChatted + " time" + ((timesChatted == 1) ? "!" : "s!");
        if (timesFed != 0) response += " You fed me " + timesFed + " time" + ((timesFed == 1) ? "!" : "s!");
        if (timesSleep != 0) response += " I napped " + timesSleep + " time" + ((timesSleep == 1) ? "!" : "s!");

        assertTrue(response.contains(testDate));
        assertFalse(response.contains("You chatted with me"));
        assertFalse(response.contains("You fed me"));
        assertFalse(response.contains("I napped"));
    }

    // check response formatting if no chatting, but feeding and sleep != 0
    @Test
    public void testEntryFormattingWithNoChat() {
        int timesChatted = 0;
        int timesFed = 2;
        int timesSleep = 1;

        String response = testDate + "\n";
        if (timesChatted != 0) response += " You chatted with me " + timesChatted + " time" + ((timesChatted == 1) ? "!" : "s!");
        if (timesFed != 0) response += " You fed me " + timesFed + " time" + ((timesFed == 1) ? "!" : "s!");
        if (timesSleep != 0) response += " I napped " + timesSleep + " time" + ((timesSleep == 1) ? "!" : "s!");

        assertTrue(response.contains(testDate));
        assertFalse(response.contains("You chatted with me"));
        assertTrue(response.contains("You fed me 2 times!"));
        assertTrue(response.contains("I napped 1 time!"));
    }
}
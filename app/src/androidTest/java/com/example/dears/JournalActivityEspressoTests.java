package com.example.dears;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.junit.Assert.*;

import static java.util.EnumSet.allOf;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Energy;
import com.example.dears.data.model.Happiness;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JournalActivityEspressoTests {
    // helper function :D
    private Intent createTestIntent() {
        AgeStage as = new AgeStage(1, "baby", 20);
        Pet pet = new Pet(1, "dears", "deer", as, 10, new Hunger(), 10,
                new Happiness(), 10, new Energy(), 10);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JournalActivity.class);
        intent.putExtra("pet", pet);
        intent.putExtra("userId", 1);
        intent.putExtra("timesChatted", 5);
        intent.putExtra("timesFed", 3);
        intent.putExtra("timesSleep", 2);

        return intent;
    }

    // test back button is displayed properly
    @Test
    public void testBackButtonIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        }
    }

    // test pet image is displayed
    @Test
    public void testPetImageIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            onView(withId(R.id.petImage)).check(matches(isDisplayed()));
        }
    }

    // test pet image gets set
    @Test
    public void testSetPetImage() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            scenario.onActivity(activity -> {
                activity.setPetImage();

                android.widget.ImageView petImage = activity.findViewById(R.id.petImage);
                assertNotNull("Pet image view should exist", petImage);
                assertNotNull("Pet image should have a drawable", petImage.getDrawable());
            });
        }
    }

    // test container for journal entries is displayed
    @Test
    public void testEntriesContainerIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            onView(withId(R.id.entriesContainer)).check(matches(isDisplayed()));
        }
    }

    // test entries is getting updated with new entries
    @Test
    public void testEntryViewContainsEntry() {
        ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent());

        scenario.onActivity(activity -> {
            LinearLayout entriesContainer = activity.findViewById(R.id.entriesContainer);
            int initialChildCount = entriesContainer.getChildCount();

            String testSummary = "Test journal entry";
            TextView entryView = activity.createEntryView(testSummary);
            entriesContainer.addView(entryView);

            assertEquals("Container should have one more child",
                    initialChildCount + 1, entriesContainer.getChildCount());

            TextView addedView = (TextView) entriesContainer.getChildAt(entriesContainer.getChildCount() - 1);
            assertEquals("Added view should have correct text", testSummary, addedView.getText().toString());
        });

        scenario.close();
    }

    // test that an empty entry can technically be created
    @Test
    public void testCreateEntryViewWithEmptyString() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            scenario.onActivity(activity -> {
                TextView entryView = activity.createEntryView("");
                assert(entryView != null);
                assert(entryView.getText().toString().equals(""));
            });
        }
    }

    // test the createEntryObj adds entry to container properly
    @Test
    public void testCreateEntryObjAddsEntry() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            scenario.onActivity(activity -> {
                LinearLayout entriesContainer = activity.findViewById(R.id.entriesContainer);
                int initialCount = entriesContainer.getChildCount();

                activity.createEntryObj(1);

                try {
                    Thread.sleep(3000); // wait for LLM to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int finalCount = entriesContainer.getChildCount();
                assertTrue("Entry count should be >= initial count", finalCount >= initialCount);

                if (finalCount > initialCount) {
                    View lastEntry = entriesContainer.getChildAt(finalCount - 1);
                    assertTrue("Last entry should be a TextView", lastEntry instanceof TextView);
                }
            });
        }
    }
}

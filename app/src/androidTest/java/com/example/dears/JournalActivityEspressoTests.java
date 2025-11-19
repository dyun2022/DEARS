package com.example.dears;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
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
    private Intent testIntent;
    private Pet pet;

    @Rule
    public ActivityScenarioRule<JournalActivity> activityRule = new ActivityScenarioRule<>(JournalActivity.class);

    @Before
    public void setUp() {
        testIntent = new Intent(ApplicationProvider.getApplicationContext(), JournalActivity.class);
        testIntent.putExtra("userId", 1);
        testIntent.putExtra("timesChatted", 5);
        testIntent.putExtra("timesFed", 3);
        testIntent.putExtra("timesSleep", 2);

//        pet = new Pet();
//        pet.setType("deer");
//        pet.setAge(new AgeStage(1,"baby", 10));
        pet = new Pet(1, "dears", "deer", new AgeStage(1, "baby", 20), 10, new Hunger(), 10, new Happiness(), 10, new Energy(), 10);

        activityRule.getScenario().onActivity(activity -> {
            activity.setPet(pet);
        });
    }


    @Test
    public void testBackButtonIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(testIntent)) {
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackButtonIsClickable() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(testIntent)) {
            onView(withId(R.id.btnBack)).check(matches(ViewMatchers.isClickable()));
        }
    }

    @Test
    public void testPetImageIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(testIntent)) {
            onView(withId(R.id.petImage)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEntriesContainerIsDisplayed() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(testIntent)) {
            onView(withId(R.id.entriesContainer)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEntryViewWithEmptyString() {
        try (ActivityScenario<JournalActivity> scenario = ActivityScenario.launch(testIntent)) {
            scenario.onActivity(activity -> {
                TextView entryView = activity.createEntryView("");
                assert(entryView != null);
                assert(entryView.getText().toString().equals(""));
            });
        }
    }
}

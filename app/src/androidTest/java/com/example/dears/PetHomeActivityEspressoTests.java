package com.example.dears;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Energy;
import com.example.dears.data.model.Happiness;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetHomeActivityEspressoTests {

    private Intent deerIntent;
    private Intent bearIntent;
    final int METER_MAX_VALUE = 20;

    /*@Rule
    public ActivityScenarioRule<PetHomeActivity> activityRule =
            new ActivityScenarioRule<>(PetHomeActivity.class);*/

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        deerIntent = new Intent(context, PetHomeActivity.class);
        bearIntent = new Intent(context, PetHomeActivity.class);

        Energy e = new Energy(METER_MAX_VALUE);
        Hunger h = new Hunger(METER_MAX_VALUE);
        Happiness ha = new Happiness(METER_MAX_VALUE);
        AgeStage a = new AgeStage(1, "baby", METER_MAX_VALUE);

        Pet deer = new Pet(1, "name", "Deer", a, 0, h, 0, ha, 0, e, 0);
        Pet bear = new Pet(1, "name", "Bear", a, 0, h, 0, ha, 0, e, 0);
        int userId = 1;

        deerIntent.putExtra("pet", deer);
        deerIntent.putExtra("userId", userId);
        bearIntent.putExtra("pet", bear);
        bearIntent.putExtra("userId", userId);
    }

    @Test
    public void verifyDeerPetImage() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(deerIntent);

        onView(withId(R.id.ivPetOval))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.baby_deer_default)));
    }

    @Test
    public void verifyBearPetImage() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(bearIntent);

        onView(withId(R.id.ivPetOval))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.baby_bear_default)));
    }
}

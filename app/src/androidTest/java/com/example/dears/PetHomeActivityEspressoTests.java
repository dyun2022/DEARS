package com.example.dears;

import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Energy;
import com.example.dears.data.model.Happiness;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createJournalRequest;
import com.example.dears.data.request.createPetRequest;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetHomeActivityEspressoTests {

    private static Intent deerIntent;
    private static Intent bearIntent;

    private static int deerId;
    private static int bearId;

    private static int deerUserId;
    private static int bearUserId;

    final static int METER_MAX_VALUE = 20;

    /*@Rule
    public ActivityScenarioRule<PetHomeActivity> activityRule =
            new ActivityScenarioRule<>(PetHomeActivity.class);*/

    @BeforeClass
    public static void setUp() throws InterruptedException {
        // Simulate a logged in user.
        Context context = ApplicationProvider.getApplicationContext();
        deerIntent = new Intent(context, PetHomeActivity.class);
        bearIntent = new Intent(context, PetHomeActivity.class);

        // *** FIX THIS
        /*Energy e = new Energy(METER_MAX_VALUE);
        Hunger h = new Hunger(METER_MAX_VALUE);
        Happiness ha = new Happiness(METER_MAX_VALUE);
        AgeStage a = new AgeStage(1, "baby", METER_MAX_VALUE);

        Pet deer = new Pet(949545, "name", "Deer", a, 0, h, 0, ha, 0, e, 0);
        Pet bear = new Pet(949545, "name", "Bear", a, 0, h, 0, ha, 0, e, 0);
        int userIdDeer = 1;
        int userIdBear = 2;

        deerIntent.putExtra("pet", deer);
        deerIntent.putExtra("userId", userIdDeer);
        bearIntent.putExtra("pet", bear);
        bearIntent.putExtra("userId", userIdBear);*/
        CountDownLatch latch = new CountDownLatch(1);

        InterfaceAPI api = APIClient.getClient().create(InterfaceAPI.class);

        changeUserRequest deerUserReq = new changeUserRequest("deerUser", "pwd", LocalDate.now(), "avatar1");
        changeUserRequest bearUserReq = new changeUserRequest("bearUser", "pwd", LocalDate.now(), "avatar2");

        api.registerUser(deerUserReq).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp1) {
                User deerUser = resp1.body();
                int userIdDeer = deerUser.getUserID();

                createPetRequest deerPetReq = new createPetRequest("name", "Deer");
                api.createPet(userIdDeer, deerPetReq).enqueue(new Callback<Pet>() {
                    @Override public void onResponse(Call<Pet> call2, Response<Pet> resp2) {
                        Pet deerPet = resp2.body();
                        deerIntent.putExtra("pet", deerPet);
                        deerIntent.putExtra("userId", userIdDeer);
                        deerUserId = userIdDeer;
                        deerId = deerPet.getPetID();

                        // Now create Bear user
                        api.registerUser(bearUserReq).enqueue(new Callback<User>() {
                            @Override public void onResponse(Call<User> call3, Response<User> resp3) {
                                User bearUser = resp3.body();
                                int userIdBear = bearUser.getUserID();

                                createPetRequest bearPetReq = new createPetRequest("name", "Bear");
                                api.createPet(userIdBear, bearPetReq).enqueue(new Callback<Pet>() {
                                    @Override public void onResponse(Call<Pet> call4, Response<Pet> resp4) {
                                        Pet bearPet = resp4.body();
                                        bearIntent.putExtra("pet", bearPet);
                                        bearIntent.putExtra("userId", userIdBear);
                                        bearId = bearPet.getPetID();
                                        bearUserId = userIdBear;
                                        latch.countDown();
                                    }

                                    @Override public void onFailure(Call<Pet> call4, Throwable t4) {
                                        latch.countDown();
                                    }
                                });
                            }

                            @Override public void onFailure(Call<User> call3, Throwable t3) {
                                latch.countDown();
                            }
                        });
                    }

                    @Override public void onFailure(Call<Pet> call2, Throwable t2) {
                        latch.countDown();
                    }
                });
            }

            @Override public void onFailure(Call<User> call, Throwable t1) {
                latch.countDown();
            }
        });

        latch.await();
    }

    /*@Before
    public void disableAnimations() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Settings.Global.putFloat(context.getContentResolver(), Settings.Global.WINDOW_ANIMATION_SCALE, 0f);
        Settings.Global.putFloat(context.getContentResolver(), Settings.Global.TRANSITION_ANIMATION_SCALE, 0f);
        Settings.Global.putFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 0f);
    }*/

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

    @Test public void feedDeer() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(deerIntent);

        onView(withId(R.id.btnFeed)).perform(click());

        onView(withId(R.id.btnSleep)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnFeed)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnChat)).check(matches(not(isDisplayed())));
        onView(withId(R.id.lowFood)).check(matches(isDisplayed()));
        onView(withId(R.id.midFood)).check(matches(isDisplayed()));
        onView(withId(R.id.highFood)).check(matches(isDisplayed()));
        onView(withId(R.id.lowFood))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.bark)));
        onView(withId(R.id.midFood))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.berries)));
        onView(withId(R.id.highFood))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.mushroom)));

        onView(withId(R.id.lowFood)).perform(click());
        onView(withId(R.id.barHunger)).check(hasRatio(0.5, 0.1));

        onView(withId(R.id.lowFood)).check(matches(not(isDisplayed())));
        onView(withId(R.id.midFood)).check(matches(not(isDisplayed())));
        onView(withId(R.id.highFood)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnSleep)).check(matches(isDisplayed()));
        onView(withId(R.id.btnFeed)).check(matches(isDisplayed()));
        onView(withId(R.id.btnChat)).check(matches(isDisplayed()));

        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.highFood)).perform(click());
        onView(withId(R.id.barHunger)).check(hasRatio(1, 0.1));
    }

    @Test public void feedBear() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(deerIntent);


    }

    @Test public void sleepDeer() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(deerIntent);


    }

    @Test public void sleepBear() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(deerIntent);


    }

    @Test public void hungerDecay() {

    }

    @Test public void energyDecay() {

    }

    @AfterClass
    public static void tearDown() {
        InterfaceAPI api = APIClient.getClient().create(InterfaceAPI.class);

        try {
            api.deletePet(deerId).execute();
        } catch (Exception ignored) {}

        try {
            api.deletePet(bearId).execute();
        } catch (Exception ignored) {}

        try {
            api.deleteUser(deerUserId).execute();
        } catch (Exception ignored) {}

        try {
            api.deleteUser(bearUserId).execute();
        } catch (Exception ignored) {}

    }

    public static ViewAssertion hasRatio(final double expectedRatio, final double tolerance) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) throw noViewFoundException;

            View parent = (View) view.getParent();
            int parentWidth = parent.getWidth();
            int childWidth = view.getWidth();

            if (parentWidth == 0) {
                throw new AssertionError("Parent width is 0, layout not measured yet");
            }

            double actualRatio = (double) childWidth / parentWidth;

            if (Math.abs(actualRatio - expectedRatio) > tolerance) {
                throw new AssertionError(
                        "Expected ratio: " + expectedRatio + " but was: " + actualRatio
                );
            }
        };
    }

    public static ViewAction waitUntilViewIsReady() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Wait until view is stable";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
            }
        };
    }


}

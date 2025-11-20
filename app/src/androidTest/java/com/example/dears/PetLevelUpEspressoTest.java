package com.example.dears;

import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetLevelUpEspressoTest {

    private static Intent testIntent;
    private static int testPetId;
    private static int testUserId;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        // Set up a Pet & User using the same method as your groupmate
        Context context = ApplicationProvider.getApplicationContext();
        testIntent = new Intent(context, PetHomeActivity.class);

        CountDownLatch latch = new CountDownLatch(1);

        InterfaceAPI api = APIClient.getClient().create(InterfaceAPI.class);

        changeUserRequest req = new changeUserRequest("happyTestUser", "pwd",
                LocalDate.now(), "avatar1");

        api.registerUser(req).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> respUser) {
                User user = respUser.body();
                testUserId = user.getUserID();

                createPetRequest petReq = new createPetRequest("TestPet", "Bear");
                api.createPet(testUserId, petReq).enqueue(new Callback<Pet>() {
                    @Override public void onResponse(Call<Pet> call2, Response<Pet> respPet) {
                        Pet pet = respPet.body();
                        testPetId = pet.getPetID();
                        testIntent.putExtra("pet", pet);
                        testIntent.putExtra("userId", testUserId);
                        latch.countDown();
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

    @Test
    public void happyReaction_changesImageThenReverts() {
        ActivityScenario<PetHomeActivity> scenario = ActivityScenario.launch(testIntent);

        // Step 1: Trigger happy reaction
        onView(withId(R.id.ivPetOval)).perform(click()); // or trigger button if needed
        scenario.onActivity(activity -> activity.happyReaction());

        // Step 2 — immediately should be HAPPY image
        onView(withId(R.id.ivPetOval))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.baby_bear_happy)));

        // Step 3 — wait for ImageView to change back after Handler delay
        onView(isRoot()).perform(waitForDrawableChange(
                R.id.ivPetOval,
                R.drawable.baby_bear_default,
                2000
        ));

        // Step 4 — verify DEFAULT image is restored
        onView(withId(R.id.ivPetOval))
                .check(matches(DrawableMatcher.withDrawable(R.drawable.baby_bear_default)));
    }

    @AfterClass
    public static void tearDown() {
        InterfaceAPI api = APIClient.getClient().create(InterfaceAPI.class);

        try { api.deletePet(testPetId).execute(); } catch (Exception ignored) {}
        try { api.deleteUser(testUserId).execute(); } catch (Exception ignored) {}
    }


    // ======== CUSTOM DRAWABLE WAIT ACTION ===========
    public static ViewAction waitForDrawableChange(int viewId, int expectedDrawable, long timeoutMs) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for ImageView drawable to change to expected one.";
            }

            @Override
            public void perform(UiController uiController, View rootView) {
                long end = System.currentTimeMillis() + timeoutMs;

                ImageView view = rootView.findViewById(viewId);
                if (view == null) throw new AssertionError("ImageView not found.");

                do {
                    onView(withId(R.id.ivPetOval))
                            .check(matches(DrawableMatcher.withDrawable(expectedDrawable)));
                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < end);

                throw new AssertionError("Drawable did not change within timeout.");
            }
        };
    }
}

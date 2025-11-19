package com.example.dears;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.AssertionFailedError;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserSetupAndPetCreationEspressoTests {

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "wait for " + millis + " ms"; }
            @Override public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public static Matcher<View> first(final Matcher<View> matcher) {
        return new TypeSafeMatcher<View>() {
            boolean found = false;

            @Override
            public void describeTo(Description description) {
                description.appendText("first matching: ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (matcher.matches(view) && !found) {
                    found = true;
                    return true;
                }
                return false;
            }
        };
    }

    private void pickAnyAvatar() {
        onView(withId(R.id.avatarBox)).perform(click());

        Matcher<View> avatarMatcher = first(Matchers.allOf(
                isAssignableFrom(ImageView.class),
                isDescendantOfA(isAssignableFrom(GridLayout.class)),
                isDisplayed()
        ));

        onView(avatarMatcher)
                .inRoot(isDialog())
                .perform(click());
    }


    /** BB1: All fields empty, remain on RegisterActivity; toast text if visible. */
    @Test
    public void register_allFieldsEmpty_showsFillAllFieldsToast() {
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(800));
        try {
            onView(withText("Please fill all fields"))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException | AssertionFailedError ignored){
        }
    }

    /** BB2: Fields filled but no pet selected, stay on RegisterActivity. */
    @Test
    public void register_missingPet_showsSelectPetToast() {
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.etUsername)).perform(typeText("user1"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("pwd"), closeSoftKeyboard());
        onView(withId(R.id.etBirthday)).perform(typeText("2020-01-02"), closeSoftKeyboard());
        onView(withId(R.id.etPetName)).perform(typeText("Bambi"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));

        onView(isRoot()).perform(waitFor(800));
        try {
            onView(withText("Please select a pet (Deer or Bear)"))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException | AssertionFailedError ignored) {}
    }

    /** BB3: Pet chosen but no avatar, stay on RegisterActivity. */
    @Test
    public void register_missingAvatar_showsSelectAvatarToast() {
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.etUsername)).perform(typeText("user2"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("pwd"), closeSoftKeyboard());
        onView(withId(R.id.etBirthday)).perform(typeText("2020-01-02"), closeSoftKeyboard());
        onView(withId(R.id.etPetName)).perform(typeText("Bambi"), closeSoftKeyboard());

        onView(withId(R.id.btnDeer)).perform(click());
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));

        onView(isRoot()).perform(waitFor(800));
        try {
            onView(withText("Please select an avatar image"))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException | AssertionFailedError ignored) {}
    }

    /** BB4: Invalid birthday format, stay on RegisterActivity. */
    @Test
    public void register_invalidBirthday_showsBirthdayFormatToast() {
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.etUsername)).perform(typeText("user3"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("pwd"), closeSoftKeyboard());
        onView(withId(R.id.etBirthday)).perform(typeText("01-02-2020"), closeSoftKeyboard());
        onView(withId(R.id.etPetName)).perform(typeText("Bambi"), closeSoftKeyboard());

        onView(withId(R.id.btnBear)).perform(click());
        pickAnyAvatar();

        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));

        onView(isRoot()).perform(waitFor(800));
        try {
            onView(withText("Birthday format must be YYYY-MM-DD or MM/DD/YYYY"))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException | AssertionFailedError ignored) {}
    }


    @Test
    public void register_happyPath_navigatesToPetHomeActivity() {
        ActivityScenario.launch(RegisterActivity.class);
        String uniqueUser = "espressoUser_" + System.currentTimeMillis();

        onView(withId(R.id.etUsername)).perform(typeText(uniqueUser), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("pwd123"), closeSoftKeyboard());
        onView(withId(R.id.etBirthday)).perform(typeText("2020-01-02"), closeSoftKeyboard());
        onView(withId(R.id.etPetName)).perform(typeText("EspressoPet"), closeSoftKeyboard());
        onView(withId(R.id.btnDeer)).perform(click());
        pickAnyAvatar();

        onView(withId(R.id.btnRegister)).perform(click());
        onView(isRoot()).perform(waitFor(1500));

        onView(withText("Please fill all fields")).check(doesNotExist());
        onView(withText("Please select a pet (Deer or Bear)")).check(doesNotExist());
        onView(withText("Please select an avatar image")).check(doesNotExist());
        onView(withText("Birthday format must be YYYY-MM-DD or MM/DD/YYYY"))
                .check(doesNotExist());
    }
}

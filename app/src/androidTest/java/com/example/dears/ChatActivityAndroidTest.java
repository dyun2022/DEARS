package com.example.dears;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.AgeStage;

import static org.hamcrest.CoreMatchers.containsString;

import com.example.dears.ChatActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.activity.result.ActivityResult;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;




import android.content.Intent;

public class ChatActivityAndroidTest {

    //black box testing with espresso,
    //fix so that multiple responses pop up sequentially (appending text response)

    public ActivityScenarioRule<ChatActivity> activityRule =
            new ActivityScenarioRule<>(ChatActivity.class);

    private Intent intent;
    ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(intent);
    
    //test that user messages are appearing in the chat, starting with greeting "Hello!"
    public void testUserMessageAppears(){
        //after clicking greeting, check if message has the greeting text, LLMResults TextView should also confirm the thank you for chatting message
        onView(withId(R.id.chatGreeting)).perform(click());
        onView(withId(R.id.messageLayout)).check(matches(hasDescendant(withText(("Hello!")))));
        onView(withId(R.id.LLMResults)).check(matches(withText(containsString("Thanks for chatting"))));

    }


    //check that the back button will return the pet with updated times chatted
    public void testBackButtonUpdates(){
        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0, "baby", 100));
        testPet.setHappinessMeter(50);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class);
        intent.putExtra("pet", testPet);
        intent.putExtra("userId", 1);

        ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            activity.timesChatted = 3;
            activity.findViewById(R.id.backButton).performClick();
        });
    }

    //test taht if the LLM cannot be iniatialized or used, a Toast message will pop up to alert the user
    public void testLLMToastAlert(){
        //click a chat option to trigger the LLM failure
        scenario.onActivity(activity -> {
            activity.findViewById(R.id.backButton).performClick();

            onView(withText("LLM init error"))
                    .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        });
    }

    //test that when chat option is clicked, LLM response is sent
    public void testLLMResponseAppears(){
        //click a chat option give LLM a prompt
        onView(withId(R.id.chatGreeting)).perform(click());

        //check that the LLM response text view has updated with the closing response
        onView(withId(R.id.LLMResults)).check(matches(withText(containsString("Thanks for chatting"))));

    }

    //make sure that happiness bar fill updates after chats
    public void testHappinessBarUpdates(){
        //click chat option to increase happiness
        onView(withId(R.id.chatGreeting)).perform(click());
        //check that happiness bar has been updated
        onView(withId(R.id.barHappiness)).check(matches(isDisplayed()));

    }


}

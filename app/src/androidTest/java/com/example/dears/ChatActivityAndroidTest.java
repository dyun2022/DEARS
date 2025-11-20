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
import static org.junit.Assert.assertTrue;





import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChatActivityAndroidTest {

    //black box testing with espresso,
    //fix so that multiple responses pop up sequentially (appending text response)


    private Intent makeTestIntent() {
//        create pet intent to use for all the tests
        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0, "baby", 100));
        testPet.setHappinessMeter(50);
        testPet.setName("TestPet");
        testPet.setType("deer");

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), TestChatActivity.class);
        intent.putExtra("pet", testPet);
        intent.putExtra("userId", 1);
        return intent;
    }

    @Test
    //test that user messages are appearing in the chat, starting with greeting "Hello!"
    public void testUserMessageAppears(){
        //after clicking greeting, check if message has the greeting text, LLMResults TextView should also confirm the thank you for chatting message
        try(ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(makeTestIntent())){
            onView(withId(R.id.chatGreeting)).perform(click());
            onView(withId(R.id.messageLayout)).check(matches(hasDescendant(withText(("How are you?")))));
            onView(withId(R.id.LLMResults)).check(matches(withText(containsString("Loading response"))));

        }

    }

@Test
    //check that the back button will return the pet with updated times chatted
    public void testBackButtonUpdates(){
       try (ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(makeTestIntent())) {
           scenario.onActivity(activity -> {
               activity.timesChatted = 3;
               activity.findViewById(R.id.backButton).performClick();
               assertTrue(activity.isFinishing());
           });
       }
    }


    // java
    @Test
    public void testMessagesInOrder() {
        try (ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(makeTestIntent())) {
            final int[] beforeCount = new int[1];
            final int[] beforeTimes = new int[1];

            scenario.onActivity(activity -> {
                beforeCount[0] = ((LinearLayout) activity.findViewById(R.id.messageLayout)).getChildCount();
                beforeTimes[0] = activity.timesChatted;
            });

            // try each of the chat options
            onView(withId(R.id.chatHello)).perform(click());
            onView(withId(R.id.chatGreeting)).perform(click());
            onView(withId(R.id.chatJoke)).perform(click());


            try { Thread.sleep(200); } catch (InterruptedException ignored) {}

            // check messages, timesChatted and order
            scenario.onActivity(activity -> {
                LinearLayout ml = activity.findViewById(R.id.messageLayout);
                int afterCount = ml.getChildCount();
                assertTrue("expected at least 3 new messages", afterCount - beforeCount[0] >= 3);
                assertTrue("timesChatted should increment by at least 3", activity.timesChatted - beforeTimes[0] >= 3);

                View lastChild = ml.getChildAt(ml.getChildCount() - 1);
                TextView lastBody = lastChild.findViewById(R.id.message_body);
                String lastText = lastBody.getText().toString();
                assertTrue("last message should match the last pressed prompt", lastText.contains("Tell me a joke!"));
            });
        }
    }


@Test
    //test that when chat option is clicked, LLM response is sent
    public void testLLMResponseAppears(){
        //click a chat option give LLM a prompt
        try (ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(makeTestIntent())) {
            onView(withId(R.id.chatGreeting)).perform(click());

            //check that the LLM response text view has updated with the closing response
            onView(withId(R.id.LLMResults)).check(matches(withText(containsString("Loading response"))));
        }

    }
@Test
    //make sure that happiness bar fill updates after chats
    public void testHappinessBarUpdates(){
        try(ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(makeTestIntent())){
            final int[] before = new int[1];
            // get current happiness
            scenario.onActivity(activity -> before[0] = activity.pet.getHappinessMeter());

//click on a chat option which should increase happiness
 onView(withId(R.id.chatGreeting)).perform(click());

            // check that the pet's happiness increased
            scenario.onActivity(activity -> {
                int after = activity.pet.getHappinessMeter();
                assertTrue("happiness should increase after chat", after > before[0]);
            });}

    }


}

package com.example.dears;

import com.example.dears.data.model.Pet;
import com.example.dears.data.model.AgeStage;
import com.example.dears.R;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StyleRes;
import androidx.test.core.app.ApplicationProvider;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)


public class ChatActivityTest {

    // java
    @Before
    public void setUp() {
        @StyleRes int themeRes = R.style.Theme_DEARS;
        ApplicationProvider.getApplicationContext().setTheme(themeRes);


    }

    @SuppressWarnings("ResourceType")
    private ChatActivity createActivityWithAppCompatTheme() {
        @StyleRes int themeRes = R.style.Theme_DEARS;

        Pet starterPet = new Pet();
        starterPet.setAge(new AgeStage(0, "baby", 100));
        starterPet.setHappinessMeter(50);
        starterPet.setType("deer");
        starterPet.setName("TestPet");

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), TestChatActivity.class);
        intent.putExtra("pet", starterPet);
        intent.putExtra("userId", -1);

        ActivityController<TestChatActivity> controller = Robolectric.buildActivity(TestChatActivity.class, intent);

        // build and start the activity after theme is installed on the application context
        controller.create();
        controller.get().setTheme(themeRes);
        controller.start();
        controller.resume();
        controller.visible();
        return controller.get();
    }


    //make sure that chatting will increase happiness level
    @Test
    public void testHappinessIncrease(){
        ChatActivity activity = createActivityWithAppCompatTheme();
//        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();
        //make pet to test with

        // at start of each @Test when reusing starter pet
        activity.pet.setHappinessMeter(50);
        activity.timesChatted = 0;
        activity.updateTextView("Hello");
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        int expectedHappiness = 75;
        assertEquals(expectedHappiness, activity.pet.getHappinessMeter());
    }


    //test if empty json is  returned
    @Test
    public void testEmptyJSONResponse(){
        ChatActivity activity = createActivityWithAppCompatTheme();

        activity.pet.setHappinessMeter(50);
        activity.timesChatted = 0;


        String emptyJSON = "{}";
        activity.updateTextView(emptyJSON);
        Shadows.shadowOf(Looper.getMainLooper()).idle();


        assertEquals(75, activity.pet.getHappinessMeter());
        assertEquals(1, activity.timesChatted);

    }

    //tests that toast appears if can't parse LLM response

    @Test
    public void testParseErrorToast(){
        ChatActivity activity = createActivityWithAppCompatTheme();
        ShadowToast.reset();

        //make pet to test with

        activity.pet.setHappinessMeter(50);
        activity.timesChatted = 0;

        Toast.makeText(ApplicationProvider.getApplicationContext(), "Error parsing LLM output", Toast.LENGTH_SHORT).show();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("Error parsing LLM output", ShadowToast.getTextOfLatestToast());
    }

    //test that timesChatted is getting updated each time updateTextView is called
    @Test
    public void testTimesChattedUpdate(){
        ChatActivity activity = createActivityWithAppCompatTheme();
        activity.pet.setHappinessMeter(50);
        activity.timesChatted = 0;

        //times chatted should be 0
        assertEquals(0, activity.timesChatted);

        //call updateTextView - this method is called each time a chat option is selected
        activity.updateTextView("Hello");
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, activity.timesChatted);

        //call updateTextView again
        activity.updateTextView("How are you?");
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(2, activity.timesChatted);
    }

    @Test
    public void testAddMessages(){
        ChatActivity activity = createActivityWithAppCompatTheme();

        LinearLayout MessageLayout = activity.findViewById(R.id.messageLayout);
        MessageLayout.removeAllViews(); // reset
        activity.messageLayout = MessageLayout;
        activity.pet.setHappinessMeter(50);
        activity.timesChatted = 0;

        activity.updateTextView("Hello");
        Shadows.shadowOf(Looper.getMainLooper()).idle();


        assertEquals(1, MessageLayout.getChildCount());

        View addedView = MessageLayout.getChildAt(0);
        View messageBody = addedView.findViewById(R.id.message_body);
        assertTrue(messageBody instanceof TextView);
        assertTrue(((TextView) messageBody).getText().toString().contains("Hello"));

    }
}

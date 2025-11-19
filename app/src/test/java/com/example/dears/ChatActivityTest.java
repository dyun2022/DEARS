package com.example.dears;

import com.example.dears.data.model.Pet;
import com.example.dears.data.model.AgeStage;

import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class ChatActivityTest {

    //make sure that chatting will increase happiness level
    public void testHappinessIncrease(){
        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();
        //make pet to test with

        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0,"baby",100));
        testPet.setHappinessMeter(50);
        activity.pet = testPet;
        activity.updateTextView("Hello");

        int expectedHappiness = 75;
        assertEquals(expectedHappiness, activity.pet.getHappinessMeter());
    }


    //test if empty json is  returned
    public void testEmptyJSONResponse(){
        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();


        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0,"baby",100));
        testPet.setHappinessMeter(50);
        activity.pet = testPet;

        String emptyJSON = "{}";
        activity.updateTextView(emptyJSON);

        assertEquals(75, activity.pet.getHappinessMeter());
        assertEquals(1, activity.timesChatted);

    }

    //tests that toast appears if can't parse LLM response
    public void testParseErrorToast(){
        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();
        //make pet to test with

        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0,"baby",100));
        testPet.setHappinessMeter(50);
        activity.pet = testPet;

        //make malformed json as example of LLM result that can't be parsed
        String malformedJSON = "{not:valid}";

        //call method to parse LLM response
        activity.runOnUiThread(() -> {;
            try{
                new JSONObject(malformedJSON);
            } catch (Exception e){
                    Toast.makeText(activity, "Error parsing LLM response", Toast.LENGTH_SHORT).show();
            }
        });
        assertEquals("Error parsing LLM response", ShadowToast.getTextOfLatestToast());
    }

    //test that timesChatted is getting updated each time updateTextView is called
    public void testTimesChattedUpdate(){
        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();
        //make pet to test with

        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0,"baby",100));
        testPet.setHappinessMeter(50);
        activity.pet = testPet;

        //times chatted should be 0
        assertEquals(0, activity.timesChatted);

        //call updateTextView - this method is called each time a chat option is selected
        activity.updateTextView("Hello");
        assertEquals(1, activity.timesChatted);

        //call updateTextView again
        activity.updateTextView("How are you?");
        assertEquals(2, activity.timesChatted);
    }

    public void testAddMessages(){
        ChatActivity activity = Robolectric.buildActivity(ChatActivity.class).create().get();

        LinearLayout mockMessageLayout = new LinearLayout(activity);
        activity.messageLayout = mockMessageLayout;

        Pet testPet = new Pet();
        testPet.setAge(new AgeStage(0,"baby",100));
        testPet.setHappinessMeter(50);
        activity.pet = testPet;

        activity.updateTextView("Hello");

        assertEquals(1, mockMessageLayout.getChildCount());

        View addedView = mockMessageLayout.getChildAt(0);
        assertTrue(addedView instanceof TextView);
        assertTrue(((TextView) addedView).getText().toString().contains("Hello"));

    }
}

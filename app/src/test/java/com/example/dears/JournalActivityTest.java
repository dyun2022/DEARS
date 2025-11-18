package com.example.dears;

import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Energy;
import com.example.dears.data.model.Happiness;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Journal;
import com.example.dears.data.model.Pet;
import com.example.dears.data.request.createEntryRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//@RunWith(RobolectricTestRunner.class)
@RunWith(MockitoJUnitRunner.class)
public class JournalActivityTest extends TestCase {
    // testing variables
    LocalDate ld = LocalDate.now();
    DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String testDate = dateformatter.format(ld);
    JournalLogic tester = new JournalLogic();
    @Mock
    InterfaceAPI interfaceAPI;
    @Mock
    Call<Object> mockCall;
    @Mock
    View mockView;
    @Mock
    ImageView mockImageView;
    private JournalActivity journalActivity;
    private Pet pet;
    private InterfaceAPI mockApi;
    private LLMInference mockLLM;
    int timesChatted;
    int timesFed;
    int timesSleep;
    String summary = ""; // blank bc LLM generates unique one each time (theoretically but not really theoretically bc it does work)


    // check response formatting
    @Test
    public void testEntryFormatting() {
        timesChatted = 3;
        timesFed = 2;
        timesSleep = 1;

        String response = tester.writeEntry(testDate, summary, timesChatted, timesFed, timesSleep);

        assertTrue(response.contains(testDate));
        assertTrue(response.contains("You chatted with me 3 times!"));
        assertTrue(response.contains("You fed me 2 times!"));
        assertTrue(response.contains("I napped 1 time!"));
    }

    // check response formatting if nothing occurred that day (should say nothing but the date)
    @Test
    public void testEntryFormattingWithNoActivities() {
        timesChatted = 0;
        timesFed = 0;
        timesSleep = 0;

        String response = tester.writeEntry(testDate, summary, timesChatted, timesFed, timesSleep);

        assertTrue(response.contains(testDate));
        assertFalse(response.contains("You chatted with me"));
        assertFalse(response.contains("You fed me"));
        assertFalse(response.contains("I napped"));
    }

    // check response formatting if no chatting, but feeding and sleep != 0
    @Test
    public void testEntryFormattingWithNoChat() {
        timesChatted = 0;
        timesFed = 2;
        timesSleep = 1;

        String response = tester.writeEntry(testDate, summary, timesChatted, timesFed, timesSleep);

        assertTrue(response.contains(testDate));
        assertFalse(response.contains("You chatted with me"));
        assertTrue(response.contains("You fed me 2 times!"));
        assertTrue(response.contains("I napped 1 time!"));
    }

    // testing setPetImage for adult bear
    @Test
    public void testSetPetImageAdultBear() {
        pet = new Pet();
        pet.setType("bear");
        pet.setAge(new AgeStage(3,"adult", 30));
        tester.pet = pet;
        String response = tester.getPetImageKey();

        assertTrue(response.contains("adult_bear_default"));

    }

    // testing setPetImage for baby deer
    @Test
    public void testSetPetImageBabyDeer() {
        pet = new Pet();
        pet.setType("deer");
        pet.setAge(new AgeStage(1,"baby", 10));
        tester.pet = pet;
        String response = tester.getPetImageKey();

        assertTrue(response.contains("baby_deer_default"));

    }

}
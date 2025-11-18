package com.example.dears;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetLevelUpTest {

    @Mock InterfaceAPI interfaceAPI;
    @Mock Call<Pet> mockCall;
    @Mock View mockView;
    @Mock Button mockButton;
    @Mock ImageButton mockImageButton;
    @Mock ImageView mockImageView;

    PetHomeActivity activity;
    Pet pet;

    final int METER_MAX_VALUE = 20;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        activity = mock(PetHomeActivity.class);
        pet = new Pet();
        pet.setHungerMeter(0);
        Hunger h = new Hunger();
        h.setMeterMax(METER_MAX_VALUE);
        pet.setHunger(h);
        pet.setPetID(1);

        activity.pet = pet;
        activity.interfaceAPI = interfaceAPI;

        when(activity.findViewById(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            if (id == R.id.btnSleep || id == R.id.btnFeed || id == R.id.btnChat) {
                return mockButton;
            } else if (id == R.id.btnJournal || id == R.id.btnSettings) {
                return mockImageButton;
            } else if (id == R.id.lowFood || id == R.id.midFood || id == R.id.highFood || id == R.id.ivPetOval) {
                return mockImageView;
            } else {
                return mockView;
            }
        });

        doCallRealMethod().when(activity).petSleep();
        doCallRealMethod().when(activity).petFeed(Mockito.anyString());
        doCallRealMethod().when(activity).wakeUp();
        doCallRealMethod().when(activity).getUpdatedWidth(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        doCallRealMethod().when(activity).setPetImage(Mockito.anyString());
        doCallRealMethod().when(activity).happyReaction();
        doCallRealMethod().when(activity).updateHungerBar();
        doCallRealMethod().when(activity).updateEnergyBar();
    }

    // WB1: Test that AgeStage changes
    @Test
    public void petGrowthTrigger() {
        // Mock AgeStage to simulate growth
        AgeStage oldAge = mock(AgeStage.class);
        AgeStage newAge = mock(AgeStage.class);

        when(oldAge.getAgeID()).thenReturn(1);
        when(oldAge.getAgeStage()).thenReturn("baby");
        when(newAge.getAgeID()).thenReturn(2);
        when(newAge.getAgeStage()).thenReturn("teen");

        // Assign oldAge to the pet initially
        pet = spy(new Pet());
        doReturn(oldAge).when(pet).getAge();
        pet.setPetID(1);

        // Assign the spy pet to the activity
        activity.pet = pet;
        activity.ageId = oldAge.getAgeID();

        doNothing().when(activity).happyReaction();
        doNothing().when(activity).showToast(anyString());

        // Now simulate the "pet has grown" by swapping AgeStage
        doReturn(newAge).when(pet).getAge();

        // Call setPetImage
        activity.setPetImage("default");

        // Verify happy reaction & toast
        verify(activity).happyReaction();
        verify(activity).showToast("Your pet grew!");
    }

    // WB2: Test that the correct image is displayed for new age
    @Test
    public void petGrowthSetsCorrectImage() {
        // Mock old and new AgeStage
        AgeStage oldAge = mock(AgeStage.class);
        AgeStage newAge = mock(AgeStage.class);

        when(oldAge.getAgeID()).thenReturn(1);
        when(oldAge.getAgeStage()).thenReturn("baby");
        when(newAge.getAgeID()).thenReturn(2);
        when(newAge.getAgeStage()).thenReturn("teen");

        // Spy pet
        pet = spy(new Pet());
        pet.setType("Bear");
        doReturn(oldAge).when(pet).getAge();

        activity.pet = pet;
        activity.ageId = oldAge.getAgeID();

        // Mock ImageView
        ImageView ivPetOval = mock(ImageView.class);
        when(activity.findViewById(R.id.ivPetOval)).thenReturn(ivPetOval);

        doNothing().when(activity).happyReaction();
        doNothing().when(activity).showToast(anyString());

        // Simulate pet growing
        doReturn(newAge).when(pet).getAge();

        // Call setPetImage
        activity.setPetImage("default");

        // Verify that ImageView setImageResource was called with teen bear default drawable
        verify(ivPetOval).setImageResource(R.drawable.teen_bear_default);

        // Verify happy reaction and toast
        verify(activity).happyReaction();
        verify(activity).showToast("Your pet grew!");
    }
}


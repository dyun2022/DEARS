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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetHomeActivityTest {

    @Mock InterfaceAPI interfaceAPI;
    @Mock Call<Pet> mockCall;
    @Mock View mockView;
    @Mock
    Button mockButton;
    @Mock
    ImageButton mockImageButton;
    @Mock
    ImageView mockImageView;

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
            } else if (id == R.id.lowFood || id == R.id.midFood || id == R.id.highFood) {
                return mockImageView;
            } else {
                return mockView;
            }
        });

        doCallRealMethod().when(activity).petSleep();
        doCallRealMethod().when(activity).petFeed(Mockito.anyString());
        doCallRealMethod().when(activity).wakeUp();
        doCallRealMethod().when(activity).getUpdatedWidth(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void petSleepSuccess() {
        doNothing().when(activity).updateEnergyBar();
        when(interfaceAPI.sleepPet(anyInt())).thenReturn(mockCall);

        activity.petSleep();

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());
        captor.getValue().onResponse(mockCall, Response.success(pet));

        verify(activity).updateEnergyBar();
    }

    @Test
    public void petSleepFail() {
        doNothing().when(activity).updateEnergyBar();
        when(interfaceAPI.sleepPet(Mockito.anyInt())).thenReturn(mockCall);

        activity.petSleep();

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());

        captor.getValue().onFailure(mockCall, new Throwable("Network error"));

        verify(activity).fail();
    }

    @Test
    public void wakeUp() {
        doNothing().when(activity).setPetImage(anyString());

        activity.wakeUp();

        verify(activity).setPetImage("default");
    }

    @Test
    public void getCorrectBarWidth() {
        int width1 = activity.getUpdatedWidth(5, 10, 24);
        int width2 = activity.getUpdatedWidth(2, 8, 40);
        assertSame(12, width1);
        assertSame(10, width2);
    }

    @Test
    public void petFeedSuccess() {
        doNothing().when(activity).updateHungerBar();
        doNothing().when(activity).happyReaction();
        when(interfaceAPI.feedPet(Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockCall);

        activity.petFeed("salmon");

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());

        captor.getValue().onResponse(mockCall, Response.success(pet));

        verify(interfaceAPI).feedPet(Mockito.anyInt(), eq(5));
        verify(activity).updateHungerBar();
    }

    @Test
    public void petFeedFail() {
        doNothing().when(activity).updateHungerBar();
        when(interfaceAPI.feedPet(Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockCall);

        activity.petFeed("salmon");

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());

        captor.getValue().onFailure(mockCall, new Throwable("Network error"));

        verify(activity).fail();
    }
}




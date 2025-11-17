package com.example.dears;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.example.dears.data.api.InterfaceAPI;
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

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, manifest = Config.NONE)
public class PetHomeActivityTest {

    @Mock InterfaceAPI interfaceAPI;
    @Mock Call<Pet> mockCall;

    private PetHomeActivity activity;
    private Pet pet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activity = spy(Robolectric.buildActivity(PetHomeActivity.class).get());
        pet = new Pet();
        pet.setPetID(1);
        activity.pet = pet;
        activity.interfaceAPI = interfaceAPI;
    }

    @Test
    public void petSleepSuccess() {
        doNothing().when(activity).updateEnergyBar();
        Mockito.when(interfaceAPI.sleepPet(Mockito.anyInt())).thenReturn(mockCall);

        activity.petSleep();

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());

        captor.getValue().onResponse(mockCall, Response.success(pet));

        verify(activity).updateEnergyBar();
    }

    @Test
    public void petSleepFail() {
        doNothing().when(activity).updateEnergyBar();
        Mockito.when(interfaceAPI.sleepPet(Mockito.anyInt())).thenReturn(mockCall);

        activity.petSleep();

        ArgumentCaptor<Callback<Pet>> captor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(captor.capture());

        captor.getValue().onFailure(mockCall, new Throwable("Network error"));

        verify(activity).fail();
    }
}

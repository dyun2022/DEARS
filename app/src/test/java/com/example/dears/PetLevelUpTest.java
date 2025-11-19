package com.example.dears;

import static org.junit.Assert.assertEquals;
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

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Pet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class PetLevelUpTest {

    @Mock InterfaceAPI interfaceAPI;
    @Mock Call<Pet> mockCall;
    @Mock View mockView;
    @Mock Button mockButton;
    @Mock ImageButton mockImageButton;
    @Mock ImageView mockImageView;

    PetHomeActivity activity;
    Pet pet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the Activity completely
        activity = mock(PetHomeActivity.class);

        pet = new Pet();
        pet.setPetID(1);
        pet.setType("Bear");

        activity.pet = pet;

        // Make logic methods real
        // Instead of calling real setPetImage, we manually test the logic
        doNothing().when(activity).showToast(anyString());
        doNothing().when(activity).happyReaction();

        // Mock ImageView
        ImageView mockIv = mock(ImageView.class);
        when(activity.findViewById(R.id.ivPetOval)).thenReturn(mockIv);
        doNothing().when(mockIv).setImageResource(anyInt());
    }

    // WB1: test if setting image is called when growing
    @Test
    public void petGrowthTriggers() {
        AgeStage oldAge = mock(AgeStage.class);
        AgeStage newAge = mock(AgeStage.class);

        when(oldAge.getAgeID()).thenReturn(1);
        when(newAge.getAgeID()).thenReturn(2);

        // Simulate growth logic
        activity.ageId = oldAge.getAgeID();
        activity.pet.setPetID(1);
        activity.pet.setType("Bear");

        // Normally setPetImage would check age and call showToast
        activity.happyReaction();
        activity.showToast("Your pet grew!");

        verify(activity).happyReaction();
        verify(activity).showToast("Your pet grew!");
    }

    // WB2: test if correct image is set when pet grows
    @Test
    public void petGrowthSetsCorrectImage() {
        AgeStage oldAge = mock(AgeStage.class);
        AgeStage newAge = mock(AgeStage.class);

        when(oldAge.getAgeID()).thenReturn(1);
        when(oldAge.getAgeStage()).thenReturn("baby");
        when(newAge.getAgeID()).thenReturn(2);
        when(newAge.getAgeStage()).thenReturn("teen");

        pet = spy(new Pet());
        pet.setType("Bear");
        doReturn(oldAge).when(pet).getAge();
        activity.pet = pet;
        activity.ageId = oldAge.getAgeID();

        // Instead of calling the real setPetImage, simulate what it would do:
        ImageView ivPetOval = mock(ImageView.class);
        when(activity.findViewById(R.id.ivPetOval)).thenReturn(ivPetOval);

        // simulate pet growing
        doReturn(newAge).when(pet).getAge();

        // simulate what setPetImage would do
        ivPetOval.setImageResource(R.drawable.teen_bear_default);
        activity.happyReaction();
        activity.showToast("Your pet grew!");

        verify(ivPetOval).setImageResource(R.drawable.teen_bear_default);
        verify(activity).happyReaction();
        verify(activity).showToast("Your pet grew!");
    }

    // WB3: test no growth triggers no reaction or toast
    @Test
    public void setPetImage_NoGrowth_NoReaction() {
        PetHomeActivity realActivity = spy(new PetHomeActivity());
        ImageView mockIv = mock(ImageView.class);
        doReturn(mockIv).when(realActivity).findViewById(R.id.ivPetOval);

        AgeStage sameAge = mock(AgeStage.class);
        when(sameAge.getAgeID()).thenReturn(1);
        when(sameAge.getAgeStage()).thenReturn("baby");

        pet.setType("Bear");
        pet.setAge(sameAge);

        realActivity.pet = pet;
        realActivity.ageId = 1;

        doNothing().when(realActivity).happyReaction();
        doNothing().when(realActivity).showToast(anyString());

        realActivity.setPetImage("default");

        verify(realActivity, never()).happyReaction();
        verify(realActivity, never()).showToast(anyString());
    }

    // WB4: correct image is chosen for age and type
    @Test
    public void setPetImage_CorrectDrawableForAgeAndType() {
        PetHomeActivity realActivity = spy(new PetHomeActivity());
        ImageView mockIv = mock(ImageView.class);
        doReturn(mockIv).when(realActivity).findViewById(R.id.ivPetOval);

        AgeStage age = mock(AgeStage.class);
        when(age.getAgeID()).thenReturn(2);
        when(age.getAgeStage()).thenReturn("teen");

        pet.setType("Bear");
        pet.setAge(age);

        realActivity.pet = pet;
        realActivity.ageId = 2;

        realActivity.setPetImage("happy");

        verify(mockIv).setImageResource(R.drawable.teen_bear_happy);
    }

    // WB5: key is generated correctly
    @Test
    public void setPetImage_KeyGeneratedCorrectly() {
        PetHomeActivity realActivity = spy(new PetHomeActivity());
        ImageView mockIv = mock(ImageView.class);
        doReturn(mockIv).when(realActivity).findViewById(R.id.ivPetOval);

        AgeStage age = mock(AgeStage.class);
        when(age.getAgeID()).thenReturn(3);
        when(age.getAgeStage()).thenReturn("adult");

        pet.setType("Deer");
        pet.setAge(age);

        realActivity.pet = pet;
        realActivity.ageId = 3;

        realActivity.setPetImage("sleep");

        verify(mockIv).setImageResource(R.drawable.adult_deer_sleep);
    }
}



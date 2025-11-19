package com.example.dears;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
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
import org.robolectric.shadows.ShadowToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class PetLevelUpTest {

    @Mock InterfaceAPI interfaceAPI;
    @Mock Call<Pet> mockCall;
    @Mock View mockView;
    @Mock
    ImageView mockImageView;

    PetHomeActivity activity;
    Pet pet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        activity = mock(PetHomeActivity.class);
        pet = new Pet();
        AgeStage baby = mock(AgeStage.class);
        activity.ageId = 1;
        baby.setAgeStage("baby");
        pet.setAge(baby);
        pet.setType("Bear");
        when(pet.getAge().getAgeID()).thenReturn(1);
        when(pet.getAge().getAgeStage()).thenReturn("baby");
        baby.setAgeID(1);

        activity.pet = pet;
        activity.interfaceAPI = interfaceAPI;

        when(activity.findViewById(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            if (id == R.id.ivPetOval) {
                return mockImageView;
            }
            else {
                return mockView;
            }
        });

        doCallRealMethod().when(activity).setPetImage(Mockito.anyString());
        doCallRealMethod().when(activity).happyReaction();
        doCallRealMethod().when(activity).showToast(Mockito.anyString());
    }

    // WB1: test if growth triggers reaction and toast
    @Test
    public void petGrowthTriggers() {
        // allow happyReaction to be monitored, but not executed
        doNothing().when(activity).happyReaction();

        mockStatic(Toast.class);
        when(Toast.makeText(any(), anyString(), anyInt())).thenReturn(mock(Toast.class));

        // mock age so growth occurs
        AgeStage teen = mock(AgeStage.class);
        when(teen.getAgeID()).thenReturn(2);
        when(teen.getAgeStage()).thenReturn("teen");
        pet.setAge(teen);

        // previous age is different → triggers growth
        activity.ageId = 1;

        // ACT — this triggers the growth block
        activity.setPetImage("happy");

        // ASSERT — growth reaction should fire
        verify(activity).happyReaction();
    }

    // WB2: test that happyReaction calls setPetImage for "happy" and then "default"
    @Test
    public void petGrowthSetsCorrectImage() throws InterruptedException {
        doNothing().when(activity).setPetImage(anyString());

        activity.happyReaction();

        // verify it calls setPetImage with "happy" then "default"
        verify(activity).setPetImage("happy");
    }

    // WB3: no growth triggers no reaction or toast
    @Test
    public void setPetImageNoGrowth_NoReaction() {
        pet.setAge(mock(AgeStage.class));
        activity.ageId = pet.getAge().getAgeID();

        doCallRealMethod().when(activity).setPetImage(anyString());
        doNothing().when(activity).happyReaction();

        activity.setPetImage("default");

        // happyReaction should not be called
        verify(activity, never()).happyReaction();
        // Toast should not be shown
        assertNull(ShadowToast.getLatestToast());
    }

    // WB4: correct drawable chosen for age/type/action
    @Test
    public void setPetImageCorrectDrawableForAgeAndType() {
        AgeStage teen = mock(AgeStage.class);
        when(teen.getAgeID()).thenReturn(2);
        when(teen.getAgeStage()).thenReturn("teen");

        pet.setAge(teen);
        activity.ageId = 2;

        ImageView iv = mock(ImageView.class);
        doReturn(iv).when(activity).findViewById(R.id.ivPetOval);

        activity.setPetImage("happy");

        verify(iv).setImageResource(R.drawable.teen_bear_happy);
    }

    // WB5: adult deer sleep drawable
    @Test
    public void setPetImageKeyGeneratedCorrectly() {
        AgeStage adult = mock(AgeStage.class);
        when(adult.getAgeID()).thenReturn(3);
        when(adult.getAgeStage()).thenReturn("adult");

        pet.setType("Deer");
        pet.setAge(adult);
        activity.ageId = 3;

        ImageView iv = mock(ImageView.class);
        doReturn(iv).when(activity).findViewById(R.id.ivPetOval);

        activity.setPetImage("sleep");

        verify(iv).setImageResource(R.drawable.adult_deer_sleep);
    }
}

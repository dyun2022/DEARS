package com.example.dears.data.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dears.data.model.Pet;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Pet> pet = new MutableLiveData<>();
    private final MutableLiveData<Integer> userId = new MutableLiveData<>();

    // Pet
    public LiveData<Pet> getPet() {
        return pet;
    }

    public void setPet(Pet p) {
        pet.setValue(p);
    }

    // User ID
    public LiveData<Integer> getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        userId.setValue(id);
    }
}
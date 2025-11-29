package com.example.dears.data.api;

import com.example.dears.data.model.Food;
import com.example.dears.data.model.Journal;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createEntryRequest;
import com.example.dears.data.request.createJournalRequest;
import com.example.dears.data.request.createPetRequest;
import com.example.dears.data.request.loginUserRequest;
import com.example.dears.data.request.updatePetRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InterfaceAPI {
    // USER
    @POST("users/register")
    Call<User> registerUser(@Body changeUserRequest request);

    @POST("users/login")
    Call<User> loginUser(@Body loginUserRequest request);

    @POST("users")
    Call<User> saveUser(@Body changeUserRequest request);

    @GET("users")
    Call<User> getAllUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int userId);

    @PUT("users/{id}/password")
    Call<User> updatePassword(@Path("id") int userId, @Body Map<String, String> body);

    @PUT("users/{id}/birthday")
    Call<User> updateBirthday(@Path("id") int userId, @Body Map<String, String> body);

    @PUT("users/{id}/avatar")
    Call<User> updateAvatar(@Path("id") int userId, @Body Map<String, String> body);

    @PUT("users/{id}/username")
    Call<User> updateUsername(@Path("id") int userId, @Body Map<String, String> body);

    @DELETE("users/delete/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    // PET
    @GET("pet/user/{id}")
    Call<Pet> getPetById(@Path("id") int userId);

    @POST("pet/user/{id}")
    Call<Pet> createPet(@Path("id") int userId, @Body createPetRequest createPetRequest);

    @PATCH("pet/{id}")
    Call<Pet> updatePet(@Path("id") int petId, @Body updatePetRequest updatePetRequest);

    @DELETE("pet/delete/{id}")
    Call<Void> deletePet(@Path("id") int petId);

    @PATCH("pet/{id}/sleep")
    Call<Pet> sleepPet(@Path("id") int petId);

    @PATCH("pet/{id}/feed/{foodId}")
    Call<Pet> feedPet(@Path("id") int petId, @Path("foodId") int foodId);

    // FOOD
    @GET("food")
    Call<Food[]> getFoods();

    // JOURNAL
    @POST("journal/create")
    Call<Object> createJournalForUser(@Body createJournalRequest createJournalRequest);

    @GET("journal/pet/{id}")
    Call<Journal> getJournalByPetID(@Path("id") int petId);

    @GET("journal")
    Call<Journal[]> getAllJournals();

    // ENTRY
    @POST("entry/create/{date}")
    Call<Object> createEntry(@Path("date") String localDate, @Body createEntryRequest createEntryRequest);


    @PATCH("pet/{id}/chat")
    Call<Pet> chatPet(@Path("id") int petId);
}

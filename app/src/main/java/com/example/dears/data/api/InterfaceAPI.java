package com.example.dears.data.api;

import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;
import com.example.dears.data.request.loginUserRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InterfaceAPI {
    // *** USER RELATED REQUESTS *** //
    @POST("users/register")
    Call<User> registerUser(@Body changeUserRequest request);

    @POST("users/login")
    Call<User> loginUser(@Body loginUserRequest request);

    @POST("users")
    Call<List<User>> saveUser(@Body changeUserRequest request);

    @GET("users")
    Call<User> getAllUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int userId);

    // TO-DO finish edit routes
    /*@PUT("/users/{id}/password")
    Call<User> editPassword(@Path("id") int userId, @Body );

    @PUT("/users/{id}/birthday")
    Call<User> editBirthday(@Path("id") int userId);

    @PUT("/users/{id}/avatar")
    Call<User> editAvatar(@Path("id") int userId);*/

    // *** PET RELATED REQUESTS *** //
    @GET("pet/user/{id}")
    Call<Pet> getPetById(@Path("id") int userId);

    @POST("pet/user/{id}")
    Call<Pet> createPet(@Path("id") int userId, @Body createPetRequest createPetRequest);

    @PATCH("pet/{id}/sleep")
    Call<Pet> sleepPet(@Path("id") int petId);
}

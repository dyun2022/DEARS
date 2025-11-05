package com.example.dears.data.api;

import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;
import com.example.dears.data.request.loginUserRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InterfaceAPI {
    // ***** USER AUTH / CRUD *****
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

    // ***** USER FIELD UPDATES (match backend @RequestMapping("/api/users")) *****
    // PUT /api/users/{id}/username
    @PUT("users/{id}/username")
    Call<User> updateUsername(@Path("id") int userId, @Body Map<String, String> body);

    // PUT /api/users/{id}/password
    @PUT("users/{id}/password")
    Call<User> updatePassword(@Path("id") int userId, @Body Map<String, String> body);

    // PUT /api/users/{id}/birthday
    @PUT("users/{id}/birthday")
    Call<User> updateBirthday(@Path("id") int userId, @Body Map<String, Object> body);

    // PUT /api/users/{id}/avatar
    @PUT("users/{id}/avatar")
    Call<User> updateAvatar(@Path("id") int userId, @Body Map<String, String> body);

    // ***** PET *****
    @GET("pet/user/{id}")
    Call<Pet> getPetById(@Path("id") int userId);

    @POST("pet/user/{id}")
    Call<Pet> createPet(@Path("id") int userId, @Body createPetRequest body);
}

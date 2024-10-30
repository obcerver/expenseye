package com.example.expenseye.remote;
import com.example.expenseye.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @FormUrlEncoded
    @POST("api/users/login")
    Call<User> login(@Field("email") String email,
                     @Field("password") String password);

    @FormUrlEncoded
    @POST("api/users/register")
    Call<User> register(@Field("email") String email,
                        @Field("password") String password);
}
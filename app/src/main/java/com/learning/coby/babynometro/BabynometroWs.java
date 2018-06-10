package com.learning.coby.babynometro;

import android.content.SharedPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BabynometroWs {

    @GET("stars-all")
    Call<List<PornStar>> getRegistros();

    @POST("addStars")
    Call<Boolean> saveStar(@Body PornStar pornStar);

}

package com.example.films;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;


public interface API {
    @GET("films/{id}")
    Call<FilmResponce> getCurrentFilm(
            @Path("id") int id,
            @Header("X-API-KEY") String apiKey
    );
}
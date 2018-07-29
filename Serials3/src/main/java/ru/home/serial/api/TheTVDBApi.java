package ru.home.serial.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ru.home.serial.entity.AuthCredentials;
import ru.home.serial.entity.AuthToken;
import ru.home.serial.entity.LanguageResponse;

public interface TheTVDBApi
{
    @POST("login")
    Call<AuthToken> getToken(@Body AuthCredentials authCredentials);

    @GET("languages")
    Call<LanguageResponse> getLanguages();
}

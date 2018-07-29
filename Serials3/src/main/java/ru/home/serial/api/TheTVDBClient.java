package ru.home.serial.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.home.serial.entity.AuthCredentials;
import ru.home.serial.entity.AuthToken;

public class TheTVDBClient
{
    private static final String TAG = TheTVDBClient.class.getName();
    private static final String BASE_URL = "https://api.thetvdb.com/";
    private static Retrofit retrofitInstance = null;
    private static String authJWTToken = null;


    private TheTVDBClient()
    {
    }

    public static Retrofit getClient()
    {
        if (retrofitInstance == null)
        {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .authenticator(new TokenAuthenticator())
                    .addInterceptor(new TokenInterceptor())
                    .build();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return retrofitInstance;
    }


    public static class TokenAuthenticator implements Authenticator
    {
        @Override
        public Request authenticate(Route route, okhttp3.Response response) throws IOException
        {
            Log.d(TAG, "authenticate: url = " + response.request().url());
            Log.d(TAG, "authenticate: code = " + response.code());

            if (response.code() == 401)
            {
                AuthCredentials authCredentials = new AuthCredentials("6NOTIRJIEZL74WWQ", "anton-09b7f", "YCLOQ4CIC9FKSZ5R");

                Call<AuthToken> call = retrofitInstance.create(TheTVDBApi.class).getToken(authCredentials);
                Response<AuthToken> refreshResponse = call.execute();

                if (refreshResponse != null && refreshResponse.code() == 200)
                {
                    //read new JWT value from response body or interceptor depending upon your JWT availability logic
                    authJWTToken = refreshResponse.body().getToken();

                    Log.d(TAG, "authenticate: got token!!!");

                    return response.request().newBuilder()
                            .header("Authorization", String.format("Bearer %s", authJWTToken))
                            .build();
                }
                else
                {
                    Log.d(TAG, "authenticate: shit happens!!!");
                    return null;
                }
            }

            return null;
        }
    }


    public static class TokenInterceptor implements Interceptor
    {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();
            Log.d("MainActivity", "intercept: url = " + request.url());
            Log.d("MainActivity", "intercept: token = " + authJWTToken);

            if (request.url().toString().contains("/login"))
            {
                return chain.proceed(request);
            }

            Request.Builder requestBuilder = request.newBuilder();

            if (authJWTToken != null)
            {
                requestBuilder.addHeader("Authorization", "Bearer " + authJWTToken);
            }

            return chain.proceed(requestBuilder.build());
        }
    }
}

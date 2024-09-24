package com.ayanicsoft.exceldatamigration.retrofit;

import androidx.annotation.Keep;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@Keep 
public class RetrofitClient {

    public static Retrofit retrofit;
    public static final String BASE_URL = "https://middleware-camera-tweedehands.mt02.notive.app/api/variants/";


    public static Retrofit getClient(){

        if(retrofit == null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                okhttp3.Request original = chain.request();

                // Add basic authentication header
                okhttp3.Request request = original.newBuilder()
                        .header("Authorization", "Token d6b59ebdb24a32ff7bfbf54f8eac6789ba8fd81e")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            });

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}

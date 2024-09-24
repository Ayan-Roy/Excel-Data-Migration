package com.ayanicsoft.exceldatamigration.retrofit;

import androidx.annotation.Keep;


import com.ayanicsoft.exceldatamigration.RequestBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
@Keep
public interface ApiService {
    @POST("location/")
    Call<RequestBean> changeProductLocation(@Body RequestBean requestBody);

}

package com.ayanicsoft.exceldatamigration.retrofit;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ayanicsoft.exceldatamigration.RequestBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataUtils {


    public static void changeLocation(Context context, RequestBean requestBean) {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.changeProductLocation(requestBean)
                .enqueue(new Callback<RequestBean>() {
                    @Override
                    public void onResponse(Call<RequestBean> call, Response<RequestBean> response) {
                        if(response.isSuccessful()){
                            Log.e(TAG, "onResponse:      sku->> "+response.body().getSku()+"   location->> "+response.body().getLocation());

                            Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Failed to save Data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestBean> call, Throwable t) {
                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

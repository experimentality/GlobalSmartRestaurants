package co.experimentality.globalsmartrestaurants.service;

import com.google.gson.JsonObject;

import co.experimentality.globalsmartrestaurants.model.User;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by juanjo on 11/18/16.
 */

public interface AmazonApiGatewayService {

    @GET("prod/locals")
    Call<JsonObject> getRestaurant(@Query("beacon") String id, @Query("language") String temperature);

    @POST("prod/locals/{id}/visits")
    Call<JsonObject> sendVisit(@Path("id") String id, @Body User userId);

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://ym4idlez8h.execute-api.us-west-2.amazonaws.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

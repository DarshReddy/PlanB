package com.app.kagada.planb.networks;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Path;

public interface APIInterface {

  @POST("history/get_visits/")
  @FormUrlEncoded
  Call<RestaurantResponse> getRestaurants(@FieldMap Map<String, String> params);

  @POST("restaurants/")
  @FormUrlEncoded
  Call<RestaurantResponse.NearbyRestaurant> postRst(@FieldMap Map<String, String> params);

  @GET("history/my_visits/")
  Call<MyVisitsResponse> myVisits();

  @POST("users/")
  @FormUrlEncoded
  Call<UserResponse> postUser(@FieldMap Map<String, String> params);

  @POST("current/check_dates/")
  @FormUrlEncoded
  Call<DateResponse> getDates(@FieldMap Map<String, String> params);

  @POST("current/{input}/accept_date/")
  Call<DateAcceptResponse> acceptDate(@Path("input") String date_id);

  @GET("current/{input}/")
  Call<DateResponse.Date> getDate(@Path("input") String date_id);

  @POST("ratings/rate_now/")
  @FormUrlEncoded
  Call<DateAcceptResponse.Rating> rateNow(@FieldMap Map<String, String> params);

  @POST("auth/")
  @FormUrlEncoded
  Call<LoginResponse> loginUser(@FieldMap Map<String, String> params);
}

package com.app.kagada.planb.networks;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantResponse {

  @SerializedName("nearby_restaurants")
  @Expose
  private List<NearbyRestaurant> nearbyRestaurants = null;

  public List<NearbyRestaurant> getNearbyRestaurants() {
    return nearbyRestaurants;
  }

  public void setNearbyRestaurants(List<NearbyRestaurant> nearbyRestaurants) {
    this.nearbyRestaurants = nearbyRestaurants;
  }

  public class NearbyRestaurant {

    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Img_url")
    @Expose
    private String imgUrl;
    @SerializedName("Locality")
    @Expose
    private String locality;
    @SerializedName("Avg_cost")
    @Expose
    private Double avgCost;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Cuisines")
    @Expose
    private String cuisines;
    @SerializedName("resID")
    @Expose
    private String resID;

    public Float getRating() {
      return Rating;
    }

    public void setRating(Float rating) {
      Rating = rating;
    }

    @SerializedName("Rating")
    @Expose
    private Float Rating;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getImgUrl() {
      return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
      this.imgUrl = imgUrl;
    }

    public String getLocality() {
      return locality;
    }

    public void setLocality(String locality) {
      this.locality = locality;
    }

    public Double getAvgCost() {
      return avgCost;
    }

    public void setAvgCost(Double avgCost) {
      this.avgCost = avgCost;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getCuisines() {
      return cuisines;
    }

    public void setCuisines(String cuisines) {
      this.cuisines = cuisines;
    }

    public String getResID() {
      return resID;
    }

    public void setResID(String resID) {
      this.resID = resID;
    }

  }

}
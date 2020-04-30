package com.app.kagada.planb.networks;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyVisitsResponse {

    @SerializedName("visits")
    @Expose
    private List<Visit> visits = null;

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public class Visit {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user")
        @Expose
        private Integer user;
        @SerializedName("restaurant")
        @Expose
        private Integer restaurant;
        @SerializedName("dayofvisit")
        @Expose
        private String dayofvisit;
        @SerializedName("rest")
        @Expose
        private RestaurantResponse.NearbyRestaurant rest;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUser() {
            return user;
        }

        public void setUser(Integer user) {
            this.user = user;
        }

        public Integer getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(Integer restaurant) {
            this.restaurant = restaurant;
        }

        public String getDayofvisit() {
            return dayofvisit;
        }

        public void setDayofvisit(String dayofvisit) {
            this.dayofvisit = dayofvisit;
        }

        public RestaurantResponse.NearbyRestaurant getRest() {
            return rest;
        }

        public void setRest(RestaurantResponse.NearbyRestaurant rest) {
            this.rest = rest;
        }


    }

}
package com.app.kagada.planb.networks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DateResponse {

    @SerializedName("dates")
    @Expose
    private List<Date> dates = null;

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public class Date {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("guy")
        @Expose
        private Object guy;
        @SerializedName("girl")
        @Expose
        private Integer girl;
        @SerializedName("restaurant")
        @Expose
        private Integer restaurant;
        @SerializedName("dateaccepted")
        @Expose
        private Boolean dateaccepted;
        @SerializedName("timeofvisit")
        @Expose
        private String timeofvisit;
        @SerializedName("female")
        @Expose
        private UserResponse female;
        @SerializedName("male")
        @Expose
        private UserResponse male;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Object getGuy() {
            return guy;
        }

        public void setGuy(Object guy) {
            this.guy = guy;
        }

        public Integer getGirl() {
            return girl;
        }

        public void setGirl(Integer girl) {
            this.girl = girl;
        }

        public Integer getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(Integer restaurant) {
            this.restaurant = restaurant;
        }

        public Boolean getDateaccepted() {
            return dateaccepted;
        }

        public void setDateaccepted(Boolean dateaccepted) {
            this.dateaccepted = dateaccepted;
        }

        public String getTimeofvisit() {
            return timeofvisit;
        }

        public void setTimeofvisit(String timeofvisit) {
            this.timeofvisit = timeofvisit;
        }

        public UserResponse getFemale() {
            return female;
        }

        public void setFemale(UserResponse female) {
            this.female = female;
        }

        public UserResponse getMale() {
            return male;
        }

        public void setMale(UserResponse male) {
            this.male = male;

        }
    }
}
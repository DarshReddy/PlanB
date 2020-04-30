package com.app.kagada.planb.networks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateAcceptResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("rating")
    @Expose
    private Rating rating;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public class Result {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("guy")
        @Expose
        private Integer guy;
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

        public Integer getGuy() {
            return guy;
        }

        public void setGuy(Integer guy) {
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

    public class Rating {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("guy")
        @Expose
        private Integer guy;
        @SerializedName("girl")
        @Expose
        private Integer girl;
        @SerializedName("rated_date")
        @Expose
        private Integer ratedDate;
        @SerializedName("rating1")
        @Expose
        private Integer rating1;
        @SerializedName("rating2")
        @Expose
        private Integer rating2;
        @SerializedName("dayofvisit")
        @Expose
        private String dayofvisit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getGuy() {
            return guy;
        }

        public void setGuy(Integer guy) {
            this.guy = guy;
        }

        public Integer getGirl() {
            return girl;
        }

        public void setGirl(Integer girl) {
            this.girl = girl;
        }

        public Integer getRatedDate() {
            return ratedDate;
        }

        public void setRatedDate(Integer ratedDate) {
            this.ratedDate = ratedDate;
        }

        public Integer getRating1() {
            return rating1;
        }

        public void setRating1(Integer rating1) {
            this.rating1 = rating1;
        }

        public Integer getRating2() {
            return rating2;
        }

        public void setRating2(Integer rating2) {
            this.rating2 = rating2;
        }

        public String getDayofvisit() {
            return dayofvisit;
        }

        public void setDayofvisit(String dayofvisit) {
            this.dayofvisit = dayofvisit;
        }

    }

}
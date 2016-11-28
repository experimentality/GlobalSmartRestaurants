package co.experimentality.globalsmartrestaurants.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juanjo on 11/21/16.
 */

public class User {

    @SerializedName("user")
    String mId;

    public User(String id) {
        this.mId = id;
    }
}

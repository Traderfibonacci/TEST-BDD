package model;

import com.google.gson.annotations.Expose;

public class ErrorMessageModel {
    @Expose
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


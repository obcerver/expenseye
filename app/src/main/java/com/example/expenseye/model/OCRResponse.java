package com.example.expenseye.model;

public class OCRResponse {
    private String image_url;
    private String prediction;

    public OCRResponse(String image_url, String prediction) {
        this.image_url = image_url;
        this.prediction = prediction;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getPrediction() {
        return prediction;
    }
}


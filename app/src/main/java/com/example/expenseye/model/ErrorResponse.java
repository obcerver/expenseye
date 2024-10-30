package com.example.expenseye.model;

public class ErrorResponse {
    // fail login
    private String status;
    private java.lang.Error error;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public java.lang.Error getError() {
        return error;
    }
    public void setError(java.lang.Error error) {
        this.error = error;
    }
}

package com.example.expenseye.model;

public class TotalDailyExpenses {

    private String day;
    private String totalAmount;

    public TotalDailyExpenses(String day, String totalAmount) {
        this.day = day;
        this.totalAmount = totalAmount;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "TotalDailyExpenses{" +
                "day='" + day + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                '}';
    }
}

package com.example.expenseye.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense {
    private int expenseId;
    private double expenseValue;
    private Date expenseDate;
    private int userId;
    private int categoryId;
    private User user;
    private Category category;

    public Expense(int expenseId, double expenseValue, Date expenseDate, int userId, int categoryId) {
        this.expenseId = expenseId;
        this.expenseValue = expenseValue;
        this.expenseDate = expenseDate;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public int getExpenseId() {return expenseId;}

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public double getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue(double expenseValue) {
        this.expenseValue = expenseValue;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", expenseValue=" + expenseValue +
                ", expenseDate=" + expenseDate +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                '}';
    }
}

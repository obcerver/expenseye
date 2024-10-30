package com.example.expenseye.model;

public class CategoryExpenses {
    private int categoryId;
    private String categoryName;
    private double totalExpenses;

    public CategoryExpenses(int categoryId, String categoryName, double totalExpenses) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalExpenses = totalExpenses;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    @Override
    public String toString() {
        return "CategoryExpenses{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", totalExpenses=" + totalExpenses +
                '}';
    }
}

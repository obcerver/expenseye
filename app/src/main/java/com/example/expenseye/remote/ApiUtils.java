package com.example.expenseye.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = ("https://obc2001.000webhostapp.com/prestige/");

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static CategoryService getCategoryService() {
        return RetrofitClient.getClient(BASE_URL).create(CategoryService.class);
    }

    public static ExpenseService getExpenseService() {
        return RetrofitClient.getClient(BASE_URL).create(ExpenseService.class);
    }
}

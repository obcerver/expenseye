package com.example.expenseye.remote;

import com.example.expenseye.model.Category;
import com.example.expenseye.model.CategoryExpenses;
import com.example.expenseye.model.Expense;
import com.example.expenseye.model.DeleteResponse;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.TotalDailyExpenses;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ExpenseService {

    //Count Operation
    @GET("api/expenses/getExpensesCount")
    Call<NumberResponse> getExpensesCount(@Header ("api-key") String apiKey,
                                          @Query("userId") int userId);


    @GET("api/expenses/getExpensesCountByDate")
    Call<NumberResponse> getExpensesCountByDate(@Header ("api-key") String apiKey,
                                                @Query("userId") int userId,
                                                @Query("expenseDate") String expenseDate);
    @GET("api/expenses/getCategoryExpensesCountByDate")
    Call<NumberResponse> getCategoryExpensesCountByDate(@Header ("api-key") String apiKey,
                                                        @Query("userId") int userId,
                                                        @Query("expenseDate") String expenseDate);

    @GET("api/expenses/getCategoryExpensesCountMonthly")
    Call<NumberResponse> getCategoryExpensesCountMonthly(@Header ("api-key") String apiKey,
                                                  @Query("userId") int userId);

    @GET("api/expenses/getCategoryExpensesCount")
    Call<NumberResponse> getCategoryExpensesCount(@Header ("api-key") String apiKey,
                                                @Query("userId") int userId);

    //Retrieve Operation
    @GET("api/expenses/listExpenseById")
    Call<List<Expense>> getListExpenseById(@Header ("api-key") String apiKey,
                                           @Query("userId") int userId);

    @GET("api/expenses/listExpenseById")
    Call<Expense> getSingleListExpenseById(@Header ("api-key") String apiKey,
                                           @Query("userId") int userId);

    @GET("api/expenses/listExpenseByIdAndDate")
    Call<List<Expense>> getListExpenseByIdAndDate(@Header ("api-key") String apiKey,
                                                  @Query("userId") int userId,
                                                  @Query("expenseDate")String expenseDate);

    @GET("api/expenses/listExpenseByIdAndDate")
    Call<Expense> getSingleListExpenseByIdAndDate(@Header ("api-key") String apiKey,
                                                  @Query("userId") int userId,
                                                  @Query("expenseDate")String expenseDate);

    @GET("api/expenses/listTotalExpensesByIdAndCategoryAndDate")
    Call<List<CategoryExpenses>> getListTotalExpensesByIdAndCategoryAndDate(@Header ("api-key") String apiKey,
                                                                            @Query("userId") int userId,
                                                                            @Query("expenseDate")String expenseDate);

    @GET("api/expenses/listTotalExpensesByIdAndCategoryAndDate")
    Call<CategoryExpenses> getSingleListTotalExpensesByIdAndCategoryAndDate(@Header ("api-key") String apiKey,
                                                                            @Query("userId") int userId,
                                                                            @Query("expenseDate")String expenseDate);

    @GET("api/expenses/listTotalExpensesByIdAndCategoryMonthly")
    Call<List<CategoryExpenses>> getListTotalExpensesByIdAndCategoryMonthly(@Header ("api-key") String apiKey,
                                                                     @Query("userId") int userId);

    @GET("api/expenses/listTotalExpensesByIdAndCategoryMonthly")
    Call<CategoryExpenses> getSingleListTotalExpensesByIdAndCategoryMonthly(@Header ("api-key") String apiKey,
                                                                     @Query("userId") int userId);

    @GET("api/expenses/listTotalExpensesByIdAndCategory")
    Call<List<CategoryExpenses>> getListTotalExpensesByIdAndCategory(@Header ("api-key") String apiKey,
                                                                     @Query("userId") int userId);

    @GET("api/expenses/listTotalExpensesByIdAndCategory")
    Call<CategoryExpenses> getSingleListTotalExpensesByIdAndCategory(@Header ("api-key") String apiKey,
                                                                     @Query("userId") int userId);

    @GET("api/expenses/getTotalDailyExpenses")
    Call<List<TotalDailyExpenses>> getTotalDailyExpenses(@Header ("api-key") String apiKey,
                                                         @Query("userId") int userId);

    // Sum Operation
    @GET("api/expenses/getTotalExpensesByIdAndDate")
    Call<NumberResponse> getTotalExpensesByIdAndDate(@Header ("api-key") String apiKey,
                                                     @Query("userId") int userId,
                                                     @Query("expenseDate") String expenseDate);
    @GET("api/expenses/getTotalExpensesByIdMonthly")
    Call<NumberResponse> getTotalExpensesByIdMonthly(@Header ("api-key") String apiKey,
                                                     @Query("userId") int userId);

    @GET("api/expenses/getTotalExpensesById")
    Call<NumberResponse> getTotalExpensesById(@Header ("api-key") String apiKey,
                                              @Query("userId") int userId);

    // Other Operation
    @FormUrlEncoded
    @POST("api/expenses/newExpenses")
    Call<String> newExpense(@Header ("api-key") String apiKey,
                            @Field("expenseValue") double expenseValue,
                            @Field("userId") int userId,
                            @Field("categoryId") int categoryId);

}



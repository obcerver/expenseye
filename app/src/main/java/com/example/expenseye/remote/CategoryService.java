package com.example.expenseye.remote;

import com.example.expenseye.model.Category;
import com.example.expenseye.model.DeleteResponse;
import com.example.expenseye.model.NumberResponse;

import okhttp3.ResponseBody;
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

public interface CategoryService {

    @GET("api/categories/getCategoriesCount")
    Call<NumberResponse> getCategoriesCount(@Header ("api-key") String apiKey,
                                            @Query("userId") int userId);

    @GET("api/categories/listCategoryById")
    Call<List<Category>> getListCategoryById(@Header ("api-key") String apiKey,
                                             @Query("userId") int userId);

    @GET("api/categories/listCategoryById")
    Call<Category> getSingleListCategoryById(@Header ("api-key") String apiKey,
                                             @Query("userId") int userId);

    @POST("api/categories")
    Call<Category> newCategory(@Header ("api-key") String apiKey,
                               @Body Category category);

//    @FormUrlEncoded
//    @POST("api/categories/newCategory")
//    Call<Category> newCategory(@Header ("api-key") String apiKey,
//                               @Field("categoryId") int categoryId,
//                               @Field("categoryName") String categoryName,
//                               @Field("userId") int userId);

    @FormUrlEncoded
    @POST("api/categories/updateCategory")
    Call<Category> updateCategory(@Header ("api-key") String apiKey,
                                  @Field("categoryId") int categoryId,
                                  @Field("categoryName") String categoryName);

    @POST("api/categories/delete/{id}")
    Call<DeleteResponse> deleteCategory(@Header ("api-key") String apiKey,
                                        @Path("id") int categoryId);

    @GET("api/categories/getDeletedCategoryId")
    Call<Category> getDeletedCategoryId(@Header ("api-key") String apiKey,
                                             @Query("userId") int userId);

    @FormUrlEncoded
    @POST("api/expenses/transferToDeletedCategory")
    Call<ResponseBody> transferToDeletedCategory(@Header ("api-key") String apiKey,
                                                 @Field("categoryIdNew") int categoryIdNew,
                                                 @Field("categoryIdOld") int categoryIdOld);
}

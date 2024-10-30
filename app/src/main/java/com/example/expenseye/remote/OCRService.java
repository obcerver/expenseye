package com.example.expenseye.remote;

import com.example.expenseye.model.Category;
import com.example.expenseye.model.CategoryExpenses;
import com.example.expenseye.model.Expense;
import com.example.expenseye.model.DeleteResponse;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.OCRResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface OCRService {
    @Multipart
    @POST("predict")
    Call<OCRResponse> getPrediction(@Part MultipartBody.Part file);
}

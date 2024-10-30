package com.example.expenseye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.expenseye.adapter.CategoryAdapter;
import com.example.expenseye.adapter.CategorySpinnerAdapter;
import com.example.expenseye.model.Category;
import com.example.expenseye.model.Expense;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.User;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.CategoryService;
import com.example.expenseye.remote.ExpenseService;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewExpenseActivity extends AppCompatActivity {

    CategoryService categoryService;
    ExpenseService expenseService;
    Context context;
    List<Category> userCategories = new ArrayList<>();  // Initialize with an empty list
    Category selectedCategory;
    String receiptValue="0.0";
    String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        Intent intent = getIntent();

        Button btnAddNewExpense = findViewById(R.id.btnAddNewExpense);
        EditText edtExpenseDate = findViewById(R.id.edtExpenseDate);
        EditText edtExpenseValue = findViewById(R.id.edtExpenseValue);
        Spinner spCategory = findViewById(R.id.spCategory);
        context = this;

        if(intent.getStringExtra("source").equalsIgnoreCase("scan")){
            receiptValue = intent.getStringExtra("value");
            edtExpenseValue.setText(receiptValue);
        }

        // Get the current date
        Date currentDate = new Date();

        // Define the format for SQL date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Format the current date into SQL date format
        String sqlDateFormat = sdf.format(currentDate);

        edtExpenseDate.setText(sqlDateFormat);
        edtExpenseDate.setEnabled(false);

        // Get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // Get category service instance
        categoryService = ApiUtils.getCategoryService();
        categoryService.getCategoriesCount(user.getToken(), user.getId()).enqueue(new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the count
                    String countString = response.body().getCategories();
                    int count = Integer.parseInt(countString);

                    if (count > 1) {
                        // Execute the call. Send the user token when sending the query
                        categoryService.getListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<List<Category>>() {
                            @Override
                            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                                if (response.isSuccessful()) {
                                    List<Category> categories = response.body();

                                    for(int i=0; i<categories.size(); i++) {
                                        Category currCategory = categories.get(i);

                                        if(currCategory.getCategoryName().equalsIgnoreCase("Deleted Category"))
                                            categories.remove(currCategory);
                                    }

                                    userCategories = categories;

                                    // Create an instance of the custom adapter and set it to the spinner
                                    CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(context, userCategories);
                                    spCategory.setAdapter(adapter);
                                } else {
                                    // Handle unsuccessful response
                                    if (response.code() == 200) {
                                        // If the response code is 200, it might be a single item (object) instead of a list
                                        handleSingleCategory(response);
                                    } else {
                                        Log.e("MyApp:", "Response code: " + response.code());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Category>> call, Throwable t) {
                                Log.e("MyApp:", "Failure: " + t.getMessage());
                            }
                        });
                    } else if (count == 1) {
                        // Execute the call. Send the user token when sending the query
                        categoryService.getSingleListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful()) {
                                    // For debug purpose
                                    Log.d("MyApp:", "Response: " + response.raw().toString());

                                    Category category = response.body();
                                    // Handle single object scenario
                                    List<Category> categories = new ArrayList<>();
                                    categories.add(category);
                                    userCategories = categories;
                                    Log.e("MyApp", userCategories.toString());

                                    // Create an instance of the custom adapter and set it to the spinner
                                    CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(context, userCategories);
                                    spCategory.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                                Log.e("MyApp:", t.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Retrieve the selected object from the userCategories list
                selectedCategory = userCategories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // get user info from SharedPreferences
        btnAddNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newExpenseValueString = edtExpenseValue.getText().toString();
                Double newExpenseValue = Double.parseDouble(newExpenseValueString);

                expenseService = ApiUtils.getExpenseService();
                Call<String> call = expenseService.newExpense(user.getToken(), newExpenseValue, user.getId(), selectedCategory.getCategoryId());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        // for debug purpose
                        Log.d("MyApp:", "Response: " + response.raw().toString());
                        Log.d("MyApp:", "Response: " + sqlDateFormat);

                        // invalid session?
                        if (response.code() == 401)
                            displayToast("Invalid session. Please re-login");

                        if(response.code() == 200) { // 204 no content but success
                            Log.e("MyApp", "response message: " + response.message());
                            // display message
                            Toast.makeText(context.getApplicationContext(),
                                    "Expense added successfully",
                                    Toast.LENGTH_LONG).show();

                            //end this activity and forward to CategoriesActivity
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            displayToast("Failed to add new expense.");
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        displayToast("Error [" + t.getMessage() + "]");
                        // for debug purpose
                        Log.d("MyApp:", "Error: " + t.getCause().getMessage());

                    }
                });
            }
        });
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleSingleCategory(Response<List<Category>> response) {
        // Convert the single item response to a list
        Category singleCategory = response.body().get(0);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(singleCategory);
    }
}
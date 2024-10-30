package com.example.expenseye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.example.expenseye.adapter.CategoryAdapter;
import com.example.expenseye.adapter.ExpenseAdapter;
import com.example.expenseye.model.Category;
import com.example.expenseye.model.Expense;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.User;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.CategoryService;
import com.example.expenseye.remote.ExpenseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImage;

public class MainActivity extends AppCompatActivity {
    ExpenseService expenseService;
    CategoryService categoryService;
    Context context;
    RecyclerView mainExpenseList;
    int categoryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the textview
        TextView txtHello = findViewById(R.id.txtHello);
        TextView txtTodayTotal = findViewById(R.id.txtTodayTotal);
        TextView txtNoExpensesToday = findViewById(R.id.txtNoExpensesToday);
        Button btnCategory = findViewById(R.id.btnCategory);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        Button btnAnalysis = findViewById(R.id.btnAnalysis);
        ImageButton btnLogout = findViewById(R.id.btnLogout);

        context = this;
        mainExpenseList = findViewById(R.id.mainExpenseList);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        // set the textview to display username
        txtHello.setText(user.getEmail());

        // Get the current date
        Date currentDate = new Date();

        // Define the format for SQL date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Format the current date into SQL date format
        String sqlDateFormat = sdf.format(currentDate);

        getCategoryCount(user, btnAddExpense);

        expenseService = ApiUtils.getExpenseService();

        expenseService.getTotalExpensesByIdAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getTotalExpenses() != null) {
                        // get the total
                        String totalString = response.body().getTotalExpenses();
                        double total = Double.parseDouble(totalString);
                        txtTodayTotal.setText("Today Total\nRM " + total);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        }));

        expenseService.getExpensesCountByDate(user.getToken(), user.getId(), sqlDateFormat).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getExpenses();
                    int count = Integer.parseInt(countString);

                    if (count > 1) {
                        mainExpenseList.setVisibility(View.VISIBLE);
                        txtNoExpensesToday.setVisibility(View.INVISIBLE);

                        // execute the call. send the user token when sending the query
                        expenseService.getListExpenseByIdAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue(new Callback<List<Expense>>() {
                            @Override
                            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                                if (response.isSuccessful()) {
                                    List<Expense> expenses = response.body();

                                    getCategoryList(expenses);

                                    // add separator between item in the list
                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainExpenseList.getContext(),
                                            DividerItemDecoration.VERTICAL);
                                    mainExpenseList.addItemDecoration(dividerItemDecoration);

                                } else {
                                    // Handle unsuccessful response
                                    if (response.code() == 200) {
                                        // If the response code is 200, it might be a single item (object) instead of a list
                                        handleSingleExpense(response);
                                    } else {
                                        Log.e("MyApp:", "Response code: " + response.code());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Expense>> call, Throwable t) {
                                Log.e("MyApp:", "Failure: " + t.getMessage());
                            }
                        });
                    } else if (count == 1) {
                        mainExpenseList.setVisibility(View.VISIBLE);
                        txtNoExpensesToday.setVisibility(View.INVISIBLE);

                        // execute the call. send the user token when sending the query
                        expenseService.getSingleListExpenseByIdAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue(new Callback<Expense>() {
                            @Override
                            public void onResponse(Call<Expense> call, Response<Expense> response) {
                                if (response.isSuccessful()) {
                                    Expense expense = response.body();
                                    // Handle single object scenario
                                    Expense singleExpense = (Expense) expense;
                                    // Process the single Cart object
                                    List<Expense> expenses = new ArrayList<>();
                                    expenses.add(singleExpense);

                                    getCategoryList(expenses);
                                }
                            }

                            @Override
                            public void onFailure(Call<Expense> call, Throwable t) {
                                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                                Log.e("MyApp:", t.getMessage());
                            }
                        });
                    } else {
                        mainExpenseList.setVisibility(View.INVISIBLE);
                        txtNoExpensesToday.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        }));

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
                startActivity(intent);
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewExpensesOptionDialog();
            }
        });

        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalysisActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogout();
            }
        });
    }

    private void getCategoryCount(User user, Button btnAddExpense) {
        // get category service instance
        categoryService = ApiUtils.getCategoryService();

        categoryService.getCategoriesCount(user.getToken(), user.getId()).enqueue(new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategories();
                    int categoryCount = Integer.parseInt(countString);
                    Log.e("MyApp", "" + categoryCount);

                    // Enable or disable the button based on the category count
                    if(categoryCount == 1) {
                        btnAddExpense.setVisibility(View.INVISIBLE);
                    } else {
                        btnAddExpense.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        });
    }

    private void getCategoryList(List<Expense> expenses) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get category service instance
        categoryService = ApiUtils.getCategoryService();

        categoryService.getCategoriesCount(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategories();
                    int count = Integer.parseInt(countString) - 1;

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        categoryService.getListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<List<Category>>() {
                            @Override
                            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                                if (response.isSuccessful()) {
                                    List<Category> categories = response.body();

                                    // initialize the adapter with the listener
                                    ExpenseAdapter adapter = new ExpenseAdapter(context, expenses, categories);
                                    // Set adapter to the RecyclerView
                                    mainExpenseList.setAdapter(adapter);
                                    // set layout to recycler view
                                    mainExpenseList.setLayoutManager(new LinearLayoutManager(context));

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

                        // execute the call. send the user token when sending the query
                        categoryService.getSingleListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful()) {
                                    // for debug purpose

                                    Category category = response.body();
                                    // Handle single object scenario
                                    Category singleCategory = (Category) category;
                                    // Process the single Cart object
                                    List<Category> categories = new ArrayList<>();
                                    categories.add(singleCategory);

                                    // initialize the adapter with the listener
                                    ExpenseAdapter adapter = new ExpenseAdapter(context, expenses, categories);
                                    // Set adapter to the RecyclerView
                                    mainExpenseList.setAdapter(adapter);
                                    // set layout to recycler view
                                    mainExpenseList.setLayoutManager(new LinearLayoutManager(context));
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
        }));
    }

    void showNewExpensesOptionDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_new_expenses_option);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Initializing the views of the dialog.
        Button btnNewExpenseAuto = dialog.findViewById(R.id.btnNewExpenseAuto);
        Button btnNewExpenseManual = dialog.findViewById(R.id.btnNewExpenseManual);

        btnNewExpenseAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionDialog();
                dialog.dismiss();
            }
        });

        btnNewExpenseManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), NewExpenseActivity.class);
                intent.putExtra("source", "manual");
                startActivity(intent);

                dialog.dismiss();
            }
        });

        // Set dialog width and height
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT; // Set width to match parent
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set height to wrap content
        dialog.getWindow().setAttributes(params);

        dialog.show();
    }

    void showInstructionDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        // Disable the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make the dialog cancelable
        dialog.setCancelable(true);
        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.dialog_new_expenses_auto_instuction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize the views in the dialog
        Button btnInstructionNext = dialog.findViewById(R.id.btnInstructionNext);

        btnInstructionNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        // Set dialog width and height
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT; // Set width to match parent
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set height to wrap content
        dialog.getWindow().setAttributes(params);

        dialog.show();
    }


    public void doLogout() {
        // clear the shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).logout();
        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // forward to LoginActivity
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleSingleExpense(Response<List<Expense>> response) {
        // Convert the single item response to a list
        Expense singleExpense = response.body().get(0);
        List<Expense> expenseList = new ArrayList<>();
        expenseList.add(singleExpense);
    }

    private void handleSingleCategory(Response<List<Category>> response) {
        // Convert the single item response to a list
        Category singleCategory = response.body().get(0);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(singleCategory);
    }
}
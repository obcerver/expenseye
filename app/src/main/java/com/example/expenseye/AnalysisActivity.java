package com.example.expenseye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expenseye.adapter.CategoryExpensesAdapter;
import com.example.expenseye.model.Category;
import com.example.expenseye.model.CategoryExpenses;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.TotalDailyExpenses;
import com.example.expenseye.model.User;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.CategoryService;
import com.example.expenseye.remote.ExpenseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.XAxis;

public class AnalysisActivity extends AppCompatActivity {

    ExpenseService expenseService;
    CategoryService categoryService;
    Context context;
    RecyclerView percentageCategoryDailyList;
    RecyclerView percentageCategoryAllTimeList;
    RecyclerView percentageCategoryMonthlyList;
    double totalExpenses = 0;
    private PieChart pieChartAllTime;
    private PieChart pieChartMonthly;
    private PieChart pieChartDaily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_analysis);

        Button btnCategory = findViewById(R.id.btnCategory);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        Button btnHome = findViewById(R.id.btnHome);

        Button btnAnalysisDaily = findViewById(R.id.btnAnalysisDaily);
        Button btnAnalysisMonthly = findViewById(R.id.btnAnalysisMonthly);
        Button btnAnalysisAllTime = findViewById(R.id.btnAnalysisAllTime);

        LinearLayout layoutAnalysisDaily = findViewById(R.id.layoutAnalysisDaily);
        LinearLayout layoutAnalysisMonthly = findViewById(R.id.layoutAnalysisMonthly);
        LinearLayout layoutAnalysisAllTime= findViewById(R.id.layoutAnalysisAllTime);

        percentageCategoryDailyList = findViewById(R.id.percentageCategoryDailyList);
        percentageCategoryMonthlyList = findViewById(R.id.percentageCategoryMonthlyList);
        percentageCategoryAllTimeList = findViewById(R.id.percentageCategoryAllTimeList);

        layoutAnalysisDaily.setVisibility(View.VISIBLE);
        layoutAnalysisMonthly.setVisibility(View.INVISIBLE);
        layoutAnalysisAllTime.setVisibility(View.INVISIBLE);
        btnAnalysisDaily.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnAnalysisDaily.setTextColor(Color.parseColor("#000000"));

        getDailyLayout();

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        expenseService = ApiUtils.getExpenseService();

        getCategoryCount(user, btnAddExpense);

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

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnAnalysisDaily.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutAnalysisDaily.setVisibility(View.VISIBLE);
                layoutAnalysisMonthly.setVisibility(View.INVISIBLE);
                layoutAnalysisAllTime.setVisibility(View.INVISIBLE);

                btnAnalysisDaily.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnAnalysisDaily.setTextColor(Color.parseColor("#000000"));
                btnAnalysisMonthly.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisMonthly.setTextColor(Color.parseColor("#FFFFFF"));
                btnAnalysisAllTime.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisAllTime.setTextColor(Color.parseColor("#FFFFFF"));

                getDailyLayout();
            }
        });

        btnAnalysisMonthly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutAnalysisDaily.setVisibility(View.INVISIBLE);
                layoutAnalysisMonthly.setVisibility(View.VISIBLE);
                layoutAnalysisAllTime.setVisibility(View.INVISIBLE);

                btnAnalysisMonthly.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnAnalysisMonthly.setTextColor(Color.parseColor("#000000"));
                btnAnalysisDaily.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisDaily.setTextColor(Color.parseColor("#FFFFFF"));
                btnAnalysisAllTime.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisAllTime.setTextColor(Color.parseColor("#FFFFFF"));

                getMonthlyLayout(user);
            }
        });

        btnAnalysisAllTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutAnalysisDaily.setVisibility(View.INVISIBLE);
                layoutAnalysisMonthly.setVisibility(View.INVISIBLE);
                layoutAnalysisAllTime.setVisibility(View.VISIBLE);

                btnAnalysisAllTime.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnAnalysisAllTime.setTextColor(Color.parseColor("#000000"));
                btnAnalysisMonthly.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisMonthly.setTextColor(Color.parseColor("#FFFFFF"));
                btnAnalysisDaily.setBackgroundColor(Color.parseColor("#008877"));
                btnAnalysisDaily.setTextColor(Color.parseColor("#FFFFFF"));

                getAllTimeLayout();
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

    void showNewExpensesOptionDialog() {
        final Dialog dialog = new Dialog(AnalysisActivity.this);
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
        final Dialog dialog = new Dialog(AnalysisActivity.this);
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

    private void getDailyLayout() {
        TextView tvDailyTotal = findViewById(R.id.tvDailyTotal);

        TextView tvCurrentDay = findViewById(R.id.tvCurrentDay);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault());
        String currentDay = monthFormat.format(calendar.getTime());
        tvCurrentDay.setText(currentDay);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // Get the current date
        Date currentDate = new Date();

        // Define the format for SQL date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Format the current date into SQL date format
        String sqlDateFormat = sdf.format(currentDate);

        pieChartDaily = findViewById(R.id.pieChartDaily);
        setupPieChart(pieChartDaily);

        expenseService = ApiUtils.getExpenseService();
        expenseService.getTotalExpensesByIdAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getTotalExpenses() != null) {
                        // get the total
                        String totalString = response.body().getTotalExpenses();
                        totalExpenses = Double.parseDouble(totalString);
                        tvDailyTotal.setText("Total Expenses: RM " + totalExpenses);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        }));

        expenseService.getCategoryExpensesCountByDate(user.getToken(), user.getId(), sqlDateFormat).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategoriesExpenses();
                    int count = Integer.parseInt(countString);
                    Log.e("MyApp:", "Count: " + count);

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        expenseService.getListTotalExpensesByIdAndCategoryAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue(new Callback<List<CategoryExpenses>>() {
                            @Override
                            public void onResponse(Call<List<CategoryExpenses>> call, Response<List<CategoryExpenses>> response) {
                                if (response.isSuccessful()) {
                                    List<CategoryExpenses> categoryExpenses = response.body();

                                    getCategoryList(user.getToken(), categoryExpenses, "daily");

                                    loadPieChartData(categoryExpenses, totalExpenses, pieChartDaily);

                                    Log.e("MyApp:", "List" + categoryExpenses.toString());

                                } else {
                                    // Handle unsuccessful response
                                    if (response.code() == 200) {
                                        // If the response code is 200, it might be a single item (object) instead of a list
                                        handleSingleCategoryExpenses(response);
                                    } else {
                                        Log.e("MyApp:", "Response code: " + response.code());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CategoryExpenses>> call, Throwable t) {
                                Log.e("MyApp:", "Failure: " + t.getMessage());
                            }
                        });
                    } else if (count == 1) {

                        // execute the call. send the user token when sending the query
                        expenseService.getSingleListTotalExpensesByIdAndCategoryAndDate(user.getToken(), user.getId(), sqlDateFormat).enqueue(new Callback<CategoryExpenses>() {
                            @Override
                            public void onResponse(Call<CategoryExpenses> call, Response<CategoryExpenses> response) {
                                if (response.isSuccessful()) {
                                    // for debug purpose
                                    Log.d("MyApp:", "Response: " + response.raw().toString());

                                    CategoryExpenses categoryExpenses = response.body();
                                    // Handle single object scenario
                                    CategoryExpenses singleCategoryExpenses = (CategoryExpenses) categoryExpenses;
                                    // Process the single Cart object
                                    List<CategoryExpenses> singleCategoryExpensesList  = new ArrayList<>();
                                    singleCategoryExpensesList.add(singleCategoryExpenses);

                                    loadPieChartData(singleCategoryExpensesList, totalExpenses, pieChartDaily);

                                    Log.e("MyApp:", "List" + singleCategoryExpensesList.toString());

                                    getCategoryList(user.getToken(), singleCategoryExpensesList, "daily");
                                }
                            }

                            @Override
                            public void onFailure(Call<CategoryExpenses> call, Throwable t) {
                                displayToast("Error connecting to the server");
                                Log.e("MyApp:", t.getMessage());
                            }
                        });
                    } else {
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

    private void getMonthlyLayout(User user) {
        TextView tvMonthlyTotal = findViewById(R.id.tvMonthlyTotal);

        TextView tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        String currentMonth = monthFormat.format(calendar.getTime());
        tvCurrentMonth.setText(currentMonth);

        expenseService = ApiUtils.getExpenseService();

        expenseService.getTotalExpensesByIdMonthly(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getTotalExpenses() != null) {
                        // get the total
                        String totalString = response.body().getTotalExpenses();
                        totalExpenses = Double.parseDouble(totalString);
                        tvMonthlyTotal.setText("Total Expenses: RM " + totalExpenses);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        }));

        expenseService.getTotalDailyExpenses(user.getToken(), user.getId()).enqueue(new Callback<List<TotalDailyExpenses>>() {
            @Override
            public void onResponse(Call<List<TotalDailyExpenses>> call, Response<List<TotalDailyExpenses>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TotalDailyExpenses> tdes = response.body();
                    setupChart(tdes);
                    Log.e("MyAppNow1:", tdes.toString());
                }
            }

            @Override
            public void onFailure(Call<List<TotalDailyExpenses>> call, Throwable t) {
                // Handle failure
                Log.e("MyAppNow2:", "Failure: " + t.getMessage());
            }
        });

        pieChartMonthly = findViewById(R.id.pieChartMonthly);
        setupPieChart(pieChartMonthly);

        expenseService.getCategoryExpensesCountMonthly(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategoriesExpenses();
                    int count = Integer.parseInt(countString);
                    Log.e("MyApp:", "Count: " + count);

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        expenseService.getListTotalExpensesByIdAndCategoryMonthly(user.getToken(), user.getId()).enqueue(new Callback<List<CategoryExpenses>>() {
                            @Override
                            public void onResponse(Call<List<CategoryExpenses>> call, Response<List<CategoryExpenses>> response) {
                                if (response.isSuccessful()) {
                                    List<CategoryExpenses> categoryExpenses = response.body();

                                    loadPieChartData(categoryExpenses, totalExpenses, pieChartMonthly);

                                    getCategoryList(user.getToken(), categoryExpenses, "monthly");

                                    Log.e("MyApp:", "List" + categoryExpenses.toString());

                                } else {
                                    // Handle unsuccessful response
                                    if (response.code() == 200) {
                                        // If the response code is 200, it might be a single item (object) instead of a list
                                        handleSingleCategoryExpenses(response);
                                    } else {
                                        Log.e("MyApp:", "Response code: " + response.code());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CategoryExpenses>> call, Throwable t) {
                                Log.e("MyApp:", "Failure: " + t.getMessage());
                            }
                        });
                    } else if (count == 1) {

                        // execute the call. send the user token when sending the query
                        expenseService.getSingleListTotalExpensesByIdAndCategoryMonthly(user.getToken(), user.getId()).enqueue(new Callback<CategoryExpenses>() {
                            @Override
                            public void onResponse(Call<CategoryExpenses> call, Response<CategoryExpenses> response) {
                                if (response.isSuccessful()) {
                                    // for debug purpose
                                    Log.d("MyApp:", "Response: " + response.raw().toString());

                                    CategoryExpenses categoryExpenses = response.body();
                                    // Handle single object scenario
                                    CategoryExpenses singleCategoryExpenses = (CategoryExpenses) categoryExpenses;
                                    // Process the single Cart object
                                    List<CategoryExpenses> singleCategoryExpensesList  = new ArrayList<>();
                                    singleCategoryExpensesList.add(singleCategoryExpenses);

                                    loadPieChartData(singleCategoryExpensesList, totalExpenses, pieChartMonthly);

                                    Log.e("MyApp:", "List" + singleCategoryExpensesList.toString());

                                    getCategoryList(user.getToken(), singleCategoryExpensesList, "monthly");
                                }
                            }

                            @Override
                            public void onFailure(Call<CategoryExpenses> call, Throwable t) {
                                displayToast("Error connecting to the server");
                                Log.e("MyApp:", t.getMessage());
                            }
                        });
                    } else {
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

    private void getAllTimeLayout() {
        TextView tvAllTimeTotal = findViewById(R.id.tvAllTimeTotal);

        pieChartAllTime = findViewById(R.id.pieChartAllTime);
        setupPieChart(pieChartAllTime);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        expenseService = ApiUtils.getExpenseService();

        expenseService.getTotalExpensesById(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getTotalExpenses() != null) {
                        // get the total
                        String totalString = response.body().getTotalExpenses();
                        totalExpenses = Double.parseDouble(totalString);
                        tvAllTimeTotal.setText("Total Expenses: RM " + totalExpenses);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp:", "Failure: " + t.getMessage());
            }
        }));

        expenseService.getCategoryExpensesCount(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategoriesExpenses();
                    int count = Integer.parseInt(countString);
                    Log.e("MyApp:", "Count: " + count);

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        expenseService.getListTotalExpensesByIdAndCategory(user.getToken(), user.getId()).enqueue(new Callback<List<CategoryExpenses>>() {
                            @Override
                            public void onResponse(Call<List<CategoryExpenses>> call, Response<List<CategoryExpenses>> response) {
                                if (response.isSuccessful()) {
                                    List<CategoryExpenses> categoryExpenses = response.body();

                                    loadPieChartData(categoryExpenses, totalExpenses, pieChartAllTime);

                                    getCategoryList(user.getToken(), categoryExpenses, "allTime");

                                    Log.e("MyApp:", "List" + categoryExpenses.toString());

                                } else {
                                    // Handle unsuccessful response
                                    if (response.code() == 200) {
                                        // If the response code is 200, it might be a single item (object) instead of a list
                                        handleSingleCategoryExpenses(response);
                                    } else {
                                        Log.e("MyApp:", "Response code: " + response.code());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CategoryExpenses>> call, Throwable t) {
                                Log.e("MyApp:", "Failure: " + t.getMessage());
                            }
                        });
                    } else if (count == 1) {

                        // execute the call. send the user token when sending the query
                        expenseService.getSingleListTotalExpensesByIdAndCategory(user.getToken(), user.getId()).enqueue(new Callback<CategoryExpenses>() {
                            @Override
                            public void onResponse(Call<CategoryExpenses> call, Response<CategoryExpenses> response) {
                                if (response.isSuccessful()) {
                                    // for debug purpose
                                    Log.d("MyApp:", "Response: " + response.raw().toString());

                                    CategoryExpenses categoryExpenses = response.body();
                                    // Handle single object scenario
                                    CategoryExpenses singleCategoryExpenses = (CategoryExpenses) categoryExpenses;
                                    // Process the single Cart object
                                    List<CategoryExpenses> singleCategoryExpensesList  = new ArrayList<>();
                                    singleCategoryExpensesList.add(singleCategoryExpenses);

                                    loadPieChartData(singleCategoryExpensesList, totalExpenses, pieChartAllTime);

                                    Log.e("MyApp:", "List" + singleCategoryExpensesList.toString());

                                    getCategoryList(user.getToken(), singleCategoryExpensesList, "allTime");
                                }
                            }

                            @Override
                            public void onFailure(Call<CategoryExpenses> call, Throwable t) {
                                displayToast("Error connecting to the server");
                                Log.e("MyApp:", t.getMessage());
                            }
                        });
                    } else {
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

    private void setupPieChart(PieChart pieChart) {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setUsePercentValues(false);
        pieChart.setEntryLabelTextSize(0);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Expense Category");
        pieChart.setCenterTextSize(0);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    private void loadPieChartData(List<CategoryExpenses> categoryExpenses, double totalExpenses, PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for(int i=0; i<categoryExpenses.size(); i++) {
            CategoryExpenses curr = categoryExpenses.get(i);
            entries.add(new PieEntry((float) ((curr.getTotalExpenses()/totalExpenses)*100), curr.getCategoryName()));
        }

        colors.add(ContextCompat.getColor(this, R.color.color0));
        colors.add(ContextCompat.getColor(this, R.color.color1));
        colors.add(ContextCompat.getColor(this, R.color.color2));
        colors.add(ContextCompat.getColor(this, R.color.color3));
        colors.add(ContextCompat.getColor(this, R.color.color4));
        colors.add(ContextCompat.getColor(this, R.color.color5));
        colors.add(ContextCompat.getColor(this, R.color.color6));
        colors.add(ContextCompat.getColor(this, R.color.color7));
        colors.add(ContextCompat.getColor(this, R.color.color8));
        colors.add(ContextCompat.getColor(this, R.color.color9));
        colors.add(ContextCompat.getColor(this, R.color.color10));
        colors.add(ContextCompat.getColor(this, R.color.color11));
        colors.add(ContextCompat.getColor(this, R.color.color12));
        colors.add(ContextCompat.getColor(this, R.color.color13));
        colors.add(ContextCompat.getColor(this, R.color.color14));
        colors.add(ContextCompat.getColor(this, R.color.color15));
        colors.add(ContextCompat.getColor(this, R.color.color16));
        colors.add(ContextCompat.getColor(this, R.color.color17));
        colors.add(ContextCompat.getColor(this, R.color.color18));
        colors.add(ContextCompat.getColor(this, R.color.color19));
        colors.add(ContextCompat.getColor(this, R.color.color20));

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    public class DayFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value); // Return only the day as an integer
        }
    }

    public void setupChart(List<TotalDailyExpenses> tdes) {
        try {

            List<Entry> entries = new ArrayList<>();

            // Parse the JSON data
            for (int i = 0; i < tdes.size(); i++) {
                TotalDailyExpenses tde = tdes.get(i);
                String dayStr = tde.getDay();
                float totalAmount = Float.parseFloat(tde.getTotalAmount());

                // Convert date to float index (e.g., 1 for 1st day, 2 for 2nd day)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dayStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                entries.add(new Entry(day, totalAmount));
            }

            // Add entries for days without data
            Calendar calendar = Calendar.getInstance();
            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= maxDays; i++) {
                boolean hasData = false;
                for (Entry entry : entries) {
                    if (entry.getX() == i) {
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    entries.add(new Entry(i, 0)); // Adding zero for days without data
                }
            }

            // Sort entries by day
            entries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));

            // Set up the LineDataSet and LineChart
            LineDataSet dataSet = new LineDataSet(entries, "Daily Expenses");
            LineData lineData = new LineData(dataSet);
            LineChart lineChart = findViewById(R.id.lineChart);
            lineChart.setData(lineData);

            // Customize XAxis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // Show every day
            xAxis.setValueFormatter(new DayFormatter()); // Set the custom day formatter

            // Customize the description
            Description description = new Description();
            description.setText("Day of a Month");
            description.setTextSize(12f); // Set the text size
            description.setPosition(lineChart.getWidth() - 20, lineChart.getHeight() - 20); // Set the position
            lineChart.setDescription(description);

            lineChart.invalidate(); // Refresh chart
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCategoryList(String token, List<CategoryExpenses> categoryExpenses, String layout) {
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
                    int count = Integer.parseInt(countString);

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        categoryService.getListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<List<Category>>() {
                            @Override
                            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                                if (response.isSuccessful()) {
                                    List<Category> categories = response.body();

                                    // initialize the adapter with the listener
                                    CategoryExpensesAdapter adapter = new CategoryExpensesAdapter(getApplicationContext(), categoryExpenses, categories, totalExpenses);

                                    if(layout.equalsIgnoreCase("daily")) {
                                        // Set adapter to the RecyclerView
                                        percentageCategoryDailyList.setAdapter(adapter);
                                        // set layout to recycler view
                                        percentageCategoryDailyList.setLayoutManager(new LinearLayoutManager(context));

                                        // add separator between item in the list
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(percentageCategoryDailyList.getContext(),
                                                DividerItemDecoration.VERTICAL);
                                        percentageCategoryDailyList.addItemDecoration(dividerItemDecoration);
                                    }

                                    else if (layout.equalsIgnoreCase("monthly")) {
                                        // Set adapter to the RecyclerView
                                        percentageCategoryMonthlyList.setAdapter(adapter);
                                        // set layout to recycler view
                                        percentageCategoryMonthlyList.setLayoutManager(new LinearLayoutManager(context));

                                        // add separator between item in the list
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(percentageCategoryMonthlyList.getContext(),
                                                DividerItemDecoration.VERTICAL);
                                        percentageCategoryMonthlyList.addItemDecoration(dividerItemDecoration);
                                    }

                                    else if (layout.equalsIgnoreCase("allTime")) {
                                        // Set adapter to the RecyclerView
                                        percentageCategoryAllTimeList.setAdapter(adapter);
                                        // set layout to recycler view
                                        percentageCategoryAllTimeList.setLayoutManager(new LinearLayoutManager(context));

                                        // add separator between item in the list
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(percentageCategoryAllTimeList.getContext(),
                                                DividerItemDecoration.VERTICAL);
                                        percentageCategoryAllTimeList.addItemDecoration(dividerItemDecoration);
                                    }
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
                                    Category category = response.body();
                                    // Handle single object scenario
                                    Category singleCategory = (Category) category;
                                    // Process the single Cart object
                                    List<Category> categories = new ArrayList<>();
                                    categories.add(singleCategory);

                                    // initialize the adapter with the listener
                                    CategoryExpensesAdapter adapter = new CategoryExpensesAdapter(getApplicationContext(), categoryExpenses, categories, totalExpenses);

                                    if(layout.equalsIgnoreCase("daily")) {
                                        // Set adapter to the RecyclerView
                                        percentageCategoryDailyList.setAdapter(adapter);
                                        // set layout to recycler view
                                        percentageCategoryDailyList.setLayoutManager(new LinearLayoutManager(context));

                                        // add separator between item in the list
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(percentageCategoryDailyList.getContext(),
                                                DividerItemDecoration.VERTICAL);
                                        percentageCategoryDailyList.addItemDecoration(dividerItemDecoration);
                                    }

                                    else if (layout.equalsIgnoreCase("allTime")) {
                                        // Set adapter to the RecyclerView
                                        percentageCategoryAllTimeList.setAdapter(adapter);
                                        // set layout to recycler view
                                        percentageCategoryAllTimeList.setLayoutManager(new LinearLayoutManager(context));

                                        // add separator between item in the list
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(percentageCategoryAllTimeList.getContext(),
                                                DividerItemDecoration.VERTICAL);
                                        percentageCategoryAllTimeList.addItemDecoration(dividerItemDecoration);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                displayToast("Error connecting to the server");
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

    private void handleSingleCategoryExpenses(Response<List<CategoryExpenses>> response) {
        // Convert the single item response to a list
        CategoryExpenses singleCategoryExpenses = response.body().get(0);
        List<CategoryExpenses> singleCategoryExpensesList = new ArrayList<>();
        singleCategoryExpensesList.add(singleCategoryExpenses);
    }

    private void handleSingleCategory(Response<List<Category>> response) {
        // Convert the single item response to a list
        Category singleCategory = response.body().get(0);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(singleCategory);
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
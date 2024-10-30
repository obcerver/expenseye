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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import com.example.expenseye.adapter.CategoryAdapter;
import com.example.expenseye.model.Category;
import com.example.expenseye.model.DeleteResponse;
import com.example.expenseye.model.NumberResponse;
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.User;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.CategoryService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryButtonClickListener {

    CategoryService categoryService;
    Context context;
    RecyclerView categoryList;
    Button btnAddCategory;
    TextView tvNoCategories;
    int count = 0;
    int deletedCategoryId = 0;
    List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Button btnAnalysis = findViewById(R.id.btnAnalysis);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        Button btnHome = findViewById(R.id.btnHome);

        tvNoCategories = findViewById(R.id.tvNoCategories);
        tvNoCategories.setVisibility(View.INVISIBLE);

        btnAddCategory = findViewById(R.id.btnAddCategory);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewCategoryDialog();
            }
        });

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        getCategoryCount(user, btnAddExpense);

        context = this;

        // get reference to the RecyclerView categoryList
        categoryList = findViewById(R.id.categoryList);
        getCategoryList();

        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalysisActivity.class);
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
    }

    public void getCategoryList() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        categoryService = ApiUtils.getCategoryService();

        categoryService.getCategoriesCount(user.getToken(), user.getId()).enqueue((new Callback<NumberResponse>() {
            @Override
            public void onResponse(Call<NumberResponse> call, Response<NumberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get the count
                    String countString = response.body().getCategories();
                    count = Integer.parseInt(countString);

                    if (count > 1) {
                        // execute the call. send the user token when sending the query
                        categoryService.getListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<List<Category>>() {
                            @Override
                            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                                if (response.isSuccessful()) {
                                    categories = response.body();

                                    for(int i=0; i<categories.size(); i++) {
                                        Category currCategory = categories.get(i);

                                        if(currCategory.getCategoryName().equalsIgnoreCase("Deleted Category"))
                                            categories.remove(currCategory);
                                    }

                                    // initialize the adapter with the listener
                                    CategoryAdapter adapter = new CategoryAdapter(context, categories, CategoriesActivity.this);
                                    // Set adapter to the RecyclerView
                                    categoryList.setAdapter(adapter);
                                    // set layout to recycler view
                                    categoryList.setLayoutManager(new LinearLayoutManager(context));

                                    // add separator between item in the list
                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryList.getContext(),
                                            DividerItemDecoration.VERTICAL);
                                    categoryList.addItemDecoration(dividerItemDecoration);

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

                        tvNoCategories.setVisibility(View.VISIBLE);

//                        // execute the call. send the user token when sending the query
//                        categoryService.getSingleListCategoryById(user.getToken(), user.getId()).enqueue(new Callback<Category>() {
//                            @Override
//                            public void onResponse(Call<Category> call, Response<Category> response) {
//                                if (response.isSuccessful()) {
//                                    // for debug purpose
//                                    Log.d("MyApp1:", "Response: " + response.raw().toString());
//
//                                    Category category = response.body();
//                                    // Handle single object scenario
//                                    Category singleCategory = (Category) category;
//                                    // Process the single Cart object
//                                    categories.add(singleCategory);
//
//                                    // initialize the adapter with the listener
//                                    CategoryAdapter adapter = new CategoryAdapter(context, categories, CategoriesActivity.this);
//                                    // Set adapter to the RecyclerView
//                                    categoryList.setAdapter(adapter);
//                                    // set layout to recycler view
//                                    categoryList.setLayoutManager(new LinearLayoutManager(context));
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Category> call, Throwable t) {
//                                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
//                                Log.e("MyApp2:", t.getMessage());
//                            }
//                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberResponse> call, Throwable t) {
                // Handle failure
                Log.e("MyApp3:", "Failure: " + t.getMessage());
            }
        }));
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
        final Dialog dialog = new Dialog(CategoriesActivity.this);
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
        final Dialog dialog = new Dialog(CategoriesActivity.this);
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

    @Override
    public void onCategoryButtonClick(Category category) {
        // Handle the button click event here
        showEditCategoryDialog(category);
    }

    // Other methods in CategoriesActivity

    void showNewCategoryDialog() {
        final Dialog dialog = new Dialog(CategoriesActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_new_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Initializing the views of the dialog.
        EditText edtNewCategoryName = dialog.findViewById(R.id.edtNewCategoryName);
        Button btnAddNewCategory = dialog.findViewById(R.id.btnAddNewCategory);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategoryName = edtNewCategoryName.getText().toString();

                Category c = new Category(0, newCategoryName, user.getId());

                if(count < 20) {
                    if(isNameUsed(categories, c.getCategoryName())) {
                        displayToast("The category's name is already in use");
                    } else {
                        addNewCategory(user, c);
                    }
                } else {
                    displayToast("You have reached the maximum number of category allowed");
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static boolean isNameUsed(List<Category> categories, String nameToCheck) {
        for (Category category : categories) {
            if (category.getCategoryName().equalsIgnoreCase(nameToCheck)) {
                return true;
            }
        }
        return false;
    }

    void addNewCategory(User user, Category c) {

        Call<Category> call = categoryService.newCategory(user.getToken(), c);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayToast("Invalid session. Please re-login");

                //category added successfully
                Category addedCategory = response.body();
                if (addedCategory != null) {
                    //display message
                    Toast.makeText(context,
                            addedCategory.getCategoryName() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    getCategoryList();
                } else {
                    displayToast("Add new category failed.");
                }

            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                displayToast("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());

            }
        });
    }

    void showEditCategoryDialog(Category category) {
        final Dialog dialog = new Dialog(CategoriesActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_edit_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Category c = category;

        //Initializing the views of the dialog.
        EditText edtSelectedCategoryName = (EditText) dialog.findViewById(R.id.edtSelectedCategoryName);
        Button btnEditSelectedCategory = dialog.findViewById(R.id.btnEditSelectedCategory);
        Button btnDeleteSelectedCategory = dialog.findViewById(R.id.btnDeleteSelectedCategory);

        edtSelectedCategoryName.setText(c.getCategoryName());

        //get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        getDeletedCategoryId();

        btnEditSelectedCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedCategoryName = edtSelectedCategoryName.getText().toString();

                Call<Category> call = categoryService.updateCategory(user.getToken(),c.getCategoryId(), editedCategoryName);

                call.enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(Call<Category> call, Response<Category> response) {
                        // for debug purpose
                        Log.d("MyApp:", "Response: " + response.raw().toString());

                        // invalid session?
                        if (response.code() == 401)
                            displayToast("Invalid session. Please re-login");

                        //category edited successfully
                        Category editedCategory = response.body();
                        if (editedCategory != null) {
                            //display message
                            Toast.makeText(context,
                                    "Category updated successfully.",
                                    Toast.LENGTH_LONG).show();

                            getCategoryList();
                        } else {
                            displayToast("Edit category failed.");
                        }

                    }

                    @Override
                    public void onFailure(Call<Category> call, Throwable t) {
                        displayToast("Error [" + t.getMessage() + "]");
                        // for debug purpose
                        Log.d("MyApp:", "Error: " + t.getCause().getMessage());

                    }
                });

                dialog.dismiss();
            }
        });

        btnDeleteSelectedCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteConfirmation(category);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void showDeleteConfirmation(Category category) {
        final Dialog dialog = new Dialog(CategoriesActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Category c = category;

        //Initializing the views of the dialog.
        Button btnConfirmYes = dialog.findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = dialog.findViewById(R.id.btnConfirmNo);

        //get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        getDeletedCategoryId();

        btnConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferToDeletedCategory(getDeletedCategoryId(), c.getCategoryId());
                dialog.dismiss();

            }
        });

        btnConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void transferToDeletedCategory(int categoryIdNew, int categoryIdOld) {
        //get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        Call<ResponseBody> call = categoryService.transferToDeletedCategory(user.getToken(),categoryIdNew, categoryIdOld);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());
                deleteCategory(categoryIdOld);

                // invalid session?
                if (response.code() == 401)
                    displayToast("Invalid session. Please re-login");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayToast("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());

            }
        });
    }

    public void deleteCategory(int categoryIdOld) {
        //get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        Call<DeleteResponse> call = categoryService.deleteCategory(user.getToken(), categoryIdOld);

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    displayToast("Category successfully deleted");
                    // update data in list view
                    getCategoryList();
                } else {
                    displayToast("Category failed to delete");
                    Log.e("MyApp:", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayToast("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public int getDeletedCategoryId() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // execute the call. send the user token when sending the query
        categoryService.getDeletedCategoryId(user.getToken(), user.getId()).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    // for debug purpose
                    Log.d("MyApp:", "Response: " + response.raw().toString());

                    Category category = response.body();
                    // Handle single object scenario
                    Category deletedCategory = (Category) category;
                    deletedCategoryId = deletedCategory.getCategoryId();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.getMessage());
            }
        });

        return deletedCategoryId;
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
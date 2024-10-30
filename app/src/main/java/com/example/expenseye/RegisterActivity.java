package com.example.expenseye;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expenseye.model.Category;
import com.example.expenseye.model.ErrorResponse;
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.User;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.CategoryService;
import com.example.expenseye.remote.UserService;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUserEmail;
    private EditText edtUserPassword;
    private Context context;
    private CheckBox cbShowPassword;
    private Button btnBackLogin;

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // store context
        context = this;

        // get view objects references
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        cbShowPassword = findViewById(R.id.cbShowPassword);
        btnBackLogin = findViewById(R.id.btnBackLogin);

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    edtUserPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Hide password
                    edtUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Move the cursor to the end of the text
                edtUserPassword.setSelection(edtUserPassword.length());
            }
        });
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // An item is selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos).
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback.
        }
    }

    /**
     * Called when Add User button is clicked
     * @param v
     */
    public void addUser(View v) {
        // get values in form
        String email = edtUserEmail.getText().toString();
        String password = edtUserPassword.getText().toString();

        // send request to add new user to the REST API
        UserService userService = ApiUtils.getUserService();
        Call<User> call = userService.register(email, password);

        //execute
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                // for debug purpose
                Log.d("MyApp1:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayToast("Invalid session. Please re-login");

                // user added successfully?
                User addedUser = response.body();
                if (addedUser != null) {
                    int id = addedUser.getId();
                    // display message
                    Toast.makeText(context,
                            addedUser.getEmail() + " added successfully.",
                            Toast.LENGTH_LONG).show();
                    doLogin(email, password);
                } else {
                    displayToast("Add New User failed.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp2:", "Error: " + t.getCause().getMessage());
            }
        });
    }

    private void doLogin(String email, String password) {
        UserService userService = ApiUtils.getUserService();
        Call call = userService.login(email, password);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {

                // received reply from REST API
                if (response.isSuccessful()) {
                    // parse response to POJO
                    User user = (User) response.body();
                    if (user.getToken() != null) {
                        // store value in Shared Preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        // forward user to MainActivity
                        addDeletedCategory();

                    }
                }
                else if (response.errorBody() != null){
                    // parse response to POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ErrorResponse e = new Gson().fromJson( errorResp, ErrorResponse.class);
                    //displayToast(e.getError().getMessage());
                    Log.d("MyApp3:", "Response: " + e.getError().getMessage());
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
            }

        });
    }

    void addDeletedCategory() {
        CategoryService categoryService = ApiUtils.getCategoryService();
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        Category c = new Category(0, "Deleted Category", user.getId());

        Call<Category> call = categoryService.newCategory(user.getToken(), c);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                // for debug purpose
                Log.d("MyApp4:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayToast("Invalid session. Please re-login");

                //category added successfully
                Category addedCategory = response.body();
                if (addedCategory != null) {
                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayToast("Add new category failed.");
                }

            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                displayToast("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp5:", "Error: " + t.getCause().getMessage());

            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayToast(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
package com.example.expenseye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// do not copy these imports. Use wizard to import these classes because your package name might be different
import com.example.expenseye.model.SharedPrefManager;
import com.example.expenseye.model.User;
import com.example.expenseye.model.ErrorResponse;
import com.example.expenseye.remote.ApiUtils;
import com.example.expenseye.remote.UserService;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtPassword;
    Button btnLogin;
    CheckBox cbShowPassword;
    TextView txtRegister;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // if the user is already logged in we will directly start the main activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();// stop this LoginActivity
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        // get references to form elements
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.textRegister);
        cbShowPassword  = findViewById(R.id.cbShowPassword);

        // get UserService instance
        userService = ApiUtils.getUserService();

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Hide password
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Move the cursor to the end of the text
                edtPassword.setSelection(edtPassword.length());
            }
        });

        // set onClick action to btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get username and password entered by user
                String username = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                // validate form, make sure it is not empty
                if (validateLogin(username, password)) {
                    // do login
                    doLogin(username, password);
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    /**
     * Validate value of username and password entered. Client side validation.
     * @param email
     * @param password
     * @return
     */
    private boolean validateLogin(String email, String password) {
        if (email == null || email.trim().length() == 0) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Call REST API to login
     * @param email
     * @param password
     */
    private void doLogin(String email, String password) {
        Call call = userService.login(email, password);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {

                // received reply from REST API
                if (response.isSuccessful()) {
                    // parse response to POJO
                    User user = (User) response.body();
                    if (user.getToken() != null) {
                        // successful login. server replies a token value
                        displayToast("Login successful");

                        // store value in Shared Preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        // forward user to MainActivity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

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
                    displayToast("Invalid Credentials");
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
            }

        });
    }

    /**
     * Display a Toast message
     * @param message
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
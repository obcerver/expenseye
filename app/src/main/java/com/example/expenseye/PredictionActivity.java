package com.example.expenseye;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.expenseye.model.OCRResponse;
import com.example.expenseye.remote.OCRService;
import com.example.expenseye.remote.ApiClient;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionActivity extends AppCompatActivity {
    ImageView imgPreview;
    String cameraPermission[];
    String storagePermission[];
    String value;
    Button btnRetake;
    Button btnNext;
    Uri imageUri;
    TextView tvPrediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_prediction);

        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("imageUri");

        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            getPrediction(this, imageUri);
        } else {
            Toast.makeText(this, "Image URI not found", Toast.LENGTH_SHORT).show();
        }

        imgPreview = findViewById(R.id.imgPreview);
        btnRetake = findViewById(R.id.btnRetake);
        btnNext = findViewById(R.id.btnNext);
        tvPrediction = findViewById(R.id.tvPrediction);

        imgPreview.setImageURI(imageUri);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewExpenseActivity.class);
                intent.putExtra("value", value);
                intent.putExtra("source", "scan");
                startActivity(intent);
            }
        });
    }

    private String extractDouble(String prediction) {
        // Use regex to find the double number in the string
        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(prediction);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private void getPrediction(Context context, Uri fileUri) {
        File imageFile = new File(fileUri.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        OCRService service = ApiClient.getClient().create(OCRService.class);
        Call<OCRResponse> call = service.getPrediction(body);
        call.enqueue(new Callback<OCRResponse>() {
            @Override
            public void onResponse(Call<OCRResponse> call, Response<OCRResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        OCRResponse ocrResponse = response.body();
                        value = extractDouble(ocrResponse.getPrediction());
                        tvPrediction.setText("Receipt Value: RM " + value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tvPrediction.setText("There is an error recognizing the value, please retake the picture");
                }
            }

            @Override
            public void onFailure(Call<OCRResponse> call, Throwable t) {
                Log.e("MyApp" ,"Network error: " + t.getMessage());
            }
        });
    }
}


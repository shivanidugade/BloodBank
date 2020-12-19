package com.rishabh.bloodbank.Activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rishabh.bloodbank.R;
import com.rishabh.bloodbank.Utils.Endpoints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MakeRequestActivity extends AppCompatActivity {

  EditText messageText;
  TextView chooseImageText;
  ImageView postImage;
  Button submit_button;
  Uri imageUri;
  Bitmap bitmap;
  String encodedImage;

  ProgressDialog progressDialog;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_request);

    messageText = findViewById(R.id.message);
    chooseImageText = findViewById(R.id.choose_text);
    postImage = findViewById(R.id.post_image);
    submit_button = findViewById(R.id.submit_button);

    final String number = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getString("number", "12345");

    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Please Wait...");

    chooseImageText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        Dexter.withActivity(MakeRequestActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image"),1);

                  }

                  @Override
                  public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {}

                  @Override
                  public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                  }
                }).check();

      }
    });

    submit_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.upload_request, new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            Toast.makeText(MakeRequestActivity.this, response, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MakeRequestActivity.this, MainActivity.class));
            progressDialog.dismiss();
            finish();

          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(MakeRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
          }
        }){
          @Override
          protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("image", encodedImage);
            params.put("number", number);
            params.put("message", messageText.getText().toString());
            return params;
          }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MakeRequestActivity.this);
        requestQueue.add(request);

      }
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
      Uri filePath = data.getData();
      try {
        InputStream inputStream = getContentResolver().openInputStream(filePath);
        bitmap = BitmapFactory.decodeStream(inputStream);
        postImage.setImageBitmap(bitmap);

        imageStore(bitmap);

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void imageStore(Bitmap bitmap) {

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

    byte[] imageBytes = stream.toByteArray();

    encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

  }
}

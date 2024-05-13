package com.rnimageuploadmodule;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.content.Context;
import android.os.Environment;

import android.util.Log;

@ReactModule(name = RnImageUploadModuleModule.NAME)
public class RnImageUploadModuleModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RnImageUploadModule";

  private static final String API_URL = "https://getweys-scan-bot-be-production.up.railway.app/api/driver-license-scan";
  private String apiKey;


  public RnImageUploadModuleModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void configure(ReadableMap config, Promise promise) {
    if (config.hasKey("apiKey")) {
      apiKey = config.getString("apiKey");
      promise.resolve("Configured successfully");
    } else {
      promise.reject("INVALID_CONFIG", "API Key is missing");
      Log.e(NAME, "API Key is missing");
    }
  }

  @ReactMethod
  public void scanDriverLicense(String base64Image, Promise promise) {
    OkHttpClient client = new OkHttpClient();

    if (base64Image == null || base64Image.isEmpty()) {
      promise.reject("INVALID_ARGUMENT", "Image is null or empty");
      return;
    }

    try {
     
      byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);

      String currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

      File tempImageFile = File.createTempFile("img_" + currentDateTime, ".jpg", getReactApplicationContext().getCacheDir());

      FileOutputStream fos = new FileOutputStream(tempImageFile);

      fos.write(imageBytes);
      fos.flush();
      fos.close();

      RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", tempImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), tempImageFile))
        .build();

      Request request = new Request.Builder()
        .url(API_URL)
        .addHeader("x-api-key", apiKey)
        .post(requestBody)
        .build();
      
      Response response = client.newCall(request).execute();
      String responseBody = response.body().string();
      JSONObject responseJson = new JSONObject(responseBody);
        
        if (response.isSuccessful()) {
          promise.resolve(responseJson);
        } else {
          promise.reject("UPLOAD_FAILED", responseBody);
        }

        tempImageFile.delete();
    } catch (IOException e) {
        promise.reject("UPLOAD_FAILED", "Failed to upload image due to an exception: " + e.getMessage());
    }
  }
}
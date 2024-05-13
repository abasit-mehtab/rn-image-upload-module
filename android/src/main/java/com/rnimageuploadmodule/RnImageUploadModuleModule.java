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

  private static final String API_URL = "https://picayune-harvest-canoe.glitch.me/upload";

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
  public void uploadBase64Image(String base64Image, Promise promise) {
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
        .post(requestBody)
        .build();
      
      Response response = client.newCall(request).execute();
        
        if (response.isSuccessful()) {
          String responseBody = response.body().string();
          promise.resolve("Image uploaded successfully: " + responseBody);
        } else {
          promise.reject("UPLOAD_FAILED", "Failed to upload image. Server returned unsuccessful response.");
        }

        tempImageFile.delete();
    } catch (IOException e) {
        promise.reject("UPLOAD_FAILED", "Failed to upload image due to an exception: " + e.getMessage());
    }
  }
}
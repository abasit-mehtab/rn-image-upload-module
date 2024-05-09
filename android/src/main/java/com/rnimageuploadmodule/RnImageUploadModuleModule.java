package com.rnimageuploadmodule;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

@ReactModule(name = RnImageUploadModuleModule.NAME)
public class RnImageUploadModuleModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RnImageUploadModule";

  private static final String API_URL = "http://127.0.0.1:3000/upload";

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
  public void uploadImage(String imagePath, String url, String apiKey, Promise promise) {
    OkHttpClient client = new OkHttpClient();

    File imageFile = new File(imageFile);

    if (!imageFile.exists()) {
      promise.reject("IMAGE_NOT_FOUND", "Image file not found at specified path");
      return;
    }

    RequestBody requestBody = new MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile))
      .build();

    Request request = new Request.Builder()
      .url(API_URL)
      .post(requestBody)
      .addHeader("x-api-key", apiKey)
      .build();

    try {
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        promise.resolve("Image uploaded successfully.");
      } else {
        promise.reject("UPLOAD_FAILED", "Failed to upload image. Server returned unsuccessful response.");
      }
    } catch (IOException e) {
        promise.reject("UPLOAD_FAILED", "Failed to upload image due to an exception: " + e.getMessage());
    }
  }
}
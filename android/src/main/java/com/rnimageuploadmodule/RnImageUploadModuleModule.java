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

  public void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.getParentFile().exists()) {
      destFile.getParentFile().mkdirs();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } catch (IOException e) {
      Log.e("COPY_FILE_ERROR", "Error copying file: " + e.getMessage());
      throw e;
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void uploadImage(ReadableMap imageObject, Promise promise) {
    OkHttpClient client = new OkHttpClient();

    if (imageObject == null) {
      promise.reject("INVALID_ARGUMENT", "Image object is null");
      return;
    }

    try {
      String imagePath = imageObject.getString("originalPath");
      File imageFile = new File(imagePath);

      Context context = getReactApplicationContext();

      File cacheDir = context.getCacheDir();

      Log.d("CACHE_DIR", cacheDir.getAbsolutePath());

      String newFilePath = cacheDir.getAbsolutePath() + File.separator + imageFile.getName();

      Log.d("newFilePath", newFilePath);

      File newImageFile = new File(newFilePath);

      Log.d("imagePath", imagePath);

      Log.d("UPLOAD_IMAGE_BEFORE", "Copying file...");

      copyFile(imageFile, newImageFile);

      Log.d("UPLOAD_IMAGE_AFTER", "File copied successfully");

      RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", newImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), newImageFile))
        .build();

      Request request = new Request.Builder()
        .url(API_URL)
        .post(requestBody)
        .build();
      
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
package com.example.vadim.lesson5network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.drm.ProcessedData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements DownloadCompleteListener {

  ProgressDialog mProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if(isNetworkConnected()) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setMessage("Please wait...");
      mProgressDialog.setCancelable(false);
      mProgressDialog.show();
      startDownload();
    } else {
      Log.v("","");
    }
  }

  private void startDownload() {
//    new DownloadRepoTask(this).execute("https://api.github.com/repositories");
    makeRequestWithOkHttp("https://api.github.com/repositories");
  }

  private boolean isNetworkConnected() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  private boolean isWifiConnected() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null
            && networkInfo.isConnected()
            && (ConnectivityManager.TYPE_WIFI == networkInfo.getType());
  }

  @Override
  public void downloadComplete(ArrayList<Repository> repository) {
    if (mProgressDialog != null) {
      mProgressDialog.hide();
      String mArray[] = null;
      // Transfer Repository object to String array by a small hack
      mArray = Arrays.toString(repository.toArray()).split(",");
      ArrayAdapter<String> githubAdapter =
              new ArrayAdapter<String>(this, R.layout.repo_item, R.id.textview, mArray);
      ListView repoList = (ListView) findViewById(R.id.listView);
      repoList.setAdapter(githubAdapter);
    }
  }

  private void makeRequestWithOkHttp(String url) {
    OkHttpClient client = new OkHttpClient();
    okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

    client.newCall(request).enqueue(new okhttp3.Callback() {
      @Override
      public void onFailure(okhttp3.Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(okhttp3.Call call, okhttp3.Response response)
              throws IOException {
        final String result = response.body().string();

        MainActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              downloadComplete(Util.retrieveRepositoriesFromResponse(result));  // 5
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
      }
    });
  }
}

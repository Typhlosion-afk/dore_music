package com.example.doremusic.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.doremusic.R;
import com.example.doremusic.model.Song;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.R)
public class BeginActivity extends AppCompatActivity {

    public static ArrayList<Song> listSong = new ArrayList<>();

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);


        userPermissionRequest();

    }

    @Override
    protected void onStart() {
//        BackgroundRunner backgroundRunner = new BackgroundRunner();
//        backgroundRunner.execute();

        super.onStart();
    }

    private void userPermissionRequest() {
        if (ContextCompat.checkSelfPermission(BeginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            inPermissionAllow();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                inPermissionDenied();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                inPermissionAllow();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inPermissionDenied() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("I need read file permission to continue this app")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BeginActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    private void inPermissionAllow() {
        BackgroundRunner backgroundRunner = new BackgroundRunner();
        backgroundRunner.execute();

    }

    private void startMainActivity() {
        Log.d("aaaa", "startMainActivity: " + listSong.size());
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("Recycle")
    private void getListSong() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int sum = 0;
            do {
                String uriSong = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (uriSong.substring(uriSong.lastIndexOf(".")).equals(".mp3")) {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                    Song s = new Song(uriSong, title, author, Integer.parseInt(duration));
                    listSong.add(s);
                    sum++;
                }

            } while (cursor.moveToNext());
            int i = 1;
            Log.d("====>", "getListSong: " + sum);
            Log.d("====>", "getListSong: " + listSong.get(i).getName());
            Log.d("====>", "getListSong: " + listSong.get(i).getPath());
            Log.d("====>", "getListSong: " + listSong.get(i).getAuthor());
            Log.d("====>", "getListSong: " + listSong.get(i).getTime());

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BackgroundRunner extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getListSong();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startMainActivity();

            super.onPostExecute(aVoid);
        }
    }
}
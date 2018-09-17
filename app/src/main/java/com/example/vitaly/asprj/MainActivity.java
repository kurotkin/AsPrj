package com.example.vitaly.asprj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private DownloadImageTask mDownloadImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String url = "https://i0.kym-cdn.com/entries/icons/mobile/000/013/564/doge.jpg";
        mDownloadImageTask = new DownloadImageTask(MainActivity.this);

        Button startBtn = findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadImageTask.execute(url);
            }

        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadImageTask.cancel(true);
            }
        });

    }

    // Доступ из слабой ссылки
    ImageView getImageView() {
        return findViewById(R.id.imageView);
    }

    ProgressBar getProgressBar() {
        return findViewById(R.id.progressBar);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<MainActivity> mActivityWeakReference;                     // Слабая ссылка, передаем активити

        private DownloadImageTask(MainActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null)
                activity.getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmap(strings[0]);
        }

        private Bitmap getBitmap(String url) {
            try {
                TimeUnit.SECONDS.sleep(2);
                if (isCancelled()) {
                    return null;
                }
                InputStream is = (InputStream) new URL(url).getContent();
                Bitmap d = BitmapFactory.decodeStream(is);
                is.close();
                return d;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null) {
                activity.getImageView().setImageBitmap(bitmap);
                activity.getProgressBar().setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            MainActivity mainActivity = mActivityWeakReference.get();
            if (mainActivity != null) {
                mainActivity.getProgressBar().setVisibility(View.INVISIBLE);
                Toast.makeText(mainActivity, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

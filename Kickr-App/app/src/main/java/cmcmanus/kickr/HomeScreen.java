package cmcmanus.kickr;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HomeScreen extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    Intent intent = new Intent(getApplicationContext(), Fixtures.class);
                    startActivity(intent);
                    finish();  // Finish splash activity since it is no more needed
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 3000);  // 3 seconds
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}
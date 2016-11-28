package co.experimentality.globalsmartrestaurants;

/**
 * Created by juanjo on 11/16/16.
 */

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.estimote.sdk.SystemRequirementsChecker;

public class SplashScreenActivity extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_screen);

    }

    @Override
    protected void onResume() {
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {

            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    // Start the next activity
                    Intent mainIntent = new Intent().setClass(
                            SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    // Close the activity so the user won't able to go back this
                    // activity pressing Back button
                    finish();
                }
            };

            // Simulate a long loading process on application startup.
            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
        }
        super.onResume();
    }
}

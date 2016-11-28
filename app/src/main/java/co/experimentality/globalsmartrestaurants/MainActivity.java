package co.experimentality.globalsmartrestaurants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("LANGUAGE", Locale.getDefault().getLanguage());
        Intent i = new Intent(this, RestaurantActivity.class);
        startActivity(i);
        finish();
        ((Toolbar) findViewById(R.id.toolbarFind)).setTitle("Find");
    }
}

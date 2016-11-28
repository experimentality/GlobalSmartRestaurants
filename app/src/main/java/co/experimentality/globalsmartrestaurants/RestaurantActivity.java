package co.experimentality.globalsmartrestaurants;

import android.app.Application;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import co.experimentality.globalsmartrestaurants.model.User;
import co.experimentality.globalsmartrestaurants.service.AmazonApiGatewayService;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;

    private final static String LOG_TAG = Application.class.getSimpleName();

    private String currentBeaconId = "";
    private String currentDistance = "";

    private ImageView ivRestaurantPhoto;
    private CircleImageView ivMainDishPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivRestaurantPhoto = (ImageView) findViewById(R.id.main_backdrop);
        ivMainDishPhoto = (CircleImageView) findViewById(R.id.ivPhotoRecommendedDishContentRestaurantActivity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        beaconManager = new BeaconManager(this);
        region = new Region("Ranged Region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    String s = nearestBeacon.getProximityUUID().toString() + ":" + nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor();
                    double distance = Utils.computeAccuracy(nearestBeacon);
                    if (!currentDistance.equals(distance)) {
                        TextView tv = (TextView) findViewById(R.id.tvDistanceContentRestaurantActivity);
                        tv.setText(getString(R.string.at) + " " + String.format("%.2f", distance) + " " + getString(R.string.meters));
                    }
                    Log.i("NEAREST BEACON", s);
                    if (!currentBeaconId.equals(s)) {
                        Log.e("NEAREST BEACON", "Current Beacon Change");
                        currentBeaconId = s;
                        updateRestaurantInformation();
                    }

                }
            }
        });
    }

    private void updateRestaurantInformation() {
        AmazonApiGatewayService awsService = AmazonApiGatewayService.retrofit.create(AmazonApiGatewayService.class);
        final Call<JsonObject> call = awsService.getRestaurant(currentBeaconId, Locale.getDefault().getLanguage());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray ja = response.body().getAsJsonArray("body");
                JsonObject jo;
                if (ja.size() > 0) {
                    jo = ja.get(0).getAsJsonObject();
                    Log.i("RESPONSE", jo.toString());

                    ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(jo.get("name").getAsString());
                    Picasso.with(RestaurantActivity.this).load(jo.get("picture").getAsString()).into(ivRestaurantPhoto);
                    Picasso.with(RestaurantActivity.this).load(jo.get("menu_picture").getAsString()).into(ivMainDishPhoto);
                    ((TextView) findViewById(R.id.tvRestaurantDescriptionContentRestaurantActivity)).setText(jo.get("description").getAsString());
                    ((TextView) findViewById(R.id.tvScheduleContentRestaurantActivity)).setText(jo.get("schedule").getAsString());
                    ((TextView) findViewById(R.id.tvScheduleContentRestaurantActivity)).setText(jo.get("schedule").getAsString());
                    ((TextView) findViewById(R.id.tvRecommendedNameContentRestaurantActivity)).setText(jo.get("menu").getAsString());
                    ((TextView) findViewById(R.id.tvRecommendedPriceContentRestaurantActivity)).setText(jo.get("price").getAsString() + getString(R.string.dollars));
                    String restId = jo.get("id").getAsString();
                    restId = restId.substring(0, restId.length() - 2);
                    sendVisit(restId + "es", "777");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void sendVisit(String restId, String userId) {
        Log.e("RESTAURANT", restId);
        AmazonApiGatewayService awsService = AmazonApiGatewayService.retrofit.create(AmazonApiGatewayService.class);
        final Call<JsonObject> call = awsService.sendVisit(restId, new User(userId));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });

    }

    @Override
    protected void onResume() {
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }
}

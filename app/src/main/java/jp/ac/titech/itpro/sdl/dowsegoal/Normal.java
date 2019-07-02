package jp.ac.titech.itpro.sdl.dowsegoal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

import static android.os.SystemClock.sleep;
import static com.google.android.gms.common.api.GoogleApiClient.*;

public class Normal extends AppCompatActivity implements
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, SensorEventListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final static int REQ_PERMISSIONS = 1234;

    private enum State {
        STOPPED,
        REQUESTING,
        STARTED
    }
    private State state = State.STOPPED;

    private GoogleApiClient apiClient;
    private GoogleMap map;
    private FusedLocationProviderClient locationClient;
    private LocationRequest request;
    private LocationCallback callback;
    /** スレッドUI操作用ハンドラ */
    private Handler mHandler = new Handler();
    private RotationView rotationView;
    private SensorManager manager;
    private Sensor gyroscope;
    private double now_omegaZ = 0;
    private double temp_timestamp = 0.0;
    Random random = new Random();
    Random random2 = new Random();
    int random_theta = random.nextInt(360);
    double random_dist = (500.0 + random2.nextInt(500))/(double) 1000;
    private LatLng now_LatLng;
    int start_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        Intent i = getIntent();


        /*
        現在の位置情報を取得し保存
        そこから半径distの範囲を指定しランダムにゴールを設置
        distの1/20の範囲内にゴールを捉えればいいようにする
         */

        TextView check = findViewById(R.id.check);
        check.setVisibility(View.GONE);
        TextView info_comment = findViewById(R.id.info_comment);
        info_comment.setText("Decided Goal!\n↓show the direction you are sure to want");
        Button hint_button;
        hint_button = findViewById(R.id.hint_button);
        hint_button.setVisibility(View.GONE);
        Button goal_button;
        goal_button = findViewById(R.id.goal_button);
        goal_button.setVisibility(View.GONE);

        decideGoal();

        //check.setVisibility(View.GONE);

        //goalへの方向を3秒表示
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: ここで処理を実行する
                //textの変更と方向指示器の削除
                TextView info_com = findViewById(R.id.info_comment);
                info_com.setText("Let's Find the goal!!");
                RotationView rv = findViewById(R.id.rotation_view);
                rv.setVisibility(View.GONE);
                Button hint_button;
                hint_button = findViewById(R.id.hint_button);
                hint_button.setVisibility(View.VISIBLE);
                start_count++;
                CheckIn.start = System.currentTimeMillis();
            }
        }, 3000);

        //hint button
        hint_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                start_count=0;
                RotationView rv = findViewById(R.id.rotation_view);
                rv.setVisibility(View.VISIBLE);
                Button hint_button;
                hint_button = findViewById(R.id.hint_button);
                hint_button.setVisibility(View.GONE);
                CheckIn.count_click++;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RotationView rv = findViewById(R.id.rotation_view);
                        rv.setVisibility(View.GONE);
                        Button hint_button;
                        hint_button = findViewById(R.id.hint_button);
                        hint_button.setVisibility(View.VISIBLE);
                        start_count++;
                    }
                }, 3000);
            }
        });

        //goal button
        goal_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick - Goal");
                CheckIn.end = System.currentTimeMillis();
                Intent intent = new Intent(Normal.this, Goal.class);
                startActivity(intent);
            }
        });

        rotationView = findViewById(R.id.rotation_view);
        rotationView.setDirection(Math.toRadians(random_theta));
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) {
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show();
        }

        apiClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        request = new LocationRequest();
        request.setInterval(500L);
        request.setFastestInterval(250L);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult");
                if (locationResult == null) {
                    Log.d(TAG, "onLocationResult: locationResult == null");
                    return;
                }
                Location location = locationResult.getLastLocation();
                now_LatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //確認用
                TextView check = findViewById(R.id.check);
                //check.setVisibility(View.VISIBLE);
                check.setText(getString(R.string.info_format, now_LatLng.latitude, now_LatLng.longitude));

                //Goalに近づいたらボタンを表示、バイブレーション
                if(now_LatLng != null) {
                    if (calcDistanceToGoal(now_LatLng, CheckIn.Goal_LatLng) < (CheckIn.dist * CheckIn.dist / 400)) { //もとは400
                        if(start_count>0) {
                            Button hint;
                            hint = findViewById(R.id.hint_button);
                            hint.setVisibility(View.GONE);
                            Button goal;
                            goal = findViewById(R.id.goal_button);
                            goal.setVisibility(View.VISIBLE);
                            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
                        }
                    }else{
                        if(start_count > 0) {
                            Button hint;
                            hint = findViewById(R.id.hint_button);
                            hint.setVisibility(View.VISIBLE);
                            Button goal;
                            goal = findViewById(R.id.goal_button);
                            goal.setVisibility(View.GONE);
                        }
                    }
                }
                if (map == null) {
                    Log.d(TAG, "onLocationResult: map == null");
                    return;
                }
            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        apiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        map.moveCamera(CameraUpdateFactory.zoomTo(15f));
        this.map = map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (state != State.STARTED && apiClient.isConnected()) {
            startLocationUpdate(true);
        } else {
            state = State.REQUESTING;
        }
        manager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        apiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (state == State.STARTED) {
            stopLocationUpdate();
        }
        manager.unregisterListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (state == State.REQUESTING) {
            startLocationUpdate(true);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspented");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    private void startLocationUpdate(boolean reqPermission) {
        Log.d(TAG, "startLocationUpdate");
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (reqPermission) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQ_PERMISSIONS);
                } else {
                    String text = getString(R.string.toast_requires_permission_format, permission);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        locationClient.requestLocationUpdates(request, callback, null);
        state = State.STARTED;
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grants) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (reqCode) {
            case REQ_PERMISSIONS:
                startLocationUpdate(false);
                break;
        }
    }

    private void stopLocationUpdate() {
        Log.d(TAG, "stopLocationUpdate");
        locationClient.removeLocationUpdates(callback);
        // state = State.STOPPED;
    }

    private void decideGoal(){
        double lat, lon;
        double one_lat = 6389.137/360;  //1度を簡易的に距離換算
        double one_lon = 6356.752314/360;   //1度を簡易的に距離換算
        //Goalの座標を設定
        lat = MainActivity.START_LatLng.latitude +(CheckIn.dist*random_dist*Math.cos(Math.toRadians(random_theta)))/one_lat;
        lon = MainActivity.START_LatLng.longitude + (CheckIn.dist*random_dist*Math.sin(Math.toRadians(random_theta)))/one_lon;
        //簡易デモ用
        //lat = MainActivity.START_LatLng.latitude;
        //lon = MainActivity.START_LatLng.longitude;
        CheckIn.Goal_LatLng = new LatLng(lat,lon);
    }

    private double calcDistanceToGoal(LatLng now, LatLng goal){
        double lat_dist,lon_dist;
        lat_dist = (now.latitude - goal.latitude)*6389.137/360;
        lon_dist = (now.longitude - goal.longitude)*6389.137/360;
        return lat_dist*lat_dist + lon_dist*lon_dist;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float omegaZ = event.values[2];  // z-axis angular velocity (rad/sec)
        // TODO: calculate right direction that cancels the rotation
        //元が角速度(rad/ns)なので*0.000000001して秒数を利用する
        //予定だったが区分求積法のスタイル的に0.0000000009の方がずれない...
        now_omegaZ += omegaZ * (event.timestamp - temp_timestamp) * 0.0000000009;
        rotationView.setDirection(now_omegaZ);
        temp_timestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }

}
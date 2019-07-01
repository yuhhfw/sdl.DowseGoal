package jp.ac.titech.itpro.sdl.dowsegoal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class CheckIn extends AppCompatActivity {
    private final static String DISTANCE = "Level of difficulty\nradius";
    public final static String DISCRIPTION = "This game is to find goal which is placed in a range you decided.\n\nThis is start Location↓";
    private final static String TAG = MainActivity.class.getSimpleName();
    public static double dist;
    public static LatLng Goal_LatLng;
    // 測定開始
    public static long start;
    // 測定終了
    public static long end;
    //hintを見た回数
    public static int count_click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_check);

        TextView info = findViewById(R.id.info);
        info.setText(getString(R.string.info_format, MainActivity.START_LatLng.latitude, MainActivity.START_LatLng.longitude));

        TextView discription = findViewById(R.id.discription);
        discription.setText(DISCRIPTION);

        TextView distance = findViewById(R.id.distance);
        distance.setText(DISTANCE);

        Button button1 = findViewById(R.id.input_easy1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - easy1");
                Intent intent = new Intent(CheckIn.this, Easy.class);
                dist = 0.1;
                startActivity(intent);
            }
        });
        Button button2 = findViewById(R.id.input_easy2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - easy2");
                Intent intent = new Intent(CheckIn.this, Easy.class);
                dist = 0.5;
                startActivity(intent);
            }
        });
        Button button3 = findViewById(R.id.input_easy3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - easy3");
                Intent intent = new Intent(CheckIn.this, Easy.class);
                dist = 1.0;
                startActivity(intent);
            }
        });
        Button button4 = findViewById(R.id.input_normal1);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - normal1");
                Intent intent = new Intent(CheckIn.this, Normal.class);
                dist = 0.1;
                startActivity(intent);
            }
        });
        Button button5 = findViewById(R.id.input_normal2);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - normal2");
                Intent intent = new Intent(CheckIn.this, Normal.class);
                dist = 0.5;
                startActivity(intent);
            }
        });
        Button button6 = findViewById(R.id.input_normal3);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - normal3");
                Intent intent = new Intent(CheckIn.this, Normal.class);
                dist = 1.0;
                startActivity(intent);
            }
        });
        Button button7 = findViewById(R.id.input_hard1);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - hard1");
                Intent intent = new Intent(CheckIn.this, Hard.class);
                dist = 0.1;
                startActivity(intent);
            }
        });
        Button button8 = findViewById(R.id.input_hard2);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - hard2");
                Intent intent = new Intent(CheckIn.this, Hard.class);
                dist = 0.5;
                startActivity(intent);
            }
        });
        Button button9 = findViewById(R.id.input_hard3);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - hard3");
                Intent intent = new Intent(CheckIn.this, Hard.class);
                dist = 1.0;
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
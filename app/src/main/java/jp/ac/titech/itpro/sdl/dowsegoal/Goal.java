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

public class Goal extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_goal);

            if (CheckIn.count_click == 1) {
                TextView click_on_hint = findViewById(R.id.click_on_hint);
                click_on_hint.setText(CheckIn.count_click + "click on hint button");
            }else if(CheckIn.count_click == 0){
                TextView click_on_hint = findViewById(R.id.click_on_hint);
                click_on_hint.setText("not click on hint button");
            }else{
                TextView click_on_hint = findViewById(R.id.click_on_hint);
                click_on_hint.setText(CheckIn.count_click + "clicks on hint button");
            }

        float goal_time = (CheckIn.end - CheckIn.start)/1000;
        TextView time = findViewById(R.id.time);
        time.setText("You took " + goal_time + "sec");

        TextView discription = findViewById(R.id.award);
        discription.setText("Goal!!!!!!!!");

        float score_time = goal_time + CheckIn.count_click*300*(float)CheckIn.dist;
        TextView score = findViewById(R.id.score);
        score.setText("Good!");
        if(score_time < CheckIn.dist*3000){
            score.setText("Nice!");
        }
        if(score_time < CheckIn.dist*600){
            score.setText("Congratulation!");
        }


        Button button1 = findViewById(R.id.back_HOME);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - HOME");
                Intent intent = new Intent(Goal.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
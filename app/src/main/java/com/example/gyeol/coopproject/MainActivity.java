package com.example.gyeol.coopproject;

/**
 * Created by Park Gyeol on 2018-10-12.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, Specific_Case.class);
                //startActivity(intent);
                Toast.makeText(getApplicationContext(), "1버튼이 클릭되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Specific_Case.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "2버튼이 클릭되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

    }
}

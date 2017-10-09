package com.ciu196.mobilecomputing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        TextView pianoStatusTextView = (TextView) findViewById(R.id.pianoStatusTextView);
        TextView pianoDetaildTextView = (TextView) findViewById(R.id.pianodetailedTextView);


         final Circle actionButton =  (Circle) findViewById(R.id.thirdRadius);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO STUFF HERE!!!!
                Toast.makeText(v.getContext(), "ActionClicked",Toast.LENGTH_LONG).show();
                actionButton.setColor("#d1172e");


            }
        });
    }
}

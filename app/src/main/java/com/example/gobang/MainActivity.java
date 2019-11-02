package com.example.gobang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gobang.MyPanel;
import com.example.gobang.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MyPanel panel;
    TextView mod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = (MyPanel) findViewById(R.id.MyPanel);
        mod=findViewById(R.id.mod);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart:
                panel.restart();
                break;
            case R.id.pvp:
                panel.changeMod(false);
                mod.setText("PVP");
                break;
            case R.id.computer:
                panel.changeMod(true);
                mod.setText("Computer");

        }
    }


}

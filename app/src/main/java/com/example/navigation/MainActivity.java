package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout tmapLayout = findViewById(R.id.tmapLayout);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey( "\tl7xxa5b961d8570f4cde98fa199aaa572587" );
        tmapLayout.addView( tMapView );
    }
}
package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private  boolean trackingMode = true;
    TMapView tMapView = null;
    TMapGpsManager tMapGps = null;
    private String Address;
    TMapPoint Destination_Point = null;
    private Context mContext = this;
    private boolean m_bTrackingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout tmapLayout = findViewById(R.id.tmapLayout);
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey( "\tl7xxa5b961d8570f4cde98fa199aaa572587" );
        tmapLayout.addView( tMapView );

        tMapView.setCompassMode(true);

        tMapView.setIconVisibility(true);

        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);


        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(5);
        tMapGps.setProvider((tMapGps.NETWORK_PROVIDER));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        tMapGps.OpenGps();
//
//        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        ImageButton searchButton = (ImageButton)findViewById(R.id.map_navigation_botton);
        searchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SearchDestination();
            }
        });

    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.map_navigation_botton   :   ClickDestination();  break;
//            //case R.id.btnSearchDestination  :   SearchDestination(); break;
////            case R.id.btnStartGuidance      :   StartGuidance();     break;
//        }
//    }

    TMapPoint Current_Point;
    double getCurrent_long;
    double getCurrent_lat;

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode)
        {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

            getCurrent_long = location.getLongitude();
            getCurrent_lat = location.getLatitude();

            Current_Point = new TMapPoint(getCurrent_lat, getCurrent_long);

            Log.d("getCurrent_lat : ", ""+getCurrent_lat);
            Log.d("getCurrent_long : ", ""+getCurrent_long);
        }
    }

    public void ClickDestination() {
        Toast.makeText(MainActivity.this, "원하시는 도착 지점을 터치한 후 길안내 시작버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList,
                                        ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

                TMapData tMapData = new TMapData();
                tMapData.convertGpsToAddress(tMapPoint.getLatitude(), tMapPoint.getLongitude(),
                        new TMapData.ConvertGPSToAddressListenerCallback() {
                            @Override
                            public void onConvertToGPSToAddress(String strAddress) {
                                Address = strAddress;
                                Log.d("선택한 위치의 주소는 ", strAddress);
                            }
                        });

                Toast.makeText(MainActivity.this, "선택하신 위치의 주소는 " + Address + " 입니다.", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList,
                                          ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                Destination_Point = tMapPoint;

                return false;
            }
        });

    }

    public void SearchDestination() {
        // 검색창에 입력받음
        m_bTrackingMode = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("POI 통합 검색");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strData = input.getText().toString();
                TMapData tMapData = new TMapData();

                tMapData.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        for(int i=0; i<poiItem.size(); i++){
                            TMapPOIItem item = poiItem.get(i);

                            Log.e("POI Name: ", item.getPOIName().toString());
                            Log.e(        "Address: ", item.getPOIAddress().replace("null", ""));
                            Log.e("Point: ", item.getPOIPoint().toString());

                            Address = item.getPOIAddress();
                            Destination_Point = item.getPOIPoint();

                        }
                    }
                });
//                tMapView.removeTMapPath();
////
//                setTrackingMode(m_bTrackingMode);
//
//                TMapPoint point1 = tMapView.getLocationPoint();
//                TMapPoint point2 = Destination_Point;
//
//                TMapData tmapdata = new TMapData();
//
//                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
//                    @Override
//                    public void onFindPathData(TMapPolyLine polyLine) {
//                        polyLine.setLineColor(Color.BLUE);
//                        tMapView.addTMapPath(polyLine);
//                    }
//                });
//
//                Bitmap start = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.poi_start);
//                Bitmap end = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.poi_end);
//                tMapView.setTMapPathIcon(start, end);
//
//                tMapView.zoomToTMapPoint(point1, point2);

            }
        });

        Toast.makeText(this, "입력하신 주소는 " + Address + " 입니다.", Toast.LENGTH_SHORT).show();
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
//
//    public void StartGuidance() {
//        tMapView.removeTMapPath();
//
//        tMapView.setTrackingMode(true);
//
//        TMapPoint point1 = tMapView.getLocationPoint();
//        TMapPoint point2 = Destination_Point;
//
//        TMapData tmapdata = new TMapData();
//
//        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
//            @Override
//            public void onFindPathData(TMapPolyLine polyLine) {
//                polyLine.setLineColor(Color.BLUE);
//                tMapView.addTMapPath(polyLine);
//            }
//        });
//
//        Bitmap start = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.poi_start);
//        Bitmap end = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.poi_end);
//        tMapView.setTMapPathIcon(start, end);
//
//        tMapView.zoomToTMapPoint(point1, point2);
//    }

    public void setTrackingMode(boolean m_bTrackingMode) {
        tMapView.setTrackingMode(m_bTrackingMode);
    }
}
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private boolean trackingMode = true;
    TMapView tMapView = null;
    TMapGpsManager tMapGps = null;
    private String Address;
    TMapPoint Destination_Point = null;
    private Context mContext = this;
    private boolean m_bTrackingMode = false;

    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private Button searchbutton;
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout tmapLayout = findViewById(R.id.tmapLayout);
        tMapView = new TMapView(this);
//
        tMapView.setSKTMapApiKey("\tl7xxa5b961d8570f4cde98fa199aaa572587");
        tmapLayout.addView(tMapView);

        tMapView.setCompassMode(true);

        tMapView.setIconVisibility(true);

        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);


        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(5);
        tMapGps.setProvider((tMapGps.NETWORK_PROVIDER));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
//        tMapGps.OpenGps();
//
        tMapView.setSightVisible(true);

        ImageButton searchButton = (ImageButton) findViewById(R.id.map_navigation_botton);
        searchButton.setOnClickListener(new View.OnClickListener() {

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


    //
    @Override
    public void onLocationChange(Location location) {

        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

        getCurrent_long = location.getLongitude();
        getCurrent_lat = location.getLatitude();

        Current_Point = new TMapPoint(getCurrent_lat, getCurrent_long);

        Log.d("getCurrent_lat : ", "" + getCurrent_lat);
        Log.d("getCurrent_long : ", "" + getCurrent_long);

    }

    public void SearchDestination() {
        m_bTrackingMode = true;

        LinearLayout search = (LinearLayout) findViewById(R.id.search);
        search.setVisibility(View.VISIBLE);
        Log.e("asdasd", "Asdasd");

        editSearch = (EditText) findViewById(R.id.editSearch);
        listView = (ListView) findViewById(R.id.listView);

        list = new ArrayList<String>();

        adapter = new SearchAdapter(list, mContext);

        listView.setAdapter(adapter);

        searchbutton = (Button) findViewById(R.id.search_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            TMapData tMapData = new TMapData();
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("장소 ", list.get(position));
                tMapData.findAllPOI(list.get(position), new TMapData.FindAllPOIListenerCallback() {

                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        if(poiItem != null) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                Address = poiItem.get(i).getPOIAddress();
                                Destination_Point = poiItem.get(i).getPOIPoint();
                            }
                            tMapView.setTrackingMode(true);
                            tMapView.removeTMapPath();

                            TMapPoint point1 = tMapView.getLocationPoint();
                            TMapPoint point2 = Destination_Point;

                            Log.e("point1 :", point1.toString());
                            Log.e("point2 :", point2.toString());

                            TMapData tmapdata = new TMapData();

                            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
                                @Override
                                public void onFindPathData(TMapPolyLine polyLine) {
                                    polyLine.setLineColor(Color.BLUE);
                                    tMapView.addTMapPath(polyLine);
                                }
                            });

                            Bitmap start = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_start);
                            Bitmap end = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_end);
                            tMapView.setTMapPathIcon(start, end);

                            tMapView.zoomToTMapPoint(point1, point2);

                            search.setVisibility(View.GONE);
                        }
                    }

                });

            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editSearch.getText().toString();
                TMapData tMapData = new TMapData();

                tMapData.findAllPOI(text, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        if (poiItem != null) {
                            list.clear();

                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);

                                Log.e("POI Name: ", item.getPOIName().toString());
                                Log.e("Address: ", item.getPOIAddress().replace("null", ""));
                                Log.e("Point: ", item.getPOIPoint().toString());

                                list.add(item.getPOIName());

                            }
                            Log.e("asdasdasd : ", "" + adapter.getCount());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

//        editSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String text = editSearch.getText().toString();
//                TMapData tMapData = new TMapData();
//
//                tMapData.findAllPOI(text, new TMapData.FindAllPOIListenerCallback() {
//                    @Override
//                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
//                        if(poiItem != null) {
//                            list.clear();
//
//                            for (int i = 0; i < poiItem.size(); i++) {
//                                TMapPOIItem item = poiItem.get(i);
//
//                                Log.e("POI Name: ", item.getPOIName().toString());
//                                Log.e("Address: ", item.getPOIAddress().replace("null", ""));
//                                Log.e("Point: ", item.getPOIPoint().toString());
//
//                                Address = item.getPOIAddress();
//                                Destination_Point = item.getPOIPoint();
//                                list.add(item.getPOIName());
//
//                            }
//                            Log.e("asdasdasd : ", ""+adapter.getCount());
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//                        }
//
////                        tMapView.setTrackingMode(true);
////                        tMapView.removeTMapPath();
////
////                        TMapPoint point1 = tMapView.getLocationPoint();
////                        TMapPoint point2 = Destination_Point;
////
////                        Log.e("point1 :", point1.toString());
////                        Log.e("point2 :", point2.toString());
////
////                        TMapData tmapdata = new TMapData();
////
////                        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
////                            @Override
////                            public void onFindPathData(TMapPolyLine polyLine) {
////                                polyLine.setLineColor(Color.BLUE);
////                                tMapView.addTMapPath(polyLine);
////                            }
////                        });
////
////                        Bitmap start = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_start);
////                        Bitmap end = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_end);
////                        tMapView.setTMapPathIcon(start, end);
////
////                        tMapView.zoomToTMapPoint(point1, point2);
//                    }
//                });
//            }
//        });
//    }
}





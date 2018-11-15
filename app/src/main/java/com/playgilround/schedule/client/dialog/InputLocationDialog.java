package com.playgilround.schedule.client.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.MainActivity;

/**
 * 18-07-05
 * 위치 설정 다이얼로그
 */
public class InputLocationDialog extends Activity implements View.OnClickListener, OnMapReadyCallback {

    static final String TAG = InputLocationDialog.class.getSimpleName();

    private EditText etLocationContent;
    double latitude;
    double longitude;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_location);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            //Gps 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
            Log.d(TAG, "Request Location Updates");
            progress = new ProgressDialog(this);
            progress.setTitle("위치");
            progress.setMessage("계신 곳에 위치를 탐색 중입니다.");
            progress.show();

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        etLocationContent = findViewById(R.id.etLocationContent);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);

    }


    @Override
    public void onMapReady(final GoogleMap map) {
//        LatLng SEOUL = new LatLng(37.56, 126.97);
        progress.cancel();
        Log.d(TAG, "Result Map Ready ->" + latitude + "--" + longitude);
            LatLng SEOUL = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(SEOUL);
            markerOptions.title("내 위치");
            markerOptions.snippet("니 위치");
            map.addMarker(markerOptions);

            map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
            map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.tvConfirm:
                Log.d(TAG, "Location Result ->" + etLocationContent.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("location",etLocationContent.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }


    public void finishLocation() {
        FragmentManager fragmentManager = getFragmentManager();
        /*MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //위치값 갱신 시
            Log.d("test", "onLocationChanged, location:" + location);

            latitude = location.getLatitude();   //위도
            longitude = location.getLongitude(); //경도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            Log.d(TAG, "위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

            finishLocation();
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("test", "onStatusChanged, provider:" + s + ", status:" + i + " ,Bundle:" + bundle);

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("test", "onProviderEnabled, provider:" + s);

        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("test", "onProviderDisabled, provider:" + s);

        }
    };
}

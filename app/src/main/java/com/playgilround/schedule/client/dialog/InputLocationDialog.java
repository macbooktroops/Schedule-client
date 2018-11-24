package com.playgilround.schedule.client.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.MainActivity;

import java.io.IOException;
import java.util.List;

/**
 * 18-07-05
 * 위치 설정 다이얼로그
 *
 */
public class InputLocationDialog extends Activity implements View.OnClickListener, OnMapReadyCallback,
        MaterialSearchBar.OnSearchActionListener {
    String resLocation;
    static final String TAG = InputLocationDialog.class.getSimpleName();

    double latitude;
    double longitude;

    double scheLatitude; //위도
    double scheLongitude; //경도
    String scheLocation;

    ProgressDialog progress;

    private MaterialSearchBar searchBar;
    private boolean isInit = true;
    private boolean isSearch = true;
    private boolean isLocation = false; //최초만.

    Double resLatitude;
    Double resLongitude;
    private Geocoder geocoder;
    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_location);

        Intent intent = getIntent();
        scheLatitude = intent.getDoubleExtra("latitude", 0);
        scheLongitude = intent.getDoubleExtra("longitude", 0);
        scheLocation = intent.getStringExtra("location");

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            //Gps 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
            Log.d(TAG, "Request Location Updates");
            progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setTitle("위치");

            if (scheLatitude == 0.0 && scheLongitude == 0.0) {
                progress.setMessage("계신 곳에 위치를 탐색 중입니다.");
            } else {
                progress.setMessage("설정 된 위치로 표시됩니다.");
            }
            progress.show();

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("위치 검색");
        searchBar.setSpeechMode(false);

        searchBar.setOnSearchActionListener(this);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Material Text ->" + getClass().getSimpleName() + "text changed ->" + searchBar.getText());
                isInit = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.tvConfirm).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                Log.d(TAG, "Button Navigation MaterialSearchBar");
                break;

            case MaterialSearchBar.BUTTON_SPEECH:
                Log.d(TAG, "Button speech MaterialSearchBar..");
                break;
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
    }


    /**
     * @param text
     */
    @Override
    public void onSearchConfirmed(CharSequence text) {

        if (isSearch) {
            if (isInit) {
                Log.d(TAG, "Confirmed -> " + text.toString());
                resLocation = text.toString();
                List<Address> addressList = null;

                try {
                    //GeoCoding
                    addressList = geocoder.getFromLocationName(resLocation, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Check Size -> " + addressList.size());

                if (addressList.size() != 0) {

//
                    String title = addressList.get(0).getFeatureName();
                    String snippet = addressList.get(0).getCountryName();

                    resLatitude =  addressList.get(0).getLatitude();
                    resLongitude = addressList.get(0).getLongitude();

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(resLatitude, resLongitude);
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title(title);
                    mOptions2.snippet(snippet);
                    mOptions2.position(point);
                    mOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    // 마커 추가
                    mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

                } else if (addressList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "그런 장소는 없습니다.", Toast.LENGTH_LONG).show();
                }

                isSearch = false;
            }
        } else {
            Log.d(TAG, "Already to INputLocation.");
            isSearch = true;
        }
    }

    /**
     * Zoom level 0 1:20088000.56607700 meters
     * Zoom level 1 1:10044000.28303850 meters
     * Zoom level 2 1:5022000.14151925 meters
     * Zoom level 3 1:2511000.07075963 meters
     * Zoom level 4 1:1255500.03537981 meters
     * Zoom level 5 1:627750.01768991 meters
     * Zoom level 6 1:313875.00884495 meters
     * Zoom level 7 1:156937.50442248 meters
     * Zoom level 8 1:78468.75221124 meters
     * Zoom level 9 1:39234.37610562 meters
     * Zoom level 10 1:19617.18805281 meters
     * Zoom level 11 1:9808.59402640 meters
     * Zoom level 12 1:4909.29701320 meters
     * Zoom level 13 1:2452.14850660 meters
     * Zoom level 14 1:1226.07425330 meters
     * Zoom level 15 1:613.03712665 meters
     * Zoom level 16 1:306.51856332 meters
     * Zoom level 17 1:153.25928166 meters
     * Zoom level 18 1:76.62964083 meters
     * Zoom level 19 1:38.31482042 meters
     *
     * 목적지와, 내위치거리를 계산해서,
     * Google map zoom level을 결정함.
     */
    public int setZoomeLevel(Double distance) {
        if (distance < 38.31482042) {
            return 19;
        } else if (distance < 76.62964083) {
            return 18;
        } else if (distance < 153.25928166) {
            return 17;
        } else if (distance < 306.51856332) {
            return 16;
        } else if (distance < 613.03712665) {
            return 15;
        } else if (distance < 1226.07425330) {
            return 14;
        } else if (distance < 2452.14850660) {
            return 13;
        } else if (distance < 4909.29701320) {
            return 12;
        } else if (distance < 9808.59402640) {
            return 11;
        } else if (distance < 19617.18805281) {
            return 10;
        } else if (distance < 39234.37610562) {
            return 9;
        } else if (distance < 78468.75221124) {
            return 8;
        } else if (distance < 156937.50442248) {
            return 7;
        } else if (distance < 313875.00884495) {
            return 6;
        } else if (distance < 627750.01768991) {
            return 5;
        } else if (distance < 1255500.03537981) {
            return 4;
        } else if (distance < 2511000.07075963) {
            return 3;
        } else if (distance < 5022000.14151925) {
            return 2;
        } else if (distance < 10044000.28303850) {
            return 1;
        } else if (distance < 20088000.56607700) {
            return 0;
        }
        return 0;
    }


     /**
     * @param map
     */
    @Override
    public void onMapReady(final GoogleMap map) {

        mMap = map;
//        LatLng SEOUL = new LatLng(37.56, 126.97);
        geocoder = new Geocoder(this);
        progress.cancel();

        Log.d(TAG, "Result Get Ready ->" + scheLatitude + "--" + scheLongitude);
        MarkerOptions markerOptions = new MarkerOptions();

        if (scheLatitude != 0.0 && scheLongitude != 0.0) {
            Log.d(TAG, "설정된 장소가 이미 지정된경우" + scheLatitude + "//" + scheLongitude + "지금 내 위치 --> " + latitude + "//" + longitude);
            //설정된 장소가 이미 지정된경우
            LatLng destMap = new LatLng(scheLatitude, scheLongitude);
            markerOptions.position(destMap);

            // 반경 500M원
            CircleOptions circle500M = new CircleOptions().center(destMap) //원점
                    .radius(500)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#880000ff")); //배경색

            markerOptions.title("도착지");
            markerOptions.snippet(scheLocation);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


            resLocation = scheLocation;
            resLatitude = scheLatitude;
            resLongitude = scheLongitude;



            //내 위치가 표시될 마커 생성
            MarkerOptions currentMarker = new MarkerOptions();
            currentMarker.position(new LatLng(latitude, longitude));
            currentMarker.title("내 위치");

            map.addCircle(circle500M);
            map.addMarker(markerOptions);
            map.addMarker(currentMarker);

            //내 위치, 목적지 거리 계산
            Location curLocation = new Location("Current");
            curLocation.setLatitude(latitude);
            curLocation.setLongitude(longitude);


            Location destLocation = new Location("destination");
            destLocation.setLatitude(scheLatitude);
            destLocation.setLongitude(scheLongitude);

            double distance = curLocation.distanceTo(destLocation);

            Log.d(TAG, "Distance state - >" + distance + "m");



            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destMap, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            Log.d(TAG, "Result Map Ready ->" + latitude + "--" + longitude);
            LatLng destMap = new LatLng(latitude, longitude);

            markerOptions.position(destMap);

            // 반경 500M원
            CircleOptions circle500M = new CircleOptions().center(destMap) //원점
                    .radius(500)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#880000ff")); //배경색

            markerOptions.title("내 위치");
            markerOptions.snippet("내 위치");
            map.addMarker(markerOptions);
            map.addCircle(circle500M);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destMap, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.tvConfirm:
                Log.d(TAG, "Location Result ->" + resLocation);
                Intent intent = new Intent();
                intent.putExtra("location",resLocation);
                intent.putExtra("latitude", resLatitude);
                intent.putExtra("longitude", resLongitude);
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
        isLocation = true;
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

            if (!isLocation) {
                finishLocation();
            }
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

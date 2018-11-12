package com.playgilround.schedule.client.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

    private OnLocationBackListener mOnLocationBackListener;
    private EditText etLocationContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_location);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);

        FragmentManager fragmentManager = getFragmentManager();

        Log.d(TAG, "fragment state -> " + fragmentManager);
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        Log.d(TAG, "fragment state 2-> " + mapFragment);

        mapFragment.getMapAsync(this);

    }
/*
    public InputLocationDialog(Context context, OnLocationBackListener onLocationBackListener) {
        super(context, R.style.DialogFullScreen);
        mOnLocationBackListener = onLocationBackListener;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_input_location);
//        etLocationContent = (EditText) findViewById(R.id.etLocationContent);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        Activity activity = MainActivity.getActivity();
        FragmentManager fragmentManager = activity.getFragmentManager();

        Log.d(TAG, "fragment state -> " + fragmentManager);
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        Log.d(TAG, "fragment state 2-> " + mapFragment);

        mapFragment.getMapAsync(this);
    }
*/

    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
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
                if (mOnLocationBackListener != null) {
                    mOnLocationBackListener.onLocationBack(etLocationContent.getText().toString());
                }

                finish();
                break;
        }
    }
        public interface OnLocationBackListener {
            void onLocationBack(String text);
        }
}

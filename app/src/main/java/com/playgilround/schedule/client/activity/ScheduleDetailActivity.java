package com.playgilround.schedule.client.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.base.app.BaseActivity;
import com.playgilround.schedule.client.dialog.InputLocationDialog;
import com.playgilround.schedule.client.dialog.SelectDateDialog;
import com.playgilround.schedule.client.dialog.SelectEventSetDialog;
import com.playgilround.schedule.client.fragment.ArrivedRankFragment;
import com.playgilround.schedule.client.gson.ArrivedAtJsonData;
import com.playgilround.schedule.client.gson.ShareScheduleJsonData;
import com.playgilround.schedule.client.gson.ShareUserScheJsonData;
import com.playgilround.schedule.client.gson.UserJsonData;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.EventSetR;
import com.playgilround.schedule.client.realm.ScheduleR;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;
import com.playgilround.schedule.client.gson.Result;
import com.playgilround.schedule.client.task.eventset.LoadEventSetRMapTask;
import com.playgilround.schedule.client.utils.CalUtils;
import com.playgilround.schedule.client.utils.DateUtils;
import com.playgilround.schedule.client.utils.ToastUtils;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-06-29
 * 스케줄을 위치 등등, 자세하게 적을수있는 Activity
 *
 * 스케줄 항목, 시간, 위치, 내용 Realm적용 후
 * 각각 Dialog 확인만 눌러도
 * Realm 에 바로 저장되는 문제가 있어서,
 *
 * 최종 confirm()에서만 Realm Transaction이 실행되도록 수정.
 */
public class ScheduleDetailActivity extends BaseActivity implements View.OnClickListener,
        OnTaskFinishedListener<Map<Integer, EventSetR>>, SelectEventSetDialog.OnSelectEventSetListener, SelectDateDialog.OnSelectDateListener {

    static final String TAG = ScheduleDetailActivity.class.getSimpleName();

    public static int UPDATE_SCHEDULE_CANCEL = 1;
    public static int UPDATE_SCHEDULE_FINISH = 2;

    private View vSchedule;
    private ImageView ivEventIcon;
    private EditText etTitle, etDesc;

    private TextView tvEventSet, tvTime, tvLocation, tvShare;
    private Map<Integer, EventSetR> mEventSetsMap;

    private ScheduleR mSchedule;

    private int scheId;
    public static String SCHEDULE_OBJ = "schedule.obj";
    public static String CALENDAR_POSITION = "calendar.position";

    private int mPosition = -1;

    private SelectEventSetDialog mSelectEventSetDialog;
    private SelectDateDialog mSelectDateDialog;
    private InputLocationDialog mInputLocationDialog;

    Realm realm;

    String location; //위치
    Double latitude; //위도
    Double longitude; //경도

    Double curLatitude; //현재 위도
    Double curLongitude; //현재 경도
    int eventColor, eventSetId; //뷰 색상, 스케줄분류 아이디
    int resYear, resMonth, resDay;
    long resTime;

    private Button btnArrived;


    public boolean isSetLocation = false;
    public boolean isGpsEnable = false;


    //realm 에 time을 보기 편하게 변환
    private String HUMAN_TIME_FORMAT = "";
    private String resultTime;
    //선택된 스케줄 Primary 데이터
    int curScheduleSeq;

    SharedPreferences pref;

    //자기자신의 아이디
    int userId;

    String authToken;
    ProgressDialog progress;

    ArrayList<String> arrName; //도착 랭킹 이름

    //도착 시간 여부, arrived_at 이 없으면 도착하지않음.
    //arrived_at 이 있으면 도착버튼 클릭 안되도록.
    ArrayList<String> arrArrived;
    @Override
    protected void bindView() {
        setContentView(R.layout.activity_schedule_detail);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            //목적지 도착 주변일 경우 판단하기 위해, 현재 위치를 얻어옴
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);

            progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setTitle("위치");
            progress.setMessage("계신 곳에 위치를 탐색 중입니다.");
            progress.show();
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        TextView tvTitle = searchViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.schedule_event_detail_setting));

        searchViewById(R.id.tvCancel).setOnClickListener(this);
        searchViewById(R.id.tvFinish).setOnClickListener(this);
        searchViewById(R.id.llScheduleEventSet).setOnClickListener(this);
        searchViewById(R.id.llScheduleTime).setOnClickListener(this);
        searchViewById(R.id.llScheduleLocation).setOnClickListener(this);
        searchViewById(R.id.llShare).setOnClickListener(this);

        vSchedule = searchViewById(R.id.vScheduleColor);
        ivEventIcon = searchViewById(R.id.ivScheduleEventSetIcon);

        etTitle = searchViewById(R.id.etScheduleTitle);
        etDesc = searchViewById(R.id.etScheduleDesc);
        HUMAN_TIME_FORMAT = getString(R.string.human_time_format);

        tvEventSet = searchViewById(R.id.tvScheduleEventSet);
        tvTime = searchViewById(R.id.tvScheduleTime);
        tvLocation = searchViewById(R.id.tvScheduleLocation);

        tvShare = searchViewById(R.id.tvShare);

        btnArrived = searchViewById(R.id.btnArrived);
        searchViewById(R.id.btnArrived).setOnClickListener(this);

        pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        authToken = pref.getString("loginToken", "default");
        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void initData() {
        super.initData();
        mEventSetsMap = new HashMap<>();
//        mSchedule = (ScheduleR)getIntent().getSerializableExtra(SCHEDULE_OBJ);

        curScheduleSeq = (int) getIntent().getSerializableExtra(SCHEDULE_OBJ);
//        Log.d(TAG, "mSchedule Result ->" + curScheduleSeq);

        /**
         * 스케줄 클릭 시 seq 값을 받아서 스케줄 세팅.
         */
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                Log.d(TAG, "find Schedule Data =====>"+ curScheduleSeq);
                ScheduleR resScheduleR = realm.where(ScheduleR.class)
                        .equalTo("seq", curScheduleSeq).findFirst();



                 mSchedule = resScheduleR;

                scheId = mSchedule.getScheId();
            }
        });

        mPosition = getIntent().getIntExtra(CALENDAR_POSITION, -1);

//        new LoadEventSetMapTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadEventSetRMapTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setScheduleData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                setResult(UPDATE_SCHEDULE_CANCEL);
                finish();
                break;
            case R.id.tvFinish:
                confirm();
                break;
            case R.id.llScheduleEventSet:
                //스케줄 제목적혀있는 레이아웃 클릭
                showSelectEventSetDialog();
                break;

            case R.id.llScheduleTime:
                //날짜 선택 레이아웃 클릭 시.
                showSelectDateDialog();
                break;

            case R.id.llScheduleLocation:
                //위치 선택 레이아웃 클릭
                showInputLocationDialog();
                break;

            case R.id.llShare:
//                showS
                break;

            case R.id.btnArrived:
                //도착완료버튼 클릭

                arrivedDest(new LoginActivity.ApiCallback() {
                    @Override
                    public void onSuccess(String success) {
                        arrivedRanking();
                    }

                    @Override
                    public void onFail(String result) {

                    }
                });


        }
    }

    //최초 DetailActivity 실행 시, 도착 여부 판단 (버튼 비활성화 처리)
    private void isArrivedSchedule(final LoginActivity.ApiCallback callback) {
        Retrofit retrofit = APIClient.getClient();
        APIInterface getDetailSche = retrofit.create(APIInterface.class);

        Call<JsonObject> result = getDetailSche.getScheduleDetail(authToken, scheId);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String nickName = pref.getString("loginName", "");

                    Log.d(TAG, "result -->" + response.body().toString());

                    String strResponse = response.body().toString();

                    Type list = new TypeToken<ShareUserScheJsonData>() {
                    }.getType();

                    Type list2 = new TypeToken<List<ArrivedAtJsonData>>(){
                    }.getType();

                    ShareUserScheJsonData scheList = new Gson().fromJson(strResponse, list);

                    List<ArrivedAtJsonData> arrivedList = new Gson().fromJson(scheList.user, list2);

                    for (ArrivedAtJsonData resArrivedAt : arrivedList) {
                        String name = resArrivedAt.name;
                        String arriveTime = resArrivedAt.arriveTime;

                        /**
                         * 먼저 도착한 순서대로 name, arrived_at 저장
                         * arrived_at 이 없으면 null
                         */
                        if (nickName.equals(name)) {
                            Log.d(TAG, "Same Detail Schedule." + arriveTime);
                            callback.onSuccess(arriveTime);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();

                        Log.d(TAG, "result error -->" + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "t-->" + t.toString());
            }
        });
    }

    //도착 랭킹 메기기
    private void arrivedRanking() {

        //도착완료 버튼이 눌리면 도착 순위 작업
        //
        /**
         * {
         * 	"id": 106,
         * 	"title": "kill",
         * 	"state": 0,
         * 	"start_time": "2018-10-30 00:00:00",
         * 	"latitude": 0.0,
         * 	"longitude": 0.0,
         * 	"user": [{
         * 		"id": 1,
         * 		"name": "c004245",
         * 		"email": "c004245@naver.com",
         * 		"arrive": true,
         * 		"arrived_at": "2018-10-30 13:50:46"
         *        }, {
         * 		"id": 1,
         * 		"name": "c004245",
         * 		"email": "c004245@naver.com",
         * 		"arrive": true,
         * 		"arrived_at": "2018-10-30 13:50:46"
         *    }, {
         * 		"id": 5,
         * 		"name": "hyun123",
         * 		"email": "c00@naver.com",
         * 		"arrive": true
         *    }]
         * }
         */
        Retrofit retrofit = APIClient.getClient();
        APIInterface getDetailSche = retrofit.create(APIInterface.class);

        Log.d(TAG, "authToken ->" + authToken + "--" + scheId);
        Call<JsonObject> result = getDetailSche.getScheduleDetail(authToken, scheId);

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    arrName = new ArrayList<String>();
                    arrArrived = new ArrayList<String>();
                    Log.d(TAG, "result -->" + response.body().toString());

                    String strResponse = response.body().toString();

                    Type list = new TypeToken<ShareUserScheJsonData>() {
                    }.getType();

                    Type list2 = new TypeToken<List<ArrivedAtJsonData>>(){
                    }.getType();

                    ShareUserScheJsonData scheList = new Gson().fromJson(strResponse, list);

//                                    JsonArray user = scheList.user;


                    List<ArrivedAtJsonData> arrivedList = new Gson().fromJson(scheList.user, list2);

                    for (ArrivedAtJsonData resArrivedAt : arrivedList) {
                        String name = resArrivedAt.name;
                        String arriveTime = resArrivedAt.arriveTime;
                        Log.d(TAG, "userData --> " + name + "--" + arriveTime);

                        /**
                         * 먼저 도착한 순서대로 name, arrived_at 저장
                         * arrived_at 이 없으면 null
                         */
                        arrName.add(name);
                        arrArrived.add(arriveTime);
                    }

                    for (int i = 0; i < arrName.size(); i++) {
                        Log.d(TAG, "name -->" + arrName.get(i) + "--arrived --> " + arrArrived.get(i));
                    }

                    //도착 랭킹 DialogFragment 표시
                    final ArrivedRankFragment af = ArrivedRankFragment.getInstance(arrName, arrArrived);
                    final FragmentManager fm  = getFragmentManager();

                    af.show(fm, "TAG");


                } else {
                    try {
                        String error = response.errorBody().string();

                        Log.d(TAG, "result error -->" + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "t-->" + t.toString());
            }
        });
    }


    //도착 완료 버튼 클릭
    private void arrivedDest(final LoginActivity.ApiCallback callback) {
        DateTime dateTime = new DateTime();
        String retTime = dateTime.toString("yyyy-MM-dd HH:mm:ss");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("arrived_at", retTime);

        Retrofit retrofit = APIClient.getClient();
        APIInterface postArriveSche = retrofit.create(APIInterface.class);
        Call<JsonObject> result = postArriveSche.postScheduleArrive(jsonObject, authToken, scheId);

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "arrive result ->" + response.body().toString());
                    callback.onSuccess("success");

                } else {
                    try {
                        String error = response.errorBody().string();

                        Result result = new Gson().fromJson(error, Result.class);

                        List<String> message = result.message;

                        if (message.contains("Unauthorized auth_token.")) {
                            Toast.makeText(getApplicationContext(),"도착완료 토큰 에러입니다. 앱을 재실행해주세요.", Toast.LENGTH_LONG).show();
                        } else if (message.contains("Not found schedule.")) {
                            Toast.makeText(getApplicationContext(), "스케줄을 찾을 수 없습니다..", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
    //확인 버튼
    private void confirm() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "confirm");
                if (etTitle.getText().length() != 0) {
                    mSchedule.setTitle(etTitle.getText().toString());
                    mSchedule.setDesc(etDesc.getText().toString());

                    Log.d(TAG, "mSchedule getDesc -->" + mSchedule.getDesc());

                    /**
                     * onSelectEventSet
                     */
                    mSchedule.setColor(eventColor);
                    mSchedule.setEventSetId(eventSetId);

                    /**
                     * onSelectDate
                     */
                    mSchedule.setYear(resYear);
                    mSchedule.setMonth(resMonth);
                    mSchedule.setDay(resDay);
                    mSchedule.setTime(resTime);
                    mSchedule.sethTime(resultTime);

                    Log.d(TAG, "mSchedule getDate -->" + mSchedule.getYear() + "/" + mSchedule.getMonth() + "/" + mSchedule.getDay() + "/" + mSchedule.getTime() + "/" + mSchedule.gethTime());

                    /**
                     * onLocationBack
                     */
                    mSchedule.setLocation(location);
                    mSchedule.setLatitude(latitude);
                    mSchedule.setLongitude(longitude);

//                    mSchedule.setEventSetId();
                    setResult(UPDATE_SCHEDULE_FINISH);
//                    Log.d(TAG, "mSchedule Check title --->" + mSchedule.getTitle());
//                    Log.d(TAG, "mSchedule Check Desc --->" + mSchedule.getDesc());
//                    Log.d(TAG, "mschedule check seq --->" + mSchedule.getSeq());

//                    ScheduleR schedule = realm.where(Schedule.)
                  /*  new UpdateScheduleRTask(getApplicationContext(), new OnTaskFinishedListener<Boolean>() {
                        @Override
                        public void onTaskFinished(Boolean data) {
                            setResult(UPDATE_SCHEDULE_FINISH);
                            finish();
                        }
                    }, mSchedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
//                  getMyUserID();
                } else {
                    ToastUtils.showShortToast(getApplicationContext(), R.string.schedule_input_content_is_no_null);
                }
            }
        });

        finish();
        addScheduleServer(mSchedule.getScheId());
//        getMyUserID();



    }

    //서버에 스케줄 추가된 내용 저장
    /**
     * {
     *     "title": "오늘창업허브 ㅋㅋ",
     *     "start_time": "2018-09-30 13:00:00",
     *     "content": "adasdsadasdadsadasadasd",
     *     "latitude": 37.6237604,
     *     "longitude": 126.9218479,
     *     "user_ids" [ 2, 3 ]
     * }
     */
    public void addScheduleServer(int scheId) {
        Log.d(TAG, "addScheduleServer -->" + scheId);
        Log.d(TAG, "addSchedule ->" + etTitle.getText().toString() + "--" + resTime + "--" + etDesc.getText().toString() + latitude + longitude);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", etTitle.getText().toString());
        jsonObject.addProperty("start_time", resYear +"-"+resMonth+"-"+resDay);
        jsonObject.addProperty("content", etDesc.getText().toString());
        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);

        Log.d(TAG, "jsonObject add ->" + jsonObject + "--" + authToken);

        Retrofit retrofit = APIClient.getClient();
        APIInterface postNewSche = retrofit.create(APIInterface.class);
        Call<JsonObject> result = postNewSche.postUpdateSchedule(jsonObject, scheId, authToken);

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String success = response.body().toString();
                } else  {
                    try {
                        String error = response.errorBody().string();


                        Result result = new Gson().fromJson(error, Result.class);

                        List<String> message = result.message;

                        if (message.contains("Unauthorized auth_token.")) {

                        } else if (message.contains("Do not update.")) {

                        } else if (message.contains("Not found schedule.")) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "fail schedule -> " + t.toString());
            }
        });
    }

    @Override
    public void onTaskFinished(Map<Integer, EventSetR> data) {
        Log.d(TAG, "onTask eventDetail --> "+ data.size());
        mEventSetsMap = data;

        EventSetR eventSet = new EventSetR();
        eventSet.setName(getString(R.string.menu_no_category));

        mEventSetsMap.put(eventSet.getSeq(), eventSet);
//        EventSetR current = mEventSetsMap.get(mSchedule.getEventSetId());
        EventSetR current = mEventSetsMap.get(mSchedule.getEventSetId());
//        Log.d(TAG, "mschedule --->" + mSchedule.getEventSetId());
//        Log.d(TAG, "current ->" + current.getName() + "--" + current.getSeq());

//        Log.d(TAG, "current -> " +current.getName());
        EventSetR titleEvent = realm.where(EventSetR.class).equalTo("seq", mSchedule.getEventSetId()).findFirst();

        if (current != null) {
            if (current.getName().equals("미정")){
                tvEventSet.setText("미정");
            } else {
                tvEventSet.setText(titleEvent.getName());
            }
        } else if (current == null) {
            tvEventSet.setText("공휴일");
        }
    }

    //이벤트 설정 레이아웃클릭
    private void showSelectEventSetDialog() {
        if (mSelectEventSetDialog == null) {
            Log.d(TAG, "mSchedule eventset dialog -->" + mSchedule.getEventSetId());
            mSelectEventSetDialog = new SelectEventSetDialog(this, this, mSchedule.getEventSetId());
        }
        mSelectEventSetDialog.show();
    }


    //시간 설정 레이아웃클릭
    private void showSelectDateDialog() {
        if (mSelectDateDialog == null) {
            Log.d(TAG, "mSchedule getMonth state ->" + mSchedule.getMonth());
            mSelectDateDialog = new SelectDateDialog(this, this, mSchedule.getYear(), mSchedule.getMonth() -1, mSchedule.getDay(), mPosition);
        }
        mSelectDateDialog.show();
    }

    //위치 설정 레이아웃 클릭
    private void showInputLocationDialog() {
//        if (mInputLocationDialog == null) {
//            mInputLocationDialog = new InputLocationDialog(this, this);
//        }
//        mInputLocationDialog.show();
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "ShowInputLocation -> " +latitude + "--" + longitude + isGpsEnable);
            Intent intent = new Intent(ScheduleDetailActivity.this, InputLocationDialog.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("location", location);
            startActivityForResult(intent, 3000);
            isGpsEnable = true;
        } else {
            Toast.makeText(getApplicationContext(), "현재 위치를 얻기 위해 \n GPS 위치 기능을 켜주세요!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            isGpsEnable = false;
        }



    }


    private void setScheduleData() {
        Log.d(TAG, "set eventid ==>"+ mSchedule.getEventSetId());
        eventSetId = mSchedule.getEventSetId();
        eventColor =mSchedule.getColor();

        vSchedule.setBackgroundResource(CalUtils.getEventSetColor(eventColor));//색상 설정
        ivEventIcon.setImageResource(eventSetId == 0 ? R.mipmap.ic_detail_category : R.mipmap.ic_detail_icon); //설정한 이벤트셋이 있다면.
        etTitle.setText(mSchedule.getTitle());
        etDesc.setText(mSchedule.getDesc()); //자세한 내용

        EventSetR current = mEventSetsMap.get(eventSetId);

        Log.d(TAG, "SetSchedule Data -->"+ current);
        if (current != null) {
            tvEventSet.setText(current.getName()); //스케줄 이름
        }
        resetDateTimeUi();

//        location = mSchedule.getLocation();

        Log.d(TAG, "location ->" + location + "//"  +isSetLocation);
        if (isSetLocation) {
            //지도에서 위치를 정해줬을 경우.
            if (TextUtils.isEmpty(location)) {
                Log.d(TAG, "mSchedule location true - >" + location);
                tvLocation.setText(R.string.click_here_select_location);
            } else {
//                location = mSchedule.getLocation();
                tvLocation.setText(location);
//                latitude = mSchedule.getLatitude();
//                longitude = mSchedule.getLongitude();
                Log.d(TAG, "mschedule location true2- >" + location + latitude + longitude);


            }
        } else {
            if (TextUtils.isEmpty(mSchedule.getLocation())) {
                Log.d(TAG, "mSchedule location - >" + mSchedule.getLocation() + mSchedule.getLatitude() + "--" + mSchedule.getLongitude());
                latitude = mSchedule.getLatitude();
                longitude = mSchedule.getLongitude();
                tvLocation.setText(R.string.click_here_select_location);
            } else {
                Log.d(TAG, "mschedule location 2- >" + mSchedule.getLocation());
                location = mSchedule.getLocation();
                latitude = mSchedule.getLatitude();
                longitude = mSchedule.getLongitude();
                tvLocation.setText(location);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectEventSetDialog.ADD_EVENT_SET_CODE) { //event set dialog에서 추가버튼을 누르고.
            if (resultCode == AddEventSetActivity.ADD_EVENT_SET_FINISH) {
//                EventSetR eventSet = (EventSetR) data.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ); //작업끝
                final int eventSetId = (int) data.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ);
                Log.d(TAG, "Schedule Detail activtyResult --->" + eventSetId);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
//                        Log.d(TAG, "occur execute");
//                        long seq = realm.where(EventSetR.class).max("seq").longValue();

                        EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", eventSetId).findFirst();
                        Log.d(TAG, "eventSetR detail -> " +eventSetR.getName());

                        if (eventSetR != null) {
                            mSelectEventSetDialog.addEventSet(eventSetR);
                        }
//                        sendBroadcast(new Intent(MainActivity.ADD_EVENT_SET_ACTION).putExtra(AddEventSetActivity.EVENT_SET_OBJ, eventSetR));
                        sendBroadcast(new Intent(MainActivity.ADD_EVENT_SET_ACTION).putExtra(AddEventSetActivity.EVENT_SET_OBJ, eventSetR.getSeq()));

                    }
                });
//                if (eventSet != null) {
//                    mSelectEventSetDialog.addEventSet(eventSet);
                    /**
                     * 스케줄 분류 항목추가.
                     * 스케줄 분류 다이얼로그에서 항목을 추가했을 경우,
                     * Broadcast로 좌측메뉴에도 그 항목을 추가한다고 전송.
                     */
                }
            } else if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    //위치 선택 화면에서 위치를 선택하고 확인버튼을 눌렀을 경우 호출.
                    case 3000:
                        Log.d(TAG, "Location FINISH");
                        isSetLocation = true;
                        location = data.getStringExtra("location");
                        latitude = data.getDoubleExtra("latitude", 0);
                        longitude = data.getDoubleExtra("longitude", 0);
                        Log.d(TAG, "onLocationBack -> " +location);
                        Log.d(TAG, "result 위도 경도 "+ latitude + "--" + longitude);
                        Log.d(TAG, "mschedule location -> " + mSchedule.getLocation());

                        if (TextUtils.isEmpty(location)) {
                            tvLocation.setText(R.string.click_here_select_location);
                        } else {
                            tvLocation.setText(location);
                        }
                        break;
                }
            }
        }


    private void resetDateTimeUi() {
        resYear = mSchedule.getYear();
        resMonth = mSchedule.getMonth();
        resDay = mSchedule.getDay();

        resTime = mSchedule.getTime();
        resultTime = mSchedule.gethTime();
        if (mSchedule.getTime() == 0) {
            if (mSchedule.getYear() != 0) {
//                Log.d(TAG, "ResetDateTimeUI -->" + mSchedule.getYear() + "/" + mSchedule.getMonth() + "/" + mSchedule.getDay());



                Log.d(TAG, "ResetDateTimeUI -->" + resYear + "/" + resMonth + "/" + resDay + "/" + resTime + "/" + resultTime);


//                tvTime.setText(String.format(getString(R.string.date_format_no_time), mSchedule.getYear(), mSchedule.getMonth() , mSchedule.getDay()));
                tvTime.setText(String.format(getString(R.string.date_format_no_time), resYear, resMonth, resDay));
            } else {
                tvTime.setText(R.string.click_here_select_date);
            }
        } else {
            Log.d(TAG, "ResetDateTimeUI -->" + resYear + "/" + resMonth + "/" + resDay + "/" + resTime + "/" + resultTime);

            tvTime.setText(DateUtils.timeStamp2Date(resTime, getString(R.string.date_format)));
        }
    }
    //스케줄 목록다이얼로그 클릭
   @Override
    public void onSelectEventSet(final EventSetR eventSet) {
        Log.d(TAG, "eventSet onSelectEventSet -->" + eventSet.getName());


        eventColor = eventSet.getColor();
        eventSetId = eventSet.getSeq();


        Log.d(TAG, "eventColor -> " +eventSet.getColor());
        Log.d(TAG, "eventSetId -> " +eventSet.getSeq());

        vSchedule.setBackgroundResource(CalUtils.getEventSetColor(eventColor));
        tvEventSet.setText(eventSet.getName());
        ivEventIcon.setImageResource(eventSetId == 0 ? R.mipmap.ic_detail_category : R.mipmap.ic_detail_icon);
    }

    //디테일 한 날짜/시간 설정 완료 클릭
    @Override
    public void onSelectDate(final int year, final int month, final int day, final long time, final int position) {
        Log.d(TAG, "onSelectDate");
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                mSchedule.setYear(year);
//                mSchedule.setMonth(month);
//                mSchedule.setDay(day);
//                mSchedule.setTime(time);
//
//                SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
//                resultTime = sdf.format(time);
//
//                mSchedule.sethTime(resultTime);
//                mPosition = position;


        resYear = year;
        resMonth = month;
        resDay = day;
        resTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
        resultTime = sdf.format(time);

        mPosition = position;

        //시간설정을 안하고 연도가 0이 아니면.
        if (resTime == 0) {
            if (resYear != 0) {
                tvTime.setText(String.format(getString(R.string.date_format_no_time), resYear, resMonth, resDay));
            } else {
                tvTime.setText(R.string.click_here_select_date);
            }
        } else {
            //시간설정을 했다면.
            tvTime.setText(DateUtils.timeStamp2Date(resTime, getString(R.string.date_format)));
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //위치값 갱신 시
            Log.d("test", "onLocationChanged, location:" + location);

            curLatitude = location.getLatitude();   //위도
            curLongitude = location.getLongitude(); //경도

            Log.d(TAG, "위도 : " + curLatitude + "\n경도 : " + curLongitude);

            progress.cancel();;

            isArrivedSchedule(new LoginActivity.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    if (result == null) {
                        if (mSchedule.getEventSetId() == -2) {
                            btnArrived.setVisibility(View.VISIBLE);
                            //20.593684//78.96288
                            Log.d(TAG, "Setting Location -> " + mSchedule.getLatitude() + "//" + mSchedule.getLongitude() + "//" + curLatitude + "//" + curLongitude);
                        }
                    } else {
                        if (mSchedule.getEventSetId() == -2) {
                            btnArrived.setVisibility(View.VISIBLE);
                            btnArrived.setText("도착 완료!");
                            btnArrived.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onFail(String result) {

                }
            });
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

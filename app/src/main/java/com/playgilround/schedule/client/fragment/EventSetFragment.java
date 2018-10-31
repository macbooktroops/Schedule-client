package com.playgilround.schedule.client.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.ScheduleDetailActivity;
import com.playgilround.schedule.client.adapter.ScheduleAdapter;
import com.playgilround.schedule.client.base.app.BaseFragment;
import com.playgilround.schedule.client.dialog.SelectDateDialog;
import com.playgilround.schedule.client.gson.UserJsonData;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.EventSetR;
import com.playgilround.schedule.client.realm.ScheduleR;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;
import com.playgilround.schedule.client.gson.Result;
import com.playgilround.schedule.client.schedule.ScheduleRecyclerView;
import com.playgilround.schedule.client.task.eventset.GetScheduleRTask;
import com.playgilround.schedule.client.task.schedule.AddScheduleRTask;
import com.playgilround.schedule.client.utils.DeviceUtils;
import com.playgilround.schedule.client.utils.ScheduleFriendItem;
import com.playgilround.schedule.client.utils.ToastUtils;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-06-19
 * 스케줄 목록을 보고자 할때 표시되는 프레그먼트
 *
 */
public class EventSetFragment extends BaseFragment implements View.OnClickListener,
        ScheduleAdapter.ScheduleEvent,
        SelectDateDialog.OnSelectDateListener, OnTaskFinishedListener<List<ScheduleR>> {

    static final String TAG = EventSetFragment.class.getSimpleName();
    private ScheduleRecyclerView rvScheduleList;
    private EditText etInput;
    private RelativeLayout rlNoTask;

    private ScheduleAdapter mScheduleAdapter;

    private EventSetR mEventSet;

    private SelectDateDialog mSelectDateDialog;

    private int mPosition = -1;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;
    private long mTime;

    String name;
    int id;

    RealmResults<ScheduleR> resEmp;
    List<ScheduleR> resList = new ArrayList<>();

    //친구 관련 ArrayList
    ArrayList<String> arrName;
    ArrayList<Integer> arrId;

    ArrayList<Integer> arrFriendId;


    //realm 에 time을 보기 편하게 변환
    private String HUMAN_TIME_FORMAT = "";
    private String resultTime;
    public static String EVENT_SET_OBJ = "event.set.obj";

    public boolean isSetTime = false; //SelectDateDialog 에서 날짜 체크 후 선택 눌렀을 경우

    Realm realm;

    String content;
    SharedPreferences pref;
    String authToken;

    int resultId;

    static EventSetR resultEvent; //EVENT_SET_OBJ
    static int resultYear;
    /**
     * http://milkissboy.tistory.com/34
     * @param eventSet
     * @return
     */
    public static EventSetFragment getInstance(EventSetR eventSet) {
        EventSetFragment fragment = new EventSetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_SET_OBJ, eventSet.getSeq()); //객체 넘기기
        fragment.setArguments(bundle);
        resultEvent = eventSet;
        return fragment;
    }

    //공휴일에서 EventSetFragment 접근 할 경우.
    public static EventSetFragment getInstance(EventSetR eventSet, int year) {
        EventSetFragment fragment = new EventSetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_SET_OBJ, eventSet.getSeq());
        fragment.setArguments(bundle);
        resultEvent = eventSet;
        resultYear = year;
        return fragment;
    }
    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_event_set, container, false);
    }

    @Override
    protected void bindView() {


        rvScheduleList = searchViewById(R.id.rvScheduleList);
        rlNoTask = searchViewById(R.id.rlNoTask);
        etInput = searchViewById(R.id.etInputContent);

        HUMAN_TIME_FORMAT = getString(R.string.human_time_format2);
        realm = Realm.getDefaultInstance();

        pref =  getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        authToken = pref.getString("loginToken", "default");

        searchViewById(R.id.ibMainClock).setOnClickListener(this);
        searchViewById(R.id.ibMainOK).setOnClickListener(this);
        initBottomInputBar();
        initScheduleList();
    }

    //스케줄 메모 부분 InputBar
    private void initBottomInputBar() {

        //http://egloos.zum.com/killins/v/3008925  --> TextWatcher
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //start 지점에서 시작되는 count 갯수만큼의
                //글자들이 after 길이만큼의 글자로 대치되려고 할 때 호출된다

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //start 지점에서 시작되는 before 갯수만큼의 글자들이
                //count 갯수만큼의 글자들로 대치되었을 때 호출된다.

            }

            //EditText의 텍스트가 변경되면 호출된다.
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged --->" + s.toString());
                etInput.setGravity(s.length() == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
            }
        });

        etInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    //스케줄 리스트 설정
    private void initScheduleList() {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvScheduleList.setLayoutManager(manager);

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        rvScheduleList.setItemAnimator(itemAnimator);

        //스케줄 어댑터 설정
        mScheduleAdapter = new ScheduleAdapter(mActivity, this);
        rvScheduleList.setAdapter(mScheduleAdapter);


    }

    @Override
    protected void initData() {
        super.initData();
//        Log.d(TAG, "mEvent init -->" + mEventSet);
     /*   final int mEventSetId = (int) getArguments().getSerializable(EVENT_SET_OBJ);
        Log.d(TAG, "mEventSetId -->" + mEventSetId);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", mEventSetId).findFirst();
                mEventSet = eventSetR;

                Log.d(TAG, "mEventSet fragment ->" + mEventSet.getName());
            }
        });*/
        mEventSet = resultEvent;

    }

    @Override
    protected void bindData() {
        super.bindData();
        Log.d(TAG, "mEventSet -->" + mEventSet.getSeq());
        new GetScheduleRTask(mActivity, this, mEventSet.getSeq()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMainClock:
                showSelectDateDialog();
                break;
            case R.id.ibMainOK:
                addSchedule();
                break;
        }
    }

    //시작 시간 설정 다이얼로그.
    private void showSelectDateDialog() {
        if (mSelectDateDialog == null) {
            Calendar calendar = Calendar.getInstance();
            Log.d(TAG, "show Select Event ->" + calendar.get(Calendar.MONTH));
            mSelectDateDialog = new SelectDateDialog(mActivity, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mPosition);
        }

        mSelectDateDialog.show();
    }
//키보드 닫기
    private void closeSoftInput() {
        etInput.clearFocus();
        DeviceUtils.closeSoftInput(mActivity, etInput);
    }

    //ok버튼 클릭 시 스케줄 등록
    private void addSchedule() {
        content = etInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShortToast(mActivity, R.string.schedule_input_null);
        } else {
            closeSoftInput();

            getMyFriend(new ScheduleFragment.ApiCallback() {
                            @Override
                            public void onSuccess(String result, List list) {
                                arrFriendId = new ArrayList<Integer>();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Log.d(TAG, "addSchedule EventSetFragment");

//                                        if (result.equals("share")) {
                                            //공유된 스케줄일 경우 자기자신 추가.
//                                            arrFriendId.add(resultId);
//                                        }

                                        for (int i = 0; i < list.size(); i++) {
                                            ScheduleFriendItem item = (ScheduleFriendItem) list.get(i);

//                               Log.d(TAG, "list value ->" + item.getId());
                                            arrFriendId.add(item.getId());
                                        }
                                        DateTime dateTime = new DateTime();
                                        String today = dateTime.toString("yyyy-MM-dd HH:mm:ss");

                                        int jodaYear = Integer.parseInt(today.substring(0, 4));
                                        int jodaMonth = Integer.parseInt(today.substring(5, 7));
                                        int jodaDay = Integer.parseInt(today.substring(8, 10));

                                        Log.d(TAG, "joda result ->" + today + "--" + jodaYear + "/" + jodaMonth + "/" + jodaDay);
                                        Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                                        int nextId;

                                        if (currentIdNum == null) {
                                            nextId = 0;
                                        } else {
                                            nextId = currentIdNum.intValue() + 1;
                                        }
                                        //스케줄 저장
                                        ScheduleR schedule = realm.createObject(ScheduleR.class, nextId);
                                        Log.d(TAG, "eventFragment content ==>" + content);
                                        schedule.setTitle(content);
                                        schedule.setState(0);
                                        schedule.setColor(mEventSet.getColor());
                                        schedule.setEventSetId(mEventSet.getSeq());
                                        schedule.setTime(mTime);
                                        schedule.sethTime(resultTime);

                                        if (isSetTime) {
                                            schedule.setYear(mCurrentSelectYear);
                                            schedule.setMonth(mCurrentSelectMonth);
                                            schedule.setDay(mCurrentSelectDay);
                                        } else {
                                            schedule.setYear(jodaYear);
                                            schedule.setMonth(jodaMonth);
                                            schedule.setDay(jodaDay);
                                        }

                                        Log.d(TAG, "isSetTime local -->" + isSetTime + "--" + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay);
                                        new AddScheduleRTask(mActivity, new OnTaskFinishedListener<ScheduleR>() {
                                            @Override
                                            public void onTaskFinished(ScheduleR data) {
                                                Log.d(TAG, "EventSetFragment add" + data);

                                                if (data != null) {
                                                    mScheduleAdapter.insertItem(data);
                                                    etInput.getText().clear();
                                                    rlNoTask.setVisibility(View.GONE);
                                                    mTime = 0;
                                                    addScheduleServer();

                                                }
                                            }
                                        }, schedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                });

                            }
                            @Override
                            public void onFail(String result) {

                            }
                    });
        }
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
    public void addScheduleServer() {
        int resultId = pref.getInt("loginId", 0);

        int retMonth = mCurrentSelectMonth +1;
        Log.d(TAG, "addScheduleServer -->" + resultId);
        Log.d(TAG, "isSetTime server -->" + isSetTime + "--" + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay + "/" + resultTime);
        DateTime dateTime = new DateTime();
        String today = dateTime.toString("yyyy-MM-dd HH:mm:ss");

        int jodaYear = Integer.parseInt(today.substring(0, 4));
        int jodaMonth = Integer.parseInt(today.substring(5,7));
        int jodaDay = Integer.parseInt(today.substring(8,10));
        Log.d(TAG, "addSchedule ->" + content+ "--" + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay);
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("title", content);
        jsonObject.addProperty("state", 0); //최초 0
        if (isSetTime) {
            jsonObject.addProperty("start_time", mCurrentSelectYear +"-"+mCurrentSelectMonth+"-"+mCurrentSelectDay+" " + resultTime);
        } else {
            jsonObject.addProperty("start_time", jodaYear + "-" +jodaMonth + "-" +jodaDay + " "+ "00:00:00");
        }
        jsonObject.addProperty("content", "");
        jsonObject.addProperty("latitude", 0);
        jsonObject.addProperty("longitude", 0);

        for (int id : arrFriendId) {
            jsonArray.add(id);
        }
//        jsonObject.addProperty("user_ids", [1]);

        jsonObject.add("users_ids", jsonArray);
        Log.d(TAG, "jsonObject add ->" + jsonObject + "--" + authToken);

        Retrofit retrofit = APIClient.getClient();
        APIInterface postNewSche = retrofit.create(APIInterface.class);
        Call<JsonObject> result = postNewSche.postNewSchedule(jsonObject,  authToken);

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String success = response.body().toString();
                    Log.d(TAG, "success schedule -->" + success);
                } else  {
                    try {
                        String error = response.errorBody().string();

                        Log.d(TAG, "error schedule -->" + error);

                        Log.d(TAG, "response new friend error ->" + error);

                        Result result = new Gson().fromJson(error, Result.class);

                        List<String> message = result.message;

                        if (message.contains("Unauthorized auth_token.")) {
                            Log.d(TAG, "message -->" + message);
                            Toast.makeText(getActivity(), "auth token error ", Toast.LENGTH_LONG).show();
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

    /**
     * 자신과 친구 인 유저
     * Friends Search API
     */
    public void getMyFriend(final ScheduleFragment.ApiCallback callback) {
        Retrofit retrofit = APIClient.getClient();
        APIInterface getFriendAPI = retrofit.create(APIInterface.class);
        Call<JsonArray> result = getFriendAPI.getFriendSearch(authToken);

        resultId = pref.getInt("loginId", 0);

        result.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {

                    arrName = new ArrayList<>();
                    arrId = new ArrayList<>();

                    String strFriend = response.body().toString();

                    Type list = new TypeToken<List<UserJsonData>>(){
                    }.getType();

                    List<UserJsonData> userData = new Gson().fromJson(strFriend, list);

                    arrId.add(resultId);
                    arrName.add("나");

                    for (int i = 0; i < userData.size(); i++) {
                        id = userData.get(i).id;
                        name = userData.get(i).name;

                        int assent = userData.get(i).assent;
                        Log.d(TAG, "getMyFriend data -->" + name + "--"+ assent);

                        //친구 가 서로 완료된 인원만.
                        if (assent == 2) {
                            arrId.add(id);
                            arrName.add(name);
                        }

                    }
                    //친구가 없을 경우 처리 해야 함.
                    final ScheduleFriendFragment sf = ScheduleFriendFragment.getInstance(arrId, arrName, callback);
                    final FragmentManager fm = getActivity().getFragmentManager();

                    sf.show(fm, "TAG");
//                    callback.onSuccess("success");
//                    final

                } else {
                    try {
                        String error = response.errorBody().string();

                        Result result = new Gson().fromJson(error, Result.class);

                        List<String> message = result.message;

                        if (message.contains("Unauthorized auth_token.")) {
                            Toast.makeText(getContext(), "친구 목록 얻기에 실패했습니다. 재로그인해주세요.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


    }
    //현재 날짜
    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    @Override
    public void onSelectDate(int year, int month, int day, long time, int position) {
//        Log.d(TAG, "onSelectData -->" +month);
        setCurrentSelectDate(year, month, day);

        mTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
        resultTime = sdf.format(mTime);

        Log.d(TAG, "mTime -->" + mTime + "--" + resultTime);

        isSetTime = true;
        mPosition = position;
    }

    /**
     * data 대신 resultEvent 값을 받아 처리해야함.
     *
     * @param data
     */
    @Override
    public void onTaskFinished(List<ScheduleR> data) {
        Log.d(TAG, "Event Task Finish -->" + mEventSet.getSeq());


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "get realm year- >" + resultYear);

                if (resultYear == 0) {
                    resEmp = realm.where(ScheduleR.class).equalTo("eventSetId", mEventSet.getSeq()).findAll();
                } else {
                    resEmp = realm.where(ScheduleR.class).equalTo("eventSetId", mEventSet.getSeq()).equalTo("year", resultYear).findAllSorted("id");
                }
                Log.d(TAG, "resEmp Get -->" +  resEmp.size());

                resList.addAll(resEmp);   //resList에 해당 스케줄 항목인 스케줄들을 추가.

                for (int i = 0 ; i < resList.size(); i++) {
                    Log.d(TAG, "check data -->" + resList.get(i).getTitle());
                }
            /*    for (ScheduleR sd : resList) {
                    int id = sd.getId();
                    int color = sd.getColor();
                    String title = sd.getTitle();
                    String desc = sd.getDesc();
                    String location = sd.getLocation();
                    int state = sd.getState();
                    int year = sd.getYear();
                    int month = sd.getMonth();
                    int day = sd.getDay();
                    long time = sd.getTime();
                    int eventId = sd.getEventSetId();
                    String hTime = sd.gethTime();


                }*/

            }
        });
        Log.d(TAG, "resList -->" + resList);
        resultYear = 0;
        mScheduleAdapter.changeAllData(resList);

        rlNoTask.setVisibility(resList.size() == 0 ? View.VISIBLE : View.GONE);

        resList.clear();

    }


    @Override
    public void onClick(ScheduleR schedule) {

        Log.d(TAG, "schedule fragment -->" + schedule.getSeq());
        startActivity(new Intent(getActivity(), ScheduleDetailActivity.class)
                                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule.getSeq()) //primary key seq
                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION,  -1));
    }

    @Override
    public void onReset() {

    }
}

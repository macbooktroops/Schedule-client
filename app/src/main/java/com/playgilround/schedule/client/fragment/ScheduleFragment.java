package com.playgilround.schedule.client.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.playgilround.schedule.client.activity.MainActivity;
import com.playgilround.schedule.client.activity.ScheduleDetailActivity;
import com.playgilround.schedule.client.adapter.ScheduleAdapter;
import com.playgilround.schedule.client.base.app.BaseFragment;
import com.playgilround.schedule.client.calendar.OnCalendarClickListener;
import com.playgilround.schedule.client.dialog.SelectDateDialog;
import com.playgilround.schedule.client.gson.UserJsonData;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.RealmArrayList;
import com.playgilround.schedule.client.realm.ScheduleR;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;
import com.playgilround.schedule.client.gson.Result;
import com.playgilround.schedule.client.schedule.ScheduleLayout;
import com.playgilround.schedule.client.schedule.ScheduleRecyclerView;
import com.playgilround.schedule.client.task.schedule.AddScheduleRTask;
import com.playgilround.schedule.client.task.schedule.LoadScheduleRTask;
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
 * 18-05-25
 * 스케줄 작성 메인화면 하단뷰
 */

public class ScheduleFragment extends BaseFragment implements OnCalendarClickListener,
        View.OnClickListener, ScheduleAdapter.ScheduleEvent,
        SelectDateDialog.OnSelectDateListener, OnTaskFinishedListener<List<ScheduleR>> {

    private ScheduleLayout scheduleLayout;

    private ScheduleRecyclerView rvSchedule;

    private EditText etInputContent;

    private RelativeLayout rlNoTask;
    private ScheduleAdapter mScheduleAdapter;

    private long mTime;

    //제대로 저장되었는지 확인 사이즈
    RealmResults<ScheduleR> resSchedule;
    static final String TAG = ScheduleFragment.class.getSimpleName();

    //Realm 정의 ArrayList
    ArrayList<RealmArrayList> arrSchedule;
    private String HUMAN_TIME_FORMAT = "";

    private String resultTime;
    String content;
//    private ScheduleAdapter

    Realm realm;
    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;
    public boolean isSetTime = false; //SelectDateDialog 에서 날짜 체크 후 선택 눌렀을 경우

    List<ScheduleR> schedule;
    SharedPreferences pref;
    String authToken;

    //친구 관련 ArrayList
    ArrayList<String> arrName;
    ArrayList<String> arrBirth;
    String name;



    public static ScheduleFragment getInstance() {
        return new ScheduleFragment();
    }
    //create fragment init
    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    protected void bindView() {
        scheduleLayout = searchViewById(R.id.sdLayout);
        etInputContent = searchViewById(R.id.etInputContent); //스케줄 입력 창

        rlNoTask = searchViewById(R.id.rlNoTask);
        scheduleLayout.setOnCalendarClickListener(this);

        realm = Realm.getDefaultInstance();

        HUMAN_TIME_FORMAT = getString(R.string.human_time_format2);

        pref =  getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        authToken = pref.getString("loginToken", "default");

        searchViewById(R.id.ibMainClock).setOnClickListener(this);
        searchViewById(R.id.ibMainOK).setOnClickListener(this);
        initScheduleList();
        initBottomInputBar();

    }

    @Override
    protected void initData() {
        super.initData();
        initDate();
    }

    //DateSetting
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        setCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void bindData() {
        super.bindData();
        resetScheduleList();
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        setCurrentSelectDate(year, month, day);
        Log.d(TAG, "onClickDate -------");
        resetScheduleList();
    }

    @Override
    public void onPageChange(int year, int month ,int day) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMainClock:
                showSelectDateDialog();
                //시간 설정이 가능한 다이얼로그?
                break;
            case R.id.ibMainOK:
                addSchedule();
                break;
        }
    }

    /**
     * 자신과 친구 인 유저
     * Friends Search API
     */
    public void getMyFriend(final FriendFragment.ApiCallback callback) {
        Retrofit retrofit = APIClient.getClient();
        APIInterface getFriendAPI = retrofit.create(APIInterface.class);
        Call<JsonArray> result = getFriendAPI.getFriendSearch(authToken);

        result.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {

                    arrName = new ArrayList<>();
                    arrBirth = new ArrayList<>();

                    String strFriend = response.body().toString();

                    Log.d(TAG, "getMyFriend...-->" + strFriend);

                    Type list = new TypeToken<List<UserJsonData>>() {
                    }.getType();

                    List<UserJsonData> userData = new Gson().fromJson(strFriend, list);

                    for (int i = 0; i < userData.size(); i++) {
                        name = userData.get(i).name;
                        int assent = userData.get(i).assent;

                        Log.d(TAG, "getMyFriend data -->" + name + "--"+ assent);

                        //친구 가 서로 완료된 인원만.
                        if (assent == 2) {
                            arrName.add(name);
                        }

                    }

                    //친구가 없을 경우 처리 해야 함.
                    final ScheduleFriendFragment sf = ScheduleFriendFragment.getInstance(arrName);
                    final FragmentManager fm = getActivity().getFragmentManager();

                    sf.show(fm, "TAG");
                    callback.onSuccess("success");
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
                Log.d(TAG, "response getMyFriend failure..." + t.toString());

            }
        });

    }
    //스케줄 추가
    private void addSchedule() {
        content = etInputContent.getText().toString();

        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShortToast(mActivity, R.string.schedule_input_content_is_no_null);
        } else {
            closeSoftInput();

            getMyFriend(new FriendFragment.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Log.d(TAG, "Try save");

                            Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                            int nextId;

                            if (currentIdNum == null) {
                                nextId = 0;
                            } else {
                                nextId = currentIdNum.intValue() + 1;
                            }

                            ScheduleR schedule = realm.createObject(ScheduleR.class, nextId);
                            schedule.setTitle(content);
                            schedule.setState(0);
                            schedule.setTime(mTime);
                            schedule.sethTime(resultTime);
                            schedule.setYear(mCurrentSelectYear);
                            schedule.setMonth(mCurrentSelectMonth +1);
                            schedule.setDay(mCurrentSelectDay);
//
                            new AddScheduleRTask(mActivity, new OnTaskFinishedListener<ScheduleR>() {
                                @Override
                                public void onTaskFinished(ScheduleR data) {
                                    Log.d(TAG, "AddScheduleTask finish ->" + data);
                                    if (data != null) {
                                        mScheduleAdapter.insertItem(data);
                                        etInputContent.getText().clear();
                                        rlNoTask.setVisibility(View.GONE);
                                        mTime = 0;
                                        updateTaskHintUi(mScheduleAdapter.getItemCount() - 2);
                                        addScheduleServer(data);
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
    public void addScheduleServer(ScheduleR data) {
        int resultId = pref.getInt("loginId", 0);

        DateTime dateTime = new DateTime();
        String today = dateTime.toString("yyyy-MM-dd HH:mm:ss");

        int retMonth = mCurrentSelectMonth +1;
        Log.d(TAG, "addScheduleServer -->" + resultId + isSetTime);
        Log.d(TAG, "addSchedule ->" + content+ "--" + mCurrentSelectYear + "/" + retMonth + "/" + mCurrentSelectDay + resultTime);
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("title", data.getTitle());
        jsonObject.addProperty("state", data.getState()); //최초 0
//        jsonObject.addProperty("start_time", mCurrentSelectYear +"-"+retMonth+"-"+mCurrentSelectDay+" 00:00:00");

//        if (isSetTime) {
            jsonObject.addProperty("start_time", data.getYear() +"-"+data.getMonth()+"-"+data.getDay()+" " + data.gethTime());
//        } else {
//            jsonObject.addProperty("start_time", today);
//        }
        jsonObject.addProperty("content", data.getDesc());
        jsonObject.addProperty("latitude", data.getLatitude());
        jsonObject.addProperty("longitude", data.getLongitude());
        jsonArray.add(resultId);
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
                resultTime = "0";
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "fail schedule -> " + t.toString());
            }
        });
    }



    private void closeSoftInput() {
        etInputContent.clearFocus();
        DeviceUtils.closeSoftInput(mActivity, etInputContent);
    }

    //스케줄 작성 전 시작 시간을 적을 수 있는 다이얼로그.
    private void showSelectDateDialog() {
        Log.d(TAG, "scheduleFrags -> " + mCurrentSelectMonth);
         new SelectDateDialog(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay,
                 scheduleLayout.getMonthCalendar().getCurrentItem()).show();
    }
    //스케줄 리스트 리셋
    public void resetScheduleList() {
        //병렬로 작업을 실행하는 데 사용할 수있는 실행
//        new LoadScheduleTask(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadScheduleRTask(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth +1, mCurrentSelectDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    //현재 년월일로 세팅
    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;

        //객체 검사
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).resetMainTitleDate(year, month, day);
        }
    }
    //스케줄 리스트 생성
    private void initScheduleList() {
        rvSchedule = scheduleLayout.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSchedule.setLayoutManager(manager);
        Log.d(TAG, "rvSchedule --->" + rvSchedule);
//        rvSchedule.setLayoutManager(manager);
        //item view가 추가/삭제/이동 할때 animation
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        rvSchedule.setItemAnimator(itemAnimator);


        mScheduleAdapter = new ScheduleAdapter(mActivity, this);
        rvSchedule.setAdapter(mScheduleAdapter);

    }

    //스케줄 메모 부분 InputBar
     private void initBottomInputBar() {

        //http://egloos.zum.com/killins/v/3008925  --> TextWatcher
        etInputContent.addTextChangedListener(new TextWatcher() {
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
                etInputContent.setGravity(s.length() == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
            }
        });

        etInputContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
     }

     //SelectDateDialog override
    @Override
    public void onSelectDate(final int year, final int month, final int day, long time, int position) {
        scheduleLayout.getMonthCalendar().setCurrentItem(position);

        scheduleLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "year select ->" + month);
                scheduleLayout.getMonthCalendar().getCurrentMonthView().clickThisMonth(year, month -1, day);
            }
        }, 100);
        mTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
        isSetTime = true;

        if (mTime == 0) {
            resultTime = null;
        } else {
            resultTime = sdf.format(mTime);
        }

    }

    /**
     *
     *  문제로 인해 asynctask가 끝난후 Realm Transaction 실행
     * @param data
     */
    @Override
    public void onTaskFinished(final List<ScheduleR> data) {
//        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Log.d(TAG, "getScheduleByDate ------ " + mCurrentSelectYear + "--" + mCurrentSelectMonth +1 + "--" + mCurrentSelectDay);
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                        .equalTo("year", mCurrentSelectYear)
                        .equalTo("month", mCurrentSelectMonth +1)
                        .equalTo("day", mCurrentSelectDay).findAll();
                Log.d(TAG, "check size ->" +scheduleR.size());

                schedule = data;
                Log.d(TAG, "ScheduleFragment onTaskFinished --> " + scheduleR);
                mScheduleAdapter.changeAllData(scheduleR);

                //스케줄이 하나도 없으면 이미지.
                rlNoTask.setVisibility(scheduleR.size() == 0 ? View.VISIBLE : View.GONE);
                updateTaskHintUi(scheduleR.size());


            }
        });

    }

    private void updateTaskHintUi(int size) {
        if (size == 0) {
            scheduleLayout.removeTaskHint(mCurrentSelectDay);
        } else {
            scheduleLayout.addTaskHint(mCurrentSelectDay);
        }
    }

    public int getCurrentCalendarPosition() {
        Log.d(TAG, "getCurrentCalendar -->" + scheduleLayout.getMonthCalendar().getCurrentItem());
        return scheduleLayout.getMonthCalendar().getCurrentItem();
    }

    @Override
    public void onClick(ScheduleR schedule) {
        Log.d(TAG," schedule onClick --->" + schedule.getSeq());

        startActivity(new Intent(getActivity(), ScheduleDetailActivity.class)
                //https://stackoverflow.com/questions/40648380/how-to-pass-data-via-intent-in-realm
                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule.getSeq()) //primary key seq
                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION, getCurrentCalendarPosition()));
    }

    @Override
    public void onReset() {
        Log.d(TAG, "onReset");
        resetScheduleList();
    }
}

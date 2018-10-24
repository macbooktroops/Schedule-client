package com.playgilround.schedule.client.calendar.month;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.calendar.CalendarUtils;
import com.playgilround.schedule.client.gson.HolidayJsonData;
import com.playgilround.schedule.client.realm.ScheduleR;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;
import com.playgilround.schedule.client.gson.Result;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * 18-05-27
 * Month 월마다 화면 뷰
 */
public class MonthView extends View {

    private static final int NUM_COLUMNS = 7; //요일 7개
    private static final int NUM_ROWS = 6; //6주까지
    private Paint mPaint;
    private Paint mLunarPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mHintCircleColor;
    private int mLunarTextColor;
    private int mHolidayTextColor;
    private int mLastOrNextMonthTextColor;

    private int mDaySize;
    private int mLunarTextSize;
    private int[] mHolidays;

    private boolean mIsShowLunar;
    private boolean mIsShowHint;
    private boolean mIsShowHolidayHint;

    private int mSelYear, mSelMonth, mSelDay; //선택한 연월

    private int mWeekRow;
    private int mColumnSize, mRowSize, mSelectCircleSize;

    private int mCurrYear, mCurrMonth, mCurrDay; //현재  연월일
    private DisplayMetrics mDisplayMetrics;

    private int[][] mDaysText;
    private String[][] mHolidayOrLunarText;
    private OnMonthClickListener mDateClickListener;

    private int mCircleRadius = 6;

    private GestureDetector mGestureDetector; //터치 이벤트처
    static final String TAG = MonthView.class.getSimpleName();

    Realm realm;

    SharedPreferences pref;


    int resultMinHoliday; //해당 연월에 공휴일 min
    ArrayList<Integer> arrHoliday = new ArrayList<Integer>(); //해당 연월에 공휴일 ArrayList
    ArrayList<Integer> newArrHoliday; //공휴일 중복처리가 완료된 ArrayList
    int resultSeq; //공휴일 오름차순이 된 후에, 배열 순서.

    ArrayList<Integer> arrScheData = new ArrayList<Integer>(); //해당 연월에 등록된 스케줄 ArrayList
    ArrayList<Integer> newArrScheData;

    int resultScheSeq; //스케줄 등록된 오름차순이 된 후에, 배열 순서.
    int resultMinScheData; //해당 연월일에 스케줄 min

    private boolean isRealm = true; //최초 MonthView에만 실행

    private int chkYear;

    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this (context, array , attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttrs, int year, int month) {
        super(context, attrs, defStyleAttrs);

        realm = Realm.getDefaultInstance();

        initAttrs(array, year, month);
        initPaint();
        initMonth();
        initGestureDetector();
    }



    //월 뷰 기본색상설정
    private void initAttrs(TypedArray array, int year, int month) {
        Log.d(TAG, "initAttrs array -->" + array.toString());
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_text_color, Color.parseColor("#FFFFFF")); //white
            mSelectBGColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_color, Color.parseColor("#E8E8E8")); //살짝 흰색? 그레이
            mSelectBGTodayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_today_color, Color.parseColor("#FF8594")); //분홍빨강

            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#575471")); //흑색에 가까운 블루
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#FF8594")); //분홍 빨강
            mHintCircleColor = array.getColor(R.styleable.MonthCalendarView_month_hint_circle_color, Color.parseColor("#FE8595")); //분홍 빨강
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#ACA9BC")); //흑색 블루

            mLunarTextColor = array.getColor(R.styleable.MonthCalendarView_month_lunar_text_color, Color.parseColor("#ACA9BC")); //흑색 블루
            mHolidayTextColor = array.getColor(R.styleable.MonthCalendarView_month_holiday_color, Color.parseColor("#04B404")); //보라색

            mDaySize = array.getInteger(R.styleable.MonthCalendarView_month_day_text_size, 13);
            mLunarTextSize = array.getInteger(R.styleable.MonthCalendarView_month_day_lunar_text_size, 8);

            mIsShowHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.MonthCalendarView_month_show_lunar, true);
            mIsShowHolidayHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_holiday_hint, true);
        } else {

            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mSelectBGTodayColor = Color.parseColor("#FF8594");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLastOrNextMonthTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#04B404");
            mDaySize = 13;
            mLunarTextSize = 8;
            mIsShowHint = true;
            mIsShowLunar = true;
            mIsShowHolidayHint = true;
        }
        mSelYear = year;
        mSelMonth = month;
        mHolidays = CalendarUtils.getInstance(getContext()).getHolidays(mSelYear, mSelMonth  + 1);


        Calendar calendar = Calendar.getInstance();

        chkYear = calendar.get(Calendar.YEAR);

        Log.d(TAG, "mSel Year/Month --> " + mSelYear + "-" + chkYear);
        int resultYear = mSelYear - chkYear;
        Log.d(TAG, "test mSelYear - chkYear ="+ resultYear);

        //현재 년도와, 스케줄상에 년도가 2년이상 차이일 경우
        //1월, 12월 = 작년, 다음년도가 되기 1달전에 실행됨.
        if (resultYear <= -1 || resultYear >= 1) {
            Log.d(TAG, "check try holiday info");

            //먼저 공휴일 데이터가 있는지 검사
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(final Realm realm) {
                    RealmResults<ScheduleR> scheduleY = realm.where(ScheduleR.class)
                            .equalTo("eventSetId", -1).equalTo("year", mSelYear).findAll();

                    Log.d(TAG, "scheduleY size -->" + scheduleY.size());
                    int size = scheduleY.size();

                    if (size == 0) {
                        //공휴일 정보를 mSelYear 기준으로 얻어옴
                        Log.d(TAG, "Try mSelYear getHoliday.... -->" +mSelYear);

                        Retrofit retrofit = APIClient.getClient();
                        APIInterface moreHolidayAPI = retrofit.create(APIInterface.class);
                        retrofit2.Call<ArrayList<JsonObject>> res = moreHolidayAPI.getListHoliday(mSelYear);
//                        final retrofit2.Call<ArrayList<JsonObject>> res = RequestHoliday.getInstance().getService().getListHoliday(mSelYear);

                        res.enqueue(new retrofit2.Callback<ArrayList<JsonObject>>() {
                            @Override
                            public void onResponse(retrofit2.Call<ArrayList<JsonObject>> call, final Response<ArrayList<JsonObject>> response) {
                                Log.d(TAG, " check this year retrofit->" + mSelYear);
                                Log.d(TAG, "MonthView Retrofit --->" + response.body().toString());

                                /**
                                 * ScheduleR Table eventSetId = '-1'공휴일
                                 * 현재 년도에 2년 이전, 이후 공휴일을 받아오지만
                                 * 서버는 +-1 년도 공휴일을 보내주기때문에
                                 * 양 끝 공휴일이 덮어씌워지게됨.
                                 */

                                 realm.executeTransaction(new Realm.Transaction() {
                                     @Override
                                     public void execute(Realm realm) {
                                         try {
                                             Log.d(TAG, "MonthView HolidayCheck ....");
                                             JSONArray jsonArray = new JSONArray(response.body().toString());
                                             Type list = new TypeToken<List<HolidayJsonData>>() {
                                             }.getType();

                                             List<HolidayJsonData> holidayList = new Gson().fromJson(jsonArray.toString(), list);

                                             Log.d(TAG, "MonthView holiday response -->" + jsonArray);
                                             Log.d(TAG, "MonthView HolidayList size ->" + holidayList.size());
                                             Log.d(TAG, "MonthView holiday gson ->" + holidayList.get(0).toString());


                                             Log.d(TAG, "Start Insert REalm....");

                                             for (HolidayJsonData resHoliday : holidayList) {
                                                 Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                                                 int nextId;

                                                 if (currentIdNum == null) {
                                                     nextId = 0;
                                                 } else {
                                                     nextId = currentIdNum.intValue() +1;
                                                 }

                                                 ScheduleR holidayR = realm.createObject(ScheduleR.class, nextId);

                                                 Log.d(TAG, "holiday data id MonthView ->" + resHoliday.id + "//" + resHoliday.year + ":" + resHoliday.month + ":" + resHoliday.day + ":" + resHoliday.name);

                                                 holidayR.setId(resHoliday.id);
                                                 holidayR.setYear(resHoliday.year);
                                                 holidayR.setMonth(resHoliday.month);
                                                 holidayR.setDay(resHoliday.day);
                                                 holidayR.setTitle(resHoliday.name);
                                                 holidayR.setEventSetId(-1);
                                                 holidayR.setColor(-1);
                                                 holidayR.setState(-1);
                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 });
                            }

                            @Override
                            public void onFailure(retrofit2.Call<ArrayList<JsonObject>> call, Throwable t) {
                                Log.d(TAG, "Fail MonthView ---> " +t.toString());
                            }
                        });
                    } else {
                        Log.d(TAG, "MonthView Already exist..");
                    }


                }
            });

          /*  //달력이 1월,12월일때 전년,다음년도에 등록된 스케줄 정보 얻기.
            pref = getContext().getSharedPreferences("loginData", Activity.MODE_PRIVATE);

            String authToken = pref.getString("loginToken", "");

            Log.d(TAG, "monthView authToken -->" + authToken +"/" + mSelYear);
            Retrofit retrofit = APIClient.getClient();
            APIInterface searchScheAPI = retrofit.create(APIInterface.class);
            retrofit2.Call<ArrayList<JsonObject>> result = searchScheAPI.getSearchSchedule(authToken, mSelYear);


            result.enqueue(new Callback<ArrayList<JsonObject>>() {

                String error;
                @Override
                public void onResponse(retrofit2.Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {

                    if (response.isSuccessful()) {
                        Log.d(TAG, "search schedule success-->" + response.body().toString());
                    } else {
                        try {
                            error = response.errorBody().string();
                            Log.d(TAG, "search schedule error -->" + error);

                            Result result = new Gson().fromJson(error, Result.class);

                            List<String> message = result.message;

                            if (message.contains("Unauthorized auth_token.")) {
                                Toast.makeText(getContext(), "Auth Token Error..", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ArrayList<JsonObject>> call, Throwable t) {
                    Log.d(TAG, "search schedule Failure -->" + t);
                }
            });*/
        }



    }

    //스케줄 화면 텍스트 세팅
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);

        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);

    }

    //스케줄 월 날짜 세팅
    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);

        Log.d(TAG, "check initMonth -->" + mCurrYear + "/" + mCurrMonth + "/" + mCurrDay );

        //현재 연월이 선택한 연월이랑 같을경우
        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }

    }


    @Override
    protected void onMeasure(int width, int height) {
        int widthSize = MeasureSpec.getSize(width);
        int widthMode = MeasureSpec.getMode(width);

        int heightSize = MeasureSpec.getSize(height);
        int heightMode = MeasureSpec.getMode(height);

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }

        setMeasuredDimension(widthSize, heightSize);

    }


    //그리기
    @Override
    protected void onDraw(Canvas canvas) {

        Log.d(TAG, "onDraw~~~");
        initSize();
        clearData();
//        getScheduleData();
        getHolidayInfo();

        drawLastMonth(canvas);
        int selected[] = drawThisMonth(canvas);
        drawNextMonth(canvas);

        drawHintCircle(canvas);
//        drawHoliday(canvas);

    }

    private void initSize() {
        Log.d(TAG, "initSize =====");
        mColumnSize = getWidth() / NUM_COLUMNS; //일월화수목금토 7개 사이즈 나누기
        mRowSize = getHeight() / NUM_ROWS; //최대 6주로 사이즈 계산

        mSelectCircleSize = (int) (mColumnSize / 3.2);

        while (mSelectCircleSize > mRowSize /2) {
            Log.d(TAG, "initSize mSelectCircleSize -->" + mSelectCircleSize + "::rowSize ->" + mRowSize);
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }

    }

    //날짜 텍스트, 휴일,음력 배열 세팅
    private void clearData() {
        Log.d(TAG, "clearData =====");
        mDaysText = new int[6][7]; //6주//7일
        mHolidayOrLunarText = new String[6][7]; //음력도 6주//7일//
    }

    //이번연월 공휴일 얻기
    private void getHolidayInfo() {
        Log.d(TAG, "getHoliday");
//        resultSeq = 0;


        //해당 연월에 공휴일 정보
//        final ArrayList<Integer> arrHoliday = new ArrayList<Integer>();
//        ScheduleR holiSchedule;

//         int resultMonth = mSelMonth +1;
        Log.d(TAG, "realm...-->"+ realm);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                /**
                 * 해당 연월에 공휴일인 데이터 select
                 */
                RealmResults<ScheduleR> scheduleH = realm.where(ScheduleR.class)
                        .equalTo("eventSetId", -1).equalTo("year", mSelYear).equalTo("month", mSelMonth +1).findAll();

                /**
                 * 해당 연월에 등록된 스케줄 데이터 select
                 * eventSetId = '-1' 은 공휴일이니, 제외하고 검색.
                 */
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                        .notEqualTo("eventSetId", -1).equalTo("year", mSelYear).equalTo("month", mSelMonth + 1).findAll();

                Log.d(TAG, "getScheduleData size -->" + scheduleR.size());

//                Log.d(TAG, "scheduleR result ->" + scheduleR.size());
//                Log.d(TAG, "month ----->" + (mSelMonth-1));
//                mSelMonth -1;

                //해당 연월 공휴일 데이터 arrHoliday에 add
                for (ScheduleR holiSchedule : scheduleH) {
                    Log.d(TAG, "info data ->" + holiSchedule.getTitle());
                    Log.d(TAG, "info year month -> "+ holiSchedule.getYear() + "--" + holiSchedule.getMonth() + "--" + holiSchedule.getDay());

                    arrHoliday.add(holiSchedule.getDay());
                }

                //해당 연월 저장된 스케줄 데이터 arrScheData에 add
                for (ScheduleR scheduleChk : scheduleR) {
                    Log.d(TAG, "info schedule data ->" + scheduleChk.getTitle());
                    arrScheData.add(scheduleChk.getDay());
                }

                // HashSet 데이터 형태로 생성되면서 중복 제거됨
                // 해당 연월일에 공휴일이 중복될 경우 하나로 처리.
                HashSet hs = new HashSet(arrHoliday);


                newArrHoliday = new ArrayList<Integer>(hs);
                for (int i =0; i < arrHoliday.size(); i++ ) {
                    Log.d(TAG, "arrHoliday result -> " +arrHoliday);
                }

                for (int i = 0; i < newArrHoliday.size(); i++) {
                    Log.d(TAG, "newArrHoliday result ->"  +newArrHoliday);
                }

                /**
                 * HashSet 데이터 형태로 생성되면서 중복 제거됨
                 * 해당 연월일에 스케줄이 중복될 경우
                 * 추후에 우선순위 세워서 처리 할 예정
                 */
                HashSet hsData = new HashSet(arrScheData);

                newArrScheData = new ArrayList<>(hsData);

                for (int i = 0; i < newArrScheData.size(); i++) {
                    Log.d(TAG, "newArrScheData result ->"  +newArrScheData);
                }

               if (scheduleH.size() == 0) {

               } else {
                    arrHoliday.clear();
                   Collections.sort(newArrHoliday); //오름차순 정렬

                   Log.d(TAG, "resultSeq ->" + resultSeq);
                   resultMinHoliday = newArrHoliday.get(resultSeq);
                   Log.d(TAG, "the smallist value ->" + resultMinHoliday);
               }

                if (scheduleR.size() == 0) {

                } else {
                    arrScheData.clear();
                    Collections.sort(newArrScheData); //오름차순


//                    for (int i = 0; i < newArrScheData.size(); i++) {
                    Log.d(TAG, "newArrScheData sort ->"  +newArrScheData);
//                    }

                    resultMinScheData = newArrScheData.get(resultScheSeq);
                    Log.d(TAG, "the smallist schedule value ->" + resultMinScheData);


                }
//               arrHoliday.clear(); //작업 완료 시 클리어
//                int smallest = arrHoliday.get(0);
//                for (int x : arrHoliday) {
//                    if (x < smallest) {
//                        smallest = x;
//                    }
//                }
//
//                Log.d(TAG, "smalllist -->" + smallest);
            }
        });

    }

    //지난 달 그리기
    private void drawLastMonth(Canvas canvas) {
        int lastYear, lastMonth;
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        Log.d(TAG, "drawLastMonth =====" + lastYear + "/" + lastMonth);

        mPaint.setColor(mLastOrNextMonthTextColor);
        int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth); //세팅 연월에 최대 일이 몇개인지...
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth); //선택 연월에 1일이 몇요일인지
        /** 일요일1 ~ 토요일7
         * weeknumber =6 monthdays =30 ,
         * day = 5 (Weeknumber -1 ) 배열 0 부터시작
         * 첫째 주 그리기
          */
        for (int day = 0; day < weekNumber -1; day++) {

            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            Log.d(TAG, "drawLastMonth mDaysText --->"  + mDaysText[0][day]);
            String dayString = String.valueOf(mDaysText[0][day]);
            int startX = (int) (mColumnSize * day + (mColumnSize - mPaint.measureText(dayString)) /2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) /2);

            Log.d(TAG, "drawLastMonth for -->" + dayString + "--"+ day + "--" + monthDays +"--"+ weekNumber);
            canvas.drawText(dayString, startX, startY, mPaint);

            mHolidayOrLunarText[0][day] = CalendarUtils.getHolidayFromSolar(lastYear, lastMonth, mDaysText[0][day]);
        }


    }

    //이번 달 그리기
    private int[] drawThisMonth(Canvas canvas) {
        Log.d(TAG, "drawThisMonth ======");
        String dayString;

        int selectedPoint[] = new int[2];
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth); //해당 연월 일개수
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth); //해당 연월 1일 요일

        Log.d(TAG, "check This Month -->" + mSelYear + "-" + mSelMonth + "-"+ resultMinHoliday);
        Log.d(TAG, "resultMinHoliday --->" +resultMinHoliday);

        //해당 연월에 공휴일 정보
//        ScheduleR holiSchedule;
        /**
         * monthDays = 30
         * 일요일1 ~ 토요일7
         * weeknumber =6
         */
        for (int day = 0; day < monthDays; day ++) {
            dayString = String.valueOf(day + 1);
            int col = (day + weekNumber - 1) % 7; //컬럼 (0 + 5) % 7 = 5
            int row = (day + weekNumber - 1) / 7; // (0 + 29 -1 ) /7 = 4

            mDaysText[row][col] = day + 1;

            int startX = (int) (mColumnSize * col + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);

            //선택날짜랑 dayString 이 같을경우.
            Log.d(TAG, "dayResult -> " +dayString + "-" + mSelDay + "-" + mCurrDay);
            if (dayString.equals(String.valueOf(mSelDay))) {
                int startRecX = mColumnSize * col;
                Log.d(TAG, "drawThisMonth startRecX -->" + startRecX);

                int startRecY = mRowSize * row;
                Log.d(TAG, "drawThisMonth startRecY -->" + startRecY);

                int endRecX = startRecX + mColumnSize;
                Log.d(TAG, "drawThisMonth endRecX -->" + endRecX);

                int endRecY = startRecY + mRowSize;
                Log.d(TAG, "drawThisMOnth endRecY -->" + endRecY);


                if (mSelYear == mCurrYear && mSelMonth == mCurrMonth && day + 1 == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    mPaint.setColor(mSelectBGColor);
                }

                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
                mWeekRow = row + 1;
            }



            if (dayString.equals(String.valueOf(mSelDay))) {
                selectedPoint[0] = row;
                selectedPoint[1] = col;

                mPaint.setColor(mSelectDayColor);
                //day가 현재날짜랑 같을 경우?
            } else if (dayString.equals(String.valueOf(mCurrDay)) && mCurrDay != mSelDay && mCurrMonth == mSelMonth && mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
            }

            /**
             * View Holiday Calendar View
             */
            if (dayString.equals(String.valueOf(resultMinHoliday))) {
                //오름차순
//                Collections.sort(arrHoliday);
                for (int i = 0; i < newArrHoliday.size(); i++) {
                    Log.d(TAG, "check holiday value ->" + newArrHoliday + "--" + dayString);
                }


//                Log.d(TAG, "array -> " + arrHoliday.get(0).toString());
                mPaint.setColor(Color.GREEN);
//                resultMinHoliday =

                int resHoliSize = newArrHoliday.size();
                int resSeq = resultSeq +1;

                Log.d(TAG, "resSeq ->" + resSeq);
                 /**
                 * resultSeq +1 증가시켜, newArrHoliday.get(resultSeq)값과, 현재 resultMinHoliday 값비교.
                 */
//                Log.d(TAG, "resultMin ->" + resultMinHoliday + "--" + arrHoliday.get(resultSeq + 1));

                if (resSeq == resHoliSize) {
                    Log.d(TAG, "this holiday check finish");

                    resultSeq = 0;
                } else {
                    if (resultMinHoliday < newArrHoliday.get(resSeq)) {
                        /**
                         * 오름차순으로 정렬된 해당연월 공휴일 배열 newArrHoliday 에 위치를 +1 시킨 후
                         * resultMinHoliday와 비교 후 newArrHoliday.get(resultSeq +1) 값이 더 클경우
                         */
                        resultMinHoliday = newArrHoliday.get(resSeq);
                        Log.d(TAG, "resultMinHoliday result -> " + resultMinHoliday);

                        //size 4
                        resultSeq++;
                    }
                }
            }
            //공휴일 색깔은 진한 초록? 색으로 표시
//            else if () {

            else {
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);

            /**
             * View Saved Schedule Calendar View
             */
            if (dayString.equals(String.valueOf(resultMinScheData))) {
                Log.d(TAG, "Schedule Data ---- " + newArrScheData.size() + "--" + dayString);

                mPaint.setColor(Color.RED);

                int resScheSize = newArrScheData.size();
                int resScheSeq = resultScheSeq +1;

                Log.d(TAG, "resScheSeq -> " + resScheSeq);

                /**
                 * resultScheSeq +1 증가시켜,  newArrScheData.get(resultScheSeq)값과, 현재 resultMinScheData비교
                 */
                if (resScheSeq == resScheSize) {
                    Log.d(TAG, "this month schedule data check finish...");
                    resultScheSeq = 0; //init
                } else {
                    if (resultMinScheData < newArrScheData.get(resScheSeq)) {
                        /**
                         * 오름차순으러 정렬된 해당연월 스케줄 데이터 배열 newArrScheData 에 위치를 +1 시킨 후
                         * resultMinScheData와 비교 후 newArrScheData.get(resultScheSeq +1)값이 더 클 경우
                         */
                        resultMinScheData = newArrScheData.get(resScheSeq);
                        Log.d(TAG, "resultMinScheData result -->" + resultMinScheData);
                        Log.d(TAG, "resultMin Holiday before -->" + resultMinHoliday);
                        resultScheSeq++;
                    }
                }
            }
            canvas.drawText(dayString, startX, startY, mPaint);

            mHolidayOrLunarText[row][col] = CalendarUtils.getHolidayFromSolar(mSelYear, mSelMonth, mDaysText[row][col]);
//            newArrHoliday.clear(); //작업 완료 시 클리어

        }
        return selectedPoint;
    }

    //다음 달 그리기
    private void drawNextMonth(Canvas canvas) {
        mPaint.setColor(mLastOrNextMonthTextColor);

        Log.d(TAG, "drawNextMonth ---->" + mSelYear);
        //30
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth); //선택연월 일 개수 얻기

        //현재 요일 (일요일은 1, 토요일은 7)
        //해당 연월에 1일이 무슨요일인지 .
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);  //7

        int nextMonthDays = 42 - monthDays - weekNumber + 1; //6
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;

        if (nextMonth == 12) {
            //12월이 지나면
            nextMonth = 0;
            nextYear += 1;
        }

        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber -1 + day) % 7; //(30 + 7 - 1 + 5 ) % 7
            int row = 5 - (nextMonthDays - day - 1) /7; //(5 - (6 - 0 -1 ) /7;

            try {
                mDaysText[row][column] = day + 1;
                mHolidayOrLunarText[row][column] = CalendarUtils.getHolidayFromSolar(nextYear, nextMonth, mDaysText[row][column]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String dayString = String.valueOf(mDaysText[row][column]);
            Log.d(TAG, "dayString ---> " + dayString);

            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) /2);

            canvas.drawText(dayString, startX, startY, mPaint);

        }
    }

    /**
     * 서클 힌트 그리기
     */
    private void drawHintCircle(Canvas canvas) {
        if (mIsShowHint) {
            List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mSelYear, mSelMonth);

            if (hints.size() > 0) {
                mPaint.setColor(mHintCircleColor);

                int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth); //세팅연월 일 얻기
                int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth); //선택연월 1일 요일얻기

                for (int day = 0; day < monthDays; day++) {
                    int col = (day + weekNumber -1) % 7;
                    int row = (day + weekNumber -1) / 7;

                    //day +1 값이 hint에 없으면 continue;
                    if (!hints.contains(day + 1)) continue;
                    float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
                    float circleY = (float) (mRowSize * row + mRowSize * 0.75);
                    canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
                }
            }
        }
    }

    /**
     * Drawing Lunar Calendar
     */
    private void drawLunarText(Canvas canvas, int[] selected) {
        if (mIsShowLunar) {
            Log.d(TAG, "drawLunarText -->");
            int firstYear, firstMonth, firstDay, monthDays;

            //선택 연월 1일 요일구하기.
            //(일요일은 1, 토요일은 7)
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);

            if (weekNumber == 1) {
                //sunday
                firstYear = mSelYear;
                firstMonth = mSelMonth + 1;
                firstDay = 1;
                monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth); //연월 최대 몇일인지.
            } else {
                if (mSelMonth == 0) {
                    firstYear = mSelYear -1;
                    firstMonth = 1;;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = 12;
                } else {
                    firstYear = mSelYear;
                    firstMonth = mSelMonth - 1;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = mSelMonth;
                }
                firstDay = monthDays - weekNumber + 2;
            }
        }
    }

    //휴일 그리기
    private void drawHoliday(Canvas canvas) {
        if (mIsShowHolidayHint) {
//            Rect rect = new Rect(0, 0, mREst)
        }
    }

    //선택연월
    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }


    //터치 이벤트 설정
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override //화면 터치 모든이벤트의 시작
            public boolean onDown(MotionEvent e) {
                return true;
            }


            //한번 터치일 경우 발생 된다
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    //해당위치를 터치할 경우
    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;

        int row = y / mRowSize;
        Log.d(TAG, "doClickAction row -->" + y + "-" + mRowSize + "-" + row);
        int column = x / mColumnSize;
        Log.d(TAG, "doClickAction column --> " + x + "-" + mColumnSize + "-" + column);

        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;

        Log.d(TAG, "mDaysText value-->" + mDaysText[row][column]);
        if (row == 0 ) {
            Log.d(TAG, "doClickAction row 0 ..." );
            if (mDaysText[row][column] >= 23) {
                if (mSelMonth == 0) {
                    Log.d(TAG, "doClickAction Selmonth =0");
                    clickYear = mSelYear - 1;
                    clickMonth = 11;
                } else {
                    Log.d(TAG, "doClickAction not selmonth - 0" + mSelMonth);
                    clickYear = mSelYear;
                    clickMonth = mSelMonth - 1;
                }

                if (mDateClickListener != null) {
                    mDateClickListener.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                Log.d(TAG, "doClickAction clickThismonth");
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            Log.d(TAG, "doClickAction row not 0..");
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            Log.d(TAG, "monthDays --> " +monthDays);

            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            Log.d(TAG, "weekNumber --> " + weekNumber);

            int nextMonthDays = 42 - monthDays - weekNumber + 1;

            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                Log.d(TAG, "doClickAction click ..");
                if (mSelMonth == 11) {
                    clickYear = mSelYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth + 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
    }

    /**
     * 해당 날짜로 이동
     */
    public void clickThisMonth(int year, int month, int day) {
        if (mDateClickListener != null) {
            mDateClickListener.onClickThisMonth(year, month, day);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 현재 선택 년도 얻기
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 현재 선택 달 얻기
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 현재 선택 일 얻기
     */
    public int getSelectDay() {
        return this.mSelDay;
    }

    //week of the current month
    public int getWeekRow() {
        return mWeekRow;
    }


    //OnClickListener 강제 발생
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    public boolean addTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).addTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }
        return false;
    }
    public boolean removeTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).removeTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }

        return false;
    }
    /**
     * 날짜 클릭 리스너
     * @param dateClickListener
     */



    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }



}

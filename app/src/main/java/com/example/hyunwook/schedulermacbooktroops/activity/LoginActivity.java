package com.example.hyunwook.schedulermacbooktroops.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.example.common.realm.ScheduleR;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.holiday.HolidayJsonData;
import com.example.hyunwook.schedulermacbooktroops.holiday.RequestHoliday;
import com.example.hyunwook.schedulermacbooktroops.login.JsonLogin;
import com.example.hyunwook.schedulermacbooktroops.login.RequestLogin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hyunwook.schedulermacbooktroops.activity.RegisterActivity.getBase64encode;


public class LoginActivity extends Activity {

    EditText idInput, pwInput;
    CheckBox autoCheck;

    String loginId, loginPw;

    Button registBtn, loginBtn;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Boolean loginChecked;

    static final String TAG = LoginActivity.class.getSimpleName();

    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("registInfo", Activity.MODE_PRIVATE);
        editor = pref.edit();

        idInput = (EditText) findViewById(R.id.idInput);
        pwInput = (EditText) findViewById(R.id.pwInput);

        autoCheck = (CheckBox) findViewById(R.id.checkbox);

        registBtn = (Button) findViewById(R.id.registBtn);

        loginBtn = (Button) findViewById(R.id.loginBtn);

        realm = Realm.getDefaultInstance();


        /**
         * Retrofit Holiday 로그인하기전에 실행
         * 인터넷 연결 확인필
         */

        int nYear;

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        nYear = calendar.get(Calendar.YEAR);


        Log.d(TAG, "check this year ->" + nYear);
/*
        io.reactivex.Observable.zip(RequestHoliday.getInstance().getService().getListHoliday(nYear -1), RequestHoliday.getInstance().getService().getListHoliday(nYear), RequestHoliday.getInstance().getService().getListHoliday(nYear +1), (u1,  u2, u3) -> {
            return Arrays.asList(u1, u2, u3);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn( listOfUsers -> {

//                })*/
       /* Call<ArrayList<JsonObject>> res = RequestHoliday.getInstance().getService().getListHoliday(nYear);
//        android.database.Observable.
//
////        io.reactivex.Observable.combineLatest()
        res.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, final Response<ArrayList<JsonObject>> response) {
                Log.d(TAG, "Retrofit --->" + response.body().toString());
                    *//**
                     * ScheduleR Table에 eventSetId가 '-1'이면 공휴일로 판단.
                     *//*
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Log.d(TAG, "Holiday Check ----");
//                            RealmResults<HolidayR> holidayRS = realm.where(HolidayR.class).findAll();
                            RealmResults<ScheduleR> holidayRS = realm.where(ScheduleR.class).equalTo("eventSetId", -1).findAll();

                            Log.d(TAG, "holidayRS size ->" + holidayRS.size());


                            if (holidayRS.size() == 0) {
                                //ScheduleR Table 에 공휴일정보가 저장이 되어있지않음.
                                *//**
                                 * Use gson json parsing.
                                 *//*
                                try {
                                    Gson gson = new Gson();
                                    JSONArray jsonArray = new JSONArray(response.body().toString());
                                    Type list = new TypeToken<List<HolidayJsonData>>() {
                                    }.getType();
                                    List<HolidayJsonData> holidayList = gson.fromJson(jsonArray.toString(), list);


                                    Log.d(TAG, "holidayList ->" + holidayList.size());

                                 *//*   Number currentIdNum = realm.where(HolidayR.class).max("seq");

                                    int nextId;

                                    if (currentIdNum == null) {
                                        nextId = 0;
                                    } else {
                                        nextId = currentIdNum.intValue() + 1;
                                    }*//*
                                    Log.d(TAG, "Holiday Insert Realm ....");
                                    //print
                                    for (HolidayJsonData resHoliday : holidayList) {

                                        Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                                        int nextId;

                                        if (currentIdNum == null) {
                                            nextId = 0;
                                        } else {
                                            nextId = currentIdNum.intValue() + 1;
                                        }
                                        ScheduleR holidayR = realm.createObject(ScheduleR.class, nextId);

                                        Log.d(TAG, "holiday data id -> " + resHoliday.id + "//" + resHoliday.year + ":" + resHoliday.month + ":" + resHoliday.day + ":" + resHoliday.name);

                                        holidayR.setId(resHoliday.id);
                                        holidayR.setYear(resHoliday.year);
                                        holidayR.setMonth(resHoliday.month);
                                        holidayR.setDay(resHoliday.day);
                                        holidayR.setTitle(resHoliday.name);
                                        holidayR.setEventSetId(-1); //공휴일 EventSetId -1 고정
                                        holidayR.setColor(-1); //공휴일 하늘색.
                                        holidayR.setState(-1); //상태 -1 (좌측 노란색)
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "exist HolidayR");
                            }
                        }
                    });
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d(TAG, "Fail...");
            }
        });*/
        //로그인 버튼
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginId = idInput.getText().toString();
                loginPw = pwInput.getText().toString();
//                loginPw = getBase64encode(pwInput.getText().toString());

                Log.d(TAG, "loginPw base64 ->" + loginPw);


                //입력한 아이디, 비밀번호가 등록된 자료인지..
                if (loginId.equals("") || loginPw.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디나 비밀번호가 비어있습니다.", Toast.LENGTH_LONG).show();
                } else {

                    if (checkEmail(loginId)) {
                        Log.d(TAG, "이메일 형식입니다.");
                        if (checkPassWord(loginPw)) {
                            Boolean validation = loginValidation(loginId, loginPw);

                            if (validation) {
                                Log.d(TAG, "로그인 성공 ----");
                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                mainIntent.putExtra("SuccessLogin", "OK");
                                startActivity(mainIntent);
                                finish();
                            } else {

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "패스워드에 소문자, 특수문자, 숫자가 포함되어야합니다..", Toast.LENGTH_LONG).show();
                        }
                    } else {
                       Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //회원가입 버튼
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
      /*  if (pref.getString("prefId", "").equals("") || pref.getString("prefPw", "").equals("")) {
            Log.d(TAG, "서버에 저장된 정보가 없어 자동로그인을 진행할 수 없습니다..");
            autoCheck.setEnabled(false);
        } else {*/
        //자동 로그인 체크 이벤트
        autoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                if (isChecked) {
                    editor.putString("prefAutoLogin", "check");

                    loginChecked = true;
                } else {
                    editor.putString("prefAutoLogin", "uncheck");
                    //uncheck
                    loginChecked = false;
                }
                editor.commit();
            }

        });

    }

    /**
     * 이 부분에 자동로그인 검사를 넣는 이유는,
     * 회원가입 후에 로그인화면으로 올 때에 서버에 저장된 정보가 있으면
     * 다시 자동로그인 체크박스를 활성화 시켜주기위해.
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //이전에 자동로그인을 체크했나 ?

        String strAuto = pref.getString("prefAutoLogin", "");
        Log.d(TAG, "autoCheck -->" + strAuto);

        String autoId = pref.getString("prefEmail", "");
        String autoPw = getBase64decode(pref.getString("prefPw", ""));

        Log.d(TAG, "id info --> " + autoId + "////" + autoPw);
        //기존에 자동로그인을 체크했을 경우 아이디 비밀번호 표시
        if (strAuto.equals("check")) {
            idInput.setText(autoId);
            pwInput.setText(autoPw);
            autoCheck.setChecked(true);
        } else if (autoId.equals("") || autoPw.equals("")) {
            Log.d(TAG, "서버에 저장된 정보가 없어 자동로그인을 체크가 불가능합니다..");
            autoCheck.setEnabled(false);
        } else {
            Log.d(TAG, "체크 안한경우.");
            autoCheck.setChecked(false);
            autoCheck.setEnabled(true);
        }
    }

    //check login info validate

    /**
     * SharedPreference 에 데이터가 없으면, 회원가입이 필요.
     * SharedPreference 에 데이터가 있지만, 입력받은 정보랑 다르면 저장된 정보가 아니다.
     * 같을 경우에만 true
     *
     * Base64로 인코딩된 암호를 한번 디코딩 작업을 한 후, 입력받은 패스워드와 비교.
     * @param id
     * @param pw
     * @return
     */
    private boolean loginValidation(String id, String pw) {

//        pref.getString("prefEmail", "");
//        pref.getString("prefPw", "");

        /**
         * SignIn http://localhost:3000/users/sign_in
         */
        Log.d(TAG, "pw validate ->" + pw);

        String jsonUserInfo = "";
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject userJsonObject = new JSONObject();
            userJsonObject.put("email", "test@test.com");
            userJsonObject.put("password", "asd1234");

            jsonObject.put("user", userJsonObject);

            jsonUserInfo = new Gson().toJson(jsonObject);
            Log.v(TAG, jsonUserInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonUserInfo);
        Call<ArrayList<JsonObject>> res = RequestLogin.getInstance().getService().postSignIn(body);
        res.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                Log.d(TAG, "result Response -->" + response.body().toString());
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d(TAG, "result Failure -->" + t);
            }
        });
        return true;

/*
                        String idt = pref.getString("prefEmail", "");
        String pwt = pref.getString("prefPw", "");

        String base64pw = getBase64decode(pwt);
        Log.d(TAG, "idt --->" + idt + "--" + pwt + "--" + base64pw);
        //Preference 에 저장된 정보가 없어 검사를 할 수없을 경우 (최초)
        if (idt.equals("") || base64pw.equals("")) {
            Log.d(TAG, "회원가입이 필요합니다..");
            Toast.makeText(this, "회원가입이 필요합니다.", Toast.LENGTH_LONG).show();
            return false;
        }
        //입력받은 id,pw와 저장된 id,pw 같을경우
        else if (idt.equals(id) && base64pw.equals(pw)) {
            //login success
            return true;
        } else if (idt != id || base64pw != pw) {
            //저장 된 정보랑 다를 경우
            Log.d(TAG, "저장 된 정보가 아닙니다..");
            Toast.makeText(this, "저장 된 정보가 아닙니다.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            //login failed
            return false;
        }*/
    }

    /**
     * 이메일 형식 체크
     */
    private boolean checkEmail(String email) {
        String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(mail);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 패스워드 유효성검사
     * 특수문자, 숫자, 소문자 입력
     * 정규식 (영문, 숫자, 특수문자 조합, 4~20자리)
     */
    private boolean checkPassWord(String password) {
        String valiPass =  "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{4,20}$";
        Pattern pattern = Pattern.compile(valiPass);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /**
     * Base64 디코딩(해독) password
     * @param content
     * @return
     */
    public static String getBase64decode(String content){

        return new String(Base64.decode(content, 0));

    }
}
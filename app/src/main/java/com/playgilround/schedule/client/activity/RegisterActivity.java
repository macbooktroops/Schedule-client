package com.playgilround.schedule.client.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.calendar.widget.calendar.retrofit.Result;
import com.playgilround.schedule.client.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends Activity {
    Button regBtn;
    EditText editEmail, editPw, editConfirmPw, editNickName, editPhone, editBirth;

    String strEmail, strPw, strConfirmPw, strNickName, strPhone, strBirth;

    String base64Pw;

    static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        editEmail = (EditText) findViewById(R.id.emailInput);
        editPw = (EditText) findViewById(R.id.pwInput);
        editConfirmPw = (EditText) findViewById(R.id.pwConfirmInput);
        editNickName = (EditText) findViewById(R.id.nicknameInput);
        editPhone = (EditText) findViewById(R.id.phoneInput);
        editBirth = (EditText) findViewById(R.id.birthInput);


        editBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (editBirth.getText().toString().trim().length() != 9) {
                        editBirth.setError("8자리로 생년월일 입력해주세요.");
                        Log.d(TAG, "EditBirth error");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error2");

                        editBirth.setError(null);
                    }
                //포커스 해제
                } else {
                    if (editBirth.getText().toString().trim().length() != 9) {
                        Log.d(TAG, "EditBirth error3");

                        editBirth.setError("생년월일은 8자리");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error4");

                        editBirth.setError(null);
                    }
                }

            }
        });

//        editEmail.setText("c004245@naver.com");
//        editPw.setText("whgusdnr1!");
//        editConfirmPw.setText("whgusdnr1!");
//        editPhone.setText("01027327899");
//        editNickName.setText("hyun");
//        editBirth.setText("970802");*/
        regBtn = (Button) findViewById(R.id.registFinBtn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strEmail = editEmail.getText().toString();
                strPw = editPw.getText().toString();

//                strPw = getBase64encode(editPw.getText().toString()); //baas
                strConfirmPw = editConfirmPw.getText().toString();
                strNickName = editNickName.getText().toString();
                strPhone = editPhone.getText().toString();
                strBirth = editBirth.getText().toString();

                Log.d(TAG, "Regist info -->" + strEmail + "--" + strPw + "--" + strNickName
                        + "--" + strPhone + "--" + strBirth + "--" +base64Pw);

                //id, pw, name, phone, birth 한개라도 비어있으면 토스트.
                if (strEmail.equals("") || strPw.equals("") || strNickName.equals("") || strPhone.equals("") || strBirth.equals("")) {
                    Toast.makeText(getApplicationContext(), "비어있는 항목을 채워주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    if (checkEmail(strEmail)) {
//                        savePreferences();
                        if (checkPassWord(strPw)) {
                            if (checkConfirmPassWord(strPw, strConfirmPw)) {
                                base64Pw = getBase64encode(strPw); //base64
                                Log.d(TAG, "Regist Test -->" + strEmail + "--" + strPw + "--" + strNickName + "--" + strPhone + "--"+ strBirth);

                                if (checkBirthSize()) {
                                    Log.d(TAG, "editBirth ->" + editBirth.getText().toString().trim().length());
                                    savePreferences();

                                } else {
                                    Log.d(TAG, "birth need 8");
                                    Toast.makeText(getApplicationContext(), "생년월일을 8자리로 입력해주세요", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "패스워드 확인을 다시해주세요", Toast.LENGTH_LONG).show();
                           }
                        } else {
                            if (strPw.length() < 6) {
                                Toast.makeText(getApplicationContext(), "패스워드는 최소 6자리가 넘어야됩니다..", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "패스워드에 소문자, 특수문자, 숫자가 포함되어야합니다..", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다..", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }



    /**
     * {
     * “user”: {
     * “name”: “joungsik4”,
     * “email”: “test5@test.com”,
     * “password”: “asd1234”,
     * “password_confirmation”: “asd1234”,
     * “phone”: “01030211717”,
     * “birth”: “1995-08-18”
     }
     }
     */
    //값 저장
    private void savePreferences() {

        //Create json body
        JsonObject jsonObject = new JsonObject();
        JsonObject userJsonObject = new JsonObject();

        userJsonObject.addProperty("name", strNickName);
        userJsonObject.addProperty("email", strEmail);
        userJsonObject.addProperty("password", strPw);
        userJsonObject.addProperty("password_confirmation", strConfirmPw);
        userJsonObject.addProperty("phone", strPhone);
        userJsonObject.addProperty("birth", strBirth);

        jsonObject.add("user", userJsonObject);

//        Call<Result> res = RequestRegister.getInstance().getService().postSignUp(jsonObject);
//        res.enqueue(new Callback<Result>() {

        Retrofit retrofit = APIClient.getClient();
        APIInterface registAPI = retrofit.create(APIInterface.class);
        Call<Result> result = registAPI.postSignUp(jsonObject);

        result.enqueue(new Callback<Result>() {
            String error;
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

//                Log.d(TAG, "response ->" + response);
                if (response.isSuccessful()) {
                    Log.v(TAG, "response - " + response.body().toString());
                } else {
                    try {
                        error = response.errorBody().string();
                        Log.v(TAG, "response error - " + response.errorBody().string());
                    } catch (Exception e) {

                    }
                }

                if (error == null) {
                    Log.d(TAG, "Success Register....");
                    Toast.makeText(getApplicationContext(), "회원 저장완료", Toast.LENGTH_LONG).show();
                    finish();
                } else {
//                Log.d(TAG, "register response -->" + response.body());

                    // 정상 {"id":6,"name":"hyun2","email":"c0024245@naver.com","phone":"222","birth":"2018-09-27T00:00:00.000Z","auth_token":null,"fcm_token":null,"created_at":"2018-09-27T09:47:32.382Z","updated_at":"2018-09-27T09:47:32.382Z"}
                    // nickname 이 이미 있는 경우 {"message":{"code":400,"message":["Name has already been taken"]}}
                    // Email이 이미 있는 경우 {"message":{"code":400,"message":["Email has already been taken"]}}
                    // nickname, email 중복 {"message":{"code":400,"message":["Email has already been taken","Name has already been taken"]}}
                    // password 짧을 경우 Password is too short (minimum is 6 characters)"]}}
                    Log.d(TAG, "check result ->" + error);
                    Result result = new Gson().fromJson(error, Result.class);

                    int code = result.code;
                    List<String> message = result.message;
//                JsonElement message = registList.get("code");
                    Log.d(TAG, "code -->" + code);
                    Log.d(TAG, "message -->" + message);

//                JsonObject jsonResult = message;

//                Type listSec = new TypeToken<Message2>() {
//                }.getType();
//
//                Message2 regist2List = gson.fromJson(jsonResult, listSec);

//                int code = regist2List.code;
//                List<String> message2 = regist2List.message;

//                Log.d(TAG, "message List -> " +code + "--" + message2);

                        //회원가입 에러로 판단.
                    Log.d(TAG, "Register Error -----");

                    if (message.contains("Name has already been taken") && message.contains("Email has already been taken")) {
                        Toast.makeText(getApplicationContext(),"이미 존재하는 닉네임과 이메일입니다.", Toast.LENGTH_LONG).show();
                    } else if (message.contains("Email has already been taken")) {
                        Toast.makeText(getApplicationContext(), "이미 존재하는 이메일입니다.", Toast.LENGTH_LONG).show();
                    } else if (message.contains("Name has already been taken")) {
                        Toast.makeText(getApplicationContext(), "이미 존재하는 닉네임입니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
                    }
                        //{"message":{"code":400,"message":["Email has already been taken","Password is too short (minimum is 6 characters)","Name has already been taken"]}}

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d(TAG, "register onFailure -->" + t.toString());
            }
        });
        //1. 그냥 json형태로만 보내면 insert가 바로 되는지
        //2. Sequel Pro 로 확인하는법..
        //3. 이미 회원가입 된 유저 판단
        //4. 핸드폰 번호 인증?
        Log.d(TAG, "json Register ->" + jsonObject);
      /*  SharedPreferences pref = getSharedPreferences("registInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("prefEmail", strEmail);
        editor.putString("prefPw", base64Pw);
        editor.putString("prefNickName", strNickName);
        editor.putString("prefPhone", strPhone);
        editor.putString("prefBirth", strBirth);*/


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
        String valiPass =  "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{6,20}$";
        Pattern pattern = Pattern.compile(valiPass);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /**
     * Base64 인코딩(암호화) password
     */

    public static String getBase64encode(String content){
        return Base64.encodeToString(content.getBytes(), 0);
    }

    /**
     * 패스워드, 패스워드 확인칸에 동일하게 입력했는지,
     * 추후에는 패스워드 확인 EditText 포커스 사라지면
     * 바로 검사할 수있도록 수정예정
     * @param pw
     * @param pwConfirm
     * @return
     */
    public boolean checkConfirmPassWord(String pw, String pwConfirm) {
        Log.d(TAG, "check pw ->" + pw + "--" + pwConfirm);
        if (pw.equals(pwConfirm)) {
            Log.d(TAG, "samePassword");
            return true;
        } else {
            Log.d(TAG, "different password");
            return false;
        }
    }

    //EditBirth 8자리 입력 체크
    public boolean checkBirthSize() {
        if (editBirth.getText().toString().trim().length() == 8) {
            return true;
        } else {
            return false;
        }
    }
}


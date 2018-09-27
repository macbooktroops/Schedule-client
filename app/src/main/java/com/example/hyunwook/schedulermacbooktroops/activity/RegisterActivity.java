package com.example.hyunwook.schedulermacbooktroops.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.login.RequestRegister;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                                savePreferences();
                                Log.d(TAG, "Regist Test -->" + strEmail + "--" + strPw + "--" + strNickName + "--" + strPhone + "--"+ strBirth);
                            } else {
                                Toast.makeText(getApplicationContext(), "패스워드 확인을 다시해주세요", Toast.LENGTH_LONG).show();
                           }
                        } else {
                            Toast.makeText(getApplicationContext(), "패스워드에 소문자, 특수문자, 숫자가 포함되어야합니다..", Toast.LENGTH_LONG).show();
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

        Call<JsonObject> res = RequestRegister.getInstance().getService().postSignUp(jsonObject);
        res.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "register response -->" + response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "register onFailure -->" + t.toString());
            }
        });
        //1. 그냥 json형태로만 보내면 insert가 바로 되는지
        //2. Sequel Pro 로 확인하는법..
        //3. 이미 회원가입 된 유저 판단
        //4. 핸드폰 번호 인증?
        Log.d(TAG, "json Register ->" + jsonObject);
        SharedPreferences pref = getSharedPreferences("registInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("prefEmail", strEmail);
        editor.putString("prefPw", base64Pw);
        editor.putString("prefNickName", strNickName);
        editor.putString("prefPhone", strPhone);
        editor.putString("prefBirth", strBirth);

        Toast.makeText(getApplicationContext(), "회원 저장완료", Toast.LENGTH_LONG).show();

        editor.commit();

        finish();
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
}


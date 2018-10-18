package com.playgilround.schedule.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.dialog.FindEmailDialog;
import com.playgilround.schedule.client.gson.EmailJsonData;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;
import com.playgilround.schedule.client.gson.PassToken;
import com.playgilround.schedule.client.gson.Result;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-10-01
 * Find Account
 */
public class FindAccountActivity extends Activity {

    static final String TAG = FindAccountActivity.class.getSimpleName();

    String strNickName, strPhone, strBirth;
    EditText editEmail, editNickName, editPhone, editBirth;

    Button btnFind;

    private FindEmailDialog mFindEmailDialog;

//    private ResetPasswordDialog mResetPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String check = intent.getStringExtra("check");

        Log.d(TAG, "check find ->" + check);

        if (check.equals("Email")) {
            setContentView(R.layout.activity_find_email);
            editNickName = findViewById(R.id.nicknameInput);
            editPhone = findViewById(R.id.phoneInput);

            editBirth = findViewById(R.id.birthInput);

            editBirth.setOnFocusChangeListener((View.OnFocusChangeListener) (v, hasFocus) -> {
                if (hasFocus) {
                    if (editBirth.getText().toString().trim().length() != 8) {
                        editBirth.setError("8자리로 생년월일 입력해주세요.");
                        Log.d(TAG, "EditBirth error");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error2");

                        editBirth.setError(null);
                    }
                    //포커스 해제
                } else {
                    if (editBirth.getText().toString().trim().length() != 8) {
                        Log.d(TAG, "EditBirth error3");

                        editBirth.setError("생년월일은 8자리");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error4");

                        editBirth.setError(null);
                    }
                }
            });

            btnFind = findViewById(R.id.findBtn);
            btnFind.setOnClickListener(l -> findUserEmail(new EmailCallback() {
                @Override
                public void onSuccess(String retName, String retEmail) {
                    Log.d(TAG, "onSuccess -->" + retName + "--" + retEmail);
                    if (mFindEmailDialog == null) {
                        mFindEmailDialog = new FindEmailDialog(FindAccountActivity.this, retName, retEmail);
                    }

                    mFindEmailDialog.show();

                }

                @Override
                public void onFail(String result) {
                    Toast.makeText(getApplicationContext(), "이메일 찾기 실패", Toast.LENGTH_LONG).show();
                }
            }));
        } else if (check.equals("Password")) {
            setContentView(R.layout.activity_find_password);


            editEmail = findViewById(R.id.emailInput);
            editNickName = findViewById(R.id.nicknameInput);
            editPhone = findViewById(R.id.phoneInput);
            editBirth = findViewById(R.id.birthInput);


            editBirth.setOnFocusChangeListener((View.OnFocusChangeListener) (v, hasFocus) -> {
                if (hasFocus) {
                    if (editBirth.getText().toString().trim().length() != 8) {
                        editBirth.setError("8자리로 생년월일 입력해주세요.");
                        Log.d(TAG, "8자리로 생년월일 입력해주세요.");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error2");

                        editBirth.setError(null);
                    }
                    //포커스 해제
                } else {
                    if (editBirth.getText().toString().trim().length() != 8) {
                        Log.d(TAG, "EditBirth error3");

                        editBirth.setError("생년월일은 8자리");
                    } else {
                        // your code here
                        Log.d(TAG, "EditBirth error4");

                        editBirth.setError(null);
                    }
                }
            });
            btnFind = findViewById(R.id.findBtn);

            btnFind.setOnClickListener(l -> findUserPassword(new PassCallback() {
                @Override
                public void onSuccess(String retToken, String name){
                    Log.d(TAG, "onSuccess -->" + retToken + "--" + name);
                    Intent intent = new Intent(FindAccountActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("resultToken", retToken);
                    intent.putExtra("resultName", name);
                    startActivity(intent);
                   /* if (mResetPasswordDialog == null) {
                        mResetPasswordDialog = new ResetPasswordDialog(FindAccountActivity.this, name);
                    }

                    mResetPasswordDialog.show();*/

                  /*  if (mFindEmailDialog == null) {
                        mFindEmailDialog = new FindEmailDialog(FindAccountActivity.this, retName, retEmail);
                    }

                    mFindEmailDialog.show();*/

                  /*  JsonObject jsonObject = new JsonObject();
                    JsonObject userJsonObject = new JsonObject();

                    userJsonObject.addProperty("password", resToken);

                    jsonObject.add("user", userJsonObject); */
                }

                @Override
                public void onFail(String result) {
                    Toast.makeText(getApplicationContext(), "이메일 찾기 실패", Toast.LENGTH_LONG).show();
                }
            }));

        }
    }

    //유저 이메일 찾기
    protected void findUserEmail(final EmailCallback callback) {
        Log.d(TAG, "findUserEmail...");
        String resNick = editNickName.getText().toString().trim();
        String resPhone = editPhone.getText().toString().trim();
        String resBirth = editBirth.getText().toString().trim();
        Log.d(TAG, "test ->" + resNick + "--" + resPhone+ "--" + resBirth);
        boolean isValidPhone = checkPhone(resPhone); //숫자만입력
        boolean isValidBirth = checkBirthSize(resBirth); //8자리 숫자만 입력


        Log.d(TAG, "isValid -->" + isValidBirth + "--" );

        if (resNick.equals("") || resPhone.equals("") || resBirth.equals("")) {
            Toast.makeText(getApplicationContext(), "비어 있는 항목을 모두 채워주세요.", Toast.LENGTH_LONG).show();
        } else {
            if (isValidPhone) {
                Log.d(TAG, "phone is only number");
                if (isValidBirth) {
                    //yyyyMMdd 포맷을 subString 을 이용해 year, Month, day 각각 얻기
                    String resYear = resBirth.substring(0, 4);
                    String resMonth = resBirth.substring(4,6);
                    String resDay = resBirth.substring(6, 8);
                    Log.d(TAG, "Retrofit get FindEmail----" + resYear +"--" + resMonth + "--" + resDay);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("name", resNick);
                    jsonObject.addProperty("phone",resPhone);
                    jsonObject.addProperty("birth", resYear+"-"+resMonth+"-"+resDay);

                    Log.d(TAG, "jsonObject ->" + jsonObject);

                    Retrofit retrofit = APIClient.getClient();
                    APIInterface fEmailAPI = retrofit.create(APIInterface.class);
                    Call<JsonObject> result = fEmailAPI.postFindEmail(jsonObject);

                    result.enqueue(new Callback<JsonObject>() {
                        String error;
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                String retResponse = response.body().toString();

                                Log.d(TAG, "response -->" + retResponse);

                                Type list = new TypeToken<EmailJsonData>() {
                                }.getType();

                                EmailJsonData emailList = new Gson().fromJson(retResponse, list);

                                String retName = emailList.name;
                                String retEmail = emailList.email;

                                Log.d(TAG, "retEMail -->" + retEmail);

                                if (retEmail != null) {

                                    Log.d(TAG, "이메일 찾기 성공");
                                    callback.onSuccess(retName, retEmail);
                                }


                            } else {
                                try {
                                    error = response.errorBody().string();
                                    Log.d(TAG, "response error -->" + error);

                                    Result result = new Gson().fromJson(error, Result.class);

                                    List<String> message = result.message;

                                    Log.d(TAG, "message -->" + message);

                                    if (message.contains("Not found user.")) {
                                        Toast.makeText(getApplicationContext(), "등록되지 않은 유저입니다.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d(TAG, t.toString());
                            callback.onFail("fail");
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "생년월일은 숫자 8자리로만입력해주세요.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "핸드폰번호는 숫자만 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //유저 비밀번호 찾기
    //먼저 패스워드 토큰을 얻음
     protected void findUserPassword(final PassCallback callback) {
        Log.d(TAG, "findUserPassword");

        String resEmail = editEmail.getText().toString().trim();
        String resNick = editNickName.getText().toString().trim();
        String resPhone = editPhone.getText().toString().trim();
        String resBirth = editBirth.getText().toString().trim();

        Log.d(TAG, "test -->" + resEmail + "--"+ resNick + "--" + resPhone + "--"+ resBirth);

        boolean isValidEmail = checkEmail(resEmail); //이메일 포맷인지
        boolean isValidPhone = checkPhone(resPhone); //숫자만
        boolean isValidBirth = checkBirthSize(resBirth); //8자리 숫자만

        if (resEmail.equals("") || resNick.equals("") || resPhone.equals("") || resBirth.equals("")) {
            Toast.makeText(getApplicationContext(), "비어 있는 항목을 모두 채워주세요.", Toast.LENGTH_LONG).show();
        } else {
            if (isValidEmail) {
                Log.d(TAG, "email ok");
                if (isValidPhone) {
                    Log.d(TAG, "phone ok");
                    if (isValidBirth) {
                        //yyyyMMdd 포맷을 subString을 이용해 분리
                        String resYear = resBirth.substring(0, 4);
                        String resMonth = resBirth.substring(4, 6);
                        String resDay = resBirth.substring(6,8);

                        Log.d(TAG, "Retrofit get FindPassword ----" + resYear + "-" + resMonth + "-"+ resDay);

                        /**
                         * {
                         * "email": "test@test.com",
                         * "name": "test",
                         * "phone": "01030211717",
                         * "birth": "1995-08-18"
                         }
                         */
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("email", resEmail);
                        jsonObject.addProperty("name", resNick);
                        jsonObject.addProperty("phone", resPhone);
                        jsonObject.addProperty("birth", resYear+"-"+resMonth+"-"+resDay);

                        Log.d(TAG, "jsonObject -->" + jsonObject);

                        Retrofit retrofit = APIClient.getClient();
                        APIInterface fPassAPI = retrofit.create(APIInterface.class);
                        Call<JsonObject> result = fPassAPI.postFindPassWord(jsonObject);

                        result.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                String error;
                                if (response.isSuccessful()) {
                                    String retResponse = response.body().toString();

                                    Log.d(TAG, "retResponse -->" + retResponse);

                                    Type list = new TypeToken<PassToken>() {
                                    }.getType();

                                    PassToken passToken = new Gson().fromJson(retResponse, list);

                                    String retToken = passToken.passToken;

                                    Log.d(TAG, "retToken ----> " + retToken);

                                    if (retToken != null) {
                                        Log.d(TAG, "success getResult.");
                                        callback.onSuccess(retToken, resNick);
                                    }

                                } else {
                                    try {
                                        error = response.errorBody().string();

                                        Log.d(TAG, "response error -->" + error);

                                        Result result = new Gson().fromJson(error, Result.class);

                                        List<String> message = result.message;

                                        Log.d(TAG, "message --->" + message);

                                        if (message.contains("Not found user.")) {
                                            Toast.makeText(getApplicationContext(), "등록되지 않은 유저입니다.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.d(TAG, t.toString());
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "생년월일은 숫자 8자리로만 입력해주세요.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "핸드폰번호는 숫자만 입력해주세요.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "이메일 양식에맞춰주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //EditBirth 8자리 입력 체크
    public boolean checkBirthSize(String birth) {
//        birth = editBirth.getText().toString().trim();
        if (birth.length() == 8) {
            Log.d(TAG, "birth is 10 size");

//            if (birth.matches("^(\\d+)[/|\\-|\\s]+[0|1](\\d)[/|\\-|\\s]+([0|1|2|3]\\d)$")) {
//                Log.d(TAG, "birth is only number");
                return true;

        } else {
            return false;
        }
    }

    //전화번호 숫자만 입력
    public boolean checkPhone(String phone) {
        if (phone.matches("^[0-9]*$")) {
            Log.d(TAG, "phone is only number");
            return true;
        } else {
            return false;
        }
    }

    //이메일 포맷
    private boolean checkEmail(String email) {
        String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(mail);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //Retrofit Callback id
    public interface EmailCallback {
        void onSuccess(String name, String email);
        void onFail(String result);
    }

    //Retrofit Callback password
    public interface PassCallback {
        void onSuccess(String passToken, String name);
        void onFail(String result);
    }
}

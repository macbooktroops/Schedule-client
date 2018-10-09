package com.playgilround.schedule.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.common.base.app.BaseActivity;
import com.playgilround.schedule.client.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    String retName; // 표시할 닉네임

    EditText etPw;
    EditText etPwConfirm;
    Intent intent;
    static final String TAG = ResetPasswordActivity.class.getSimpleName();

    @Override
    protected void bindView() {
        setContentView(R.layout.activity_reset_password);

        intent = getIntent();
        String name = intent.getStringExtra("resultName");
        Log.d(TAG, "name -->" + name);
        TextView title = findViewById(R.id.tvTitle);
        title.setText(name + "님 패스워드를 변경합니다.");

        etPw = findViewById(R.id.editPw);
        etPwConfirm = findViewById(R.id.editPwConfirm);

//        etPwConfirm.setText("ㅇㄹㅁㄴㅇㄹㄷㄷ");

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.tvConfirm:

                String pwToken = intent.getStringExtra("resultToken");
                String strPw = etPw.getText().toString().trim();
                String strPwConfirm = etPwConfirm.getText().toString().trim();
                if (strPw.equals("") || strPwConfirm.equals("")) {
                    Toast.makeText(this, "비어 있는 항목을 모두 채워주세요.", Toast.LENGTH_LONG).show();
                } else {
                    if (checkPassWord(strPw)) {
                        if (checkConfirmPassWord(strPw, strPwConfirm)) {
                            /**
                             * {
                             *   "user": {
                             *     "password": "test1234",
                             *     "password_confirmation": "test1234"
                             *   }
                             * }
                             */

                            JsonObject jsonObject = new JsonObject();
                            JsonObject userJsonObject = new JsonObject();

                            userJsonObject.addProperty("password", strPw);
                            userJsonObject.addProperty("password_confirmation", strPwConfirm);

                            jsonObject.add("user", userJsonObject);

                            Log.d(TAG, "jsonObject -->" + jsonObject);
                            Log.d(TAG, "confirm test ->" + strPw + "--" + strPwConfirm + "--" + pwToken);

                            Retrofit retrofit = APIClient.getClient();
                            APIInterface resetPwAPI = retrofit.create(APIInterface.class);
                            Call<JsonObject> result = resetPwAPI.postResetPassword(jsonObject, pwToken);

                            result.enqueue(new Callback<JsonObject>() {
                                String error;
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "response ->" + response.body().toString());
                                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();
//                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } else {
                                        try {
                                            error = response.errorBody().string();
                                            Toast.makeText(getApplicationContext(), "토큰 오류입니다. 비밀번호 찾기부터 다시해주세요.", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Log.d(TAG, "서버 에러" + t.toString());
                                }
                            });

                        } else {
                            Toast.makeText(this, "패스워드가 다르게 입력되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "패스워드에 소문자, 특수문자, 숫자가 포함되어야합니다..", Toast.LENGTH_LONG).show();
                    }
                }

        }
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

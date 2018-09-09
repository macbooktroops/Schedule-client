package com.example.hyunwook.schedulermacbooktroops.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyunwook.schedulermacbooktroops.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {
    Button regBtn;
    EditText editEmail, editPw, editName, editPhone, editBirth;

    String strEmail, strPw, strName, strPhone, strBirth;

    static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        editEmail = (EditText) findViewById(R.id.emailInput);
        editPw = (EditText) findViewById(R.id.pwInput);
        editName = (EditText) findViewById(R.id.nameInput);
        editPhone = (EditText) findViewById(R.id.phoneInput);
        editBirth = (EditText) findViewById(R.id.birthInput);


        regBtn = (Button) findViewById(R.id.registFinBtn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strEmail = editEmail.getText().toString();
                strPw = editPw.getText().toString();
                strName = editName.getText().toString();
                strPhone = editPhone.getText().toString();
                strBirth = editBirth.getText().toString();

                Log.d(TAG, "Regist info -->" + strEmail + "--" + strPw + "--" + strName
                        + "--" + strPhone + "--" + strBirth);

                //id, pw, name, phone, birth 한개라도 비어있으면 토스트.
                if (strEmail.equals("") || strPw.equals("") || strName.equals("") || strPhone.equals("") || strBirth.equals("")) {
                    Toast.makeText(getApplicationContext(), "비어있는 항목을 채워주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    if (checkEmail(strEmail)) {
                        savePreferences();

                    } else {
                        Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다..", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //값 저장
    private void savePreferences() {
        SharedPreferences pref = getSharedPreferences("registInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("prefEmail", strEmail);
        editor.putString("prefPw", strPw);
        editor.putString("prefName", strName);
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
}

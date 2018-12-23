/*
package com.playgilround.schedule.client.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask.execute
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.kakao.auth.ErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.UserManagement.requestMe
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException
import com.playgilround.schedule.client.R
import com.playgilround.schedule.client.dialog.SelectFindDialog
import com.playgilround.schedule.client.facebook.LoginCallback
import com.playgilround.schedule.client.gson.HolidayJsonData
import com.playgilround.schedule.client.realm.ScheduleR
import com.playgilround.schedule.client.retrofit.APIClient
import com.playgilround.schedule.client.retrofit.APIInterface
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.*

*/
/**
 * 18-12-20
 * 로그인 화면
 *//*

class LoginActivity : Activity, SelectFindDialog.OnFindSetListener {

    lateinit var idInput : EditText
    lateinit var pwInput : EditText

    lateinit var autoCheck : CheckBox

    lateinit var registBtn : Button
    lateinit var loginBtn : Button
    lateinit var findBtn: Button

    lateinit var pref : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    var loginChecked : Boolean = false

    private lateinit var btn_facebook : LoginButton
    private lateinit var mLoginCallback: LoginCallback
    private lateinit var mCallbackManager : CallbackManager

    lateinit var mSelectFindDialog: SelectFindDialog

    lateinit var resPushTitle : String
    lateinit var authToken : String
    lateinit var realm : Realm

    lateinit var arrUserId : RealmList<Integer> //userId, NickName 배열
    lateinit var arrNickName : RealmList<String>

    lateinit var callback : SessionCallback

    private val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET)

    var arrScheId : Int  = 0 //자기 자신 스케줄 아이디만 저장


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val permissionListener : PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한 요청을 해주세요.")
                .setPermissions(*PERMISSION_STORAGE)
                .check()

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        requestMe()

        mCallbackManager = CallbackManager.Factory.create()
        mLoginCallback = LoginCallback()

        pref = getSharedPreferences("loginData", Context.MODE_PRIVATE)
        editor = pref.edit()

        idInput = findViewById(R.id.idInput)
        pwInput = findViewById(R.id.pwInput)

        autoCheck = findViewById(R.id.checkbox)
        registBtn = findViewById(R.id.registBtn)

        loginBtn = findViewById(R.id.loginBtn)
        findBtn = findViewById(R.id.findBtn)

        btn_facebook = findViewById(R.id.btn_facebook_login)
        btn_facebook.setReadPermissions(Arrays.asList("public_profile", "email"))
        btn_facebook.registerCallback(mCallbackManager, mLoginCallback)

        realm = Realm.getDefaultInstance()

        */
/**
         * Retrofit Holiday
         *//*

//        check
    }

    fun checkHoliday(callback : ApiCallback) {
        var nYear: Int

        val calendar: Calendar = GregorianCalendar(Locale.KOREA)
        nYear = calendar.get(Calendar.YEAR)

        Log.d(TAG, "checkHoliday -> $nYear")

        val retrofit: Retrofit = APIClient.getClient()
        val holidayAPI = retrofit.create(APIInterface::class.java)
        val result : Call<ArrayList<JsonObject>> = holidayAPI.getListHoliday(nYear)
        result.enqueue(object : Callback<ArrayList<JsonObject>> {
            override fun onResponse(call: Call<ArrayList<JsonObject>>, response: Response<ArrayList<JsonObject>>) {
                Log.d(TAG, "checkResponse ------- " + response.code())

                if (response.isSuccessful && response.body() != null) {
                    realm.executeTransaction {
                        Log.d(TAG, "Check Holdiay ")
                        val holidayRS = realm.where(ScheduleR::class.java).equalTo("eventSetId", -1).findAll()
                        if (holidayRS.size == 0) {
                            //ScheduleR Table 에 공휴일정보가 저장이 되어있지 않음
                            */
/**
                             * Use gson json parsing..
                             *//*

                            val list = object : TypeToken<List<HolidayJsonData>>(){}.type
                            var holidays: List<HolidayJsonData> = Gson().fromJson(response.body().toString(), list)

                            for (resHoliday: HolidayJsonData in holidays) {
                                val currentIdNum = realm.where(ScheduleR::class.java).max("seq")

                                val nextId: Int

                                if (currentIdNum == null) {
                                    nextId = 0
                                } else {
                                    nextId = currentIdNum.toInt() + 1
                                }

                                var holidayR: ScheduleR = realm.createObject(ScheduleR::class.java, nextId)

                                holidayR.id = resHoliday.id
                                holidayR.year = resHoliday.year
                                holidayR.month = resHoliday.month
                                holidayR.day = resHoliday.day
                                holidayR.title = resHoliday.name
                                holidayR.eventSetId = -1 //공휴일 EventSetId -1 고정
                                holidayR.color = -1 //공휴일 하늘색.
                                holidayR.state = -1 //상태 -1 (좌측 노란색)
                            }
                        } else {
                        }
                        callback.onSuccess("success")
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<JsonObject>>, t: Throwable) {
                Log.d(TAG, "error ->" + t.toString())
            }
        })

    }

    //카카오톡 유저정보 관련
    fun requestMe() {
        //유저의 정보를 받아오는 함수
        UserManagement.requestMe(object : MeResponseCallback() {
            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d(TAG, "onSessionClosed ->" + errorResult)
            }

            override fun onNotSignedUp() {
                //카카오톡 회원이 아님
                Log.d(TAG, "onNotSignedUp..")
            }

            override fun onSuccess(result: UserProfile?) {
                Log.d(TAG, "Kakao user profile -> " + result.toString())
                Log.d(TAG, "Kakao user profile -->" + result?.id)
            }

            override fun onFailure(errorResult: ErrorResult) {
                Log.d(TAG, "Error Result - >" + errorResult)
            }
        })
    }


    //Kakao Callback Listener
    class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            requestMe(object : MeResponseCallback() {
                override fun onSessionClosed(errorResult: ErrorResult) {
                    Log.d(TAG, "Kakao onSessionClose ->" + errorResult)
                }

                override fun onNotSignedUp() {
                    Log.d(TAG, "Kakao onNotSignedUp -->");
                }

                override fun onSuccess(result: UserProfile) {
                    Log.d(TAG, "Kakao onSuccess -> " + result.toString())
                    //로그인 성공 시 , 로그인한 사용자에 일련번호, 이미지 url, 닉네임 리턴
                    //사용자 아이디는 보안상의 문제로 제공하지않고 일련번호만 제공.
                    val number = result.id
                }

                override fun onFailure(errorResult: ErrorResult) {
                    val message: String = "failed to get user info. msg = " + errorResult

                    var result: ErrorCode = ErrorCode.valueOf(errorResult.errorCode)

                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //error login fail
                        Log.d(TAG, "Error login fail...")
                    } else {

                    }
                }

            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d(TAG, "Kakao onSessionOpenFail...")
        }
    }


    interface ApiCallback {
        fun onSuccess(result: String)
        fun onFail(result: String)
    }


    companion object {
        val TAG = LoginActivity.javaClass.simpleName
    }

}
*/

package com.example.hyunwook.schedulermacbooktroops.holiday.model;

import android.util.Log;

import com.example.hyunwook.schedulermacbooktroops.holiday.api.HolidayService;
import com.example.hyunwook.schedulermacbooktroops.holiday.api.HolidayUrl;
import com.example.hyunwook.schedulermacbooktroops.holiday.presenter.BasePresenter;
import com.example.hyunwook.schedulermacbooktroops.holiday.presenter.MainPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 18-09-18 Holiday Request
 */
public class RequestHolidayModel extends BaseModel {

    static final String TAG = RequestHolidayModel.class.getSimpleName();

    public RequestHolidayModel(BasePresenter presenter) {
        super(presenter);
    }

    //휴일 정보 요청
    public void requestHoliday(int year) {
        Log.d(TAG, "requestHoliday ==");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HolidayUrl.HOLI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HolidayService service = retrofit.create(HolidayService.class);


        //Gson
        Call<Holiday> holidayCall = service.requestHoliInfo(year); //연도 설정
        holidayCall.enqueue(new Callback<Holiday>() {
            @Override
            public void onResponse(Call<Holiday> call, Response<Holiday> response) {
                Holiday holiday = response.body();
                Log.d(TAG, "holiday result ->" + response.body().toString());
                if (holiday != null) {
                    getPresenter().response(MainPresenter.TYPE_SEARCH_HOLI, 200, response.message(), holiday);
                } else {
                    //holiday 결과 없음
                    getPresenter().response(MainPresenter.TYPE_SEARCH_HOLI, 400, response.message(), null);
                }
            }

            @Override
            public void onFailure(Call<Holiday> call, Throwable t) {
                Log.d(TAG, "onFailure---");
                getPresenter().response(MainPresenter.TYPE_SEARCH_HOLI, 400, "Fail", null);
            }
        });


    }
}

package com.example.hyunwook.schedulermacbooktroops.holiday.presenter;

import android.util.Log;

import com.example.hyunwook.schedulermacbooktroops.holiday.model.Holiday;
import com.example.hyunwook.schedulermacbooktroops.holiday.model.RequestHolidayModel;
import com.example.hyunwook.schedulermacbooktroops.holiday.presenter.view.MainPresenterView;
import com.google.gson.Gson;

/**
 * 18-09-18
 */
public class MainPresenter extends BasePresenter<MainPresenterView> {

    public static final int TYPE_SEARCH_HOLI = 0;

    static final String TAG = MainPresenter.class.getSimpleName();

    private RequestHolidayModel holidayModel;

    public MainPresenter(MainPresenterView view) {
        super(view);

        holidayModel = new RequestHolidayModel(this);
    }

    //Holiday 정보 요
    public void onRequestHoliday(int year) {
        Log.d(TAG, "onRequestHoliday ====");
//        getView().onProgressBar(true); //Show Progress
        holidayModel.requestHoliday(year); //year
    }

    //결과 처리
    @Override
    public void response(int id, int response, String message, Object result) {
        switch (id) {
            case TYPE_SEARCH_HOLI:
//                getView().onProgressBar(false); //finish progress

                if (result != null) {
                    //Holiday있음
                    Holiday holiday = (Holiday) result;

                    getView().onSearch(true ,message, new Gson().toJson(holiday));
                } else {
                    getView().onSearch(false, message, null);
                }
        }
    }

}

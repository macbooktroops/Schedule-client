package com.example.hyunwook.schedulermacbooktroops.holiday.presenter;

import com.example.hyunwook.schedulermacbooktroops.holiday.presenter.view.BasePresenterView;

/**
 * 18-09-18
 * @param <V>
 */
public abstract class BasePresenter<V extends BasePresenterView> {

    private V view;

    public BasePresenter(V view) {
        this.view = view;
    }

    protected V getView() {
        return view;
    }

    public abstract void response(int id, int response, String message, Object result);
}

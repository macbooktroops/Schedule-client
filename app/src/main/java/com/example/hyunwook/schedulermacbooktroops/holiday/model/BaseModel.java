package com.example.hyunwook.schedulermacbooktroops.holiday.model;

import com.example.hyunwook.schedulermacbooktroops.holiday.presenter.BasePresenter;

/**
 * 18-09-18
 */
public abstract class BaseModel {

    private BasePresenter presenter;

    public BaseModel(BasePresenter presenter) {
        this.presenter = presenter;
    }

    protected BasePresenter getPresenter() {
        return presenter;
    }
}

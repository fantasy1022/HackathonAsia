package com.fantasy1022.hackathonasia.base;

/**
 * Created by fantasy_apple on 2017/8/5.
 */


public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
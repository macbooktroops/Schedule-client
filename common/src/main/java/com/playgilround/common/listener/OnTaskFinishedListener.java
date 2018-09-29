package com.playgilround.common.listener;

/**
 * 18-06-07
 * Task 관련 인터페이스
 * @param <T>
 */
public interface OnTaskFinishedListener<T> {
    void onTaskFinished(T data);
}

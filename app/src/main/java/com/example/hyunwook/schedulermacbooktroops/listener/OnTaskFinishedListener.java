package com.example.hyunwook.schedulermacbooktroops.listener;

/**
 * 18-05-22
 * 작업이 끝나면 완료 리스너
 */
public interface OnTaskFinishedListener<T> {
    void onTaskFinished(T data);
}

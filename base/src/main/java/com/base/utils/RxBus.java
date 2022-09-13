package com.base.utils;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * RxBus管理类
 */
public class RxBus {

    private static volatile RxBus instance;
    private PublishSubject<Object> bus;

    private RxBus() {
        bus = PublishSubject.create();
    }

    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                instance = new RxBus();
            }
        }
        return instance;
    }

    /**
     * 发送事件
     *
     * @param object
     */
    public void post(Object object) {
        bus.onNext(object);
    }

    /**
     * 根据类型接收相应类型事件
     *
     * @param eventType
     * @return
     */
    public @NonNull <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

}

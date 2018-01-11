package com.github.mayurkaul.rxobservablelist;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public abstract class RxBaseObject<T> {

    private BehaviorSubject<STATUS> mStatusObservable  = BehaviorSubject.create();

    abstract void setKey(T key);

    abstract T getKey();

    void setStatus(STATUS status)
    {
        mStatusObservable.onNext(status);
    }

    Subject<STATUS> getStatusObservable()
    {
        return mStatusObservable;
    }

    public enum STATUS
    {
        ADDED,UPDATED,REMOVED
    }

}

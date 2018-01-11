package com.github.mayurkaul.rxobservablelist

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

internal abstract class RxBaseObject<T> {

    private var mStatusObservable: BehaviorSubject<STATUS> = BehaviorSubject.create<STATUS>();

    abstract fun setKey(key: T)

    abstract fun getKey(): T

    fun setStatus(status: STATUS)
    {
        mStatusObservable.onNext(status);
    }

    fun getStatusObservable():Subject<STATUS>
    {
        return mStatusObservable;
    }

    enum class STATUS
    {
        ADDED,UPDATED,REMOVED
    }

}

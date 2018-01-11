package com.github.mayurkaul.rxobservablelist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

public class ObservableRxList<T> implements Iterable<T> {
    public final List<T> list;
    private final PublishProcessor<ObservableRxListMap.RxList<T>> subject;

    public ObservableRxList() {
        this.list = Collections.synchronizedList(new ArrayList<T>());
        this.subject = PublishProcessor.create();
    }

    public void add(T value) {
        list.add(value);
        subject.onNext(new ObservableRxListMap.RxList<T>(ObservableRxListMap.ChangeType.ADD, value,list.size()-1));
    }

    public void addAll(List<T> items) {
        List<T> oldItems = new ArrayList<>(list);
        list.addAll(items);
        ObservableRxListMap.RxList<T> publishObject = new ObservableRxListMap.RxList<>(ObservableRxListMap.ChangeType.ADD_BULK, null, 0);
        publishObject.oldList = oldItems;
        subject.onNext(publishObject);
    }

    public void update(T value) {
        for (ListIterator<T> it = list.listIterator(); it.hasNext(); ) {
            if (value.equals(it.next())) {
                int pos = list.indexOf(value);
                it.set(value);
                subject.onNext(new ObservableRxListMap.RxList<T>(ObservableRxListMap.ChangeType.UPDATE, value,pos));
                return;
            }
        }
    }

    public void clear() {
        list.clear();
        subject.onNext(new ObservableRxListMap.RxList<T>(ObservableRxListMap.ChangeType.CLEAR, null,0));
    }

    public void remove(T value) {
        int pos = list.indexOf(value);
        list.remove(value);
        subject.onNext(new ObservableRxListMap.RxList<T>(ObservableRxListMap.ChangeType.REMOVE, value, pos));
    }

    public void removeAll(List<T> collection)
    {
        List<T> oldItems = new ArrayList<>(list);
        list.removeAll(collection);

        ObservableRxListMap.RxList<T> publishObject = new ObservableRxListMap.RxList<>(ObservableRxListMap.ChangeType.REMOVE_BULK, null, 0);
        publishObject.oldList = oldItems;
        subject.onNext(publishObject);
    }

    public int size()
    {
        return list.size();
    }

    public Flowable<ObservableRxListMap.RxList<T>> getObservable() {
        return subject;
    }

    public T get(int position) {
        return list.get(position);
    }

    public void throwError(Exception lalaa) {
    }

    public void sortUsingComparator(Comparator<T> comparator)
    {
        List<T> oldItems = new ArrayList<>(list);
        Collections.sort(list, comparator);
        ObservableRxListMap.RxList<T> publishObject = new ObservableRxListMap.RxList<>(ObservableRxListMap.ChangeType.SORTED, null, 0);
        publishObject.oldList = oldItems;
        subject.onNext(publishObject);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
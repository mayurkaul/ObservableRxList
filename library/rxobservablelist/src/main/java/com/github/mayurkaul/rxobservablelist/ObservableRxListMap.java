package com.github.mayurkaul.rxobservablelist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by mayurkaul on 24/05/17.
 *
 * @param <V> the type parameter
 * @param <T> the type parameter
 */
public class ObservableRxListMap<V, T extends RxBaseObject<V>> implements Iterable<T> {
    /**
     * The List.
     */
    public final List<T> list;
    /**
     * The Hash map.
     */
    @NonNull
    public final LinkedHashMap<V, T> hashMap;
    @NonNull
    private final PublishProcessor<RxList<T>> subject;

    /**
     * Instantiates a new Observable rx list.
     */
    public ObservableRxListMap() {
        this.list = Collections.synchronizedList(new ArrayList<T>());
        this.hashMap = new LinkedHashMap<>();
        this.subject = PublishProcessor.create();
    }

    private ObservableRxListMap(List<T> list){
        this.list = Collections.synchronizedList(list);
        this.hashMap = new LinkedHashMap<>();
        this.subject = PublishProcessor.create();
    }

    public static <V,T extends RxBaseObject<V>>ObservableRxListMap<V,T> convert(List<T> list)
    {
        ObservableRxListMap<V, T> returnList = new ObservableRxListMap<>(list);
        returnList.initMap();
        return returnList;
    }

    private void initMap() {
        for (T value :
                this.list) {
            hashMap.put(value.getKey(),value);
        }
    }

    public void add(@NonNull T value, int pos) {
        if(value == null || value.getKey()==null){
            return;
        }

        List<T> oldItems = new ArrayList<>(list);
        list.add(pos, value);
        hashMap.put(value.getKey(), value);
        RxList<T> rxList = new RxList<T>(ChangeType.ADD, value, pos);
        rxList.oldList = oldItems;
        subject.onNext(rxList);
    }
    /**
     * Add.
     *
     * @param value the value
     */
    public void add(@NonNull T value) {
        add(value,0);
    }

    /**
     * Gets item by key.
     *
     * @param key the key
     * @return the item by key
     */
    @Nullable
    public T getItemByKey(V key) {
        return hashMap.get(key);
    }

    /**
     * Add all.
     *
     * @param items the items
     */
    public void addAll(@NonNull List<T> items) {
        if(items != null && items.size() > 0) {
            List<T> oldItems = new ArrayList<>(list);
            int addedCounter = 0;
            for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); ) {
                T value = iterator.next();
                if (getItemByKey(value.getKey()) == null) {
                    list.add(value);
                    hashMap.put(value.getKey(), value);
                    value.setStatus(RxBaseObject.STATUS.ADDED);
                    addedCounter++;
//                add(value);
                } else {
                    update(value);
                }
            }
            if (addedCounter > 0) {
                RxList<T> publishObject = new RxList<>(addedCounter > 1 ? ChangeType.ADD_BULK : ChangeType.ADD, null, 0);
                publishObject.oldList = oldItems;
                subject.onNext(publishObject);
            }
        }
    }

    /**
     * Update.
     *
     * @param value the value
     */
    public void update(@NonNull T value) {
        List<T> oldItems = new ArrayList<>(list);
        for (ListIterator<T> it = list.listIterator(); it.hasNext(); ) {
            if (value.equals(it.next())) {
                int pos = list.indexOf(value);
                if(pos>=0) {
                    it.set(value);
                    value.setStatus(RxBaseObject.STATUS.UPDATED);
                    RxList<T> rxList = new RxList<T>(ChangeType.UPDATE, value, pos);
                    rxList.oldList = oldItems;
                    subject.onNext(rxList);
                    return;
                }
            }
        }

        // update hash map only when list size is greater than zero
        if(list != null && list.size() > 0) {
            hashMap.put(value.getKey(), value);
        }

    }


    public void updateAll(@NonNull List<T> items) {
        List<T> oldItems = new ArrayList<>(list);

        int updatedCounter = 0;
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); ) {
            T value = iterator.next();
            hashMap.put(value.getKey(), value);
            value.setStatus(RxBaseObject.STATUS.UPDATED);
            updatedCounter++;
        }

        if (updatedCounter > 0) {
            RxList<T> publishObject = new RxList<>(updatedCounter > 1 ? ChangeType.UPDATE_BULK:ChangeType.UPDATE, null, 0);
            publishObject.oldList = oldItems;
            subject.onNext(publishObject);
        }
    }


    /**
     * Clear.
     */
    public void clear() {
        list.clear();
        hashMap.clear();
        subject.onNext(new RxList<T>(ChangeType.CLEAR, null, 0));
    }

    /**
     * Remove.
     *
     * @param value the value
     */
    public void remove(@NonNull T value) {
        List<T> oldItems = new ArrayList<>(list);
        int pos = list.indexOf(value);
        if(pos>=0) {
            list.remove(value);
            value.setStatus(RxBaseObject.STATUS.REMOVED);
            RxList<T> rxList = new RxList<T>(ChangeType.REMOVE, value, pos);
            rxList.oldList = oldItems;
            hashMap.remove(value.getKey());
            subject.onNext(rxList);
        }
    }


    /**
     * Remove all.
     *
     * @param collection the collection
     */
    public void removeAll(@NonNull List<T> collection) {
        if(collection != null && collection.size() > 0) {
            List<T> oldItems = new ArrayList<>(list);
            list.removeAll(collection);
            for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
                T value = iterator.next();
                hashMap.remove(value.getKey());
                value.setStatus(RxBaseObject.STATUS.REMOVED);
            }
            RxList<T> publishObject = new RxList<>(ChangeType.REMOVE_BULK, null, 0);
            publishObject.oldList = oldItems;
            subject.onNext(publishObject);
        }
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return list.size();
    }

    /**
     * Gets observable.
     *
     * @return the observable
     */
    @NonNull
    public Flowable<RxList<T>> getObservable() {
        return subject;
    }

    /**
     * Get t.
     *
     * @param position the position
     * @return the t
     */
    public T get(int position) {
        return list.get(position);
    }

    /**
     * Throw error.
     *
     * @param exception the exception
     */
    public void throwError(Throwable exception) {
        RxList<T> publishObject = new RxList<>(ChangeType.ERROR, null, 0);
        publishObject.error = exception;
        subject.onNext(publishObject);
    }

    /**
     * Sort using comparator.
     *
     * @param comparator the comparator
     */
    public void sortUsingComparator(Comparator<T> comparator) {
        List<T> oldItems = new ArrayList<>(list);
        Collections.sort(list, comparator);
        RxList<T> publishObject = new RxList<>(ChangeType.SORTED, null, 0);
        publishObject.oldList = oldItems;
        subject.onNext(publishObject);
    }

    /**
     * Clear cache data.
     */
    public void clearCacheData() {
        list.clear();
        hashMap.clear();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    /**
     * Complete.
     */
    public void complete() {
        subject.onComplete();
    }

    /**
     * The enum Change type.
     */
    public enum ChangeType {
        /**
         * Add change type.
         */
        ADD, /**
         * Add bulk change type.
         */
        ADD_BULK, /**
         * Remove change type.
         */
        REMOVE, /**
         * Remove bulk change type.
         */
        REMOVE_BULK,
        /**
         * Update change type.
         */
        UPDATE,
        /**
         * Update change type bulk.
         */
        UPDATE_BULK,
        /**
         * Clear change type.
         */
        CLEAR, /**
         * Sorted change type.
         */
        SORTED, /**
         * Error change type.
         */
        ERROR,

    }

    /**
     * The type Rx list.
     *
     * @param <T> the type parameter
     */
    public static class RxList<T> {
        /**
         * The Change type.
         */
        public ChangeType changeType;
        /**
         * The Item.
         */
        public T item;
        /**
         * The Change pos.
         */
        public int changePos;
        /**
         * The Old list.
         */
        public List<T> oldList;
        /**
         * The Error.
         */
        public Throwable error;

        /**
         * Instantiates a new Rx list.
         *
         * @param changeType the change type
         * @param item       the item
         * @param changePos  the change pos
         */
        RxList(ChangeType changeType, T item, int changePos) {
            this.changeType = changeType;
            this.changePos = changePos;
            this.item = item;
        }
    }
}

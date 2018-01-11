package com.github.mayurkaul.rxobservablelist.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.mayurkaul.rxobservablelist.sample.data.Block;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxViewHolder extends RecyclerView.ViewHolder {
    private CompositeDisposable disposable = new CompositeDisposable();

    public RxViewHolder(View itemView) {
        super(itemView);
    }

    public void registerDisposable(Disposable value)
    {
        disposable.add(value);
    }

    protected void dispose()
    {
        if(disposable!=null && !disposable.isDisposed())
        {
            disposable.dispose();
        }
    }

    protected abstract void onDestroy();

    /**
     * Item click listener
     */
    public interface ClickListener {

        void onViewClicked(int position, Block item, View v);

        void onItemClicked(int position, Block item);

        void onFileItemClicked(int position);
    }

}

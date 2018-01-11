package com.github.mayurkaul.rxobservablelist.sample.adapter;

import android.support.v7.widget.RecyclerView;


public abstract class RxAdapter<VH extends RxViewHolder> extends RecyclerView.Adapter<VH> {
    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        holder.dispose();
    }

    protected abstract void onDestroy();
}

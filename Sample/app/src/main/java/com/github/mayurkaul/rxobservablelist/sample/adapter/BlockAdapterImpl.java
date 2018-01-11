package com.github.mayurkaul.rxobservablelist.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mayurkaul.rxobservablelist.ObservableRxList;
import com.github.mayurkaul.rxobservablelist.sample.R;
import com.github.mayurkaul.rxobservablelist.sample.data.Block;

import java.util.List;

public class BlockAdapterImpl extends RxAdapter<BlockViewHolder> {

    private final ObservableRxList<Block> mList;

    public BlockAdapterImpl(ObservableRxList<Block> list){
        mList = list;
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public BlockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_feed_item, parent, false);
        return new BlockViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(BlockViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

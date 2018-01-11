package com.github.mayurkaul.rxobservablelist.sample.util;

import android.support.v7.util.DiffUtil;

import com.github.mayurkaul.rxobservablelist.sample.data.Block;

import java.util.List;


public class BlockDiffUtilCallback extends DiffUtil.Callback {

    private List<Block> oldList;
    private List<Block> newList;

    public BlockDiffUtilCallback(List<Block> oldList, List<Block> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).hash.equals(newList.get(newItemPosition).hash);
    }
}

package com.github.mayurkaul.rxobservablelist.sample.adapter;

import android.view.View;
import android.widget.TextView;

import com.github.mayurkaul.rxobservablelist.sample.R;
import com.github.mayurkaul.rxobservablelist.sample.data.Block;

public class BlockViewHolder extends RxViewHolder {

    private TextView mNameView;
    private TextView mHashView;
    private TextView mPrevHashView;

    BlockViewHolder(View view, int viewType){
        super(view);
        mNameView = view.findViewById(R.id.textViewName);
        mHashView = view.findViewById(R.id.textViewHash);
        mPrevHashView = view.findViewById(R.id.textViewPrevHash);
    }

    @Override
    protected void onDestroy() {

    }

    public void bind(Block block) {
        mNameView.setText(block.data);
        mHashView.setText(String.format(mHashView.getContext().getString(R.string.currHash),block.hash));
        mPrevHashView.setText(String.format(mHashView.getContext().getString(R.string.prevHash),block.previousHash));
    }
}

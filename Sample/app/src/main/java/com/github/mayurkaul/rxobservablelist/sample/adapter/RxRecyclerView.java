package com.github.mayurkaul.rxobservablelist.sample.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class RxRecyclerView extends RecyclerView {
    public RxRecyclerView(Context context) {
        super(context);
    }

    public RxRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RxRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDestroy() {
        destroyAllViewHolders();
        if(getAdapter()!=null)
        {
            ((RxAdapter)(getAdapter())).onDestroy();
            setAdapter(null);
        }
    }

    private void destroyAllViewHolders() {
        for (int childCount = getChildCount(), i = 0; i < childCount; ++i) {
            final RxViewHolder holder = (RxViewHolder) getChildViewHolder(getChildAt(i));
            if(holder!=null) {
                holder.onDestroy();
            }
        }
    }
}

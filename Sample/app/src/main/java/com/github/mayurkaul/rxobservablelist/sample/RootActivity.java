package com.github.mayurkaul.rxobservablelist.sample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.github.mayurkaul.rxobservablelist.ObservableRxList;
import com.github.mayurkaul.rxobservablelist.ObservableRxListMap;
import com.github.mayurkaul.rxobservablelist.sample.adapter.BlockAdapterImpl;
import com.github.mayurkaul.rxobservablelist.sample.adapter.BlockViewHolder;
import com.github.mayurkaul.rxobservablelist.sample.adapter.RxAdapter;
import com.github.mayurkaul.rxobservablelist.sample.adapter.RxRecyclerView;
import com.github.mayurkaul.rxobservablelist.sample.data.Block;
import com.github.mayurkaul.rxobservablelist.sample.data.BlockChainProviderService;
import com.github.mayurkaul.rxobservablelist.sample.util.BlockDiffUtilCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rmiri.buttonloading.ButtonLoading;

public class RootActivity extends AppCompatActivity {
    private boolean mBound;
    private BlockChainProviderService mService = null;
    private CompositeDisposable _disposable = new CompositeDisposable();
    private final List<ServiceConnection> connectionColl = Collections.synchronizedList(new ArrayList<ServiceConnection>());
    private ObservableRxList<Block> mainList = null;
    private RxAdapter<BlockViewHolder> _adapter = null;
    private TextInputLayout layoutText = null;
    private ButtonLoading btnAddBlock = null;
    private RxRecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layoutText = findViewById(R.id.inputbox);
        btnAddBlock = findViewById(R.id.btnAddBlock);
        mRecyclerView = findViewById(R.id.mainRecyclerView);

        btnAddBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                btnAddBlock.setProgress(true);
                if(layoutText.getEditText()!=null && !layoutText.getEditText().getText().toString().trim().isEmpty()) {
                    if (mBound) {
                        Disposable disposable = mService.addNewBlock(layoutText.getEditText().getText().toString().trim())
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        btnAddBlock.setProgress(false);
                                        layoutText.getEditText().setText("");
                                    }
                                });
                    }
                    else
                    {
                        btnAddBlock.setProgress(false);
                        //btnAddBlock.setEnabled(true);
                    }
                }
                else{
                    btnAddBlock.setProgress(false);
                }
            }
        });
    }

    private void hideKeyboard()
    {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BlockChainProviderService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBound = true;
            registerServiceConnection(mConnection);
            BlockChainProviderService.LocalBinder binder = (BlockChainProviderService.LocalBinder)service;
            mService = binder.getService();
            mainList = mService.getCompleteBlockChain();
            _adapter = new BlockAdapterImpl(mainList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(RootActivity.this));
            mRecyclerView.setAdapter(_adapter);
            registerDisposable(
                    mService.getCompleteBlockChain().getObservable().subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ObservableRxListMap.RxList<Block>>() {
                @Override
                public void accept(ObservableRxListMap.RxList<Block> item) {
                    DiffUtil.DiffResult result;
                    switch (item.changeType) {
                        case ADD:
                            _adapter.notifyItemInserted(item.changePos);
                            break;
                        case REMOVE:
                            _adapter.notifyItemRemoved(item.changePos);
                            break;
                        case UPDATE:
                            _adapter.notifyItemChanged(item.changePos);
                            break;
                        case CLEAR:
                            _adapter.notifyDataSetChanged();
                        case REMOVE_BULK:
                        case ADD_BULK:
                        case UPDATE_BULK:
                            result = DiffUtil.calculateDiff(new BlockDiffUtilCallback(mainList.list,item.oldList));
                            result.dispatchUpdatesTo(_adapter);
                            break;
                        case SORTED:

                            break;
                    }
                }
            }));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        if(!_disposable.isDisposed()) {
            _disposable.dispose();
        }

        disposeServiceConnections();
        super.onDestroy();
    }

    private void disposeServiceConnections() {
        if(connectionColl!=null)
        {
            synchronized (connectionColl)
            {
                for (ServiceConnection connection :
                        connectionColl) {
                    unbindService(connection);
                }
            }

            connectionColl.clear();
        }

    }

    protected void registerServiceConnection(ServiceConnection connection)
    {
        connectionColl.add(connection);
    }

    protected void registerDisposable(Disposable disposable)
    {
        _disposable.add(disposable);
    }
}

package com.github.mayurkaul.rxobservablelist.sample.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.mayurkaul.rxobservablelist.ObservableRxList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BlockChainProviderService extends Service {
    private final IBinder mBinder = new LocalBinder();
    public static int difficulty = 4;
    ObservableRxList<Block> _blockChain = new ObservableRxList<>();
    ObservableRxList<Block> _blockChainCopy = new ObservableRxList<>();

    public BlockChainProviderService() {

    }

    public class LocalBinder extends Binder {
        public BlockChainProviderService getService() {
            return BlockChainProviderService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public ObservableRxList<Block> getCompleteBlockChain(){
        return _blockChain;
    }

    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < _blockChainCopy.size(); i++) {
            currentBlock = _blockChainCopy.get(i);
            previousBlock = _blockChainCopy.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

    public Single<Boolean> addNewBlock(final String blockMessage){
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                Block b = null;
                if(_blockChain.size() == 0) {
                    b = new Block(blockMessage, "0");
                }
                else
                {
                    b = new Block(blockMessage,_blockChain.get(_blockChain.size()-1).hash);
                }
                b.mineBlock(difficulty);
                _blockChainCopy.add(b);
                if (isChainValid()){
                    //add final value to main blockChain
                    _blockChain.add(b);
                }
                e.onSuccess(true);
            }
        });

    }

}

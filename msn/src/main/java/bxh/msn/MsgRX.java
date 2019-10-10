/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package bxh.msn;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.util.Pools;
import bxh.msn.factory.FactoryPools;
import bxh.msn.utils.Preconditions;

/**
 * @Author:  buxiaohui
 * @Desc: 
 * @CreateDate: 2019-10-10 13:44
 **/
public class MsgRX extends BaseMsg {
    private static final int POOL_SIZE = 10;
    private static final Pools.Pool<MsgRX> POOL = FactoryPools.threadSafe(POOL_SIZE,
            new FactoryPools.Factory<MsgRX>() {
                @Override
                public MsgRX create() {
                    return new MsgRX();
                }
            }, new FactoryPools.Resetter<MsgRX>() {
                @Override
                public void reset(@NonNull MsgRX object) {
                    object.args = null;
                    object.from = null;
                }
            });
    private Object[] args;
    private String from;

    public MsgRX() {

    }

    public MsgRX(Object... args) {
        this.args = args;
    }

    public MsgRX(String from, Object... args) {
        this.args = args;
        this.from = from;
    }

    public static MsgRX obtain() {
        return Preconditions.checkNotNull(POOL.acquire());
    }

    public static MsgRX obtain(@NonNull String from, Object[] args) {
        MsgRX msgRX = Preconditions.checkNotNull(POOL.acquire());
        msgRX.from = from;
        msgRX.args = args;
        return msgRX;
    }

    public Object[] getArgs() {
        return args;
    }

    public MsgRX setArgs(Object... args) {
        this.args = args;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public MsgRX setFrom(String from) {
        this.from = from;
        return this;
    }

    @Override
    public String toString() {
        return "LightNaviMsgTX{"
                + "args=" + Arrays.toString(args)
                + '}';
    }

    public synchronized boolean recycle() {
        boolean add2Poll = POOL.release(this);
        return add2Poll;
    }
}

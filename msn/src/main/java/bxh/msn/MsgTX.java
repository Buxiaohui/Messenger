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
 * @CreateDate: 2019-10-10 13:43
 **/
public class MsgTX extends BaseMsg {
    private static final int POOL_SIZE = 10;
    private static final Pools.Pool<MsgTX> POOL = FactoryPools.threadSafe(POOL_SIZE,
            new FactoryPools.Factory<MsgTX>() {
                @Override
                public MsgTX create() {
                    return new MsgTX();
                }
            }, new FactoryPools.Resetter<MsgTX>() {
                @Override
                public void reset(@NonNull MsgTX object) {
                    object.args = null;
                    object.from = null;
                    object.target = null;
                    object.mainThread = false;
                    object.msgType = Integer.MIN_VALUE;
                }
            });
    private int msgType;
    private Object[] args;
    private String target;
    private String from;
    private boolean mainThread;

    public MsgTX() {

    }

    public MsgTX(String from, String target, int msgType, boolean mainThread,
                 Object... args) {
        this.args = args;
        this.from = from;
        this.msgType = msgType;
        this.target = target;
        this.mainThread = mainThread;
    }

    public static MsgTX obtain() {
        return Preconditions.checkNotNull(POOL.acquire());
    }

    public static MsgTX obtain(@NonNull String from, String target) {
        MsgTX msgTX = Preconditions.checkNotNull(POOL.acquire());
        msgTX.from = from;
        msgTX.target = target;
        return msgTX;
    }

    public static MsgTX obtain(@NonNull String from, String target, int msgType,
                               boolean mainThread, Object... args) {
        MsgTX msgTX = Preconditions.checkNotNull(POOL.acquire());
        msgTX.from = from;
        msgTX.target = target;
        msgTX.msgType = msgType;
        msgTX.args = args;
        msgTX.mainThread = mainThread;
        return msgTX;
    }

    public boolean isMainThread() {
        return mainThread;
    }

    public MsgTX setMainThread(boolean mainThread) {
        this.mainThread = mainThread;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public MsgTX setTarget(String target) {
        this.target = target;
        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public MsgTX setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }

    public Object[] getArgs() {
        return args;
    }

    public MsgTX setArgs(Object... args) {
        this.args = args;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public MsgTX setFrom(String from) {
        this.from = from;
        return this;
    }

    @Override
    public String toString() {
        return "LightNaviMsgTX{"
                + "msgType=" + msgType
                + ", args=" + Arrays.toString(args)
                + ", from='" + from
                + '}';
    }

    public synchronized boolean recycle() {
        boolean add2Poll = POOL.release(this);
        return add2Poll;
    }
}

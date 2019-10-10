/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package bxh.msn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baidu.navisdk.util.common.LogUtil;
import com.baidu.navisdk.util.worker.loop.BNMainLooperHandler;

import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by buxiaohui on 2018/8/9.
 */

public class MessengerImpl implements IMessenger {
    private static final String TAG = "LightNaviMessengerImpl";
    private static final int INNER_MSG_ID = 171011;
    private ConcurrentHashMap<String, IMsgHandler> msgHandlerHashMap =
            new ConcurrentHashMap<>();
    BNMainLooperHandler mBnMainLooperHandler = new BNMainLooperHandler("") {
        @Override
        public void onMessage(Message msg) {
            super.onMessage(msg);
            if (msg.what == INNER_MSG_ID) {
                handle((MsgTX) msg.obj);
            }
        }
    };

    @Override
    public void addHandler(IMsgHandler handler) {
        if (msgHandlerHashMap == null) {
            msgHandlerHashMap = new ConcurrentHashMap<>();
        }
        if (handler != null && !TextUtils.isEmpty(handler.getTag())) {
            msgHandlerHashMap.put(handler.getTag(), handler);
        }
    }

    @Override
    public void removeHandler(IMsgHandler handler) {
        if (handler != null && TextUtils.isEmpty(handler.getTag()) && msgHandlerHashMap != null
                && !msgHandlerHashMap.isEmpty()) {
            msgHandlerHashMap.remove(handler.getTag());
        }
    }

    @Override
    public void release() {
        if (msgHandlerHashMap != null) {
            msgHandlerHashMap.clear();
        }
        if (mBnMainLooperHandler != null) {
            mBnMainLooperHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void sendMsg(final MsgTX msgTX) {
        if (msgTX == null) {
            return;
        }
        if (msgTX.isMainThread() && Looper.myLooper() != Looper.getMainLooper()) {
            Message msg = Message.obtain();
            msg.what = INNER_MSG_ID;
            msg.obj = msgTX;
            mBnMainLooperHandler.sendMessage(msg);
        } else {
            handle(msgTX);
        }
    }

    private void handle(final MsgTX msgTX) {
        String target = msgTX.getTarget();
        if (TextUtils.isEmpty(target)) { // 所有接收器都能收到
            for (Map.Entry<String, IMsgHandler> entry : msgHandlerHashMap.entrySet()) {
                handle(msgHandlerHashMap.get(entry.getKey()), msgTX);
            }
        } else { // 指定接收器可以处理
            handle(msgHandlerHashMap.get(target), msgTX);
        }
        if (msgTX != null) {
            msgTX.recycle();
        }
    }

    private void handle(final IMsgHandler handler, final MsgTX msgTX) {
        if (msgTX == null) {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "handle,msg is null");
            }
            return;
        }

        if (handler == null) {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "handle,handler is null");
            }
            return;
        }
        handler.handleMsg(msgTX);
    }

    @Override
    public MsgRX sendMsgSync(@NonNull MsgTX msgTX) {
        String target = msgTX.getTarget();
        if (TextUtils.isEmpty(target) && LogUtil.LOGGABLE) {
            throw new IllegalArgumentException("sendMsgSync,target invalid");
        }
        if (target != null && msgHandlerHashMap != null && msgHandlerHashMap.containsKey(target)
                && msgHandlerHashMap.get(target) != null) {
            IMsgHandler handler = msgHandlerHashMap.get(target);
            if (handler != null && target.equalsIgnoreCase(handler.getTag())) {
                MsgRX msgRX = handler.handleMsgSync(msgTX);
                msgTX.recycle();
                return msgRX;
            }
        } else {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "target:" + target);
            }
        }
        if (msgTX != null) {
            msgTX.recycle();
        }
        return null;
    }
}

/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package bxh.msn;

/**
 * @Author:  buxiaohui
 * @Desc:
 * @CreateDate: 2019-10-10 13:43
 **/
public interface IMessenger {
    void sendMsg(MsgTX message);

    void addHandler(IMsgHandler handler);

    void removeHandler(IMsgHandler handler);

    void release();

    MsgRX sendMsgSync(MsgTX message);
}

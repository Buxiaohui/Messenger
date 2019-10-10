/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package bxh.msn;
/**
 * @Author:  buxiaohui
 * @Desc: 
 * @CreateDate: 2019-10-10 13:43
 **/
public interface IMsgHandler {
    String getTag();

    void handleMsg(MsgTX msgTX);

    MsgRX handleMsgSync(MsgTX msgTX);
}

/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package bxh.msn;

import androidx.annotation.NonNull;
import bxh.msn.factory.FactoryPools;
import bxh.msn.factory.StateVerifier;

/**
 * @Author:  buxiaohui
 * @Desc:
 * @CreateDate: 2019-10-10 13:43
 **/
public abstract class BaseMsg implements FactoryPools.Poolable {
    private StateVerifier stateVerifier;

    public BaseMsg() {
        stateVerifier = StateVerifier.newInstance();
    }

    @NonNull
    @Override
    public StateVerifier getVerifier() {
        return stateVerifier;
    }
}

package com.shengjie.entity.kernel.constraint.wrapper;

import com.shengjie.entity.kernel.constraint.Condition;

public abstract class PrimitiveCondition<T> implements Condition {
    public final T primitive;

    protected PrimitiveCondition(T primitive) {
        this.primitive = primitive;
    }
}

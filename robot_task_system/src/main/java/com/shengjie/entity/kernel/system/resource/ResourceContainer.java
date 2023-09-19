package com.shengjie.entity.kernel.system.resource;

import com.shengjie.entity.kernel.system.SystemResourceHandler;

public abstract class ResourceContainer<T> implements Resource<T>{

    protected T resource;
    public ResourceContainer(T resource){
        this.resource = resource;
    }
    public abstract SystemResourceHandler<T> getHandler();
}

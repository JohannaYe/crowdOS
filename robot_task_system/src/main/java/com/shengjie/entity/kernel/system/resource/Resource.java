package com.shengjie.entity.kernel.system.resource;

import com.shengjie.entity.kernel.system.SystemResourceHandler;

public interface Resource<T> {
    SystemResourceHandler<T> getHandler();
}

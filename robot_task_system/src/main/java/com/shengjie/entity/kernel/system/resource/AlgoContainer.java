package com.shengjie.entity.kernel.system.resource;

import com.shengjie.entity.kernel.algorithms.AlgoFactory;
import com.shengjie.entity.kernel.system.SystemResourceHandler;

public class AlgoContainer extends ResourceContainer<AlgoFactory> {

    public AlgoContainer(AlgoFactory resource) {
        super(resource);
    }
    @Override
    public SystemResourceHandler<AlgoFactory> getHandler() {
        return new SystemResourceHandler<AlgoFactory>() {
            @Override
            public AlgoFactory getResourceView() {
                return resource;
            }

            @Override
            public AlgoFactory getResource() {
                return resource;
            }
        };
    }
}

package com.shengjie;

import com.shengjie.entity.kernel.CrowdKernel;
import com.shengjie.entity.kernel.Kernel;
import org.springframework.stereotype.Component;

@Component
public class CrowdKernelComponent {
    public CrowdKernel getKernel(){
        CrowdKernel kernel = Kernel.getKernel();
        if (!kernel.isInitialed())kernel.initial();
        return kernel;
    }
}

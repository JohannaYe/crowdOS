package com.shengjie.entity;

import com.shengjie.entity.kernel.constraint.Constraint;
import com.shengjie.entity.kernel.resource.SimpleTask;


import java.util.List;


public class CoordinateTask extends SimpleTask {

    private Integer venueId;

    public CoordinateTask(List<Constraint> constraints, TaskDistributionType taskDistributionType) {
        super(constraints, taskDistributionType);
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

}

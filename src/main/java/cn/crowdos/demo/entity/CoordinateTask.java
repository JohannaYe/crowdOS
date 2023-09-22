package cn.crowdos.demo.entity;

import cn.crowdos.kernel.constraint.Constraint;
import cn.crowdos.kernel.resource.SimpleTask;

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

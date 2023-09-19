package com.shengjie.entity;

import com.shengjie.entity.kernel.constraint.Condition;
import com.shengjie.entity.kernel.constraint.Coordinate;
import com.shengjie.entity.kernel.resource.AbstractParticipant;
import com.shengjie.entity.kernel.resource.ability;

public class CoordinateParticipant extends AbstractParticipant {

    @ability
    private Coordinate coordinate;

    private Integer venueId;

    public CoordinateParticipant(Coordinate coordinate) {

        this.coordinate = coordinate;
        status = ParticipantStatus.AVAILABLE;

    }

    @Override
    public boolean hasAbility(Class<? extends Condition> conditionClass) {
        return conditionClass == Coordinate.class;
    }

    @Override
    public Condition getAbility(Class<? extends Condition> conditionClass) {
        if (!hasAbility(conditionClass))
            return null;
        else return coordinate;
    }

    @Override
    public String toString() {
        return "CoordinateParticipant{" +
                "coordinate=" + coordinate +
                '}';
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }
}

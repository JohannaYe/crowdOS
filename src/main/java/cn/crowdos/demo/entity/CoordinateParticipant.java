package cn.crowdos.demo.entity;

import cn.crowdos.kernel.constraint.Condition;
import cn.crowdos.kernel.constraint.Coordinate;
import cn.crowdos.kernel.resource.AbstractParticipant;
import cn.crowdos.kernel.resource.ability;

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

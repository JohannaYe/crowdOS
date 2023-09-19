package com.shengjie.entity.kernel.resource;

import com.shengjie.entity.kernel.constraint.Condition;

public abstract class AbstractParticipant implements Participant{


    protected ParticipantStatus status;

    @Override
    public ParticipantStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }
    public abstract boolean hasAbility(Class<? extends Condition> conditionClass);

    public abstract Condition getAbility(Class<? extends Condition> conditionClass);
    @Override
    public boolean available() {
        return status == ParticipantStatus.AVAILABLE;
    }
}

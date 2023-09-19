package com.shengjie.entity.kernel.system.resource;

import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.system.SystemResourceHandler;

import java.util.LinkedList;

public class ParticipantPool extends LinkedList<Participant> implements Resource<ParticipantPool> {
    @Override
    public SystemResourceHandler<ParticipantPool> getHandler() {
        ParticipantPool participants = this;
        return new SystemResourceHandler<ParticipantPool>() {

            @Override
            public ParticipantPool getResourceView() {
                return participants;
            }

            @Override
            public ParticipantPool getResource() {
                return participants;
            }
        };
    }
}

package com.shengjie.entity.kernel.algorithms;

import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.resource.Task;

import java.util.List;

@FunctionalInterface
public interface ParticipantSelectionAlgo {
    /**
     * Get all the participants that are eligible to be assigned to the given task.
     *
     * @param task The task for which you want to get the candidates.
     * @return A list of participants.
     */
    List<Participant> getCandidates(Task task);
}

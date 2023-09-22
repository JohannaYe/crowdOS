package cn.crowdos.demo.kernel;


import cn.crowdos.demo.entity.Action;

import java.util.List;

// The Human Instruction model is for human Instruction parsing, letting robot understand the human instruction easily
public interface HumanInstruction {
    // Parsing the humanInstruction
    List<Action> humanInstructionParsing();
    List<Action> getTaskSteps();
    String getHumanInstruction();
}

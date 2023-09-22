package cn.crowdos.demo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class RobotCoordinateComponent {
    private Map<Integer, List<Double>> robotLocationMap = new HashMap<>();

    public RobotCoordinateComponent(){

    }

}
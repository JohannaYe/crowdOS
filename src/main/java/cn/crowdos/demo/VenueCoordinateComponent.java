package cn.crowdos.demo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class VenueCoordinateComponent {
    private Map<Integer, List<Double>> venueLocationMap = new HashMap<>();

    public VenueCoordinateComponent(){
        venueLocationMap.put(0,Arrays.asList(-5.68,1.74));
        venueLocationMap.put(1,Arrays.asList(-5.77,-0.27));
        venueLocationMap.put(2,Arrays.asList(-5.73,-5.47));
        venueLocationMap.put(3,Arrays.asList(-2.34,-1.5));
        venueLocationMap.put(4,Arrays.asList(2.14,1.37));
        venueLocationMap.put(5,Arrays.asList(0.83,-6.05));
        venueLocationMap.put(6,Arrays.asList(5.32,-0.16));
        venueLocationMap.put(7,Arrays.asList(4.53,-1.57));
        venueLocationMap.put(8,Arrays.asList(6.49,-1.48));
        venueLocationMap.put(9,Arrays.asList(5.57,-6.95));
        venueLocationMap.put(10,Arrays.asList(4.57,-5.95));
        venueLocationMap.put(11,Arrays.asList(5.83,-6.35));
        venueLocationMap.put(12,Arrays.asList(4.77,-2.65));
        venueLocationMap.put(13,Arrays.asList(6.22,-5.95));
        venueLocationMap.put(14,Arrays.asList(-1.36,-5.44));
        venueLocationMap.put(15,Arrays.asList(-2.57,4.64));
        venueLocationMap.put(16,Arrays.asList(5.43,3.95));
        venueLocationMap.put(17,Arrays.asList(5.88,-4.95));
        venueLocationMap.put(18,Arrays.asList(2.17,-1.95));
    }

}

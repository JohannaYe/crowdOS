package com.shengjie.entity.kernel.constraint;

import com.shengjie.entity.kernel.DecomposeException;
import com.shengjie.entity.kernel.Decomposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleSpatioConstraint implements Constraint {

    private final Coordinate[] range;

    public SimpleSpatioConstraint(Coordinate topLeft, Coordinate bottomRight) throws InvalidConstraintException {
        if (topLeft.inLine(bottomRight)) throw new InvalidConstraintException();
        this.range = new Coordinate[]{topLeft, bottomRight};
    }

    public SimpleSpatioConstraint(Coordinate bottomRight) throws InvalidConstraintException {
        Coordinate topLeft = new Coordinate();
        if (topLeft.inLine(bottomRight)) throw new InvalidConstraintException();
        this.range = new Coordinate[]{topLeft, bottomRight};
    }

    @Override
    public boolean satisfy(Condition condition) {
        if (!(condition instanceof Coordinate)) return false;
        Coordinate coord = (Coordinate) condition;
        return range[0].longitude <= coord.longitude && range[0].latitude <= coord.latitude
                && coord.longitude < range[1].longitude && coord.latitude < range[1].latitude;
    }

    @Override
    public Class<? extends Condition> getConditionClass() {
        return Coordinate.class;
    }

    @Override
    public String description() {
        return toString();
    }

    @Override
    public Decomposer<Constraint> decomposer() {
        return new Decomposer<Constraint>() {
            @Override
            public List<Constraint> trivialDecompose() {
                try {
                    return Collections.singletonList(new SimpleSpatioConstraint(range[0], range[1]));
                } catch (InvalidConstraintException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<Constraint> scaleDecompose(int scale) throws DecomposeException {
                int k = (int) Math.ceil(Math.sqrt(scale));
                List<Constraint> subConstraints = new ArrayList<>(k*k);
                double xLen = (range[1].longitude - range[0].longitude)/k;
                double yLen = (range[1].latitude - range[0].latitude)/k;
                for (int i = 0; i < k-1; i++) {
                    double sx = range[0].longitude + i * xLen;
                    double ex = Math.min(range[0].longitude + (i+1) * xLen, range[1].longitude);
                    for (int j = 0; j < k-1; j++) {
                        double sy = range[0].latitude + j * yLen;
                        double ey = Math.min(range[0].latitude + (j+1)*yLen, range[1].latitude);
                        try {
                            subConstraints.add(new SimpleSpatioConstraint(new Coordinate(sx, sy), new Coordinate(ex, ey)));
                        } catch (InvalidConstraintException e) {
                            throw new DecomposeException(e);
                        }
                    }
                }
                return subConstraints;
            }
        };
    }

    @Override
    public String toString() {
        return "SpatioConstraint(" + range[0] + "," + range[1] + ')';
    }
}

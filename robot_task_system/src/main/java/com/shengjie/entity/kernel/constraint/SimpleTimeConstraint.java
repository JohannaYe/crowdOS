package com.shengjie.entity.kernel.constraint;

import com.shengjie.entity.kernel.DecomposeException;
import com.shengjie.entity.kernel.Decomposer;
import com.shengjie.entity.kernel.constraint.wrapper.DateCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SimpleTimeConstraint implements Constraint{

    Date[] dateRange;

    public SimpleTimeConstraint(Date startDate, Date endDate) throws InvalidConstraintException {
        if (startDate.compareTo(endDate) >= 0) throw new InvalidConstraintException();
        dateRange = new Date[]{startDate, endDate};
    }

    public SimpleTimeConstraint(String startDateStr, String endDateStr) throws InvalidConstraintException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date startDate;
        Date endDate;
        try {
            startDate = simpleDateFormat.parse(startDateStr);
            endDate = simpleDateFormat.parse(endDateStr);
        } catch (ParseException e) {
            throw new InvalidConstraintException(e.getCause());
        }
        if (startDate.compareTo(endDate) >= 0) throw new InvalidConstraintException();
        dateRange = new Date[]{startDate, endDate};
    }

    @Override
    public boolean satisfy(Condition condition) {
        if (!(condition instanceof Date)) return false;
        Date date = (Date) condition;
        return dateRange[0].compareTo(date) <= 0 && date.compareTo(dateRange[1]) < 0;
    }

    @Override
    public Class<? extends Condition> getConditionClass() {
        return DateCondition.class;
    }


    @Override
    public String description() {
        return this.toString();
    }

    @Override
    public Decomposer<Constraint> decomposer() {
        return new Decomposer<Constraint>() {
            @Override
            public List<Constraint> trivialDecompose() {
                try {
                    return Collections.singletonList(new SimpleTimeConstraint(dateRange[0], dateRange[1]));
                } catch (InvalidConstraintException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<Constraint> scaleDecompose(int scale) throws DecomposeException {
                if (scale < 0) throw new DecomposeException("invalid decompose scale");
                if (scale == 1) return trivialDecompose();
                long tLen = (long) Math.ceil(1.0*(dateRange[1].getTime() - dateRange[0].getTime()) / scale);
                List<Constraint> subConstraints = new ArrayList<>(scale);
                for (int i = 0; i < scale-1; i++) {
                    long st = dateRange[0].getTime() + i * tLen;
                    long et = Math.min(dateRange[0].getTime() + (i+1)*tLen, dateRange[1].getTime());
                    try {
                        subConstraints.add(new SimpleTimeConstraint(new Date(st), new Date(et)));
                    } catch (InvalidConstraintException e) {
                        throw new DecomposeException(e);
                    }
                }
                return subConstraints;
            }
        };
    }

    @Override
    public String toString() {
        return "TimeConstraint(" + dateRange[0] + "-" + dateRange[1] + ")";
    }
}

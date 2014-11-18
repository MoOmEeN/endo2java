package com.moomeen.endo2java.stats;

import static java.math.BigDecimal.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.moomeen.endo2java.model.DetailedWorkout;
import com.moomeen.endo2java.model.Point;
import com.moomeen.endo2java.model.stats.DistanceTime;

public class StatsCalculator {
	
	private DetailedWorkout workout;

	public StatsCalculator(DetailedWorkout workout) {
		this.workout = workout;
	}

	public DistanceTime getDistanceTime(double distance){
		List<Point> pointsOfBest = new ArrayList<Point>();

		for (int i = 0; i < workout.getPoints().size(); i++){
			Point point = workout.getPoints().get(i);
			pointsOfBest.add(point);
			if (point.getDistance() == distance){
				DistanceTime best = new DistanceTime();
				best.setPoints(pointsOfBest);
				best.setDistance(distance);
				best.setTime(new Duration(workout.getStartTime(), point.getTime()));
				return best;
			}
			if (point.getDistance() > distance){
				// not exact
				Point prev = workout.getPoints().get(--i);
				BigDecimal distanceDiff = valueOf(point.getDistance()).subtract(valueOf(prev.getDistance()));
				long millisTimeDiff = point.getTime().getMillis() - prev.getTime().getMillis();

				BigDecimal prevToDistance = valueOf(distance).subtract(valueOf(prev.getDistance()));
				double pointAfterDistance = point.getDistance() - distance;

				double ratio = prevToDistance.doubleValue()/distanceDiff.doubleValue();

				double millisToAdd = ratio * millisTimeDiff;


				DistanceTime best = new DistanceTime();
				best.setDistance(distance);
				best.setTime(new Duration(workout.getStartTime(), new DateTime(new Date(new Double(prev.getTime().getMillis() + millisToAdd).longValue()))));
				best.setPoints(pointsOfBest);
				return best;
			}
		}
		throw new IllegalArgumentException("workout does not contain point with given distance");
	}
}

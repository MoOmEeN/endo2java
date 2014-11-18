package com.moomeen.endo2java.stats;

import static java.math.BigDecimal.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import com.moomeen.endo2java.model.DetailedWorkout;
import com.moomeen.endo2java.model.Point;
import com.moomeen.endo2java.model.PointInstruction;
import com.moomeen.endo2java.model.stats.DistanceTime;

public class StatsCalculator {

	private DetailedWorkout workout;

	public StatsCalculator(DetailedWorkout workout) {
		this.workout = workout;
	}

	public DistanceTime getDistanceBest(double distance){
		TreeMap<Double, ExtendedPoint> pointsMap = new TreeMap<Double, ExtendedPoint>();
		
		ExtendedPoint prevP = null;
		for (Point point : workout.getPoints()) {
			long pointMillis = -1;
			if (prevP != null){
				if (prevP.getInstruction() == PointInstruction.PAUSE){
					pointMillis = prevP.relativeTime;
				} else {
					pointMillis = new Duration(prevP.getTime(), point.getTime()).getMillis() + prevP.relativeTime;
				}
				
			}
			ExtendedPoint ex = new ExtendedPoint(point, pointMillis);
			prevP = ex;
			pointsMap.put(point.getDistance(), ex);
		}
		
		DistanceTime time = new DistanceTime();
		time.setDistance(distance);

		for (Map.Entry<Double, ExtendedPoint> pointEntry : pointsMap.entrySet()){
			Double endPointDistance = pointEntry.getValue().getDistance() + distance;
			
			if (pointsMap.containsKey(endPointDistance)){
				ExtendedPoint next = pointsMap.get(endPointDistance);
				Duration thisTime = new Duration(pointEntry.getValue().relativeTime, next.relativeTime);
				if (time.getTime()== null || thisTime.getMillis() < time.getTime().getMillis()){
					time.setTime(thisTime);
					time.setPoints(workout.getPoints().subList(workout.getPoints().indexOf(pointEntry.getValue().point), workout.getPoints().indexOf(next.point) + 1));
				}
			} else {
				Map.Entry<Double, ExtendedPoint> prevEntry = pointsMap.floorEntry(endPointDistance);
				Map.Entry<Double, ExtendedPoint> nextEntry = pointsMap.ceilingEntry(endPointDistance);
				if (prevEntry == null || nextEntry == null){
					continue;
				}
				ExtendedPoint prev = prevEntry.getValue();
				ExtendedPoint next = nextEntry.getValue();
				BigDecimal distanceDiff = valueOf(next.getDistance()).subtract(valueOf(prev.getDistance()));
				long millisTimeDiff = next.relativeTime - prev.relativeTime;
				
				BigDecimal prevToDistance = valueOf(endPointDistance).subtract(valueOf(prev.getDistance()));
				
				double ratio = prevToDistance.doubleValue()/distanceDiff.doubleValue();
				
				double millisToAdd = ratio * millisTimeDiff;
				Duration thisTime = new Duration(pointEntry.getValue().relativeTime, new Double(prev.relativeTime + millisToAdd).longValue());
				
				// DEBUG
				Period p = new Period(thisTime);
				String s = pointEntry.getValue().getDistance() + " - " + next.getDistance() + "="+ p.getMinutes() + ":" + p.getSeconds() + ":" + p.getMillis();
				
				if (time.getTime()== null || thisTime.getMillis() < time.getTime().getMillis()){
					time.setTime(thisTime);
					time.setPoints(workout.getPoints().subList(workout.getPoints().indexOf(pointEntry.getValue().point), workout.getPoints().indexOf(next.point) + 1));
				}
				
			}
		}
		
//		for (Point point : workout.getPoints()) {
//			Double endPointDistance = point.getDistance() + distance;
//			if (pointsMap.containsKey(endPointDistance)){
//				Point next = pointsMap.get(endPointDistance);
//				Duration thisTime = new Duration(point.getTime(), next.getTime());
//				if (time.getTime()== null || thisTime.getMillis() < time.getTime().getMillis()){
//					time.setTime(thisTime);
//					time.setPoints(workout.getPoints().subList(workout.getPoints().indexOf(point), workout.getPoints().indexOf(next) + 1));
//				}
//			} else {
//				Map.Entry<Double, ExtendedPoint> prevEntry = pointsMap.floorEntry(endPointDistance);
//				Map.Entry<Double, ExtendedPoint> nextEntry = pointsMap.ceilingEntry(endPointDistance);
//				if (prevEntry == null || nextEntry == null){
//					continue;
//				}
//				Point prev = prevEntry.getValue();
//				Point next = nextEntry.getValue();
//				BigDecimal distanceDiff = valueOf(next.getDistance()).subtract(valueOf(prev.getDistance()));
//				long millisTimeDiff = next.getTime().getMillis() - prev.getTime().getMillis();
//				
//				BigDecimal prevToDistance = valueOf(endPointDistance).subtract(valueOf(prev.getDistance()));
//				
//				double ratio = prevToDistance.doubleValue()/distanceDiff.doubleValue();
//				
//				double millisToAdd = ratio * millisTimeDiff;
//				Duration thisTime = new Duration(point.getTime(), new DateTime(new Date(new Double(prev.getTime().getMillis() + millisToAdd).longValue())));
//				
//				// DEBUG
//				Period p = new Period(thisTime);
//				String s = point.getDistance() + " - " + next.getDistance() + "="+ p.getMinutes() + ":" + p.getSeconds() + ":" + p.getMillis();
//				
//				if (time.getTime()== null || thisTime.getMillis() < time.getTime().getMillis()){
//					time.setTime(thisTime);
//					time.setPoints(workout.getPoints().subList(workout.getPoints().indexOf(point), workout.getPoints().indexOf(next) + 1));
//				}
//				
//			}
//		}
		return time;
		
		//return getDistanceTime(distance);
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

	private class ExtendedPoint extends Point {
		
		private Point point;
		private long relativeTime;
		
		public ExtendedPoint(Point p, long relativeTime) {
			this.point = p;
			this.relativeTime = relativeTime;
		}
		
		@Override
		public DateTime getTime() {
			return point.getTime();
		}
		@Override
		public Double getSpeed() {
			return point.getSpeed();
		}
		@Override
		public Double getDistance() {
			return point.getDistance();
		}
		@Override
		public Double getAltitude() {
			return point.getAltitude();
		}
		@Override
		public Double getLongitude() {
			return point.getLongitude();
		}
		@Override
		public Double getLatitude() {
			return point.getLatitude();
		}
		@Override
		public PointInstruction getInstruction() {
			return point.getInstruction();
		}
	}
	
}

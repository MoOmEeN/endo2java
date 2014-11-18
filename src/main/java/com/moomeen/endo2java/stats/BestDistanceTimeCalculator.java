package com.moomeen.endo2java.stats;

import static java.math.BigDecimal.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.moomeen.endo2java.model.DetailedWorkout;
import com.moomeen.endo2java.model.Point;
import com.moomeen.endo2java.model.PointInstruction;
import com.moomeen.endo2java.model.stats.DistanceTime;

public class BestDistanceTimeCalculator {

	private DetailedWorkout workout;
	
	private TreeMap<Double, PointWithRelativeTime> pointsMap;

	public BestDistanceTimeCalculator(DetailedWorkout workout) {
		this.workout = workout;
		this.pointsMap = normalizePoints();
	}
	
	private TreeMap<Double, PointWithRelativeTime> normalizePoints() {
		TreeMap<Double, PointWithRelativeTime> pointsMap = new TreeMap<Double, PointWithRelativeTime>();
		PointWithRelativeTime previousWithRelativeTime = null;
		for (Point currentPoint : workout.getPoints()){
			PointWithRelativeTime normalizedPoint = normalizePoint(pointsMap, previousWithRelativeTime, currentPoint);
			pointsMap.put(currentPoint.getDistance(), normalizedPoint);
			previousWithRelativeTime = normalizedPoint;
		}
		return pointsMap;
	}

	private PointWithRelativeTime normalizePoint(TreeMap<Double, PointWithRelativeTime> pointsMap, PointWithRelativeTime previousWithRelativeTime, Point currentPoint) {
		long pointRelativeTime = 0;
		boolean isFirstPoint = currentPoint.getInstruction() == PointInstruction.START;
		if (!isFirstPoint) {
			if (wasPaused(previousWithRelativeTime, currentPoint)){
				pointRelativeTime = previousWithRelativeTime.relativeTime;
			} else {
				pointRelativeTime = previousWithRelativeTime.relativeTime + durationInMillis(previousWithRelativeTime, currentPoint);
			}
		}
		return new PointWithRelativeTime(currentPoint, pointRelativeTime);
	}
	
	private long durationInMillis(Point previousPoint, Point currentPoint) {
		return new Duration(previousPoint.getTime(), currentPoint.getTime()).getMillis();
	}

	private boolean wasPaused(Point previousPoint, Point point) {
		return previousPoint.getInstruction() == PointInstruction.PAUSE && point.getInstruction() == PointInstruction.RESUME;
	}

	public DistanceTime calculate(double distance){
		DistanceTime bestTime = new DistanceTime();
		bestTime.setDistance(distance);

		for (Map.Entry<Double, PointWithRelativeTime> pointEntry : pointsMap.entrySet()){
			Double desiredPointDistance = pointEntry.getKey() + distance;
			processPoint(bestTime, pointEntry.getValue(), desiredPointDistance);
		}
		return bestTime;
	}

	private void processPoint(DistanceTime bestTime, PointWithRelativeTime point, Double desiredPointDistance) {
		if (pointsMap.containsKey(desiredPointDistance)){
			processExactDistancePoint(bestTime, point, desiredPointDistance);
		} else {
			processCloseDistancePoints(bestTime, point, desiredPointDistance);
		}
	}

	private void processExactDistancePoint(DistanceTime bestTime, PointWithRelativeTime point, Double desiredPointDistance) {
		PointWithRelativeTime pointWithDesiredDistance = pointsMap.get(desiredPointDistance);
		long relativeTimeDiff = pointWithDesiredDistance.relativeTime - point.relativeTime;
		if (isBetter(bestTime, relativeTimeDiff)){
			setNewBestTime(bestTime, point, pointWithDesiredDistance, relativeTimeDiff);
		}
	}
	
	private void processCloseDistancePoints(DistanceTime bestTime, PointWithRelativeTime point, Double desiredPointDistance){
		PointWithRelativeTime desiredDistanceFloorPoint = getFloorPoint(desiredPointDistance);
		PointWithRelativeTime desiredDistanceCeilingPoint = getCeilingPoint(desiredPointDistance);
		if (desiredDistanceFloorPoint == null || desiredDistanceCeilingPoint == null){
			return;
		}
		long relativeTimeDiff = calculateTimeDiff(point, desiredDistanceFloorPoint, desiredDistanceCeilingPoint, desiredPointDistance);
		if (isBetter(bestTime, relativeTimeDiff)){
			setNewBestTime(bestTime, point, desiredDistanceCeilingPoint, relativeTimeDiff);
		}
	}
	
	private PointWithRelativeTime getFloorPoint(double distance){
		Map.Entry<Double, PointWithRelativeTime> desiredDistanceFloorEntry = pointsMap.floorEntry(distance);
		if (desiredDistanceFloorEntry == null){
			return null;
		}
		return desiredDistanceFloorEntry.getValue();
	}
	
	private PointWithRelativeTime getCeilingPoint(double distance){
		Map.Entry<Double, PointWithRelativeTime> desiredDistanceCeilingEntry = pointsMap.ceilingEntry(distance);
		if (desiredDistanceCeilingEntry == null){
			return null;
		}
		return desiredDistanceCeilingEntry.getValue();
	}
	
	private long calculateTimeDiff(PointWithRelativeTime currentPoint, PointWithRelativeTime desiredDistanceFloorPoint, PointWithRelativeTime desiredDistanceCeilingPoint, double desiredPointDistance){
		BigDecimal floorCeilingDistanceDiff = valueOf(desiredDistanceCeilingPoint.getDistance()).subtract(valueOf(desiredDistanceFloorPoint.getDistance()));
		BigDecimal floorDesiredDistanceDiff = valueOf(desiredPointDistance).subtract(valueOf(desiredDistanceFloorPoint.getDistance()));
		double ratio = floorDesiredDistanceDiff.doubleValue() / floorCeilingDistanceDiff.doubleValue();
		long floorCeilingRelativeTimeDiff = desiredDistanceCeilingPoint.relativeTime - desiredDistanceFloorPoint.relativeTime;
		double millisToAddToFloorDistance = ratio * floorCeilingRelativeTimeDiff;
		long relativeTimeDiff = new Double(desiredDistanceFloorPoint.relativeTime + millisToAddToFloorDistance).longValue() - currentPoint.relativeTime;
		return relativeTimeDiff;
	}

	private boolean isBetter(DistanceTime bestTime, long relativeTimeDiff) {
		return bestTime.getTime() == null || relativeTimeDiff < bestTime.getTime().getMillis();
	}
	
	private void setNewBestTime(DistanceTime bestTime, PointWithRelativeTime currentPoint, PointWithRelativeTime pointWithDesiredDistance, long relativeTimeDiff) {
		bestTime.setTime(new Duration(relativeTimeDiff));
		bestTime.setPoints(subList(currentPoint, pointWithDesiredDistance));
	}
	
	private List<Point> subList(PointWithRelativeTime currentPoint, PointWithRelativeTime pointWithDesiredDistance) {
		return workout.getPoints().subList(workout.getPoints().indexOf(currentPoint.point), workout.getPoints().indexOf(pointWithDesiredDistance.point) + 1);
	}
	

	private class PointWithRelativeTime extends Point {
		
		private Point point;
		private long relativeTime;
		
		public PointWithRelativeTime(Point p, long relativeTime) {
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

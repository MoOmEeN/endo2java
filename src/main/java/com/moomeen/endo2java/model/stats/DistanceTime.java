package com.moomeen.endo2java.model.stats;

import java.util.List;

import org.joda.time.Duration;

import com.moomeen.endo2java.model.Point;

public class DistanceTime {

	private Duration time;
	private double distance;
	private List<Point> points;

	public Duration getTime() {
		return time;
	}
	public void setTime(Duration time) {
		this.time = time;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
}

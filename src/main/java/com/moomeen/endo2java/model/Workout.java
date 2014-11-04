package com.moomeen.endo2java.model;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Workout {
	private Long id;
	private Duration duration;
	private String distance;
	private Double burgersBurned;
	private Sport sport;
	private DateTime startDate;
	private Double calories;
	private Boolean live;

	public void setId(Long id) {
		this.id = id;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setBurgersBurned(Double burgersBurned) {
		this.burgersBurned = burgersBurned;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	public void setLive(Boolean live) {
		this.live = live;
	}

	public Long getId() {
		return id;
	}

	public Duration getDuration() {
		return duration;
	}

	public String getDistance() {
		return distance;
	}

	public Double getBurgersBurned() {
		return burgersBurned;
	}

	public Sport getSport() {
		return sport;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public Double getCalories() {
		return calories;
	}

	public Boolean getLive() {
		return live;
	}

}

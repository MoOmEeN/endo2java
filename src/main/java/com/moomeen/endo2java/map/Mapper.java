package com.moomeen.endo2java.map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;

import com.moomeen.endo2java.model.Workout;
import com.moomeen.endo2java.model.Sport;
import com.moomeen.endo2java.schema.EndoWorkout;

public class Mapper {

	private static final String DATE_FORMAT = "y-M-d H:m:s z";

	public static Workout toSimpleWorkout(EndoWorkout endoWorkout){
		Workout workout = new Workout();
		workout.setId(endoWorkout.id);
		if (endoWorkout.duration != null) {
			workout.setDuration(new Duration(endoWorkout.duration));
		}
		if (endoWorkout.distance != null){
			workout.setDistance(endoWorkout.distance.toString());
		}
		workout.setBurgersBurned(endoWorkout.burgersBurned);
		workout.setSport(Sport.fromNumber(endoWorkout.sport));
		if (endoWorkout.startTime != null) {
			workout.setStartDate(date(endoWorkout.startTime));
		}
		workout.setCalories(endoWorkout.calories);
		workout.setLive(endoWorkout.live);

		return workout;
	}

	private static DateTime date(String str){
		return DateTime.parse(str, DateTimeFormat.forPattern(DATE_FORMAT));
	}

}

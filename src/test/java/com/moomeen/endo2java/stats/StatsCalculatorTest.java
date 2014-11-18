package com.moomeen.endo2java.stats;

import static org.joda.time.DateTime.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.moomeen.endo2java.model.DetailedWorkout;
import com.moomeen.endo2java.model.Point;
import com.moomeen.endo2java.model.PointInstruction;
import com.moomeen.endo2java.model.stats.DistanceTime;

public class StatsCalculatorTest {

	@Test
	public void distanceBestTest_onePossibleBest(){
		double DISTANCE = 1;
		int TIME_IN_SECONDS = 60;

		// given
		Point START_POINT = getPoint(
				parse("2010-01-01T00:00:00"),
				0);
		Point FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS),
				DISTANCE);
		DetailedWorkout workout = getWorkout(START_POINT.getTime(),
				START_POINT,
				FIRST_KM);

		// when
		DistanceTime best = new StatsCalculator(workout).getDistanceBest(DISTANCE);

		// then
		assertEquals(DISTANCE, best.getDistance(), 0);
		assertEquals(TIME_IN_SECONDS * 1000, best.getTime().getMillis());
		assertEquals(2, best.getPoints().size());
		assertListContains(best.getPoints(), START_POINT, FIRST_KM);
	}

	@Test
	public void distanceBestTest_onePossibleBest_notExactPoint(){
		double DISTANCE_IN_KM = 1;
		int TIME_IN_SECONDS = 60;

		// given
		Point START_POINT = getPoint(
				parse("2010-01-01T00:00:00"),
				0);
		Point ALMOST_FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS - 1),
				DISTANCE_IN_KM - 0.01);
		Point MORE_THAN_FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS + 2),
				DISTANCE_IN_KM + 0.02);
		DetailedWorkout workout = getWorkout(START_POINT.getTime(),
				START_POINT,
				ALMOST_FIRST_KM,
				MORE_THAN_FIRST_KM);

		// when
		DistanceTime best = new StatsCalculator(workout).getDistanceBest(DISTANCE_IN_KM);

		// then
		assertEquals(DISTANCE_IN_KM, best.getDistance(), 0);
		assertEquals(TIME_IN_SECONDS * 1000, best.getTime().getMillis());
		assertEquals(3, best.getPoints().size());
		assertListContains(best.getPoints(), START_POINT, ALMOST_FIRST_KM, MORE_THAN_FIRST_KM);
	}

	@Test
	public void distanceBestTest_manyPossibleBests(){
		double DISTANCE_IN_KM = 1;
		int TIME_IN_SECONDS = 60;

		// given
		Point START_POINT = getPoint(
				parse("2010-01-01T00:00:00"),
				0);
		Point FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS + 1),
				DISTANCE_IN_KM);
		Point SECOND_KM = getPoint(
				FIRST_KM.getTime().plusSeconds(TIME_IN_SECONDS + 2),
				DISTANCE_IN_KM * 2);
		Point SECOND_AND_HALF_KM = getPoint(
				SECOND_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2),
				DISTANCE_IN_KM * 2.5);
		Point THIRD_KM = getPoint(
				SECOND_AND_HALF_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2),
				DISTANCE_IN_KM * 3);
		DetailedWorkout workout = getWorkout(START_POINT.getTime(),
				START_POINT,
				FIRST_KM,
				SECOND_KM,
				SECOND_AND_HALF_KM,
				THIRD_KM);

		// when
		DistanceTime best = new StatsCalculator(workout).getDistanceBest(DISTANCE_IN_KM);

		// then
		assertEquals(DISTANCE_IN_KM, best.getDistance(), 0);
		assertEquals(TIME_IN_SECONDS * 1000, best.getTime().getMillis());
		assertEquals(3, best.getPoints().size());
		assertListContains(best.getPoints(), SECOND_KM, SECOND_AND_HALF_KM, THIRD_KM);
	}

	@Test
	public void distanceBestTest_manyPossibleBests_notExactPoint(){
		double DISTANCE_IN_KM = 1;
		int TIME_IN_SECONDS = 60;

		// given
		Point START_POINT = getPoint(
				parse("2010-01-01T00:00:00"),
				0);
		Point FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS + 1),
				DISTANCE_IN_KM);
		Point SECOND_KM = getPoint(
				FIRST_KM.getTime().plusSeconds(TIME_IN_SECONDS + 2),
				DISTANCE_IN_KM * 2);
		Point SECOND_AND_HALF_KM = getPoint(
				SECOND_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2),
				DISTANCE_IN_KM * 2.5);
		Point ALMOST_THIRD_KM = getPoint(
				SECOND_AND_HALF_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2 - 1),
				DISTANCE_IN_KM * 3 - 0.01);
		Point MORE_THAN_THIRD_KM = getPoint(
				SECOND_AND_HALF_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2 + 2),
				DISTANCE_IN_KM * 3 + 0.02);
		DetailedWorkout workout = getWorkout(START_POINT.getTime(),
				START_POINT,
				FIRST_KM,
				SECOND_KM,
				SECOND_AND_HALF_KM,
				ALMOST_THIRD_KM,
				MORE_THAN_THIRD_KM);

		// when
		DistanceTime best = new StatsCalculator(workout).getDistanceBest(DISTANCE_IN_KM);

		// then
		assertEquals(DISTANCE_IN_KM, best.getDistance(), 0);
		assertEquals(TIME_IN_SECONDS * 1000, best.getTime().getMillis());
		assertEquals(4, best.getPoints().size());
		assertListContains(best.getPoints(), SECOND_KM, SECOND_AND_HALF_KM, ALMOST_THIRD_KM, MORE_THAN_THIRD_KM);
	}
	
	@Test
	public void distanceBestTest_manyPossibleBests_pauseInMeantime(){
		double DISTANCE_IN_KM = 1;
		int TIME_IN_SECONDS = 60;

		// given
		Point START_POINT = getPoint(
				parse("2010-01-01T00:00:00"),
				0);
		Point FIRST_KM = getPoint(
				START_POINT.getTime().plusSeconds(TIME_IN_SECONDS + 1),
				DISTANCE_IN_KM);
		Point SECOND_KM = getPoint(
				FIRST_KM.getTime().plusSeconds(TIME_IN_SECONDS + 2),
				DISTANCE_IN_KM * 2);
		Point SECOND_AND_HALF_KM_PAUSE = getPausePoint(
				SECOND_KM.getTime().plusSeconds(TIME_IN_SECONDS / 2),
				DISTANCE_IN_KM * 2.5);
		Point SECOND_AND_HALF_KM_RESUME = getResumePoint(
				SECOND_AND_HALF_KM_PAUSE.getTime().plusMinutes(30),
				DISTANCE_IN_KM * 2.5);
		Point THIRD_KM = getPoint(
				SECOND_AND_HALF_KM_RESUME.getTime().plusSeconds(TIME_IN_SECONDS / 2),
				DISTANCE_IN_KM * 3);
		DetailedWorkout workout = getWorkout(START_POINT.getTime(),
				START_POINT,
				FIRST_KM,
				SECOND_KM,
				SECOND_AND_HALF_KM_PAUSE,
				SECOND_AND_HALF_KM_RESUME,
				THIRD_KM);

		// when
		DistanceTime best = new StatsCalculator(workout).getDistanceBest(DISTANCE_IN_KM);

		// then
		assertEquals(DISTANCE_IN_KM, best.getDistance(), 0);
		assertEquals(TIME_IN_SECONDS * 1000, best.getTime().getMillis());
		assertEquals(4, best.getPoints().size());
		assertListContains(best.getPoints(), SECOND_KM, SECOND_AND_HALF_KM_PAUSE, SECOND_AND_HALF_KM_RESUME, THIRD_KM);
	}

	private void assertListContains(List<Point> points, Point... pointsToCheck){
		for (Point point : pointsToCheck) {
			assertListContains(points, point);
		}
	}

	private void assertListContains(List<Point> points, Point point){
		boolean found = false;
		for (Point p : points) {
			if (p.equals(point)){
				found = true;
			}
		}
		assertTrue(found);
	}

	private DetailedWorkout getWorkout(DateTime start, Point... points){
		DetailedWorkout workout = mock(DetailedWorkout.class);
		when(workout.getStartTime()).thenReturn(start);
		when(workout.getPoints()).thenReturn(Arrays.asList(points));
		return workout;
	}
	
	private Point getPoint(DateTime time, double distance){
		Point point = mock(Point.class);
		when(point.getTime()).thenReturn(time);
		when(point.getDistance()).thenReturn(distance);
		return point;
	}
	
	private Point getPausePoint(DateTime time, double distance){
		Point point = getPoint(time, distance);
		when(point.getInstruction()).thenReturn(PointInstruction.PAUSE);
		return point;
	}
	
	private Point getResumePoint(DateTime time, double distance){
		Point point = getPoint(time, distance);
		when(point.getInstruction()).thenReturn(PointInstruction.RESUME);
		return point;
	}
}


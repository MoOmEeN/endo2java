package com.moomeen.endo2java;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.error.LoginException;
import com.moomeen.endo2java.model.Workout;

public class EndomondoSessionTest {

	private static final String EMAIL = System.getenv("ENDOMONDO_EMAIL");
	private static final String PASSWORD = System.getenv("ENDOMONDO_PASSWORD");

	@Test
	public void loginTest() throws LoginException {
		// given
		EndomondoSession session = new EndomondoSession(EMAIL, PASSWORD);

		// when
		session.login();

		// then no exception
	}

	@Test(expected = LoginException.class)
	public void loginTest_wrongPassword() throws LoginException {
		// given
		String WRONG_PASSWORD = PASSWORD + "qwqew";
		EndomondoSession session = new EndomondoSession(EMAIL, WRONG_PASSWORD);

		// when
		session.login();

		// then exception
	}

	@Test
	public void getWorkoutsTest() throws InvocationException {
		int MAX_RESULTS = 1;
		// given
		EndomondoSession session = new EndomondoSession(EMAIL, PASSWORD);

		// when
		session.login();
		List<Workout> workouts = session.getWorkouts(MAX_RESULTS);

		// then
		assertEquals(MAX_RESULTS, workouts.size());
	}

	@Test(expected = IllegalStateException.class)
	public void getWorkoutsTest_notLoggedIn() throws InvocationException {
		// given
		EndomondoSession session = new EndomondoSession(EMAIL, PASSWORD);

		// when
		session.getWorkouts(1);

		// then exception
	}

	@Test
	public void getSingleWorkoutTest(){
		fail("TODO");
	}

	@Test
	public void getSingleWorkoutTest_notLoggedIn(){
		fail("TODO");
	}

	@Test
	public void getAccountInfoTest(){
		fail("TODO");
	}

	@Test
	public void getAccountInfoTest_notLoggedIn(){
		fail("TODO");
	}

}

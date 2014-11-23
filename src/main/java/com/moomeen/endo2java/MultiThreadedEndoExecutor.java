package com.moomeen.endo2java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.model.Workout;

public class MultiThreadedEndoExecutor {

	private final static Logger LOG = LoggerFactory.getLogger(MultiThreadedEndoExecutor.class);

	private static final int WORKOUTS_PER_THREAD = 10;

	private ExecutorService executorService = Executors.newFixedThreadPool(20);
	private EndomondoSession session;

	public MultiThreadedEndoExecutor(EndomondoSession session) {
		this.session = session;
	}

	public List<Workout> getAllWorkouts() throws InvocationException {
		List<Workout> simpleWorkouts = session.getWorkouts("simple", 999); // TODO
		List<Future<List<Workout>>> futures = new ArrayList<Future<List<Workout>>>();

		futures.add(submitTask(1));

		for (int i = 1; i < simpleWorkouts.size(); i = i + WORKOUTS_PER_THREAD){
			final DateTime before = simpleWorkouts.get(i - 1).getStartTime();
			futures.add(submitTask(WORKOUTS_PER_THREAD, before));
		}
		return extractData(futures);
	}

	private List<Workout> extractData(List<Future<List<Workout>>> futures) {
		List<Workout> ret = new ArrayList<Workout>();
		for (Future<List<Workout>> future : futures) {
			try {
				ret.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Couldn't retrieve workouts list from future object", e);
			}
		}
		return ret;
	}

	private Future<List<Workout>> submitTask(final int max){
		return executorService.submit(new Callable<List<Workout>>() {

			@Override
			public List<Workout> call() throws Exception {
				return session.getWorkouts(max);
			}
		});
	}

	private Future<List<Workout>> submitTask(final int max, final DateTime before){
		return executorService.submit(new Callable<List<Workout>>() {

			@Override
			public List<Workout> call() throws Exception {
				return session.getWorkouts(max, before);
			}
		});
	}


}

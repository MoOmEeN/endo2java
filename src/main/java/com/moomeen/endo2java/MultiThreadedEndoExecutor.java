package com.moomeen.endo2java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private ExecutorService executorService = Executors.newFixedThreadPool(20);
	private EndomondoSession session;

	public MultiThreadedEndoExecutor(EndomondoSession session) {
		this.session = session;
	}

	public List<Workout> getWorkouts(int workoutsPerThread) throws InvocationException {
		List<Workout> workoutsHeaders = getWorkoutsHeaders();
		Map<Future<List<Workout>>, Callable<List<Workout>>> tasks = new HashMap<Future<List<Workout>>, Callable<List<Workout>>>();

		TaskEntry firstTask = submitTask(1);
		tasks.put(firstTask.result, firstTask.task);

		for (int i = 1; i < workoutsHeaders.size(); i = i + workoutsPerThread){
			final DateTime before = workoutsHeaders.get(i - 1).getStartTime();
			TaskEntry task = submitTask(workoutsPerThread, before);
			tasks.put(task.result, task.task);
		}
		return extractResults(tasks);
	}

	private List<Workout> extractResults(Map<Future<List<Workout>>, Callable<List<Workout>>> tasks) {
		int maxRetries = 2;
		int sleepBetweenRetries = 100;
		
		List<Workout> ret = new ArrayList<Workout>();
		int retriesCount = 0;
		while (!tasks.isEmpty() && retriesCount <= maxRetries){
			boolean isFirstAttempt = retriesCount == 0;
			if (!isFirstAttempt){
				sleep(sleepBetweenRetries);
			}
			Map<Future<List<Workout>>, Callable<List<Workout>>> failedTasks = new HashMap<Future<List<Workout>>, Callable<List<Workout>>>();
			for (Future<List<Workout>> future : tasks.keySet()){
				try {
					ret.addAll(future.get());
				} catch (InterruptedException | ExecutionException e) {
					LOG.error("Couldn't retrieve workouts list from future object", e);
					Callable<List<Workout>> taskToRetry = tasks.get(future);
					failedTasks.put(submit(taskToRetry).result, taskToRetry);
				}
			}
			tasks = failedTasks;
			if (!isFirstAttempt){
				LOG.debug("Retry number {}, tasks left: {}", retriesCount, tasks.size());
			}
			retriesCount++;
		}
		
		return ret;
	}

	private void sleep(int sleepBetweenRetries) {
		try {
			Thread.sleep(sleepBetweenRetries);
		} catch (InterruptedException e) {
			LOG.debug("Couldn't put thread to sleep", e);
		}
	}
	
	private List<Workout> getWorkoutsHeaders() throws InvocationException{
		return session.getWorkouts("simple");
	}

	private TaskEntry submitTask(final int max){
		Callable<List<Workout>> task = new Callable<List<Workout>>() {

			@Override
			public List<Workout> call() throws Exception {
				return session.getWorkouts(max);
			}
		};
		
		return submit(task);
	}

	private TaskEntry submitTask(final int max, final DateTime before){
		Callable<List<Workout>> task = new Callable<List<Workout>>() {

			@Override
			public List<Workout> call() throws Exception {
				return session.getWorkouts(max, before);
			}
		};
		
		return submit(task);
	}

	private TaskEntry submit(Callable<List<Workout>> task) {
		Future<List<Workout>> result =  executorService.submit(task);
		return new TaskEntry(task, result);
	}
	
	private class TaskEntry {
		Future<List<Workout>> result;
		Callable<List<Workout>> task;
		
		public TaskEntry(Callable<List<Workout>> task, Future<List<Workout>> result) {
			this.result = result;
			this.task = task;
		}
	}


}

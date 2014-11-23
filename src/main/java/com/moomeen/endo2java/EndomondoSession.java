package com.moomeen.endo2java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.joda.time.DateTime;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.error.LoginException;
import com.moomeen.endo2java.model.AccountInfo;
import com.moomeen.endo2java.model.DetailedWorkout;
import com.moomeen.endo2java.model.Workout;
import com.moomeen.endo2java.schema.response.AccountInfoResponse;
import com.moomeen.endo2java.schema.response.WorkoutsResponse;

public class EndomondoSession {

	private static final String URL = "https://api.mobile.endomondo.com/mobile";

	private static final String AUTH_PATH = "auth";
	private static final String WORKOUTS_PATH = "api/workouts";
	private static final String SINGLE_WORKOUT_PATH = "api/workout/get";
	private static final String ACCOUNT_PATH = "api/profile/account/get";

	private static final String WORKOUTS_FIELDS = "simple,device,basic,lcp_count,polyline_encoded_small"; // Default fields used by Endomondo 10.1 Appapi/profile/account/get + polyline_encoded_small
	private static final String SINGLE_WORKOUT_FIELDS =
			"simple,device,basic,motivation,interval,hr_zones,weather,polyline_encoded_small,points,tagged_users,pictures,feed,lcp_count"; // Default fields used by Endomondo 10.1 App

	private String email;
	private String password;

	private String authToken;

	private Client client;
	private MultiThreadedEndoExecutor multiThreadedExecutor = new MultiThreadedEndoExecutor(this);

	public EndomondoSession(String email, String password) {
		init();
		this.email = email;
		this.password = password;
	}

	private void init(){
		if (client == null){
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.register(new JacksonJaxbJsonProvider());
			client = ClientBuilder.newClient(clientConfig);
		}
	}

	public void login() throws LoginException {
		WebTarget authTarget = target().path(AUTH_PATH)
				.queryParam("deviceId", UUID.randomUUID()) // TODO what to put here?
				.queryParam("country", "pl")
				.queryParam("action", "pair")
				.queryParam("email", email)
				.queryParam("password", password);

		try {
			String response = get(authTarget, String.class);
			checkLoginSuccess(response);
			Map<String, String> responseMap = parseLoginResponse(response);

			authToken = responseMap.get("authToken");
		} catch (InvocationException e) {
			throw new LoginException(e);
		}
	}

	private void checkLoginSuccess(String content) throws LoginException {
		if (!content.startsWith("OK")){
			throw new LoginException(content);
		}
	}

	private Map<String, String> parseLoginResponse(String content) {
		Map<String, String> ret = new HashMap<String, String>();
		String[] split = content.split("\n");
		for (int i = 1; i < split.length; i++){
			String[] row = split[i].split("=");
			ret.put(row[0], row[1]);
		}
		return ret;
	}

	public List<Workout> getWorkouts(int maxResults) throws InvocationException {
		checkLoggedIn();
		WebTarget workoutsTarget = target().path(WORKOUTS_PATH)
				.queryParam("authToken", authToken)
				.queryParam("fields", WORKOUTS_FIELDS)
				.queryParam("maxResults", maxResults);

		WorkoutsResponse workouts = get(workoutsTarget, WorkoutsResponse.class);

		return workouts.data;
	}

	public List<Workout> getWorkouts(String fields, int maxResults) throws InvocationException {
		checkLoggedIn();
		WebTarget workoutsTarget = target().path(WORKOUTS_PATH)
				.queryParam("authToken", authToken)
				.queryParam("fields", fields)
				.queryParam("maxResults", maxResults);

		WorkoutsResponse workouts = get(workoutsTarget, WorkoutsResponse.class);

		return workouts.data;
	}

	public List<Workout> getWorkouts(int maxResults, DateTime before) throws InvocationException {
		checkLoggedIn();
		WebTarget workoutsTarget = target().path(WORKOUTS_PATH)
				.queryParam("authToken", authToken)
				.queryParam("fields", WORKOUTS_FIELDS)
				.queryParam("maxResults", maxResults)
				.queryParam("before", before.toString("yyyy-MM-dd HH:mm:ss Z"));

		WorkoutsResponse workouts = get(workoutsTarget, WorkoutsResponse.class);

		return workouts.data;
	}

	public DetailedWorkout getWorkout(long workoutId) throws InvocationException {
		checkLoggedIn();
		WebTarget workoutsTarget = target().path(SINGLE_WORKOUT_PATH)
				.queryParam("authToken", authToken)
				.queryParam("workoutId", workoutId)
				.queryParam("fields", SINGLE_WORKOUT_FIELDS);

		DetailedWorkout workout = get(workoutsTarget, DetailedWorkout.class);

		return workout;
	}

	public AccountInfo getAccountInfo() throws InvocationException {
		checkLoggedIn();
		WebTarget workoutsTarget = target().path(ACCOUNT_PATH)
				.queryParam("authToken", authToken);

		AccountInfoResponse info = get(workoutsTarget, AccountInfoResponse.class);

		return info.data;
	}

	public List<Workout> getAllWorkouts()  throws InvocationException {
		checkLoggedIn();
		return multiThreadedExecutor.getAllWorkouts();
	}


	private void checkLoggedIn(){
		if (authToken == null){
			throw new IllegalStateException("login first!");
		}
	}

	private WebTarget target(){
		return client.target(URL);
	}

	private <T> T get(WebTarget target, Class<T> clazz) throws InvocationException{
//		target = target.queryParam("compression", "gzip");
		Invocation.Builder invocationBuilder = target.request();
//		invocationBuilder.header(HttpHeaders.ACCEPT_ENCODING, "gzip");
		try {
			Response r =  invocationBuilder.get();
			checkHttpStatus(r);
			return r.readEntity(clazz);
		} catch (ProcessingException e){
			throw new InvocationException(e);
		}
	}

	private void checkHttpStatus(Response r) throws InvocationException {
		if (r.getStatus() != Response.Status.OK.getStatusCode()){
			throw new InvocationException(r.getStatus());
		}
	}
}

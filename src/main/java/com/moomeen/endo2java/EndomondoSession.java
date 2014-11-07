package com.moomeen.endo2java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.error.LoginException;
import com.moomeen.endo2java.map.Mapper;
import com.moomeen.endo2java.model.Workout;
import com.moomeen.endo2java.schema.EndoWorkout;
import com.moomeen.endo2java.schema.response.WorkoutsResponse;

public class EndomondoSession {

	private static final String URL = "https://api.mobile.endomondo.com/mobile";

	private static final String AUTH_PATH = "auth";
	private static final String WORKOUTS_PATH = "api/workouts";

	private String email;
	private String password;

	private String authToken;

	private Client client;

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
		WebTarget target = client.target(URL);
		WebTarget authTarget = target.path(AUTH_PATH)
				.queryParam("deviceId", UUID.randomUUID()) // TODO what to put here?
				.queryParam("country", "pl")
				.queryParam("action", "pair")
				.queryParam("email", email)
				.queryParam("password", password);

		try {
			String response = get(authTarget, String.class);
			checkLoginSuccess(response);
			Map<String, String> responseMap = parse(response);

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

		private Map<String, String> parse(String content) {
			Map<String, String> ret = new HashMap<String, String>();
			String[] split = content.split("\n");
			for (int i = 1; i < split.length; i++){
				String[] row = split[i].split("=");
				ret.put(row[0], row[1]);
			}
			return ret;
		}

	public List<Workout> getWorkouts(int maxResults) throws InvocationException{
		checkLoggedIn();
		WebTarget target = client.target(URL);
		WebTarget workoutsTarget = target.path(WORKOUTS_PATH)
				.queryParam("authToken", authToken)
				.queryParam("fields", "simple")
				.queryParam("maxResults", maxResults);

		WorkoutsResponse workouts = get(workoutsTarget, WorkoutsResponse.class);

		List<Workout> ret = new ArrayList<Workout>();
		for (EndoWorkout endoWorkout : workouts.data){
			ret.add(Mapper.toSimpleWorkout(endoWorkout));
		}
		return ret;
	}

	private void checkLoggedIn(){
		if (authToken == null){
			throw new IllegalStateException("login first!");
		}
	}

	private <T> T get(WebTarget target, Class<T> clazz) throws InvocationException{
//		target = target.queryParam("compression", "gzip");
		Invocation.Builder invocationBuilder = target.request();
		invocationBuilder.header(HttpHeaders.ACCEPT_ENCODING, "identity");
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

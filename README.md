[![Build Status](https://travis-ci.org/MoOmEeN/endo2java.svg?branch=master)](https://travis-ci.org/MoOmEeN/endo2java)

Endo2Java
=========================

This is a Java implementation of unofficial mobile API of Endomondo.
Allows to retrieve workouts (with gps coordinates if available) and account information.

#### Login ####
```java
EndomondoSession session = new EndomondoSession(userName, password);
try {
	session.login();
} catch (LoginException e) {
	LOG.error("exception while trying to login user: {}", userName, e);
}
```

#### Retrieve data ####
```java
session.getAllWorkouts() // basic data, without gps information
session.getWorkout(id) // single workout, all available data (with gps)
session.getAccountInfo()
```

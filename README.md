[![Build Status](https://travis-ci.org/MoOmEeN/endo2java.svg?branch=master)](https://travis-ci.org/MoOmEeN/endo2java)

Endo2Java
=========================

This is a Java implementation of unofficial mobile API of Endomondo.

## Usage ##
```java
EndomondoSession session = new EndomondoSession(userName, password);
try {
	session.login();
} catch (LoginException e) {
	LOG.error("exception while trying to login user: {}", userName, e);
}
```


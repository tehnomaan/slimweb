# Slimweb
Slimweb is a lightweight Java web application framework, based on servlets.

Why yet another web framework, when we have popular frameworks already?
Because sometimes, You just don't want the complexity of those popular frameworks.
Instead, You need basic request handling and a HTML-s,
which would be simple to set up, simple to use, have minimal dependencies and have shallow learning curve.
For large and complex enterprise projects, Slimweb probably lacks features and flexibility.

In Slimweb, the web application consists of components. A component is a custom Java class, serving requests on a specific URL.
In fact, Slimweb's ControllerServlet routes every _https://<my.host>/controller/*_ request to appropriate component.
Http GET and DELETE requests have parameters in URL and Slimweb maps these parameters to component fields.
Http POST and PUT requests have parameters defined in request body with content type application/json and Slimweb also maps these to component fields.
The http response body always has content type application/json and whatever You return from method, is converted to JSon object by Slimweb.

## Basic Usage

The minimum web application consists of 2 Java classes:
* A component
* An application initializer

Provide a component class, which would handle GET requests at _https://<my.host>/controller/my-component_:

```java
package mypackage.components;
@Component
public class MyComponent {
	public String[] get() {
		return new String[] {"abc", "xyz"};
	}
}
```

And You must also provide application initializer, which implements **interface ApplicationInitializer** and has a name **SlimwebInitializer**.
Other class names are not recognized and Slimweb initialization would fail.

```java
public class SlimwebInitializer implements ApplicationInitializer {
	@Override
	public String[] getComponentPackages() {
		return new String[] {"mypackage.components"};//Slimweb will only scan components in this package and its subpackages
	}
}
```

Now, compile it and You are ready to go!

## Dependencies

Add Slimweb dependency into build.gradle:

```gradle
dependencies {
    implementation 'eu.miltema:slimweb:0.1.0'
}
```

Slimweb itself depends on couple of libraries, which are resolved by build system automatically.

## Session

To store a single piece of information in session, these patterns can be used in a component class:

```java
	public Integer get(HttpAccessor htAccessor) {
		Integer userId = (Integer) htAccessor.request.getSession().getAttribute("userId"); //fetch userId from session
		htAccessor.request.getSession().setAttribute("userId", userId); //save userId in session
	}
```

In a more complex application, multiple attributes have to be stored in session.
Then it sometimes makes sense to declare a dedicated session object and register its injector.
Injector makes it possible to have that session object as method parameter.

```java
public class MySession { // this class holds all the details to store in session
	public int userId;
}

public class SlimwebInitializer implements ApplicationInitializer {
	@Override
	public void registerInjectors(Map<Class<?>, ArgumentInjector> mapInjectors) {
		mapInjectors.put(MySession.class, HttpAccessor::getSessionObject); // register MySession injector
	}
}

public class MyComponent {
	public Integer get(HttpAccessor htAccessor, MySession session) {
		if (session == null)
			htAccessor.setSessionObject(session = new MySession());// put MySession details into session
		return session.userId;
	}
}
```

## Methods and Arguments in Component

In previous topic, session injector was defined.
In fact, it is possible to declare methods with any argument type as long as appropriate injector has been registered with ApplicationInitializer.
By default, Slimweb supports these method argument types: HttpSession, HttpServletRequest, HttpServletResponse, HttpAccessor

In component, methods have special naming convention. Below is a table with some url-to-method mapping examples (in class MyComponent):

| http   | url                            | Java method  |
|--------|--------------------------------|--------------|
| GET    | /controller/my-component/users | getUsers()   |
| GET    | /controller/my-component       | get()        |
| POST   | /controller/my-component/user  | postUser()   |
| PUT    | /controller/my-component       | put()        |
| DELETE | /controller/my-component/user  | deleteUser() |

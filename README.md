﻿# Slimweb
Slimweb is a lightweight servlet-based Java web application framework.

Why yet another web framework, when we have popular frameworks already?
Because sometimes, You just don't want the complexity of those popular frameworks.
Instead, You need basic request handling and HTML-s,
which would be simple to set up, simple to use, have minimal dependencies and have shallow learning curve.
For large and complex enterprise projects, Slimweb probably lacks features and flexibility.

A Slimweb application consists of 4 kinds of artifacts:
1. Application initializer
1. [Component service(s) - (optional)](#components)
1. [View template(s) - (optional)](#views)
1. [Label translation files - (optional)](#views)

The main concept is that You have one or several views aka HTML web page(s).
To provide data and to handle interaction (for example button clicks) with these pages, You provide components with service methods.
Slimweb handles HTML page data mapping and routing to/from components.

## Features

* HTML page data mapping and routing to/from components
* Locale-specific views: a single template for multiple languages
* Request logging
* CSRF attack detection
* Strongly typed session data management
* Server push via websocket
* Requires Java 11 or later

## Basic Usage

The minimum web application consists of a build script and 2 Java classes:
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

Provide an application initializer, which implements **interface ApplicationInitializer** and is named **SlimwebInitializer**.
A name other than SlimwebInitializer is not recognized by Slimweb and initialization would fail.

```java
package mypackage.components;

// For convenience, we are using ApplicationInitializerAdapter instead of ApplicationInitializer
public class SlimwebInitializer extends ApplicationInitializerAdapter {
	@Override
	public String[] getComponentPackages() {
		//Slimweb will only scan components in this Java package and its subpackages
		return new String[] {"com.mypackage.components"};
	}
}
```

## Dependencies

Add Slimweb dependency into build.gradle:

```gradle
apply plugin: 'war'
dependencies {
    implementation 'eu.miltema:slimweb:0.1.0'
}
```

These 3 files are all You need (SlimwebInitializer.java, MyComponent.java, build.gradle). Now, build the war and You are ready to go!

Slimweb itself depends on couple of libraries, which are resolved by build system automatically.

## Components

In "Basic Usage", session argument injector was introduced.
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

## Views

A view is an HTML web page. These can be defined in project's _src/main/webapp_ folder as usual. Web server will serve such pages itself and Slimweb is unaware of these.

However, sometimes You need locale-specific views (English, Spanish, German) and You want to avoid the translation hassle in front-end technologies like Angular, React or Vue.
Then You place HTML and JS templates into project's _src/main/resources/templates_ folder, to become accessible to Slimweb template engine. Valid extensions are .html, .htm and .js.
Translation files go into project's _src/main/resources/labels_ folder.
There is a separate label file for each language, for example _en.lbl_, _de.lbl_ and _es.lbl_ (notice the .lbl extension). 

Here is an example template file:

```html
<h1>{-frontpage.title-}</h1>
<p>{-frontpage.hellotext-}</p>
{-file:footer.html-}
```

Slimweb template engine will replace _{-frontpage.title-}_ and _{-frontpage.html-}_ placeholders with proper values from label file.
Placeholder _{-file:footer.html-}_ indicates, that it must be replaced with contents from file _footer.html_.

Here is an example label file:

```properties
frontpage.title=Slimweb Demo
frontpage.hellotext=Hello, world!
```

Usually, views share a common frame (perhaps with header, footer and other components).
The frame inclusion logic is declared in SlimwebInitializer and the frame itself in templates-folder:

```java
public class SlimwebInitializer extends ApplicationInitializerAdapter {
	@Override
	public String getFrameForTemplate(String templateFile, HttpAccessor htAccessor) {
		//NB! both file names without .html extension
		return htAccessor.getSessionObject() == null ? "loginframe" : "frame";
	}
}
```

```html
<html>
<body>
{-template:-}
</body>
<footer>
<a href="mailto:someone@example.com">Contact</a>
</footer>
</html>
```
In the above example, {-template:-} in frame file indicates the placeholder for page content

## Template File Rules

* Each template must be an .html, .htm or .js file in project's _src/main/resources/templates_ folder
* Each template file is mapped to a URL, for example file mytemplate.html is mapped to _http://myserver.com/view/mytemplate_
* Templates can be grouped into subfolders. However, each template name (without file extension) must be globally unique and subfolder names are excluded from URL mapping
* Template engine replaces each _{-xyz-}_ occurence in template with a label from labels file, where xyz is a key in labels file
* Labels files reside in project's _resources/labels_ folder and have a name en.lbl, de.lbl, es.lbl or other similar locale-specific name
* To copy contents of another template into current template, use _{-file:myfile.html-}_ syntax. If referring to files in other folders, don't use folder in file path. For example, ~~{-file:otherfolder/myfile.html-}~~ is invalid

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
public class MySession { // this class holds session data
	public int userId;
	public String userFullName;
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

By default, all components are expected to require session existence. If session does not exist, browser is redirected to login page (declared in SlimwebInitializer).
Some components (like the login page itself) do not require session existence. Then add __@SessionNotRequired__ to entire component or alternatively just to a single method or a couple of methods.

## Redirecting

Especially after PUT and POST, there is often need to redirect to another view. In a component, use Redirect exception to do that.
Slimweb sends an HTTP redirect (303) as a response. However, if client is accepting content-type application/json, Slimweb responds with HTTP 250.
This is because application/json request initiator is usually browser javascript and it is unable to catch 303 response.

```java
	public void post() {
		throw new Redirect("login.html");
	}
```

## Server Push

Any component can be made to support server push, when it implements __ServerPush__ interface.
For example, here is a component that pushes a message with 2sec delay to a client, which makes a websocket connection to ws://myhost.com/push/my-component:

```java
@Component
public class MyComponent implements ServerPush {
	@Override
	public void pushStarted(PushHandle pushHandle, Map<String, String> urlParameters) throws Exception {
		new Thread(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			pushHandle.pushObject(new int[] {3, 5});
		}).start();
	}

	@Override
	public void pushTerminated(PushHandle pushHandle) throws Exception {
	}
}
```

By default, a component requires session existence or it won't accept websocket connection. Annotation @SessionNotRequired can be used to suppress session equirement.
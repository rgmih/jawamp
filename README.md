jawamp
======

**jawamp** is Just Another JAva implementation of [WAMP](http://wamp.ws/ "WebSocket Application Messaging Protocol").
My goal is to make java WAMP support library as simple and compact as possible providing access to all levels of WAMP
processing as well. Sometimes you just have to dive deeper — handle connection termination or publish events on server.

Quick start
-----------

No public repo for jawamp available yet. To install jawamp in local repo pull jawamp from github and run maven:

```
mvn install
```

> You may also want to deploy jawamp on local server (take a look at [Artifactory](http://www.jfrog.com/home/v_artifactory_opensource_overview)).

Add jawamp dependency to your maven project:

```xml
<dependency>
    <groupId>com.github.rgmih</groupId>
    <artifactId>jawamp</artifactId>
    <version>0.1.0</version>
</dependency>
```

> jawamp depends on `slf4j-api` and `gson`. In tests jawamp uses [jetty](http://jetty.codehaus.org/jetty/) as transport.

Now you are ready to use jawamp as server or client or both. To get started take a look at this [short tutorial](https://github.com/rgmih/jawamp/wiki/Tutorial).

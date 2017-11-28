# Java Metrics Catcher (JMC)
Dropwizard created [metrics](http://metrics.dropwizard.io/3.2.3/), a very powerful java metrics library. 
The issue with it is that it's aim is to collect data from a single JVM, like a server.
But imagine a desktop application or any other application that does not run on one single JVM.
Metrics from all JVMs need to be collected and aggregated in one place and JMC does this task for you!

## Warning
This project is in an early stage, thus, code may not yet be reliable and the API subject to change.

## How it works
JMC comes in two components: The client, where a local [metrics registry](http://metrics.dropwizard.io/3.2.3/manual/core.html#man-core-registries) collects the data and the server, where all clients submit the data to and which aggregates the data.
The server then forwards the data to any [available metrics reporter](http://metrics.dropwizard.io/3.2.3/manual/third-party.html) that you wish to connect.

## Getting started
### Client configuration
1. Clone this repo and build it using `mvn install`.
2. Add the client module to your project as a maven dependency.
3. Create a `MetricsRegistry` as described [here](http://metrics.dropwizard.io/3.2.3/manual/core.html#man-core-registries).
4. Add a `ServerReporter` to your registry using 
```java
ServerReporter reporter = ServerReporter.forRegistry(registry, "192.168.123.123").build();
reporter.start(2, TimeUnit.MINUTES);
```

You may now add metrics to your registry as described in the [metrics core docs](http://metrics.dropwizard.io/3.2.3/manual/core.html). 
The `ServerReporter` will report them every two minutes to the specified server using UDP. 
Please just keep the following in mind:

*NOTE: Due to the way the metrics are transmitted over the internet, you cannot 
register and create metrics the way described in the docs! 
Please always use the following replacements:*
```java
IntegerGauge integerGauge = new IntegerGauge();
registry.register("gauge1", integerGauge);

LongGauge longGauge = new LongGauge();
registry.register("gauge2", longGauge);

CustomHistogram histogram = new CustomHistogram();
registry.register("histogram1", histogram);

CustomCounter customCounter = new CustomCounter();
registry.register("counter1", customCounter);

CustomTimer customTimer = new CustomTimer();
registry.register("timer1", customTimer);
```

*For the same reason, gauge values are not updated 
by overriding their `getValue()`-method (as suggested 
by the metrics docs)but rather by calling 
`integerGauge.setValue()` each time the value changes.*

### Server configuration
[WIP]

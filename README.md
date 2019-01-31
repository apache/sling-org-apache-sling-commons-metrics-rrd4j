[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-commons-metrics-rrd4j/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-metrics-rrd4j/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-metrics-rrd4j/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-metrics-rrd4j/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.metrics-rrd4j/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.metrics-rrd4j%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.metrics-rrd4j.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.metrics-rrd4j) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling RRD4J metrics reporter

This module is part of the [Apache Sling](https://sling.apache.org) project.

This is a bundle that stores metrics on the local filesystem using
[RRD4J](https://github.com/rrd4j/rrd4j).

Build this bundle with Maven:

    mvn clean install

The reporter will not store metrics by default. You need to configure it and
tell the reporter what metrics to store.

Go to the Apache Felix Web Console and configure 'Apache Sling Metrics reporter
writing to RRD4J'. The reporter will start storing metrics once data sources
have been added and the configuration is saved. Please note, the metrics file
is recreated/cleared whenever the configuration is changed.

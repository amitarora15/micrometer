#### Micrometer Application

Project developed in Spring Boot demonstrated various capabilities of using Micrometer for monitoring application. 
Application is using Promethues, JMX and Altas as monitoring system. 
1. Load Testing of application was performed using Load Runner to record various metrices (Micrometer-Load.jmx). 
1. Visualization of metrices was performed using Grafana on Prometheus server (grafana.json)
1. Promethues server configuration for this application also checked in (prometheus.yml).
#
Following capabilities of Micrometer are used
1. Counter
    1. Normal
    1. Functional Counter
1. Gauge
1. Timer (Historgram/SLA)
    1. Normal
    1. Functional Timer
1. Distribution Summary
1. Configuration
   1. Global tags using
        1. YML
        1. Java File
   1. Deny Metrices
   1. Rename Tag name
   1. Replace tag value
   1. Ignore tag value
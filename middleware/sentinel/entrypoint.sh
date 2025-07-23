#!/bin/bash 
java -Dserver.port=8888 -Dcsp.sentinel.dashboard.server=$dashboard.server -Dproject.name=sentinel-dashboard -jar /sentinel/sentinel-dashboard-1.8.8.jar

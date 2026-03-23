#!/bin/bash

./mvnw compile spotbugs:check -DskipTests "$@"
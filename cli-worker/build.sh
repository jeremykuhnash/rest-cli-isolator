#!/bin/bash
set -e

./mvnw install -DskipTests
docker build . -t jeremykuhnash/cli-worker:latest

ifeq ($(OS),Windows_NT)
  MVN ?= mvnw.cmd
else
  MVN ?= ./mvnw
endif

JAR := target/keeplybot.jar
.DEFAULT_GOAL := help

.PHONY: help build compile test verify clean run

help:
	$(info Available targets:)
	$(info - build:   Clean and build the executable JAR)
	$(info - compile: Compile the project)
	$(info - verify:  Run Maven verification)
	$(info - clean:   Clean Maven build artifacts)
	$(info - run:     Build and run the bot locally)
	@:

build:
	$(MVN) clean package

compile:
	$(MVN) compile

test:
	$(MVN) test

verify:
	$(MVN) verify

clean:
	$(MVN) clean

run: build
	java -jar $(JAR)

ifeq ($(OS),Windows_NT)
  MVN ?= mvnw.cmd
else
  MVN ?= ./mvnw
endif

JAR     := target/keeplybot.jar
COMPOSE := docker-compose.yaml

.DEFAULT_GOAL := help
.PHONY: help build compile test verify clean run dev dev-down dev-logs

help:
	$(info Available targets:)
	$(info - build:    Clean and build the executable JAR)
	$(info - compile:  Compile the project)
	$(info - verify:   Run Maven verification)
	$(info - clean:    Clean Maven build artifacts)
	$(info - run:      Build and run the bot locally)
	$(info - dev:      Build and run the bot with Docker)
	$(info - dev-down: Stop the Docker environment)
	$(info - dev-logs: Follow the bot logs)
	@:

build: ## Clean and build the executable JAR
	$(MVN) clean package

compile: ## Compile the project
	$(MVN) compile

verify: ## Run Maven verification
	$(MVN) verify

clean: ## Clean Maven build artifacts
	$(MVN) clean

run: build ## Build and run the bot locally
	java -jar $(JAR)

dev: ## Build and run the bot with Docker
	docker compose -f $(COMPOSE) up -d --build

dev-down: ## Stop the Docker environment
	docker compose -f $(COMPOSE) down

dev-logs: ## Follow the bot logs
	docker compose -f $(COMPOSE) logs -f

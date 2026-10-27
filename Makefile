.DEFAULT_GOAL := help

JAR     := target/keeplybot.jar
MVN ?= ./mvnw
COMPOSE := docker-compose.yaml

.PHONY: help env build compile test verify clean run dev down logs

help: ## Show available commands
	@awk 'BEGIN {FS = ":.*## "} /^[a-zA-Z_-]+:.*## / {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

env: ## Create .env from .env.example if missing
	@test -f .env || cp .env.example .env

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

dev: env ## Build and start containers in the background
	docker compose -f $(COMPOSE) up -d --build

down: ## Stop the Docker environment
	docker compose -f $(COMPOSE) down

logs: ## Follow the bot logs
	docker compose -f $(COMPOSE) logs -f

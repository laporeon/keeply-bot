<h1 align="center"> Keeply Bot

![java](https://img.shields.io/static/v1?label=java&message=21&labelColor=2d3748&color=grey&logo=openjdk&logoColor=white&style=flat)
![jda](https://img.shields.io/static/v1?label=jda&message=6.7.0&labelColor=2d3748&color=grey&logo=discord&logoColor=white&style=flat)
![maven](https://img.shields.io/static/v1?label=maven&message=3.9%2B&labelColor=2d3748&color=grey&logo=apachemaven&logoColor=white&style=flat)
![docker](https://img.shields.io/static/v1?label=docker&message=29.9.0&labelColor=2d3748&color=grey&logo=docker&logoColor=white&style=flat)
[![GPL-3.0 License](https://img.shields.io/badge/license-GPL--3.0-green?style=flat-square)](./LICENSE)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [1. Configure your environment](#1-configure-your-environment)
  - [2. Build and run](#2-build-and-run)
- [Usage](#usage)

## About

Keeply is a Discord bot that lets you bookmark messages. React to a message with ⭐ and Keeply sends a copy to your
direct messages, including the original message link and available image attachment. Saved messages can be opened in
their original context or deleted directly from the DM.

## Requirements

- Java 21+ (or Docker with Docker Compose)
- A Discord bot token
- Make _(optional)_

> [!NOTE]
> - The project ships with the Maven Wrapper, so a global Maven installation is not required.

## Getting Started

The project provides a `Makefile` with commands for building, running, and managing the application.
Run `make help` to list all available targets.

| Command | Description |
|---|---|
| `make help` | Show all available commands |
| `make env` | Create `.env` from `.env.example` if it does not exist |
| `make build` | Package the application |
| `make compile` | Compile the project |
| `make verify` | Run Maven verification |
| `make clean` | Clean Maven build artifacts |
| `make run` | Build and run the bot locally |
| `make dev` | Create `.env` if missing, then build and start the containers in the background |
| `make down` | Stop and remove the containers |
| `make logs` | Follow the container logs |

> [!NOTE]
> `make` is available out of the box on Linux and macOS. On Windows, use [WSL](https://learn.microsoft.com/windows/wsl/)
> or the Maven Wrapper commands shown in the sections below.

### 1. Configure your environment

Create a local `.env` file from the provided example (or copy `.env.example` to `.env` manually) and fill in the values:

```bash
make env
```

| Variable | Required | Description |
|----------|----------|-------------|
| `DISCORD_TOKEN` | Yes | Token used to authenticate the Discord bot. |
| `ACTIVE_PROFILE` | Yes | When set to `dev`, commands are registered in one guild; any other value registers them globally. |
| `DISCORD_GUILD_ID` | Only when `ACTIVE_PROFILE=dev` | ID of the guild where development slash commands are registered. |

The application checks the system environment first and uses `.env` as a fallback.

### 2. Build and run

**Docker**

```bash
make dev # Build and start the containers.
```

**Local Development**

```bash
# Using Make (Linux / macOS only)
make run

# Direct JAR execution

# Linux / macOS
./mvnw clean package
java -jar target/keeplybot.jar

# Windows
mvnw.cmd clean package
java -jar target\keeplybot.jar
```

## Usage

| Command | Context | Description |
|---------|---------|-------------|
| `/help` | Guild or DM | Shows how to use Keeply. |
| `/ping` | Guild or DM | Replies with `PONG!`. |
| `/clear [amount]` | Bot DM | Deletes up to 100 recent messages sent by Keeply. `amount` defaults to `100`. |

React to any message with ⭐ to receive it in your DMs. Use the `Open` button to jump to the original message or the
`Delete` button to remove the saved message from the DM.

[⬆ Back to the top](#-keeply-bot)

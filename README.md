<h1 align="center">Keeply Bot

![java](https://img.shields.io/static/v1?label=java&message=21&color=2d3748&logo=openjdk&style=flat-square)
![jda](https://img.shields.io/static/v1?label=jda&message=6.7.0&color=2d3748&logo=discord&style=flat-square)
![maven](https://img.shields.io/static/v1?label=maven&message=3.9%2B&color=2d3748&logo=apachemaven&style=flat-square)
![docker](https://img.shields.io/static/v1?label=docker&message=29.9.0&color=2d3748&logo=docker&style=flat-square)
![license](https://img.shields.io/badge/license-GPL--3.0-2d3748?style=flat-square)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
    - [Quick Start](#quick-start)
    - [Environment Variables Reference](#environment-variables-reference)
- [Usage](#usage)
    - [Commands](#commands)
    - [Message Actions](#message-actions)
- [License](#license)

## About

Keeply is a Discord bot that lets you bookmark messages. React to a message with ⭐ and
Keeply sends a copy to your direct messages, including the original message link and available image attachment. Saved
messages can be opened in their original context or deleted directly from the DM.

**Key features:**

- Open the original message with a direct link.
- Delete saved messages from your DM with a button.
- Clear up to 100 recent messages sent by the bot with `/clear`.
- Support for English and Brazilian Portuguese localization. 
- Custom branding: icon and banner available in [`src/main/resources/assets/`](./src/main/resources/assets/).

## Requirements

**For Docker (Recommended):**

- Docker
- Docker Compose 
- Make _(optional)_

**For Local Development:**

- Java 21+
- Make _(optional)_

> [!NOTE]
> - The project ships with Maven Wrapper (`./mvnw` and `mvnw.cmd`), so a global Maven installation is not required.

## Getting Started

The development workflow is powered by Makefile. Run `make help` to list all available commands.

> [!NOTE]
> `make` is available out of the box on Linux and macOS. On Windows, use [WSL](https://learn.microsoft.com/windows/wsl/),
> or run the equivalent commands from the table below (use `mvnw.cmd` instead of `./mvnw`).

| Command | Description | Equivalent without Make |
|---|---|---|
| `make env` | Create `.env` from `.env.example` if it does not exist | `cp .env.example .env` |
| `make build` | Package the application  | `./mvnw clean package` |
| `make compile` | Compile the project | `./mvnw compile` |
| `make verify` | Run Maven verification | `./mvnw verify` |
| `make clean` | Clean Maven build artifacts | `./mvnw clean` |
| `make run` | Build and run the bot locally | `./mvnw clean package && java -jar target/keeplybot.jar` |
| `make dev` | Create `.env` if missing, then build and start the containers in the background | `docker compose up --build -d` |
| `make down` | Stop and remove the containers | `docker compose down` |
| `make logs` | Follow the container logs | `docker compose logs -f` |

### Quick Start

**Docker**

```bash
make env # Create the local environment file if it does not exist.
make dev # Build and start the application and MongoDB containers.
```

**Local Development**

```bash
make env # Create the local environment file if it does not exist.
make run # Build and start the application.
```

### Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DISCORD_TOKEN` | Yes | — | Token used to authenticate the Discord bot. |
| `ACTIVE_PROFILE` | Yes | — | Controls slash command registration. When set to `dev`, commands are registered in one guild; any other value registers them globally. |
| `DISCORD_GUILD_ID` | Only when `ACTIVE_PROFILE=dev` | — | ID of the guild where development slash commands are registered. |

The application reads variables from the process environment first and then from `.env`.

When `ACTIVE_PROFILE=dev`, `DISCORD_GUILD_ID` must identify a guild that the bot can access.

The bot must also be invited to Discord with the permissions and intents required to read message reactions,
send direct messages, and manage its own messages.

## Usage

### Commands

| Command | Context | Description |
|---------|---------|-------------|
| `/help` | Guild or DM | Shows how to use Keeply. |
| `/ping` | Guild or DM | Replies with `PONG!`. |
| `/clear [amount]` | Bot DM | Deletes up to 100 recent messages sent by Keeply. `amount` defaults to `100`. |

Commands are registered in the guild configured by `DISCORD_GUILD_ID` when `ACTIVE_PROFILE=dev`. In other profiles,
they are registered globally and may take time to become available across Discord.

### Message Actions

| Action | Description |
|--------|-------------|
| ⭐ reaction | Sends the reacted message to the user's DMs. |
| `Open` button | Opens the original message in Discord. |
| `Delete` button | Deletes the saved message from the DM. |

The bot ignores reactions added by other bots.

If direct messages are disabled or unavailable, the bot reports the failure in the original channel
and removes that notification after a short timeout.

## License

This project is licensed under the GPL-3.0 License. See the [LICENSE](LICENSE) file for details.

[⬆ Back to the top](#keeply-bot)

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
    - [Running with Docker (Recommended)](#running-with-docker-recommended)
    - [Running Locally](#running-locally)
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
- Development mode with instant guild command registration.
- Production mode with global command registration.

## Requirements

**For Docker (Recommended):**

- Docker & Docker Compose 
- Make

**For Local Development:**

- Java 21+
- Make
- Maven 3.9+ (optional; the Maven Wrapper is included)

## Getting Started

### Running with Docker (Recommended)

1. Copy the example environment file and fill in your Discord credentials:

```bash
cp .env.example .env
# Edit .env with your local values
```

2. Build and start the bot:

```bash
make dev
```

3. Follow the logs when needed:

```bash
make dev-logs
```

To stop the bot:

```bash
make dev-down
```

### Running Locally

1. Set the required environment variables using either method below:

    - **Option 1: Using a `.env` file**

      ```bash
      cp .env.example .env
      # Edit .env with your local values
      ```

    - **Option 2: Exporting variables through the shell**

      ```bash
      export DISCORD_TOKEN=<your-bot-token>
      export DISCORD_GUILD_ID=<your-development-guild-id>
      export ACTIVE_PROFILE=dev
      ```

2. Build and run the bot:

```bash
make run
```

### Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DISCORD_TOKEN` | Yes | — | Token used to authenticate the Discord bot. |
| `ACTIVE_PROFILE` | Yes | — | Registration profile: `dev` registers commands in one guild; any other value registers them globally. |
| `DISCORD_GUILD_ID` | Only in `dev` | — | ID of the guild where development slash commands are registered. |

The application reads variables from the process environment first and then from `.env`. In development mode,
`DISCORD_GUILD_ID` must identify a guild that the bot can access.

The bot must also be invited to Discord with the permissions and intents required to read message reactions, send direct
messages, and manage its own messages.

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

The bot ignores reactions added by other bots. If direct messages are disabled or unavailable, the bot reports the
failure in the original channel and removes that notification after a short timeout.

## License

This project is licensed under the GPL-3.0 License. See the [LICENSE](LICENSE) file for details.

[⬆ Back to the top](#keeply-bot)

# CraftWork
> Turn minecraft into your virtual office with Discord

CraftWork is a bukkit plugin that lets you designate areas of your server to
discord rooms.

Players on your server can link their discord account to their minecraft
account, and then join voice chat. If they enter one of the areas you've
designated, then CraftWork discord bot will move them to the corresponding
discord voice channel.

This lets you build a virtual office in minecraft, with meeting rooms that
correspond to actual discord rooms. Combine with a plugin like WorldGuard
to protect your 'main office' and give everyone their own personal area
to customize.

This is just a side project and it's a little janky. Anyone is welcome to steal
this idea.

# Installation Instructions

## Install CraftBukkit

You'll need a CraftBukkit server to run this plugin. [You can get CraftBukkit
by following spigot's instructions.](https://www.spigotmc.org/wiki/buildtools/)

Make sure you add the `--compile craftbukkit` flag when you run buildtools, and
make sure you're building for version 1.15.2 (`--rev 1.15.2`).

## Download the CraftWork plugin jar

The `.jar` file for CraftWork can be found in the root of this repository, as
`CraftWork-0.0.1-SNAPSHOT.jar`.

Download this jar file and put it in the `plugins` folder of your CraftBukkit
server. If the server is running, then use `/reload` to pick up the new plugin.

## Add the CraftWork discord bot to your server

You must be a discord admin for this step.

[Use this link to authorize the CraftWork discord bot on your
server.](https://discordapp.com/api/oauth2/authorize?client_id=693580122795540482&permissions=29427712&scope=bot)

On the menu that it presents you with, select the discord server that you want
to connect your minecraft server to.

## Link the discord server to your mincraft server

You must be a discord admin and a minecraft op for this step.

In a public channel on your discord server, type `craftwork server-link`. The
CraftWork bot will send you a private message with a server link code.

Enter the server code on your minecraft server with `/craftwork server-link
XXXXXXXX` where `XXXXXXXX` is the code that was sent to you by the CraftWork
discord bot.

## Link players between minecraft and discord

Each player that wants to use craftwork must link their user on your Discord server to
their user on your minecraft server.

Each player should type `/craftwork` on the minecraft server, and will get in response a
six digit code, e.g. `123456`.

Then each player must send that code to a public text channel on your server by
saying `craftwork link 123456` where `123456` is the code they received on the
minecraft server.

To use the plugin, each player must also join voice chat on your discord. The default
channel when not in a CraftWork room is #General.

## Create a room

You must be a minecraft op for this step. [Click here to see a video
demonstration of this.](https://streamable.com/fat27)

Point at one corner of the 3-dimensional area you want to designate and type
`/craftwork mkroom`.

Point at the other corner of the 3-dimensional area you want to designate and
type `/craftwork mkroom`.

Now point at any block **inside** the area and type `/craftwork setroom
name-of-discord-voice-channel`. The discord voice channel name is case
insensitive but should not contain spaces.

Now whenever you connected to Discord voice chat and walk into the designated
area, the discord bot will move you to the designated voice channel!

If you need to clean up your room you can type `/craftwork rmroom` to delete
it.

# Dev TODOs

- [ ] minecraft permissions
- [ ] ability to point at custom craftwork server
- [ ] player unlink from discord
- [ ] player unlink from minecraft
- [ ] server rotate key
- [ ] player link via private channel
- [ ] better error messages

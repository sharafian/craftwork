import {
  Client as DiscordClient,
  Message,
  Permissions
} from 'discord.js'

import { Injector } from 'reduct'
import { Config } from './Config'
import { PlayerKeys } from './PlayerKeys'
import { DiscordServerKeys } from './DiscordServerKeys'
import {
  DiscordServer,
  DiscordMember,
  PlayerKey
} from '../lib'

interface CommandMap {
  [name: string]: BotCommand
}

interface BotCommand {
  run: (msg: Message, tokens: string[]) => Promise<void>
  usage: string
  help: string
}

export class Bot {
  private config: Config
  private playerKeys: PlayerKeys
  private serverKeys: DiscordServerKeys
  private client = new DiscordClient()
  private static prefix = 'craftwork'

  private commands: CommandMap = {
    'server-link': {
      run: this.serverLink.bind(this),
      usage: 'craftwork server-link',
      help: 'Admin only. Requests key to link this Discord server to a Minecraft server.'
    },
    'link': {
      run: this.playerLink.bind(this),
      usage: 'craftwork link <code>',
      help: 'Link your Discord user on this server to your Minecraft account. Use `/craftwork` on the Minecraft server to get your link code.'
    },
    'help': {
      run: this.help.bind(this),
      usage: 'craftwork help [command]',
      help: 'Get information on a craftwork command. If you\'re an admin setting up your server, you should run `craftwork server-link`'
    }
  }

  constructor (deps: Injector) {
    this.config = deps(Config)
    this.playerKeys = deps(PlayerKeys)
    this.serverKeys = deps(DiscordServerKeys)
  }

  async help (msg: Message, tokens: string[]) {
    if (tokens[0]) {
      const command = this.commands[tokens[0]]
      if (!command) {
        await msg.reply('Can\'t help with unknown commands. Commands are: ' +
          Object.keys(this.commands).join(', '))
      } else {
        await msg.reply(`\n\nUsage: ${command.usage}\n\n${command.help}`)
      }
    } else {
      await msg.reply('Specify a command to get help on it. Commands are: ' +
        Object.keys(this.commands).join(', '))
    }
  }

  async playerLink (msg: Message, tokens: string[]) {
    if (!msg.guild || !msg.member) {
      await msg.reply('You need to run this on a Discord server, not privately.')
      return
    }

    const key = tokens[0]
    if (!key) {
      await msg.reply('You need to provide your link code to link your Minecraft account. Get your link code by running `/craftwork` on the Minecraft server.')
      return
    }

    const player = await this.playerKeys.setDiscordMember(
      new DiscordServer(msg.guild.id),
      new PlayerKey(key),
      new DiscordMember(msg.member.id)
    )

    await msg.reply(`Successfully linked your Discord user to "${player}"`)
  }

  async serverLink (msg: Message, _: string[]) {
    if (!msg.guild || !msg.member) {
      await msg.reply('You need to run this on a Discord server, not privately. The key will be sent privately.')
      return
    }

    if ((msg.member.permissions.bitfield & Permissions.FLAGS.ADMINISTRATOR) === 0) {
      await msg.reply('You need to be an administrator on the Discord server to run this command.')
      return
    }

    const key = await this.serverKeys.getKey(new DiscordServer(msg.guild.id))
    const privateChannel = await msg.author.createDM()

    await privateChannel.send(`Your key is "${key}". Enter it into the server with \`/craftwork server-link ${key}\``)
    msg.reply('Your server key has been sent privately. Check your direct messages for instructions.')
  }

  async start () {
    this.client.on('ready', () => {
      console.log(`Logged in as ${this.client?.user?.tag}`)
    })

    this.client.on('message', async (msg: Message) => {
      const tokens = msg.content.split(' ')

      if (tokens[0] === Bot.prefix) {
        const command = this.commands[tokens[1]]

        if (!command) {
          await msg.reply('Command not recognized. Available commands are: ' +
            Object.keys(this.commands).join(', '))
        } else {
          await command.run(msg, tokens.slice(2))
        }
      }
    })

    await this.client.login(this.config.discordToken)
  }
}

import { Injector } from 'reduct'
import { Save } from './Save'
import {
  DiscordServer,
  MinecraftPlayer,
  PlayerKey,
  DiscordUser,
  sixDigitCode
} from './lib'

function playerKeyKey (server: DiscordServer, player: MinecraftPlayer) {
  return `playerkey:${server}:${player}`
}

function playerNameKey (server: DiscordServer, key: PlayerKey) {
  return `playername:${server}:${key}`
}

function playerUserKey (server: DiscordServer, player: MinecraftPlayer) {
  return `playeruser:${server}:${player}`
}

export class PlayerKeys {
  private db: Save

  constructor (deps: Injector) {
    this.db = deps(Save)
  }

  async setDiscordUser (
    server: DiscordServer,
    key: PlayerKey,
    user: DiscordUser
  ): Promise<void> {
    const name = await this.db.get(playerNameKey(server, key))

    if (!name) {
      throw new Error('Player key is not recognized on this server')  
    } else {
      await this.db.set(
        playerUserKey(server, new MinecraftPlayer(name)),
        user.toString()
      )
    }
  }

  async getDiscordUser (
    server: DiscordServer,
    player: MinecraftPlayer
  ): Promise<DiscordUser | void> {
    const user = await this.db.get(playerUserKey(server, player))
    return user
      ? new DiscordUser(user)
      : undefined
  }

  async getKey (
    server: DiscordServer,
    player: MinecraftPlayer
  ): Promise<PlayerKey> {
    const key = await this.db.get(playerKeyKey(server, player))

    if (!key) {
      // TODO: too easy to get collisions?
      const newKey = new PlayerKey(sixDigitCode())
      await this.db.set(playerKeyKey(server, player), newKey.toString())
      await this.db.set(playerNameKey(server, newKey), player.toString())
      return newKey
    } else {
      return new PlayerKey(key)
    }
  }
}

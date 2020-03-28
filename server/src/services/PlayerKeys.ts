import { Injector } from 'reduct'
import { Save } from './Save'
import {
  DiscordServer,
  MinecraftPlayer,
  PlayerKey,
  DiscordMember,
  sixDigitCode
} from '../lib'

function playerKeyKey (server: DiscordServer, player: MinecraftPlayer) {
  return `playerkey:${server}:${player}`
}

function playerNameKey (server: DiscordServer, key: PlayerKey) {
  return `playername:${server}:${key}`
}

function playerUserKey (server: DiscordServer, player: MinecraftPlayer) {
  return `playermember:${server}:${player}`
}

export class PlayerKeys {
  private db: Save

  constructor (deps: Injector) {
    this.db = deps(Save)
  }

  async setDiscordMember (
    server: DiscordServer,
    key: PlayerKey,
    member: DiscordMember
  ): Promise<MinecraftPlayer> {
    const name = await this.db.get(playerNameKey(server, key))

    if (!name) {
      throw new Error('Player key is not recognized on this server')
    } else {
      const player = new MinecraftPlayer(name)
      const existingUser = await this.db.get(playerUserKey(server, player)) 

      if (existingUser) {
        throw new Error('Key has already been registered to a member')
      }

      await this.db.set(playerUserKey(server, player), member.toString())
      return player
    }
  }

  async getDiscordMember (
    server: DiscordServer,
    player: MinecraftPlayer
  ): Promise<DiscordMember | void> {
    const member = await this.db.get(playerUserKey(server, player))
    return member
      ? new DiscordMember(member)
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

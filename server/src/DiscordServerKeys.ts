import { Injector } from 'reduct'
import { Save } from './Save'
import { randomBytes } from 'crypto'
import { Context, Next } from 'koa'
import {
  DiscordServer,
  ServerKey,
  base64url
} from './lib'

export class DiscordServerKeys {
  private db: Save

  constructor (deps: Injector) {
    this.db = deps(Save)
  }

  private generateKey (): ServerKey {
    return new ServerKey(base64url(randomBytes(32)))
  }

  async makeKey (server: DiscordServer): Promise<void> {
    const key = this.generateKey()

    await this.db.set(`serverkey:${key}`, server.toString())
    await this.db.set(`server:${server}`, key.toString())
  }

  async getServer (key: ServerKey): Promise<DiscordServer | void> {
    const server = await this.db.get(`serverkey:${key}`)
    return server
      ? new DiscordServer(server)
      : undefined
  }

  async getKey (server: DiscordServer): Promise<ServerKey | void> {
    const key = await this.db.get(`server:${server}`)
    return key
      ? new ServerKey(key)
      : undefined
  }

  validateKey () {
    return async (ctx: Context, next: Next) => {
      const key = ctx.get('authorization').substring('Bearer '.length)
      const server = await this.getServer(new ServerKey(key))

      if (!server) {
        return ctx.throw(404, 'No server associated with key')
      } else {
        ctx.server = server
        return next()
      }
    }
  }
}

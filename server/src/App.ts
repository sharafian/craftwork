import { Injector } from 'reduct'
import Koa, { Context } from 'koa'
import Router from 'koa-router'
import Parser from 'koa-bodyparser'

import { Config } from './Config'
import { DiscordServerKeys } from './DiscordServerKeys'
import { PlayerKeys } from './PlayerKeys'
import { MinecraftPlayer } from './lib'

export class App {
  private app = new Koa()
  private router = new Router()
  private parser = Parser()

  private config: Config
  private discordKeys: DiscordServerKeys
  private playerKeys: PlayerKeys

  constructor (deps: Injector) {
    this.config = deps(Config)
    this.discordKeys = deps(DiscordServerKeys)
    this.playerKeys = deps(PlayerKeys)
  }

  async start () {
    this.router.get('/server/id',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        ctx.body = {
          server: ctx.server.toString()
        }
      })

    this.router.get('/players/:name/key',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        ctx.body = {
          key: await this.playerKeys.getKey(
            ctx.server,
            new MinecraftPlayer(ctx.params.name)
          )
        }
      })

    this.router.put('/players/:name/room',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        const user = await this.playerKeys.getDiscordUser(
          ctx.server,
          new MinecraftPlayer(ctx.params.name)
        )

        if (!user) {
          return ctx.throw(404, 'No discord user associated to this player')
        }

        // TODO: dispatch to Bot with server and member
        ctx.status = 204
      })
  }
}

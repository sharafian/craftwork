import { Injector } from 'reduct'
import Koa, { Context } from 'koa'
import Router from 'koa-router'
import Parser from 'koa-bodyparser'

import { Config } from './Config'
import { DiscordServerKeys } from './DiscordServerKeys'
import { PlayerKeys } from './PlayerKeys'
import { Bot } from './Bot'
import { MinecraftPlayer, DiscordVoiceChannel } from '../lib'

export class App {
  private app = new Koa()
  private router = new Router()
  private parser = Parser()

  private config: Config
  private discordKeys: DiscordServerKeys
  private playerKeys: PlayerKeys
  private bot: Bot

  constructor (deps: Injector) {
    this.config = deps(Config)
    this.discordKeys = deps(DiscordServerKeys)
    this.playerKeys = deps(PlayerKeys)
    this.bot = deps(Bot)
  }

  async start () {
    this.router.get('/', async (ctx: Context) => {
      ctx.body = 'OK'
    })

    this.router.get('/server/id',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        ctx.body = {
          server: ctx.server.toString()
        }
      })

    this.router.get('/server/rooms',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        if (!ctx.query.name) {
          return ctx.throw(400, 'Must provide a voice channel name')
        }

        const room = await this.bot.findVoiceChannelByName(
          ctx.server,
          ctx.query.name
        )

        if (!room) {
          return ctx.throw(404, 'No voice channel by that name')
        }

        ctx.body = {
          room: room.toString()
        }
      })

    this.router.get('/player/:name/key',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        const key = await this.playerKeys.getKey(
          ctx.server,
          new MinecraftPlayer(ctx.params.name)
        )

        ctx.body = {
          key: key.toString()
        }
      })

    this.router.put('/player/:name/room/name',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        const { name } = ctx.request.body
        if (!name) {
          return ctx.throw(400, 'Must specify name in request body')
        }

        const member = await this.playerKeys.getDiscordMember(
          ctx.server,
          new MinecraftPlayer(ctx.params.name)
        )

        if (!member) {
          return ctx.throw(404, 'No discord member associated to this player')
        }

        const room = await this.bot.findVoiceChannelByName(ctx.server, name)

        if (!room) {
          return ctx.throw(404, 'No voice channel by that name')
        }

        try {
          await this.bot.moveToVoiceChannel(
            ctx.server,
            member,
            room
          )

          ctx.status = 204
        } catch (e) {
          return ctx.throw(403, e.message)
        }
      })

    this.router.put('/player/:name/room',
      this.discordKeys.validateKey(),
      async (ctx: Context) => {
        const { room } = ctx.request.body
        if (!room) {
          return ctx.throw(400, 'Must specify room in request body')
        }

        const member = await this.playerKeys.getDiscordMember(
          ctx.server,
          new MinecraftPlayer(ctx.params.name)
        )

        if (!member) {
          return ctx.throw(404, 'No discord member associated to this player')
        }

        try {
          await this.bot.moveToVoiceChannel(
            ctx.server,
            member,
            new DiscordVoiceChannel(room)
          )

          ctx.status = 204
        } catch (e) {
          return ctx.throw(403, e.message)
        }
      })

    this.app
      .use(this.parser)
      .use(this.router.allowedMethods())
      .use(this.router.routes())
      .listen(this.config.port)
  }
}

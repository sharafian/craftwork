export class DiscordServer {
  constructor (public serverId: string) {}

  toString (): string {
    return this.serverId
  }
}

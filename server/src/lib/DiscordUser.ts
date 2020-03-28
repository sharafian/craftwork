export class DiscordUser {
  constructor (public discordUserId: string) {}

  toString (): string {
    return this.discordUserId
  }
}

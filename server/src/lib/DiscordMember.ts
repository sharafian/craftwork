export class DiscordMember {
  constructor (public discordUserId: string) {}

  toString (): string {
    return this.discordUserId
  }
}

export class DiscordVoiceChannel {
  constructor (public voiceChannelId: string) {}

  toString (): string {
    return this.voiceChannelId
  }
}

export class MinecraftPlayer {
  constructor (public playerName: string) {}

  toString (): string {
    return this.playerName
  }
}

export class Config {
  public port = process.env.PORT || 8080
  public dbPath = process.env.DB_PATH || './data'
  public discordToken = process.env.DISCORD_TOKEN || 'test'
}

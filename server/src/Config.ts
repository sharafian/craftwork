export class Config {
  public port = process.env.PORT || 8080
  public dbPath = process.env.DB_PATH || './data'
}

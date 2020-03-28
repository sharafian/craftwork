import * as fs from 'fs-extra'
import { resolve } from 'path'
import { Injector } from 'reduct'
import { Config } from './Config'
import { base64url } from './lib'

export class Save {
  private config: Config

  constructor (deps: Injector) {
    this.config = deps(Config)
  }

  public async init () {
    await fs.mkdirp(this.config.dbPath)
  }

  private keyPath (key: string): string {
    if (!key.length) throw new Error('cannot save to empty key')
    const filename = base64url(Buffer.from(key, 'utf8')) + '.txt'
    return resolve(this.config.dbPath, filename)
  }

  public async get (key: string): Promise<string | void> {
    await this.init()

    try {
      return (await fs.readFile(this.keyPath(key), 'utf8'))
    } catch (e) {
      if (e.code !== 'ENOENT') {
        throw e
      }
    }
  }

  public async set (key: string, value: string): Promise<void> {
    await this.init()

    return fs.writeFile(this.keyPath(key), value, {
      encoding: 'utf8'
    })
  }

  public async append (key: string, value: string): Promise<void> {
    if (key.length === 0) return
    await this.init()

    return fs.appendFile(this.keyPath(key), value, {
      encoding: 'utf8'
    })
  }

  public async del (key: string): Promise<void> {
    await this.init()

    try {
      await fs.remove(this.keyPath(key))
    } catch (e) {
      if (e.code !== 'ENOENT') {
        throw e
      }
    }
  }
}

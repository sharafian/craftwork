import { App } from './services/App'
import { Bot } from './services/Bot'
import reduct from 'reduct'

const injector = reduct()

const app = injector(App)
app
  .start()
  .catch((e: Error) => {
    console.error(e)
    process.exit(1)
  })

const bot = injector(Bot)
bot
  .start()
  .catch((e: Error) => {
    console.error(e)
    process.exit(1)
  })

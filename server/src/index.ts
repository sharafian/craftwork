import { App } from './services/App'
import reduct from 'reduct'

const app = reduct()(App)
app
  .start()
  .catch((e: Error) => {
    console.error(e)
    process.exit(1)
  })

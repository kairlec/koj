import { createApp } from 'vue'
import router from './router'
import App from './App.vue'
import api from '~/api'
import { setGlobalUser } from '~/hooks/globalUser'

const app = createApp(App)

app.use(router)

setGlobalUser(app, null, true)

api
  .self()
  .then((user) => {
    setGlobalUser(app, user, false)
  })
  .catch(() => {
    setGlobalUser(app, null, false)
  })
app.mount('#app')

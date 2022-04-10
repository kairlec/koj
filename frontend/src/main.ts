import {createApp} from 'vue'
import router from "./router";
import App from './App.vue'
import Axios from 'axios'

const app = createApp(App)

app.config.globalProperties.Axios = Axios

app.use(router)

app.mount('#app')
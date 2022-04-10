<template>
  <login-dialog v-if="showLoginDialog" @login-success="onLoginSuccess" @login-cancel="onLoginCancel"></login-dialog>
  <register-dialog v-if="showRegisterDialog" @register-success="onRegisterSuccess"
                   @register-cancel="onRegisterCancel"></register-dialog>

  <el-menu :default-active="activeIndex" mode="horizontal" @select="handleSelect" router="router">
    <el-image src="/src/assets/logo.svg" class="logo"></el-image>
    <el-menu-item index="1" route="/home">首页</el-menu-item>
    <el-menu-item index="2" route="/problem">题目</el-menu-item>
    <el-menu-item index="3" route="/submit">提交</el-menu-item>
    <el-menu-item index="4" route="/contents">比赛</el-menu-item>
    <el-menu-item index="5" route="/rank">排行</el-menu-item>
    <el-button type="primary" round="round" class="loginOrOut" @click="loginOrOut">{{ loginOrOutName }}</el-button>
    <el-button v-if="username.length===0" type="primary" round="round" @click="registerClick">注册</el-button>
  </el-menu>
</template>

<script lang="ts">
import {onBeforeMount, onMounted} from "vue";
import {ref} from 'vue'
import LoginDialog from "./LoginDialog.vue";
import RegisterDialog from "./RegisterDialog.vue";

export default {
  name: "MenuBar",
  components: {RegisterDialog, LoginDialog},
  setup() {
    const activeIndex = ref('1')
    const loginOrOutName = ref("登录")
    const showLoginDialog = ref(false)
    const showRegisterDialog = ref(false)
    const username = ref('')

    onBeforeMount(() => {
// todo 如果已经有cookie
      //   api.loginUser("kairlec", "123456").then(res => {
      //     console.log(res)
      //   })
      //   console.log(this)
    })
    onMounted(() => {
      console.log('Component is mounted!')
    })
    return {
      username,
      showLoginDialog,
      showRegisterDialog,
      activeIndex,
      loginOrOutName,
      handleSelect(key: string) {
        console.log(key)
      },
      loginOrOut() {
        showLoginDialog.value = true
      },
      onLoginSuccess() {
        showLoginDialog.value = false
        loginOrOutName.value = "退出"
      },
      onLoginCancel() {
        showLoginDialog.value = false
      },
      onRegisterSuccess() {
        showRegisterDialog.value = false
      },
      onRegisterCancel() {
        showRegisterDialog.value = false
      },
      registerClick() {
        showRegisterDialog.value = true
      }
    }
  }
}
</script>

<style scoped>
.el-menu-item {
  width: 6%;
}

.el-button {
  margin-left: 35px;
  border-radius: 30px;
  height: 49px;
  width: 9%;
}

.loginOrOut {
  margin-left: auto;
}

.logo {
  margin-right: 30px;
}
</style>
<style>
.logo .el-image__inner {
  padding: 6px 0 14px 0;
  max-height: 38px;
}
</style>
<template>
  <login-dialog v-if='showLoginDialog' @login-success='onLoginSuccess' @login-cancel='onLoginCancel'></login-dialog>
  <register-dialog
    v-if='showRegisterDialog' @register-success='onRegisterSuccess'
    @register-cancel='onRegisterCancel'></register-dialog>

  <el-menu v-if='showMenu' :default-active='activeIndex' mode='horizontal' :router='true' @select='handleSelect'>
    <el-image class='logo' src='/src/assets/logo.svg'></el-image>
    <el-menu-item index='1' route='/home'>首页</el-menu-item>
    <el-menu-item index='2' route='/problem'>题目</el-menu-item>
    <el-menu-item index='3' route='/submit'>提交</el-menu-item>
    <el-menu-item index='4' route='/contents'>比赛</el-menu-item>
    <el-menu-item index='5' route='/rank'>排行</el-menu-item>
    <template v-if='user.user?.type===0'>
      <el-menu-item index='6' route='/userManage'>用户管理</el-menu-item>
    </template>
    <template v-if='!user.user'>
      <el-button
        class='login nologin-btn' :loading='user.initing' :round='true' type='primary'
        @click='showLoginDialog = true'>登录
      </el-button>
      <el-button
        class='nologin-btn' :loading='user.initing' :round='true' type='primary'
        @click='showRegisterDialog = true'>注册
      </el-button>
    </template>
    <template v-else>
      <el-dropdown class='login'>
        <el-button type='primary' class='user-btn' round='round'>
          {{ user.user.username }}
          <el-icon class='el-icon--right'>
            <arrow-down />
          </el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>修改密码</el-dropdown-item>
            <el-dropdown-item @click='logout()'>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>
  </el-menu>
</template>

<script lang='ts'>
import { defineComponent, getCurrentInstance, nextTick, onBeforeMount, ref } from 'vue';
import LoginDialog from './LoginDialog.vue';
import RegisterDialog from './RegisterDialog.vue';
import { ArrowDown } from '@element-plus/icons-vue';
import { User } from '~/api';
import { getGlobalUser, setGlobalUser } from '~/hooks/globalUser';
import { KOJStorage } from '~/storage';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'MenuBar',
  components: { ArrowDown, RegisterDialog, LoginDialog },
  setup() {
    const showMenu = ref(true);

    function reload() {
      showMenu.value = false;
      nextTick(() => {
        showMenu.value = true;
      });
    }

    const instance = getCurrentInstance()!;
    const activeIndex = ref('1');
    const showLoginDialog = ref(false);
    const showRegisterDialog = ref(false);
    const user = getGlobalUser(instance.appContext);
    const route = useRouter();

    onBeforeMount(() => {

    });

    return {
      showMenu,
      user,
      showLoginDialog,
      showRegisterDialog,
      activeIndex,
      handleSelect(key: string) {
        console.log(key);
      },
      onLoginSuccess(user: User) {
        setGlobalUser(instance.appContext, user);
        showLoginDialog.value = false;
      },
      onLoginCancel() {
        showLoginDialog.value = false;
      },
      onRegisterSuccess() {
        showRegisterDialog.value = false;
      },
      onRegisterCancel() {
        showRegisterDialog.value = false;
      },
      logout() {
        setGlobalUser(instance.appContext, null);
        KOJStorage.identity(null);
        reload();
      }
    };
  }
})
</script>

<style scoped>
.el-menu-item {
  width: 6%;
}

.nologin-btn {
  margin-left: 35px;
  border-radius: 30px;
  height: 49px;
  width: 9%;
}

.user-btn {
  border-radius: 30px;
  height: 49px;
}

.login {
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

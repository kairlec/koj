<template>
  <login-dialog v-if='showLoginDialog' @login-success='onLoginSuccess' @login-cancel='onLoginCancel'></login-dialog>
  <register-dialog
    v-if='showRegisterDialog' @register-success='onRegisterSuccess'
    @register-cancel='onRegisterCancel'></register-dialog>

  <el-menu v-if='showMenu' :id='id' :default-active='activeIndex' mode='horizontal' :router='true'>
    <el-image class='logo' :src='logo'></el-image>
    <template
      v-for='(item,idx) in userMenuRoutes' :key='idx'>
      <el-menu-item :index='`1${idx}`' :route='item.path'>{{ item.name }}</el-menu-item>
    </template>
    <template v-if='user.user?.type===0'>
      <template
        v-for='(item,idx) in manageMenuRoutes' :key='idx'>
        <el-menu-item :index='`0${idx}`' :route='item.path'>{{ item.name }}</el-menu-item>
      </template>
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
import { defineComponent, getCurrentInstance, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import LoginDialog from './LoginDialog.vue';
import RegisterDialog from './RegisterDialog.vue';
import { ArrowDown } from '@element-plus/icons-vue';
import { User } from '~/apiDeclaration';
import { getGlobalUser, setGlobalUser } from '~/hooks/globalUser';
import { KOJStorage } from '~/storage';
import { useRoute, useRouter } from 'vue-router';
import { routes } from '../router';

export default defineComponent({
  name: 'MenuBar',
  components: { ArrowDown, RegisterDialog, LoginDialog },
  props:{
    id: {
      type: String,
      default: 'menu-bar'
    }
  },
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
    const route = useRoute();
    const router = useRouter();
    const allRoutes = routes.filter((it) => it.displayName);
    const userRoutes = allRoutes.filter((it) => !it.manage);
    const manageRoutes = allRoutes.filter((it) => it.manage);
    const userMenuRoutes = ref(userRoutes.map((item) => {
      return {
        path: item.path,
        name: item.displayName
      };
    }));
    const manageMenuRoutes = ref(manageRoutes.map((item) => {
      return {
        path: item.path,
        name: item.displayName
      };
    }));
    const routeWatcher = watch(() => route.fullPath,
      (rt) => {
        for (let i = 0; i < manageRoutes.length; i++) {
          const current = manageRoutes[i];
          if (rt.startsWith(current.path)) {
            activeIndex.value = `0${i}`;
            return;
          }
        }
        for (let i = 0; i < userRoutes.length; i++) {
          const current = userRoutes[i];
          if (rt.startsWith(current.path)) {
            activeIndex.value = `1${i}`;
            return;
          }
        }
      });
    // 用户变动（初始化成功、退出登录、新登录等）
    const userWatcher = watch(() => user.user,
      (newUser) => {
        if (!newUser) {
          checkManageNeedRouteBack()
        }
      });
    // 监听初始化事件（比如初始化失败）
    const initWatcher = watch(() => user.initing,
      (initing) => {
        if (!initing) {
          checkManageNeedRouteBack()
        }
      });
    onBeforeUnmount(()=>{
      routeWatcher();
      userWatcher();
      initWatcher();
    })
    function checkManageNeedRouteBack(){
      const rt = route.fullPath;
      for (let i = 0; i < manageRoutes.length; i++) {
        const current = manageRoutes[i];
        if (rt.startsWith(current.path)) {
          router.push({ name: 'KojHome' });
          return;
        }
      }
    }
    return {
      userMenuRoutes,
      manageMenuRoutes,
      showMenu,
      user,
      showLoginDialog,
      showRegisterDialog,
      activeIndex,
      onLoginSuccess(user: User) {
        setGlobalUser(instance.appContext, user);
        showLoginDialog.value = false;
      },
      onLoginCancel() {
        showLoginDialog.value = false;
      },
      onRegisterSuccess() {
        showRegisterDialog.value = false;
        showLoginDialog.value = true;
      },
      onRegisterCancel() {
        showRegisterDialog.value = false;
      },
      logout() {
        setGlobalUser(instance.appContext, null);
        KOJStorage.identity(null);
        reload();
      },
      logo: new URL('../assets/logo.svg', import.meta.url).href
    };
  }
});
</script>

<style scoped>
.el-menu-item {
  width: 8%;
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

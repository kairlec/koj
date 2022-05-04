<template>
  <el-dialog
    v-model='dialogVisible'
    title='登录'
    @close='cancel'
  >
    <el-form
      v-if='loginMode'
      ref='loginRuleFormRef'
      :model='loginForm'
      :rules='loginRules'
    >
      <el-form-item :label-width='formLabelWidth' label='账户' prop='usernameOrEmail'>
        <el-input v-model='loginForm.usernameOrEmail' clearable autocomplete='email' placeholder='用户名/邮箱' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='密码' prop='password'>
        <el-input
          v-model='loginForm.password'
          autocomplete='current-password'
          show-password
          clearable
          type='password' />
      </el-form-item>
    </el-form>
    <el-form
      v-else
      ref='forgetRuleFormRef'
      :model='forgetForm'
      :rules='forgetRules'
    >
      <el-form-item :label-width='formLabelWidth' label='用户名' prop='username'>
        <el-input v-model='forgetForm.username' clearable autocomplete='username' maxlength='30' placeholder='用户名' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='邮箱' prop='email'>
        <el-input v-model='forgetForm.email' clearable autocomplete='email' maxlength='30' placeholder='邮箱' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='验证码' prop='verifyCode'>
        <el-input
          v-model='forgetForm.verifyCode' autocomplete='off' minlength='8' maxlength='8' style='width: 40%'
          @input='forgetForm.verifyCode = forgetForm.verifyCode.replace(/\D/g,"")' />
        <el-button
          :loading='getVerifyCodeBtn.loading'
          :disabled='getVerifyCodeBtn.disabled'
          @click='submitForm(forgetRuleFormRef,true)'>{{ getVerifyCodeBtn.text }}
        </el-button>
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='新密码' prop='newPassword'>
        <el-input
          v-model='forgetForm.newPassword'
          autocomplete='new-password'
          type='password'
          maxlength='30'
          show-password
          clearable />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class='dialog-footer' style='float: left'>
        <el-button
          :disabled='submitState' @click='loginMode = !loginMode'>{{ loginMode ? '忘记密码' : '去登录' }}</el-button>
      </span>
      <span class='dialog-footer'>
        <el-button @click='dialogVisible = false'>取消</el-button>
        <el-button
          type='primary'
          :loading='submitState'
          @click='submitForm(loginMode ? loginRuleFormRef : forgetRuleFormRef)'>
          {{ loginMode ? '登录' : '重置密码' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang='ts'>
import api from '~/api';
import { defineComponent, reactive, ref } from 'vue';
import type { FormInstance } from 'element-plus';

export default defineComponent({
  name: 'LoginDialog',
  emits: ['loginSuccess', 'loginCancel'],
  setup(props, context) {
    const formLabelWidth = '60px';
    const halfFormLabelWidth = '30px';
    const dialogVisible = ref(true);
    const loginMode = ref(true);
    const loginRuleFormRef = ref<FormInstance>();
    const forgetRuleFormRef = ref<FormInstance>();
    const submitState = ref(false);
    const controller = new AbortController()
    const getVerifyCodeBtn = reactive<any>({
      text: '获取验证码',
      loading: false,
      disabled: false,
      duration: 60,
      timer: null
    });

    const loginForm = reactive({
      usernameOrEmail: '',
      password: ''
    });

    const forgetForm = reactive({
      username: '',
      email: '',
      verifyCode: '',
      newPassword: ''
    });

    const getVerifyCode = () => {
      getVerifyCodeBtn.loading = true;
      getVerifyCodeBtn.timer && clearInterval(getVerifyCodeBtn.timer);
      api.forgetPassword(forgetForm.username, forgetForm.email,{
        signal:controller.signal
      }).then(() => {
        getVerifyCodeBtn.timer = setInterval(() => {
          const tmp = getVerifyCodeBtn.duration--;
          getVerifyCodeBtn.text = `${tmp}秒`;
          if (tmp <= 0) {
            clearInterval(getVerifyCodeBtn.timer);
            getVerifyCodeBtn.duration = 60;
            getVerifyCodeBtn.text = '重新获取';
            getVerifyCodeBtn.disabled = false;
          }
        }, 1000);
        getVerifyCodeBtn.loading = false;
        getVerifyCodeBtn.disabled = true;
        ElMessage({
          type: 'success',
          message: '如果输入用户名和邮箱匹配,请检查你的邮箱',
          duration: 5000
        });
      }).catch(() => {
        getVerifyCodeBtn.loading = false;
        getVerifyCodeBtn.disabled = false;
      });
    };

    const validateUsernameOrEmail = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入用户名或邮箱'));
      } else {
        if (value.indexOf('@') > -1) {
          validateEmail(rule, value, callback);
        } else {
          validateUsername(rule, value, callback);
        }
        callback();
      }
    };

    const validateEmail = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入邮箱'));
      } else if (!/^[a-zA-Z\d_.-]+@[a-zA-Z\d-]+(\.[a-zA-Z\d-]+)*\.[a-zA-Z\d]{2,6}$/.test(value)) {
        callback(new Error('邮箱格式不正确'));
      } else {
        callback();
      }
    };

    const validateUsername = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入用户名'));
      } else if (!/^[a-zA-Z\d_-]{4,}$/.test(value)) {
        callback(new Error('用户名格式不正确'));
      } else {
        callback();
      }
    };

    const validatePassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (value.length < 4) {
          callback(new Error('密码过短'));
        } else {
          callback();
        }
      }
    };

    const validateVerifyCode = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入验证码'));
      } else if (value.length < 8) {
        callback(new Error('验证码不足8位'));
      } else {
        callback();
      }
    };

    const loginRules = reactive({
      usernameOrEmail: [{ validator: validateUsernameOrEmail, trigger: ['blur', 'change'] }],
      password: [{ validator: validatePassword, trigger: ['blur', 'change'] }]
    });

    const forgetRules = reactive({
      username: [{ validator: validateUsername, trigger: ['blur', 'change'] }],
      email: [{ validator: validateEmail, trigger: ['blur', 'change'] }],
      verifyCode: [{ validator: validateVerifyCode, trigger: ['blur', 'change'] }],
      newPassword: [{ validator: validatePassword, trigger: ['blur', 'change'] }]
    });


    function login() {
      submitState.value = true;
      api.loginUser(loginForm.usernameOrEmail, loginForm.password,{
        signal:controller.signal
      }).then((user) => {
        context.emit('loginSuccess', user);
      }).finally(() => {
        submitState.value = false;
      });
    }

    function cancel() {
      controller.abort()
      context.emit('loginCancel');
    }

    const submitForm = (formEl: FormInstance | undefined, ur2 = false) => {
      if (!formEl) return;
      if (ur2) {
        formEl.validateField('username', (usernameValid: boolean) => {
          formEl.validateField('email', (valid) => {
            if (valid && usernameValid) {
              getVerifyCode();
            } else {
              return false;
            }
          });
        });
      } else {
        formEl.validate((valid) => {
          if (valid) {
            if (loginMode.value) {
              login();
            } else {
              api.resetPassword(forgetForm.username, forgetForm.email, forgetForm.verifyCode, forgetForm.newPassword,{
                signal:controller.signal
              }).then(() => {
                ElMessage({
                  type: 'success',
                  message: '重置密码成功，请重新登录',
                  duration: 5000
                });
                loginMode.value = true;
              }).finally(() => {
                submitState.value = true;
              });
            }
          } else {
            return false;
          }
        });
      }
    };

    return {
      submitState,
      getVerifyCodeBtn,
      halfFormLabelWidth,
      forgetRules,
      loginMode,
      loginRules,
      dialogVisible,
      formLabelWidth,
      loginForm,
      submitForm,
      forgetForm,
      loginRuleFormRef,
      forgetRuleFormRef,
      cancel
    };
  }
});
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
<style>
.el-dialog {
  --el-dialog-width: 450px;
}
</style>

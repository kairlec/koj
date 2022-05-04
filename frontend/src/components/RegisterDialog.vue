<template>
  <el-dialog
    v-model='dialogVisible'
    title='注册'
    @close='cancel'
  >

    <el-form
      ref='ruleFormRef'
      :model='form'
      :rules='rules'
    >
      <el-form-item :label-width='formLabelWidth' label='账户' prop='username'>
        <el-input v-model='form.username' clearable autocomplete='nickname' maxlength='30' placeholder='用户名' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='邮箱' prop='email'>
        <el-input v-model='form.email' clearable autocomplete='email' maxlength='30' placeholder='邮箱' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='密码' prop='password'>
        <el-input
          v-model='form.password' autocomplete='new-password' type='password' maxlength='30'
          show-password clearable />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class='dialog-footer'>
        <el-button @click='dialogVisible = false'>取消</el-button>
        <el-button :loading='loading' type='primary' @click='submitForm(ruleFormRef)'>注册</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang='ts'>
import api from '../utils/api';
import { defineComponent, reactive, ref } from 'vue';
import type { FormInstance } from 'element-plus';

export default defineComponent({
  name: 'RegisterDialog',
  emits: ['registerSuccess', 'registerCancel'],
  setup(props, context) {
    const formLabelWidth = '60px';
    const dialogVisible = ref(true);
    const ruleFormRef = ref<FormInstance>();
    const controller = new AbortController();
    const loading = ref(false);
    const registerApi = api.withConfig({ signal: controller.signal });

    const form = reactive({
      username: '',
      email: '',
      password: ''
    });

    const checkRequest: {
      username: {
        timeout: NodeJS.Timeout | undefined,
        controller: AbortController,
      },
      email: {
        timeout: NodeJS.Timeout | undefined,
        controller: AbortController,
      },
    } = {
      username: {
        timeout: undefined,
        controller: new AbortController()
      },
      email: {
        timeout: undefined,
        controller: new AbortController()
      }
    };

    const validateUsername = (rule: any, value: any, callback: any) => {
      if (checkRequest.username.timeout) {
        checkRequest.username.controller.abort();
        clearTimeout(checkRequest.username.timeout);
      }
      if (value === '') {
        callback(new Error('请输入用户名'));
      } else if (value.length < 6) {
        callback(new Error('用户名长度不能小于6位'));
      } else if (!/^[a-zA-Z\d_-]{4,}$/.test(value)) {
        callback(new Error('用户名只能包含字母、数字、下划线和中划线'));
      } else {
        checkRequest.username.timeout = setTimeout(() => {
          registerApi.existsUsernameOrEmail(value, { signal: checkRequest.username.controller.signal }).then(res => {
            if (res) {
              callback(new Error('用户名已存在'));
            } else {
              callback();
            }
          }).finally(() => {
            checkRequest.username.timeout = undefined;
          });
        });
      }
    };

    const validatePassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (value.length < 6) {
          callback(new Error('密码过短'));
        } else {
          callback();
        }
      }
    };

    const validateEmail = (rule: any, value: any, callback: any) => {
      if (checkRequest.email.timeout) {
        checkRequest.email.controller.abort();
        clearTimeout(checkRequest.email.timeout);
      }
      if (value === '') {
        callback(new Error('请输入邮箱'));
      } else if (!(/^[a-zA-Z\d_.-]+@[a-zA-Z\d-]+(\.[a-zA-Z\d-]+)*\.[a-zA-Z\d]{2,6}$/.test(value))) {
        callback(new Error('邮箱格式不正确'));
      } else {
        checkRequest.email.timeout = setTimeout(() => {
          registerApi.existsUsernameOrEmail(value, { signal: checkRequest.email.controller.signal }).then(res => {
            if (res) {
              callback(new Error('邮箱已存在'));
            } else {
              callback();
            }
          }).finally(() => {
            checkRequest.email.timeout = undefined;
          });
        });
      }
    };

    const rules = reactive({
      username: [{ validator: validateUsername, trigger: 'blur' }],
      email: [{ validator: validateEmail, trigger: 'blur' }],
      password: [{ validator: validatePassword, trigger: 'blur' }]
    });


    function register() {
      loading.value = true;
      registerApi.registerUser(form.username, form.password, form.email, { signal: controller.signal }).then(() => {
        context.emit('registerSuccess');
      }).finally(() => {
        loading.value = false;
      });
    }

    function cancel() {
      controller.abort();
      context.emit('registerCancel');
    }

    const submitForm = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      formEl.validate((valid) => {
        if (valid) {
          register();
        } else {
          return false;
        }
      });
    };

    return {
      loading,
      rules,
      dialogVisible,
      formLabelWidth,
      form,
      submitForm,
      ruleFormRef,
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

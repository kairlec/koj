<template>
  <el-dialog
    v-model='dialogVisible'
    title='注册'
    @close='cancel'
  >

    <el-form ref='ruleFormRef'
             :model='form'
             :rules='rules'
    >
      <el-form-item :label-width='formLabelWidth' label='账户' prop='username'>
        <el-input v-model='form.username' autocomplete='nickname' placeholder='用户名' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='邮箱' prop='email'>
        <el-input v-model='form.email' autocomplete='email' placeholder='邮箱' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='密码' prop='password'>
        <el-input v-model='form.password' autocomplete='new-password' type='password' />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class='dialog-footer'>
        <el-button @click='dialogVisible = false'>取消</el-button>
        <el-button type='primary' @click='submitForm(ruleFormRef)'>注册</el-button>
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

    const form = reactive({
      username: '',
      email: '',
      password: '',
    });

    let usernameCheckTimeout: NodeJS.Timeout | undefined;
    let emailCheckTimeout: NodeJS.Timeout | undefined;

    const validateUsername = (rule: any, value: any, callback: any) => {
      if (usernameCheckTimeout) {
        clearTimeout(usernameCheckTimeout);
      }
      if (value === '') {
        callback(new Error('请输入用户名'));
      } else if (value.length < 6) {
        callback(new Error('用户名长度不能小于6位'));
        // 检查用户名是否包含违规字符
      } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
        callback(new Error('用户名只能包含字母、数字和下划线'));
      } else {
        usernameCheckTimeout = setTimeout(() => {
          api.existsUsernameOrEmail(value).then(res => {
            if (res) {
              callback(new Error('用户名已存在'));
            } else {
              callback();
            }
          }).finally(() => {
            usernameCheckTimeout = undefined;
          });
        });
      }
    };

    const validatePassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else if (value.length < 6) {
        callback(new Error('密码长度不能小于6位'));
      } else {
        callback();
      }
    };

    const validateEmail = (rule: any, value: any, callback: any) => {
      if (emailCheckTimeout) {
        clearTimeout(emailCheckTimeout);
      }
      if (value === '') {
        callback(new Error('请输入邮箱'));
      } else if (!(/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,}$/.test(value))) {
        callback(new Error('邮箱格式不正确'));
      } else {
        emailCheckTimeout = setTimeout(() => {
          api.existsUsernameOrEmail(value).then(res => {
            if (res) {
              callback(new Error('邮箱已存在'));
            } else {
              callback();
            }
          }).finally(() => {
            emailCheckTimeout = undefined;
          });
        });
      }
    };

    const rules = reactive({
      username: [{ validator: validateUsername, trigger: 'blur' }],
      email: [{ validator: validateEmail, trigger: 'blur' }],
      password: [{ validator: validatePassword, trigger: 'blur' }],
    });


    function register() {
      api.registerUser(form.username, form.password, form.email).then(() => {
        context.emit('registerSuccess');
      });
    }

    function cancel() {
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
      rules,
      dialogVisible,
      dialogVisible,
      formLabelWidth,
      form,
      submitForm,
      ruleFormRef,
      cancel,
    };
  },
});
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>

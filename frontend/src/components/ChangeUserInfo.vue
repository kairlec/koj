<template>
  <el-dialog
    v-model='dialogVisible'
    title='修改信息'
    @close='cancel'
  >

    <el-form
      ref='ruleFormRef'
      :model='form'
      :rules='rules'
    >
      <el-form-item :label-width='formLabelWidth' label='账户' prop='username'>
        <el-input v-model='form.username' autocomplete='username' placeholder='用户名' readonly />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='邮箱' prop='email'>
        <el-input v-model='form.email' autocomplete='email' placeholder='邮箱' />
      </el-form-item>
      <el-form-item :label-width='formLabelWidth' label='新密码' prop='password'>
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
import { defineComponent, onBeforeMount, reactive, ref } from 'vue';
import type { FormInstance } from 'element-plus';

export default defineComponent({
  name: 'ChangeUserInfo',
  emits: ['loginSuccess', 'loginCancel'],
  setup(props, context) {
    const formLabelWidth = '60px';
    const dialogVisible = ref(true);
    const ruleFormRef = ref<FormInstance>();

    const form = reactive({
      usernameOrEmail: '',
      password: '',
    });

    const validateUsernameOrEmail = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入账户'));
      } else {
        callback();
      }
    };

    const validatePassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        callback();
      }
    };

    onBeforeMount(() => {
      api.self();
    });

    const rules = reactive({
      usernameOrEmail: [{ validator: validateUsernameOrEmail, trigger: 'blur' }],
      password: [{ validator: validatePassword, trigger: 'blur' }],
    });


    function login() {
      api.loginUser(form.usernameOrEmail, form.password).then(() => {
        context.emit('loginSuccess');
      });
    }

    function cancel() {
      context.emit('loginCancel');
    }

    const submitForm = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      formEl.validate((valid) => {
        if (valid) {
          login();
        } else {
          return false;
        }
      });
    };

    return {
      rules,
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

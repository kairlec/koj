<template>
    <el-dialog
        v-model='dialogVisible'
        :title='`${addMode?"添加":"修改"}比赛`'
    >
        <el-form
            ref='ruleFormRef'
            :model='form'
            :rules='rules'
        >
            <el-form-item :label-width='formLabelWidth' label='比赛名称' prop='name'>
                <el-input v-model='form.name' clearable autocomplete='off' maxlength='32' placeholder='比赛名称' />
            </el-form-item>
            <el-form-item :label-width='formLabelWidth' label='比赛密码(可选)' prop='email'>
                <el-input v-model='form.pwd' clearable autocomplete='off' maxlength='32' placeholder='比赛密码' />
            </el-form-item>
            <el-form-item label='开始时间'>
                <el-date-picker
                    v-model='form.timeRange'
                    type='datetimerange'
                    placeholder='选择开始结束时间'
                    style='width: 100%'
                />
            </el-form-item>
        </el-form>

        <template #footer>
      <span class='dialog-footer'>
        <el-button @click='dialogVisible = false'>取消</el-button>
        <el-button
            :loading='loading' type='primary'
            @click='submitForm(ruleFormRef)'>{{ `${addMode ? '添加' : '修改'}比赛` }}</el-button>
      </span>
        </template>
    </el-dialog>
</template>

<script lang='ts' setup>
import api from '~/api';
import { computed, defineProps, reactive, ref } from 'vue';
import type { FormInstance } from 'element-plus';
import { setTime } from '~/api/extra';

const props = defineProps({
    addMode: {
        type: Boolean,
        required: true
    },
    show: {
        type: Boolean,
        required: true,
        default: true
    }
});
const emits = defineEmits(['addCompetition', 'update:show']);

const dialogVisible = computed({
    get() {
        return props.show;
    },
    set(value) {
        emits('update:show', value);
    }
});
const formLabelWidth = '60px';
const ruleFormRef = ref<FormInstance>();
const controller = new AbortController();
const loading = ref(false);
const registerApi = api.withConfig({ signal: controller.signal });

const form = reactive({
    name: '',
    pwd: '',
    timeRange: [new Date(), new Date(Date.now() + 10800_000)]
});

const validateName = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入比赛名'));
    } else {
        callback();
    }
};

const rules = reactive({
    name: [{ validator: validateName, trigger: 'blur' }]
});

function register() {
    loading.value = true;
    const competition = {
        name: form.name,
        pwd: form.pwd,
        startTime: form.timeRange[0].toISOString(),
        endTime: form.timeRange[1].toISOString()
    };

    registerApi.createCompetition(competition, { signal: controller.signal }).then((id) => {
        const result = {
            id,
            ...competition
        };
        setTime(result);
        emits('addCompetition', result);
    }).finally(() => {
        loading.value = false;
    });
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

</script>
<style scoped>
.dialog-footer button:first-child {
    margin-right: 10px;
}
</style>

<template>
    <el-dialog
        v-model='dialogVisible'
        custom-class='competition-dialog'
        :title='`${props.baseInstance.id?"修改":"添加"}比赛`'
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
            <el-form-item :label-width='formLabelWidth' label='开始时间'>
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
            @click='submitForm(ruleFormRef)'>{{ `${props.baseInstance.id ? '修改' : '添加'}比赛` }}</el-button>
      </span>
        </template>
    </el-dialog>
</template>

<script lang='ts' setup>
import api from '~/api';
import { computed, defineProps, onBeforeUpdate, PropType, reactive, ref } from 'vue';
import type { FormInstance } from 'element-plus';
import { setTime } from '~/api/extra';
import { ManageCompetition } from '~/apiDeclaration';

type CompetitionModel = Omit<ManageCompetition, 'id'> & { id?: string }

const props = defineProps({
    baseInstance: {
        type: Object as PropType<CompetitionModel>,
        required: true
    },
    show: {
        type: Boolean,
        required: true,
        default: true
    }
});
const emits = defineEmits(['addCompetition', 'update:show']);

const dialogVisible = computed<boolean>({
    get() {
        return props.show;
    },
    set(value) {
        emits('update:show', value);
    }
});

onBeforeUpdate(() => {
    form.name = props.baseInstance.name;
    form.pwd = props.baseInstance.pwd ?? '';
    form.timeRange = [props.baseInstance.startTime.toDate(), props.baseInstance.endTime.toDate()];
});

const formLabelWidth = '110px';
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

function createCompetition() {
    loading.value = true;
    const competition = {
        name: form.name,
        pwd: form.pwd,
        startTime: form.timeRange[0].toISOString(),
        endTime: form.timeRange[1].toISOString()
    };

    registerApi.createCompetition(competition).then((id) => {
        const result = {
            new: true,
            id,
            ...competition
        };
        setTime(result);
        emits('addCompetition', result);
    }).finally(() => {
        loading.value = false;
    });
}

function updateCompetition(id: string) {
    loading.value = true;
    const competition = {
        name: form.name,
        pwd: form.pwd
    };

    registerApi.updateCompetition(id, competition).then(() => {
        const result = {
            ...props.baseInstance,
            new:false,
            id: id,
            ...competition,
        };
        emits('addCompetition', result);
    }).finally(() => {
        loading.value = false;
    });
}

function submitForm(formEl: FormInstance | undefined) {
    if (!formEl) return;
    formEl.validate((valid) => {
        if (valid) {
            if (props.baseInstance.id) {
                updateCompetition(props.baseInstance.id);
            } else {
                createCompetition();
            }
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

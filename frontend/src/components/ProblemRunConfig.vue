<template>
  <el-card class='box-card' style='padding: 5px;margin: 0'>
    <template #header>
      <div style='display: flex'>
        <span style='align-items: center;'>运行配置</span>
      </div>
    </template>
      <div v-loading='fetchingRunConfig'>
        <el-form
          v-if='problemRunConfigManage!==undefined'
          label-position='top'
          label-width='100px'
          :model='problemRunConfigManage'
          :disabled='updating'
        >
          <el-card v-loading='fetchingRunConfig' style='margin: 15px 0;'>
            <el-form-item label='用例输入'>
              <el-input v-model='problemRunConfigManage.stdin' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='答案输出'>
              <el-input v-model='problemRunConfigManage.ansout' type='textarea' :autosize='true' />
            </el-form-item>
          </el-card>
        </el-form>
      </div>
  </el-card>
</template>

<script lang='ts' setup>

import { onBeforeMount, onBeforeUnmount, PropType, Ref, ref } from 'vue';
import { ProblemDetail } from '~/apiDeclaration';
import api from '~/api';

const props = defineProps({
  problemDetail: {
    type: Object as PropType<ProblemDetail>,
    required: true,
  },
});

const problemRunConfigManage: Ref<{ stdin: string, ansout: string } | undefined> = ref();
const fetchingRunConfig = ref(false)
const updating = ref(false)

const controller = new AbortController();
const problemLanguageConfigApi = api.withConfig({ signal: controller.signal });

const emit = defineEmits(['close'])

function fetchRunConfigIfNeed() {
  if (problemRunConfigManage.value !== undefined) {
    return;
  }
  fetchingRunConfig.value = true
  problemLanguageConfigApi.getRunConfig(props.problemDetail.id, {
    ignoreError: true,
  }).then((data) => {
    problemRunConfigManage.value = data;
  }).catch((err) => {
    if(err?.response?.data?.code===20004){
      problemRunConfigManage.value = {stdin:'',ansout:''};
    }
    setTimeout(() => {
      fetchRunConfigIfNeed();
    }, 100);
  }).finally(() => {
    fetchingRunConfig.value = false;
  });
}

function saveRunConfig(): Promise<any> {
  if (problemRunConfigManage.value === undefined) {
    ElMessage({
      type: 'error',
      message: '运行内容为空',
    });
    return Promise.reject();
  }
  updating.value = true;
  return problemLanguageConfigApi.saveRunConfig(props.problemDetail.id, problemRunConfigManage.value).then(() => {
    ElMessage({
      type: 'success',
      message: '保存成功',
    });
    emit('close')
  }).finally(() => {
    updating.value=false
  });
}

onBeforeMount(()=>{
  fetchRunConfigIfNeed()
})

defineExpose({
  saveRunConfig
})

onBeforeUnmount(() => {
  controller.abort();
});

</script>
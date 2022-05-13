<template>
  <el-card class='box-card' style='padding: 5px;margin: 0'>
    <template #header>
      <div style='display: flex'>
        <span style='align-items: center;'>语言配置</span>
      </div>
    </template>
    <div v-loading='fetchingLanguageConfig'>
      <template v-for='(sample,idx) in problemConfigManage' :key='idx'>
        <el-form
          label-position='top'
          label-width='100px'
          :model='problemConfigManage[idx]'
          :disabled='updating'
        >
          <el-card
            style='margin: 15px 0;'>
            <template #header>
              <div class='card-header'>
                <el-select v-model='sample.languageId' placeholder='选择语言' :disabled='sample.created===false'>
                  <el-option
                    v-for='item in languageIds'
                    :key='item'
                    :label='item'
                    :value='item'
                    :disabled='existLanguage(item)'
                  />
                </el-select>
                <div style='display: flex;margin-left: auto'>
                  <el-button
                    v-if='sample.created||sample.changed'
                    :disabled='sample.languageId.length===0'
                    style='margin-right: 30px'
                    :icon='Check'
                    type='success'
                    @click='saveConfig(sample)'>
                    保存配置{{ sample.languageId }}
                  </el-button>
                  <el-button
                    :icon='Delete'
                    type='danger'
                    style='margin-left: auto;'
                    @click='deleteConfig(sample,idx)'>
                    删除配置{{ sample.languageId }}
                  </el-button>
                </div>
              </div>
            </template>
            <el-form-item label='时间限制(ms)'>
              <el-input-number v-model='sample.time' :step='100' :min='100' :max='30000' @input='sample.changed=true' />
            </el-form-item>
            <el-form-item label='内存限制(B)'>
              <el-input-number
                v-model='sample.memory' :step='100' :min='100' :max='9999999'
                @input='sample.changed=true' />
            </el-form-item>
          </el-card>
        </el-form>
      </template>
      <el-button
        type='primary' :icon='CirclePlus'
        style='margin-bottom: 10px;'
        :disabled='updating'
        @click='problemConfigManage.push({languageId:"",time:100,memory:100,created:true,changed:true})'>新增样例
      </el-button>
    </div>
  </el-card>
</template>

<script lang='ts' setup>

import { computed, onBeforeMount, onBeforeUnmount, PropType, reactive, ref } from 'vue';
import { ProblemConfigManage, ProblemDetail } from '~/apiDeclaration';
import { Check, CirclePlus, Delete } from '@element-plus/icons-vue';
import api from '~/api';

const props = defineProps({
  problemDetail: {
    type: Object as PropType<ProblemDetail>,
    required: true,
  },
  languageIds: {
    type: Array as PropType<Array<String>>,
    required: true,
  },
});


const problemConfigManage = reactive(Array<ProblemConfigManage & { changed?: boolean, created?: boolean }>());
const fetchingLanguageConfig = ref(false);
const updating = ref(false);

const controller = new AbortController();
const problemLanguageConfigApi = api.withConfig({ signal: controller.signal });

function saveConfig(config: ProblemConfigManage & { changed?: boolean, created?: boolean }) {
  updating.value = true;
  problemLanguageConfigApi.addConfig(props.problemDetail.id, config).then(() => {
    config.created = false;
    config.changed = false;
  }).finally(() => {
    updating.value = false;
  });
}

function deleteConfig(config: ProblemConfigManage & { changed?: boolean, created?: boolean }, idx: number) {
  if (config.created) {
    console.log(problemConfigManage);
    console.log(idx);
    console.log(config);
    problemConfigManage.splice(idx, 1);
    return;
  }
  updating.value = true;
  problemLanguageConfigApi.deleteConfig(props.problemDetail.id, config.languageId).then(() => {
    problemConfigManage.splice(idx, 1);
  }).finally(() => {
    updating.value = false;
  });
}


function fetchConfigIfNeed() {
  if (problemConfigManage.length > 0) {
    return;
  }
  fetchingLanguageConfig.value = true;
  problemLanguageConfigApi.getConfigs(props.problemDetail.id, {
    ignoreError: true,
  }).then((data) => {
    problemConfigManage.push(...data);
  }).catch(() => {
    setTimeout(() => {
      fetchConfigIfNeed();
    }, 100);
  }).finally(() => {
    fetchingLanguageConfig.value = false;
  });
}

onBeforeMount(() => {
  fetchConfigIfNeed();
});

onBeforeUnmount(() => {
  controller.abort();
});

const existLanguage = computed(() => {
  return (language?: string) => {
    return problemConfigManage.find(it => it.languageId === language) !== undefined;
  };
});

</script>

<style>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
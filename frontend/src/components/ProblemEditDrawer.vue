<template>
  <el-drawer v-model='show' direction='rtl' size='100%' :before-close='handleDrawerClose'>
    <template #title>
      <el-input v-model='problemNameEdit'>
        <template #prepend>{{ `标题: ${problemDetail?.id} - ` }}</template>
      </el-input>
      <h4 v-else>{{ `${problemDetail?.id} : ${problemDetail?.name}` }}</h4>
    </template>
    <template #default>
      <el-card class='box-card' style='padding: 5px;margin: 0'>
        <template #header>
          <div style='display: flex'>
            <span style='align-items: center;'>题目内容</span>
            <el-button
              type='primary'
              style='margin-left: auto' @click='flushDetail() && (showMode = showMode==="Raw"?"Detail":"Raw")'>
              切换{{ showMode === 'Raw' ? '表格视图' : '原始视图' }}
            </el-button>
          </div>
          <div v-if='showMode==="Config"' style='display: flex'>
            <span style='align-items: center;'>语言配置</span>
          </div>
        </template>
        <prism-editor
          v-if='showMode==="Raw"' v-model='problemContentEdit' class='my-editor' :highlight='highlighterJson'
          line-numbers></prism-editor>
        <template v-if='showMode==="Detail"'>
          <el-form
            label-position='top'
            label-width='100px'
            :model='problemContentObjEdit'
            :disabled='saving'
          >
            <el-form-item label='题目描述'>
              <el-input v-model='problemContentObjEdit.description' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='输入'>
              <el-input v-model='problemContentObjEdit.input' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='输出'>
              <el-input v-model='problemContentObjEdit.output' type='textarea' :autosize='true' />
            </el-form-item>
            <template v-for='(sample,idx) in problemContentObjEdit.samples' :key='idx'>
              <el-card style='margin: 15px 0;'>
                <template #header>
                  <div style='display: flex'>
                    <el-button
                      :icon='Delete'
                      type='danger'
                      style='margin-left: auto;'
                      @click='problemContentObjEdit.samples.splice(idx,1)'>
                      删除样例{{ idx + 1 }}
                    </el-button>
                  </div>
                </template>
                <el-form-item :label='`样例${idx+1}输入`'>
                  <el-input v-model='sample.input' type='textarea' :autosize='true' />
                </el-form-item>
                <el-form-item :label='`样例${idx+1}输出`'>
                  <el-input v-model='sample.output' type='textarea' :autosize='true' />
                </el-form-item>
              </el-card>
            </template>
            <el-button
              type='primary' :icon='CirclePlus'
              style='margin-bottom: 10px;'
              @click='problemContentObjEdit.samples.push({input:"",output:""})'>新增样例
            </el-button>

            <el-form-item label='提示'>
              <el-input v-model='problemContentObjEdit.hint' type='textarea' :autosize='true' />
            </el-form-item>
          </el-form>
        </template>
      </el-card>
    </template>
    <template #footer>
      <div
        v-loading='!fetchingFinishState.tagList'
        style='text-align: center'>
        <el-transfer
          v-model='rightValue'
          :disabled='saving'
          style='text-align: left; display: inline-block'
          filterable
          :titles="['可选标签', '题目标签']"
          :button-texts="['取消', '选中']"
          :format="{
          noChecked: '${total}',
          hasChecked: '${checked}/${total}',
        }"
          :data='tagData'
        >
          <template #default='{ option }'>
            <span>{{ option.label }}</span>
          </template>
        </el-transfer>
      </div>

      <div style='flex: auto'>
        <el-button :loading='saving' @click='resetClick'>重置</el-button>
        <el-button type='primary' :loading='saving' @click='confirmClick'>确定</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang='ts' setup>
import { onBeforeMount, onBeforeUnmount, PropType, reactive, ref } from 'vue';
import api from '~/api';
import { ProblemContent, ProblemDetail } from '~/apiDeclaration';
import { CirclePlus, Delete, Edit } from '@element-plus/icons-vue';
import { PrismEditor } from 'vue-prism-editor';
import Prism, { highlight } from 'prismjs';
import 'vue-prism-editor/dist/prismeditor.min.css';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-textile';
import 'prismjs/themes/prism.css';

interface Option {
  key: number;
  label: string;
  disabled: boolean;
}

const { problemDetail, show:showDrawer } = defineProps({
  problemDetail: {
    type: Object as PropType<ProblemDetail>,
    required: true,
  },
  show: {
    type: Boolean,
    required: true,
    default: true,
  },
});

defineEmits({
  closeDrawer(ok: boolean) {
    return ok;
  },
});


const problemNameEdit = ref(problemDetail.name);
const problemContentEdit = ref(problemDetail.content);
const saving = ref(false);
const fetchingFinishState: {
  tagList?: boolean,
} = reactive({});
const controller = new AbortController();
const tagData = ref(Array<Option>());
const rightValue = ref(Array<number>());
const problemEditDrawerApi = api.withConfig({ signal: controller.signal });
const showRawMode = ref(true);

const problemContentObjEdit: ProblemContent = reactive({
  description: '',
  input: '',
  output: '',
  samples: [],
  hint: '',
});

function flushDetail(): boolean {
  if (showRawMode.value) {
    try {
      Object.assign(problemContentObjEdit, JSON.parse(problemContentEdit.value));
      return true;
    } catch (e) {
      ElMessage({
        type: 'error',
        message: 'json格式错误,请修正后切换视图',
      });
      return false;
    }
  } else {
    problemContentEdit.value = JSON.stringify(problemContentObjEdit, null, 2);
    return true;
  }
}

function handleDrawerClose(done: () => void) {
  ElMessageBox.confirm('是否保存数据?', {
    confirmButtonText: '保存',
    cancelButtonText: '退出',
  })
    .then(() => {
      confirmClick().then(() => {
        done();
      }).catch(() => {
        Promise.resolve();
      });
    }).catch(() => {
    done();
  });
}

onBeforeMount(() => {
  fetchProblem();
  fetchTags();
});

function fetchTags() {
  fetchingFinishState.tagList = false;
  problemEditDrawerApi.tags({
    limit: 99999,
  }, {
    ignoreError: true,
  }).then(res => {
    tagData.value = res.record.map(tag => {
      return {
        key: tag.id,
        label: tag.name,
        disabled: false,
      };
    });
    fetchingFinishState.tagList = true;
    if (!problemDetail.tags) {
      rightValue.value = [];
    } else {
      rightValue.value = tagData.value.filter(tag => {
        return problemDetail.tags.includes(tag.label);
      }).map(tag => tag.key);
    }
  }).catch(() => {
    setTimeout(() => {
      fetchTags();
    }, 100);
  });
}

onBeforeUnmount(() => {
  controller.abort();
});

function resetClick() {
  ElMessageBox.confirm('确定重置初始内容?')
    .then(() => {
      if (problemDetail.contentObj) {
        try {
          problemContentEdit.value = JSON.stringify(problemDetail.contentObj, null, 2);
        } catch (e) {
          problemContentEdit.value = problemDetail.content ?? '';
        }
      } else {
        problemContentEdit.value = problemDetail.content ?? '';
      }
      problemNameEdit.value = problemDetail.name ?? '';
      if (!problemDetail.tags) {
        rightValue.value = [];
      } else {
        rightValue.value = tagData.value.filter(tag => {
          return problemDetail.tags.includes(tag.label);
        }).map(tag => tag.key);
      }
    });
}

function saveProblemDetail(): Promise<any> {
  let obj: ProblemContent;
  try {
    if (!showRawMode.value) {
      obj = problemContentObjEdit;
    } else {
      obj = JSON.parse(problemContentEdit.value);
    }
  } catch (e) {
    console.log(e);
    ElMessageBox.alert('内容不是合法的json', {
      type: 'error',
    });
    return Promise.reject(e);
  }
  saving.value = true;
  const realContent = JSON.stringify(obj);
  return problemEditDrawerApi.updateProblem(problemDetail.value!.id, {
    name: problemNameEdit.value,
    content: realContent,
    tags: rightValue.value,
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '保存成功',
    });
    problemDetail.tags = tagData.value.filter(tag => {
      return rightValue.value.includes(tag.key);
    }).map(tag => tag.label);
    problemDetail.content = realContent;
    problemDetail.name = problemNameEdit.value;
    problemDetail.contentObj = obj;
    emit('close', true);
  }).finally(() => {
    saving.value = false;
  });
}

function confirmClick(): Promise<any> {
  return saveProblemDetail();
}

</script>


<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/*.el-main >>> .el-scrollbar__bar {*/
/*  display: none;*/
/*}*/
.core-editor {
  min-height: 400px;
}

</style>

<style>

.el-drawer__body {
  padding-top: 0;
  padding-bottom: 0;
}

.content-label {
  color: deepskyblue !important;
  font-weight: bold !important;;
  left: 20px !important;;
  font-size: 20px !important;;
}

/* required class */
.my-editor {
  /* you must provide font-family font-size line-height. Example: */
  font-family: Fira code, Fira Mono, Consolas, Menlo, Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  padding: 5px;
}

/* optional class for removing the outline */
.prism-editor__textarea:focus {
  outline: none;
}
</style>
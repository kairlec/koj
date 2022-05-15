<template>
  <ProblemEditDrawer
    v-if='problemDetail!==undefined' v-model:problem-detail='problemDetail'
    v-model:show='showEdit'></ProblemEditDrawer>
  <ProblemManageEditDrawer
    v-if='user.user?.type===0 && problemDetail!==undefined'
    ref='managerRef'
    v-model:problem-detail='problemDetail'
    v-model:language-ids='languageIds'
    v-model:show='showManager'
  ></ProblemManageEditDrawer>
  <el-container direction='vertical'>
    <el-main>
      <el-scrollbar>
        <div style='padding-right: 8px'>
          <template v-if='problemFetchError.length'>
            <el-result
              icon='error'
              title='请求出错'
              :sub-title='problemFetchError'
              class='box-card'
            >
              <template #extra>
                <el-button type='primary' :loading='refreshing' @click='fetchProblem(undefined,true)'>刷新</el-button>
                <template v-if='user.user?.type===0 && editAble'>
                  <el-button :loading='refreshing' @click='switchContent'>编辑内容</el-button>
                </template>
              </template>
            </el-result>
          </template>
          <el-card v-else v-loading='!fetchingFinishState.problemDetail' class='box-card'>
            <template #header>
              <div class='card-header'>
                <span>{{ `${problemDetail?.id ?? '请求中'} : ${problemDetail?.name ?? '请求中'}` }}</span>
                <template v-if='user.user?.type===0 && fetchingFinishState.problemDetail'>
                  <el-button
                    type='primary' :icon='Edit'
                    style='margin-left: auto'
                    @click='switchContent'>编辑内容
                  </el-button>
                  <el-button
                    type='primary' :icon='Edit'
                    @click='switchRunConfig'>编辑运行
                  </el-button>
                  <el-button
                    :loading='!fetchingFinishState.languageList'
                    type='primary' :icon='Edit'
                    @click='switchLanguageConfig'>编辑配置
                  </el-button>
                </template>
              </div>
            </template>

            <el-descriptions
              :column='1'
              size='large'
              direction='vertical'
              style='margin-top: 32px;'
            >
              <el-descriptions-item label='题目内容' label-class-name='content-label'>
                {{ problemDetail?.contentObj?.description }}
              </el-descriptions-item>
              <el-descriptions-item label='输入' label-class-name='content-label'>{{ problemDetail?.contentObj?.input }}
              </el-descriptions-item>
              <el-descriptions-item label='输出' label-class-name='content-label'>{{ problemDetail?.contentObj?.output }}
              </el-descriptions-item>
              <template v-for='(item,index) in problemDetail?.contentObj?.samples' :key='index'>
                <el-descriptions-item :label='`样例输入${index+1}`' label-class-name='content-label'>
                  <prism-editor
                    v-model='item.input' class='my-editor' style='background-color: #f8f8f9' readonly
                    :highlight='highlighterNothing'></prism-editor>
                </el-descriptions-item>
                <el-descriptions-item :label='`样例输出${index+1}`' label-class-name='content-label'>
                  <prism-editor
                    v-model='item.output' class='my-editor' style='background-color: #f8f8f9' readonly
                    :highlight='highlighterNothing'></prism-editor>
                </el-descriptions-item>
              </template>
              <el-descriptions-item label='提示' label-class-name='content-label'>{{ problemDetail?.contentObj?.hint }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
          <el-card class='box-card'>
            <template #header>
              <div class='card-header'>
                <el-select v-model='chosenLanguageId' :loading='!fetchingFinishState.languageList' placeholder='请选择语言'>
                  <el-option
                    v-for='item in languageIds'
                    :key='item'
                    :label='item'
                    :value='item'
                    :disabled='!(problemDetail?.config?.find((it)=>it.languageId===item))'
                  />
                </el-select>
                <el-button
                  :loading='!fetchingFinishState.problemDetail || !fetchingFinishState.languageList'
                  :disabled='userCode.length===0'
                  type='primary' style='margin-left: auto' @click='submitCode'>提交
                </el-button>
              </div>
            </template>
            <prism-editor
              v-model='userCode'
              v-loading='!fetchingFinishState.problemDetail || !fetchingFinishState.languageList'
              :disabled='chosenLanguageId.length===0' class='my-editor core-editor' line-numbers
              :highlight='highlighterEditorContent'></prism-editor>
          </el-card>
        </div>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, reactive, Ref, ref, watch } from 'vue';
import api, { ProblemDetailError } from '~/api';
import { ProblemDetail } from '~/apiDeclaration';
import { useRoute } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';
import { Edit } from '@element-plus/icons-vue';
import { PrismEditor } from 'vue-prism-editor';
import Prism, { Grammar, highlight } from 'prismjs';
import 'vue-prism-editor/dist/prismeditor.min.css';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-kotlin';
import 'prismjs/components/prism-textile';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-json';
import 'prismjs/themes/prism.css';

function getLanguage(languageId: string): { language: Grammar, name: string } {
  if (/kotlin/i.test(languageId)) {
    return { language: Prism.languages.kotlin, name: 'kotlin' };
  }
  if (/java/i.test(languageId)) {
    return { language: Prism.languages.java, name: 'java' };
  }
  if (/python/i.test(languageId)) {
    return { language: Prism.languages.python, name: 'python' };
  }
  if (/c\+\+/i.test(languageId)) {
    return { language: Prism.languages.cpp, name: 'cpp' };
  }
  if (/c/i.test(languageId)) {
    return { language: Prism.languages.c, name: 'c' };
  }
  return { language: Prism.languages.textile, name: 'textile' };
}

export default defineComponent({
  name: 'ProblemDetail',
  components: {
    PrismEditor,
  },
  props: {
    competitionId: {
      type: String,
      required: false,
      default: undefined,
    },
  },
  setup(prop) {
    const problemDetail: Ref<ProblemDetail | undefined> = ref();
    const editAble = ref(false);
    const refreshing = ref(false);
    const saving = ref(false);
    const fetchingFinishState: {
      languageList?: boolean,
      problemDetail?: boolean,
    } = reactive({});
    const problemFetchError = ref('');
    const instance = getCurrentInstance()!;
    const user = getGlobalUser(instance.appContext);
    const controller = new AbortController();
    const problemDetailApi = api.withConfig({ signal: controller.signal });
    const route = useRoute();
    const languageIds = ref(Array<string>());
    const chosenLanguageId = ref('');
    const userCode = ref('');
    const showEdit = ref(false);
    const showManager = ref(false);

    const managerRef = ref(null);

    function switchLanguageConfig() {
      showEdit.value = false;
      showManager.value = true;
      (managerRef.value as any).switchToLanguageConfig();
    }

    function switchContent() {
      showManager.value = false;
      showEdit.value = true;
    }

    function switchRunConfig() {
      showEdit.value = false;
      showManager.value = true;
      (managerRef.value as any).switchToRunConfig();
    }

    const watchHandler = watch(() => route.fullPath, () => {
      if (route.name === 'ProblemDetail') {
        console.log('by watch');
        const id = route.params.id;
        let problemId;
        if (typeof id === 'string') {
          problemId = id;
        } else {
          problemId = id[0];
        }
        fetchProblem(problemId);
      }
    });

    onBeforeMount(() => {
      if (route.name === 'ProblemDetail') {
        console.log('by mount');
        const id = route.params.id;
        let problemId;
        if (typeof id === 'string') {
          problemId = id;
        } else {
          problemId = id[0];
        }
        fetchProblem(problemId);
      }
      fetchLanguages();
    });

    function highlighterEditorContent(code: string) {
      const { language, name } = getLanguage(chosenLanguageId.value);
      return highlight(code, language, name);
    }

    function fetchLanguages() {
      fetchingFinishState.languageList = false;
      problemDetailApi.languages({
        ignoreError: true,
      }).then(res => {
        fetchingFinishState.languageList = true;
        languageIds.value = res || [];
        if (problemDetail.value && chosenLanguageId.value.length === 0) {
          for (const config of problemDetail.value!.config) {
            const chosen = res.find((it) => config.languageId === it);
            if (chosen) {
              chosenLanguageId.value = chosen;
              break;
            }
          }
        }
      }).catch(() => {
        setTimeout(() => {
          fetchLanguages();
        }, 100);
      });
    }

    onBeforeUnmount(() => {
      watchHandler();
      controller.abort();
    });

    function fetchProblem(problemId?: string, force = false) {
      if (problemId === undefined) {
        if (route.name === 'ProblemDetail') {
          const id = route.params.id;
          if (typeof id === 'string') {
            problemId = id;
          } else {
            problemId = id[0];
          }
        } else {
          return;
        }
      }
      if (!force) {
        if (fetchingFinishState.problemDetail !== undefined) {
          return;
        }
        fetchingFinishState.problemDetail = false;
      }
      editAble.value = false;
      refreshing.value = true;
      problemDetailApi.problem(problemId, {
        ignoreError: true,
      }).then((data) => {
        editAble.value = true;
        fetchingFinishState.problemDetail = true;
        problemDetail.value = data;
        problemFetchError.value = '';
        if (languageIds.value.length > 0 && chosenLanguageId.value.length === 0) {
          for (const config of data.config) {
            const chosen = languageIds.value.find((it) => config.languageId === it);
            if (chosen) {
              chosenLanguageId.value = chosen;
              break;
            }
          }
        }
      }).catch((err) => {
        if (err instanceof Error) {
          if (err instanceof ProblemDetailError) {
            problemDetail.value = err.problem;
            if (err.message === 'content is not json') {
              editAble.value = true;
              problemFetchError.value = err.message;
            } else {
              editAble.value = false;
            }
          } else {
            problemFetchError.value = err.message;
          }
        } else {
          setTimeout(() => {
            fetchProblem(problemId, true);
          }, 100);
        }
      }).finally(() => {
        refreshing.value = false;
      });
    }

    function submitCode() {
      if (!user.user) {
        ElMessage.error('请先登录');
        return;
      }
      if (userCode.value.length === 0) {
        ElMessage({
          message: '请输入代码',
          type: 'error',
        });
        return;
      }
      console.log(userCode);
      problemDetailApi.submit({
        competitionId: prop.competitionId,
        code: userCode.value,
        problemId: problemDetail.value!.id,
        languageId: chosenLanguageId.value,
      }).then(() => {
        ElMessage({
          message: '提交成功',
          type: 'success',
        });
        userCode.value = '';
      });
    }

    return {
      submitCode,
      showEdit,
      showManager,
      userCode,
      languageIds,
      highlighterNothing(code: string) {
        return highlight(code, Prism.languages.textile, 'textile');
      },
      highlighterEditorContent,
      editAble,
      Edit,
      saving,
      fetchingFinishState,
      user,
      refreshing,
      problemFetchError,
      problemDetail,
      fetchProblem,
      chosenLanguageId,
      switchContent,
      switchRunConfig,
      switchLanguageConfig,
      managerRef,
    };
  },
});
</script>


<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.core-editor {
  min-height: 400px;
}

.el-drawer__body {
  padding-top: 0;
  padding-bottom: 0;
}

.box-card :deep(.content-label) {
  color: deepskyblue !important;
  font-weight: bold !important;;
  left: 20px !important;;
  font-size: 20px !important;;
}

.my-editor {
  background-color: #f8f8f9;
  font-family: Fira code, Fira Mono, Consolas, Menlo, Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  padding: 5px;
}

.my-editor :deep(.prism-editor__textarea):focus {
  outline: none;
}

</style>

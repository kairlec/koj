<template>
  <el-drawer v-model='drawerShow' direction='rtl' size='100%' :before-close='handleDrawerClose'>
    <template #title>
      <el-input v-if='showMode==="Raw"||showMode==="Detail"' v-model='problemNameEdit'>
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
              v-if='showMode==="Raw"||showMode==="Detail"' type='primary'
              style='margin-left: auto' @click='flushDetail() && (showMode = showMode==="Raw"?"Detail":"Raw")'>
              切换{{ showMode === 'Raw' ? '表格视图' : '原始视图' }}
            </el-button>
          </div>
        </template>
        <prism-editor
          v-if='showMode==="Raw"' v-model='problemContentEdit' class='my-editor' :highlight='highlighterJson'
          line-numbers></prism-editor>
        <!--        <el-input-->
        <!--          v-if='showMode==="Raw"'-->
        <!--          v-model='problemContentEdit'-->
        <!--          :disabled='saving'-->
        <!--          type='textarea'-->
        <!--          :autosize='{ minRows: 6, maxRows:15 }'-->
        <!--        />-->
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
                  <el-input v-model='problemContentObjEdit.samples[idx].input' type='textarea' :autosize='true' />
                </el-form-item>
                <el-form-item :label='`样例${idx+1}输出`'>
                  <el-input v-model='problemContentObjEdit.samples[idx].output' type='textarea' :autosize='true' />
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
        <template v-if='showMode==="Run"'>

        </template>
        <template v-if='showMode==="Config"'>

        </template>
      </el-card>
    </template>
    <template #footer>
      <div v-if='showMode==="Raw" || showMode==="Detail"' v-loading='fetchingTagList' style='text-align: center'>
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
                   <el-button :loading='refreshing' @click='drawerShow = true'>编辑内容</el-button>
                 </template>
               </template>
             </el-result>
           </template>
           <el-card v-else v-loading='!problemDetailFetchFinished' class='box-card'>
             <template #header>
               <div class='card-header'>
                 <span>{{ `${problemDetail?.id ?? '请求中'} : ${problemDetail?.name ?? '请求中'}` }}</span>
                 <template v-if='user.user?.type===0'>
                   <el-button
                     type='primary' :icon='Edit'
                     style='margin-left: auto'
                     @click='(showMode!=="Raw"&&showMode!=="Detail")?(showMode = "Raw"):(""); drawerShow = true'>编辑内容
                   </el-button>
                   <el-button
                     type='primary' :icon='Edit'
                     @click='showMode = "Run" ; drawerShow = true'>编辑运行
                   </el-button>
                   <el-button
                     type='primary' :icon='Edit'
                     @click='showMode = "Config" ; drawerShow = true'>编辑配置
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
               <el-descriptions-item label='题目内容' label-class-name='content-label'>{{ problemDetail?.contentObj?.description }}
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
                 <span>{{ `${problemDetail?.id ?? '请求中'} : ${problemDetail?.name ?? '请求中'}` }}</span>
               </div>
             </template>
             <prism-editor
               v-model='userCode' class='my-editor core-editor' style='background-color: #f8f8f9' line-numbers
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
import { ProblemContent, ProblemDetail } from '~/apiDeclaration';
import { useRoute } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';
import { CirclePlus, Delete, Edit } from '@element-plus/icons-vue';
import { PrismEditor } from 'vue-prism-editor';
import Prism, { highlight } from 'prismjs';
import 'vue-prism-editor/dist/prismeditor.min.css'; // import the styles somewhere
// import highlighting library (you can use any library you want just return html string)
// import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-kotlin';
import 'prismjs/components/prism-textile';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-json';
import 'prismjs/themes/prism.css'; // import syntax highlighting styles


interface Option {
  key: number;
  label: string;
  disabled: boolean;
}

type ShowMode = 'Raw' | 'Detail' | 'Run' | 'Config'


export default defineComponent({
  name: 'ProblemDetail',
  components: {
    PrismEditor,
  },
  setup() {
    const problemDetail: Ref<ProblemDetail | undefined> = ref();
    const problemNameEdit = ref('');
    const problemContentEdit = ref('');
    const editAble = ref(false);
    const drawerShow = ref(false);
    const problemDetailFetchFinished: Ref<boolean | undefined> = ref();
    const refreshing = ref(false);
    const saving = ref(false);
    const fetchingTagList = ref(false);
    const problemFetchError = ref('');
    const instance = getCurrentInstance()!;
    const user = getGlobalUser(instance.appContext);
    const controller = new AbortController();
    const tagData = ref(Array<Option>());
    const rightValue = ref(Array<number>());
    const problemDetailApi = api.withConfig({ signal: controller.signal });
    const route = useRoute();
    const showMode: Ref<ShowMode> = ref('Raw');
    const languageIds = ref(Array<string>());
    const userCode = ref('')

    const problemContentObjEdit: ProblemContent = reactive({
      description: '',
      input: '',
      output: '',
      samples: [],
      hint: '',
    });

    function flushDetail(): boolean {
      if (showMode.value === 'Raw') {
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

    const watchHandler = watch(() => route.fullPath, () => {
      if (route.name === 'ProblemDetail') {
        console.log('by watch');
        const id = route.params.id;
        let problemId;
        if (typeof id === 'string') {
          problemId = parseInt(id);
        } else {
          problemId = parseInt(id[0]);
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
          problemId = parseInt(id);
        } else {
          problemId = parseInt(id[0]);
        }
        fetchProblem(problemId);
      }
      fetchTags();
    });

    function highlighterEditorContent(code: string) {
      // todo highligh core editor
      return highlight(code, Prism.languages.textile, 'textile');
    }

    function fetchTags() {
      fetchingTagList.value = true;
      problemDetailApi.tags({
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
        fetchingTagList.value = false;
        if (!problemDetail.value?.tags) {
          rightValue.value = [];
        } else {
          rightValue.value = tagData.value.filter(tag => {
            return problemDetail.value!.tags.includes(tag.label);
          }).map(tag => tag.key);
        }
      }).catch(() => {
        setTimeout(() => {
          fetchTags();
        }, 100);
      });
    }

    onBeforeUnmount(() => {
      watchHandler();
      controller.abort();
    });

    function fetchProblem(problemId?: number, force = false) {
      if (problemId === undefined) {
        if (route.name === 'ProblemDetail') {
          const id = route.params.id;
          if (typeof id === 'string') {
            problemId = parseInt(id);
          } else {
            problemId = parseInt(id[0]);
          }
        } else {
          return;
        }
      }
      console.log(force);
      if (!force) {
        if (problemDetailFetchFinished.value !== undefined) {
          return;
        }
        problemDetailFetchFinished.value = false;
      }
      editAble.value = false;
      refreshing.value = true;
      problemDetailApi.problem(problemId, {
        ignoreError: true,
      }).then((data) => {
        editAble.value = true;
        problemDetailFetchFinished.value = true;
        problemDetail.value = data;
        problemNameEdit.value = data.name;
        problemFetchError.value = '';
        if (data.contentObj) {
          try {
            problemContentEdit.value = JSON.stringify(data.contentObj, null, 2);
          } catch (e) {
            problemContentEdit.value = data.content;
          }
        } else {
          problemContentEdit.value = data.content;
        }
        rightValue.value = tagData.value.filter(tag => {
          return data.tags.includes(tag.label);
        }).map(tag => tag.key);
      }).catch((err) => {
        if (err instanceof Error) {
          if (err instanceof ProblemDetailError) {
            problemDetail.value = err.problem;
            problemNameEdit.value = err.problem.name;
            if (err.problem.contentObj) {
              try {
                problemContentEdit.value = JSON.stringify(err.problem.contentObj, null, 2);
              } catch (e) {
                problemContentEdit.value = err.problem.content;
              }
            } else {
              problemContentEdit.value = err.problem.content;
            }
            rightValue.value = tagData.value.filter(tag => {
              return err.problem.tags.includes(tag.label);
            }).map(tag => tag.key);
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

    function resetClick() {
      ElMessageBox.confirm('确定重置初始内容?')
        .then(() => {
          if (problemDetail.value?.contentObj) {
            try {
              problemContentEdit.value = JSON.stringify(problemDetail.value!.contentObj, null, 2);
            } catch (e) {
              problemContentEdit.value = problemDetail.value?.content ?? '';
            }
          } else {
            problemContentEdit.value = problemDetail.value?.content ?? '';
          }
          problemNameEdit.value = problemDetail.value?.name ?? '';
          if (!problemDetail.value?.tags) {
            rightValue.value = [];
          } else {
            rightValue.value = tagData.value.filter(tag => {
              return problemDetail.value!.tags.includes(tag.label);
            }).map(tag => tag.key);
          }
        });
    }

    function saveProblemDetail(): Promise<any> {
      let obj: ProblemContent;
      try {
        if (showMode.value === 'Detail') {
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
      return problemDetailApi.updateProblem(problemDetail.value!.id, {
        name: problemNameEdit.value,
        content: realContent,
        tags: rightValue.value,
      }).then(() => {
        ElMessage({
          type: 'success',
          message: '保存成功',
        });
        problemDetail.value!.tags = tagData.value.filter(tag => {
          return rightValue.value.includes(tag.key);
        }).map(tag => tag.label);
        problemDetail.value!.content = realContent;
        problemDetail.value!.name = problemNameEdit.value;
        problemDetail.value!.contentObj = obj;
        drawerShow.value = false;
      }).finally(() => {
        saving.value = false;
      });
    }

    function saveRunConfig(): Promise<any> {
      let obj;
      try {
        obj = JSON.parse(problemContentEdit.value);
      } catch (e) {
        console.log(e);
        ElMessageBox.alert('内容不是合法的json', {
          type: 'error',
        });
        return Promise.reject(e);
      }
      saving.value = true;
      return problemDetailApi.updateProblem(problemDetail.value!.id, {
        name: problemNameEdit.value,
        content: JSON.stringify(obj),
        tags: rightValue.value,
      }).then(() => {
        ElMessage({
          type: 'success',
          message: '保存成功',
        });
        problemDetail.value!.tags = tagData.value.filter(tag => {
          return rightValue.value.includes(tag.key);
        }).map(tag => tag.label);
        problemDetail.value!.content = problemContentEdit.value;
        problemDetail.value!.name = problemNameEdit.value;
        problemDetail.value!.contentObj = JSON.parse(problemContentEdit.value);
        drawerShow.value = false;
      }).finally(() => {
        saving.value = false;
      });
    }

    function confirmClick(): Promise<any> {
      switch (showMode.value) {
        case 'Detail':
        case 'Raw':
          return saveProblemDetail();
        case 'Run':
          break;
        case 'Config':
          break;
      }
      return Promise.reject('not support');
    }

    return {
      userCode,
      languageIds,
      highlighterJson(code: string) {
        return highlight(code, Prism.languages.json, 'json');
      },
      highlighterNothing(code: string) {
        return highlight(code, Prism.languages.textile, 'textile');
      },
      highlighterEditorContent,
      handleDrawerClose,
      Delete,
      CirclePlus,
      problemContentObjEdit,
      flushDetail,
      showMode,
      problemNameEdit,
      Edit,
      saving,
      fetchingTagList,
      tagData,
      rightValue,
      user,
      editAble,
      confirmClick,
      resetClick,
      drawerShow,
      problemContentEdit,
      refreshing,
      problemFetchError,
      problemDetail,
      problemDetailFetchFinished,
      fetchProblem,
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

/*.el-main >>> .el-scrollbar__bar {*/
/*  display: none;*/
/*}*/
.core-editor{
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
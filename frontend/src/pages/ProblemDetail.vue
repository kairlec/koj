<template>
  <el-drawer v-model='drawerShow' direction='rtl' size='100%'>
    <template #title>
      <el-input v-model='problemNameEdit'>
        <template #prepend>{{ `标题: ${problemDetail?.id} - ` }}</template>
      </el-input>
    </template>
    <template #default>
        <el-card class='box-card' style='padding: 5px;margin: 0'>
          <template #header>
            <div >
              <span>题目内容</span>
              <el-button type='text'>切换视图</el-button>
            </div>
          </template>
            <el-input
              v-model='problemContentEdit'
              :disabled='saving'
              type='textarea'
              :autosize='{ minRows: 6, maxRows:15 }'
            />
        </el-card>
    </template>
    <template #footer>
      <div v-loading='fetchingTagList' style='text-align: center'>
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
          @change='handleChange'
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
          <el-button type='primary' :icon='Edit' @click='drawerShow = true' />
        </template>
      </div>
    </template>

  </el-card>
</template>

<script lang='ts'>
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, Ref, ref, watch } from 'vue';
import api, { ProblemDetail, ProblemDetailError } from '~/api';
import { useRoute } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';
import { Edit } from '@element-plus/icons-vue';

interface Option {
  key: number;
  label: string;
  disabled: boolean;
}

export default defineComponent({
  name: 'ProblemDetail',
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

    function confirmClick() {
      try {
        JSON.parse(problemContentEdit.value);
      } catch (e) {
        console.log(e);
        ElMessageBox.alert('内容不是合法的json', {
          type: 'error',
        });
        return;
      }
      saving.value = true;
      problemDetailApi.updateProblem(problemDetail.value!.id, {
        name: problemNameEdit.value,
        content: problemContentEdit.value,
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

    return {
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
      handleChange(value: number | string,
                   direction: 'left' | 'right',
                   movedKeys: string[] | number[]) {
        console.log(value, direction, movedKeys);
        console.log(rightValue);
        console.log(tagData);
      },
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

.box-card {
  width: 100%;
}

</style>

<style>
.el-drawer__body {
  padding-top: 0;
  padding-bottom: 0;
}
</style>
<template>
  <template v-if='problemFetchError.length'>
    <el-result
      icon='error'
      title='请求出错'
      :sub-title='problemFetchError'
      class='box-card'
    >
      <template #extra>
        <el-button type='primary' :loading='refreshing' @click='fetchProblem(undefined,true)'>刷新</el-button>
      </template>
    </el-result>
  </template>
  <el-card v-else v-loading='!problemDetailFetchFinished' class='box-card'>
    <template #header>
      <div class='card-header'>
        <span>{{ `${problemDetail?.id ?? '请求中'} : ${problemDetail?.name ?? '请求中'}` }}</span>
      </div>
    </template>

  </el-card>
</template>

<script lang='ts'>
import { defineComponent, onBeforeMount, onBeforeUnmount, Ref, ref, watch } from 'vue';
import api, { ProblemDetail } from '~/api';
import { useRoute } from 'vue-router';

export default defineComponent({
  name: 'ProblemDetail',
  setup() {
    const problemDetail: Ref<ProblemDetail | undefined> = ref();
    const problemDetailFetchFinished: Ref<boolean | undefined> = ref();
    const refreshing = ref(false);
    const problemFetchError = ref('');
    const controller = new AbortController();
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
    });

    onBeforeUnmount(() => {
      watchHandler()
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
      refreshing.value = true;
      problemDetailApi.problem(problemId, {
        ignoreError: true,
      }).then((data) => {
        problemDetailFetchFinished.value = true;
        problemDetail.value = data;
      }).catch((err) => {
        if (err instanceof Error) {
          if (err.message === 'content is not json') {
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

    return {
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

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.box-card {
  width: 100%;
}
</style>
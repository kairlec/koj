<template>
  <el-container class='problem-container'>
    <el-header style='text-align: right; font-size: 12px'>
      <div class='toolbar'>
        <p>这里随便写点什么吧</p>
        <el-button-group>
          <el-button
            type='primary' :loading='fetchingProblemList' :icon='ArrowLeft' :disabled='!havePrev'
            @click='fetchProblemList("Prev")'>
            {{ havePrev ? '上一页' : '没有更多了' }}
          </el-button>
          <el-button
            type='primary' :loading='fetchingProblemList' :disabled='!haveNext'
            @click='fetchProblemList("Next")'>
            {{ haveNext ? '下一页' : '没有更多了' }}
            <el-icon class='el-icon--right'>
              <ArrowRight />
            </el-icon>
          </el-button>
        </el-button-group>
        <el-button :loading='fetchingProblemList' :icon='RefreshRight' @click='fetchProblemList("Refresh")'>
          {{ fetchingProblemList ? '请求中' : '刷新' }}
        </el-button>
      </div>
    </el-header>

    <el-main style='padding: 0'>
      <el-container style='max-height: 100%'>
        <el-main style='padding: 0'>
          <el-scrollbar>
            <template v-if='problemList'>
              <p v-for='item in problemList.record' :key='item.id' style='cursor: pointer' class='problem-item'>
                {{ item.name }}</p>
              <p v-for='item in 20' :key='item' style='cursor: pointer' class='problem-item'>
                这里是模拟数据</p>
            </template>
          </el-scrollbar>
        </el-main>
        <el-aside width='35%'>
          <el-scrollbar>
            <p>这里应该放标签</p>
          </el-scrollbar>
        </el-aside>
      </el-container>

    </el-main>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api, { ListCondition, PageData, SimpleProblem } from '~/api';
import { ArrowLeft, ArrowRight, RefreshRight } from '@element-plus/icons-vue';

export default defineComponent({
  components: {
    ArrowRight
  },
  setup() {
    const controller = new AbortController();
    const problemApi = api.withConfig({ signal: controller.signal });
    let lastFetchCondition: ListCondition | undefined;
    const problemList: Ref<PageData<SimpleProblem> | undefined> = ref();
    const fetchingProblemList = ref(false);
    const havePrev = ref(false);
    const haveNext = ref(false);
    const tagFilter: Ref<string[]> = ref([]);


    type SortMode = 'Asc' | 'Desc';
    type FetchMode = 'Refresh' | 'Next' | 'Prev';

    let currentSortMode: SortMode = 'Desc';
    let searchText = '';

    onBeforeUnmount(() => {
      controller.abort();
    });


    const pageLimit = 13;

    function buildListCondition(fetchMode: FetchMode): ListCondition {
      if (fetchMode == 'Refresh' && lastFetchCondition) {
        return lastFetchCondition;
      }
      const res = {
        limit: pageLimit,
        sort: {
          id: fetchMode != 'Prev' ? currentSortMode : (currentSortMode == 'Asc' ? 'Desc' : 'Asc')
        }
      } as ListCondition;
      if (searchText) {
        res.search = {
          name: {
            mode: 'Fuzzy',
            value: searchText
          }
        };
      }
      if (problemList.value?.record?.length) {
        if (fetchMode === 'Next') {
          res.seek = [problemList.value.record[problemList.value.record.length - 1].id];
        } else if (fetchMode === 'Prev') {
          res.seek = [problemList.value.record[0].id];
        }
      }
      return res;
    }

    onBeforeMount(() => {
      fetchProblemList('Refresh');
    });

    function fetchProblemList(fetchMode: FetchMode) {
      if (fetchMode === 'Refresh') {
        problemList.value = undefined;
      }
      fetchingProblemList.value = true;
      const condition = buildListCondition(fetchMode);
      problemApi.problems(tagFilter.value, condition).then((data) => {
        if (fetchMode === 'Refresh') {
          data.record.push(...Array(11).fill({
            id: 0,
            name: '这里是模拟数据'
          }));
        }
        if (data.record.length) {
          lastFetchCondition = condition;
          if (fetchMode == 'Prev') {
            data.record = data.record.reverse();
          }
          problemList.value = data;
          if (data.record.length < pageLimit) {
            if (fetchMode === 'Next') {
              haveNext.value = false;
            }
            if (fetchMode === 'Prev') {
              havePrev.value = false;
            }
          } else {
            haveNext.value = true;
            havePrev.value = true;
          }
        } else {
          if (fetchMode === 'Next') {
            haveNext.value = false;
          }
          if (fetchMode === 'Prev') {
            havePrev.value = false;
          }
        }
      }).finally(() => {
        fetchingProblemList.value = false;
      });
    }

    return {
      havePrev,
      haveNext,
      ArrowLeft,
      RefreshRight,
      problemList,
      fetchingProblemList,
      fetchProblemList
    };
  }
});
</script>

<style scoped>
.problem-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.problem-container .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.problem-container .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
}

.problem-container .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}
</style>
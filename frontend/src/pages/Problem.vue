<template>
  <el-container class='problem-container'>
    <el-header style='text-align: right; font-size: 12px'>
      <div class='toolbar'>
        <el-row :gutter='20'>
          <el-col :span='6'></el-col>
          <el-col :span='8'>
            <el-input
              v-model='searchText'
              class='w-50 m-2'
              size='large'
              placeholder='输入名称搜索'
              :prefix-icon='Search'
              clearable
              maxlength='20'
              @keyup.enter='searchProblem'
            >
              <template #append>
                <el-button :icon='Search' @click='searchProblem' />
              </template>
            </el-input>
          </el-col>
          <el-col :span='10'>
            <el-button-group>
              <el-button
                type='primary' size='large' :loading='fetchingProblemList' :icon='ArrowLeft' :disabled='!havePrev'
                @click='fetchProblemList("Prev")'>
                {{ havePrev ? '上一页' : '没有更多了' }}
              </el-button>
              <el-button
                type='primary' size='large' :loading='fetchingProblemList' :disabled='!haveNext'
                @click='fetchProblemList("Next")'>
                {{ haveNext ? '下一页' : '没有更多了' }}
                <el-icon class='el-icon--right'>
                  <ArrowRight />
                </el-icon>
              </el-button>
              <el-button
                size='large' :loading='fetchingProblemList' :icon='RefreshRight'
                @click='fetchProblemList("Refresh")'>
                {{ fetchingProblemList ? '请求中' : '刷新' }}
              </el-button>
            </el-button-group>
          </el-col>
        </el-row>
      </div>
    </el-header>

    <el-main style='padding: 0'>
      <el-container style='max-height: 100%'>
        <el-main style='padding: 0'>
          <el-scrollbar>
            <template v-if='problemList'>
              <p v-for='item in problemList.record' :key='item.id' style='cursor: pointer' class='problem-item'>
                {{ item.name }}</p>
            </template>
          </el-scrollbar>
        </el-main>
        <el-aside width='35%'>
          <el-select
            v-model="value1"
            multiple
            placeholder="选择标签筛选"
            style="width: 240px"
          >
          </el-select>
          <el-scrollbar>
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
import { ArrowLeft, ArrowRight, RefreshRight, Search } from '@element-plus/icons-vue';

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
    const tags = ref<string[]>([]);
    const selectedTags = ref<string[]>([]);


    type SortMode = 'Asc' | 'Desc';
    type FetchMode = 'Refresh' | 'Next' | 'Prev';

    let currentSortMode: SortMode = 'Desc';
    const searchText = ref('');

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
      if (searchText.value) {
        res.search = {
          name: {
            mode: 'Fuzzy',
            value: searchText.value
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

    function searchProblem() {
      lastFetchCondition = undefined;
      fetchProblemList('Refresh');
    }

    // function fetch

    function fetchProblemList(fetchMode: FetchMode) {
      if (fetchMode === 'Refresh') {
        problemList.value = undefined;
      }
      fetchingProblemList.value = true;
      const condition = buildListCondition(fetchMode);
      problemApi.problems(tagFilter.value, condition).then((data) => {
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
          if (fetchMode == 'Refresh' && !(condition.seek?.length)) {
            havePrev.value = false;
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
      selectedTags,
      tags,
      searchProblem,
      searchText,
      Search,
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
  align-items: center;
  justify-content: center;
  height: 100%;
}

.toolbar .el-row {
  padding-top: 10px;
}
</style>
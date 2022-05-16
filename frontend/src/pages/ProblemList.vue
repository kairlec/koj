<template>
  <ProblemAddDrawer
    v-if='!fetchingTagList'
    v-model:tag-data='tags'
    v-model:show='showAdd'
    @add-problem='addProblem'
  />
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
          <el-scrollbar v-loading='fetchingProblemList'>
            <template v-if='fetchingProblemListError.length'>
              <el-result
                icon='error'
                title='请求出错'
                :sub-title='fetchingProblemListError'
              >
                <template #extra>
                  <el-button type='primary' @click='fetchProblemList("Refresh")'>刷新</el-button>
                </template>
              </el-result>
            </template>
            <template v-if='problemList'>
              <!--              <p  class='problem-item'>ID : Name</p>-->
              <!--                              <p v-for='item in problemList.record' :key='item.id' style='cursor: pointer' class='problem-item'>-->
              <!--                                {{ `${item.id} : ${item.name}` }}</p>-->
              <el-table
                :data='problemList?.record' :stripe='true' style='width: 100%;margin-top:10px;'
                :row-style='{cursor: "pointer"}' max-height='100%' :border='true' @row-click='detailProblem'>
                <el-table-column prop='id' label='ID' width='180' />
                <el-table-column prop='name' label='题目' />
                <el-table-column prop='type' label='标签'>
                  <template #default='scope'>
                    <el-tag v-for='(item,idx) in scope.row.tags' :key='idx'>{{ item }}</el-tag>
                  </template>
                </el-table-column>
                <template v-if='user.user?.type===0'>
                  <el-table-column prop='type' label='操作'>
                    <template #default='scope'>
                      <el-button type='danger' @click.stop='deleteProblem(scope.row)'>
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </template>
                <template v-if='user.user?.type===0' #append>
                  <tr class='el-table__row el-table__row--striped' style='display: flex;justify-content: center;'>
                    <td class='el-table_1_column_1 el-table__cell' rowspan='1' :colspan='user.user?.type===0?"4":"3"'>
                      <div class='cell'>
                        <el-button type='primary' @click='showAdd = true'>
                          <el-icon style='margin-right: 5px'>
                            <plus></plus>
                          </el-icon>
                          添 加 题 目
                        </el-button>
                      </div>
                    </td>
                  </tr>
                </template>
              </el-table>
            </template>
          </el-scrollbar>
        </el-main>
        <el-aside id='tag-container' width='35%'>
          <el-card v-loading='fetchingTagList' class='box-card'>
            <template #header>
              <div class='card-header'>
                <span>标签列表</span>
              </div>
            </template>
            <el-scrollbar>
              <el-check-tag
                v-for='(item,idx) in tags' :key='item.id' :checked='item.selected'
                :style='item.selected? "border-color: var(--el-color-primary)" : ""'
                @change='tagOnChange(idx)'>
                {{ item.name }}
              </el-check-tag>
            </el-scrollbar>
          </el-card>
        </el-aside>
      </el-container>
    </el-main>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api from '~/api';
import { ListCondition, PageData, SimpleProblem, Tag } from '~/apiDeclaration';
import { ArrowLeft, ArrowRight, Plus, RefreshRight, Search } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';

export default defineComponent({
  name: 'ProblemList',
  components: {
    Plus,
    ArrowRight,
  },
  setup() {
    const controller = new AbortController();
    const problemApi = api.withConfig({ signal: controller.signal });
    let lastFetchCondition: ListCondition | undefined;
    const problemList: Ref<PageData<SimpleProblem> | undefined> = ref();
    const fetchingProblemList = ref(false);
    const fetchingProblemListError = ref('');
    const fetchingTagList = ref(true);
    const havePrev = ref(false);
    const haveNext = ref(false);
    const tags = ref<({ selected?: boolean } & Tag)[]>([]);
    const showAdd = ref(false);
    const instance = getCurrentInstance()!
    const user = getGlobalUser(instance.appContext)

    function tagOnChange(idx: number) {
      tags.value[idx].selected = !tags.value[idx].selected;
    }

    type SortMode = 'Asc' | 'Desc';
    type FetchMode = 'Refresh' | 'Next' | 'Prev';

    let currentSortMode: SortMode = 'Asc';
    const searchText = ref('');

    onBeforeUnmount(() => {
      controller.abort();
    });


    const pageLimit = 4;

    function buildListCondition(fetchMode: FetchMode): ListCondition {
      if (fetchMode == 'Refresh' && lastFetchCondition) {
        return lastFetchCondition;
      }
      const res = {
        limit: pageLimit,
        sort: {
          id: fetchMode != 'Prev' ? currentSortMode : (currentSortMode == 'Asc' ? 'Desc' : 'Asc'),
        },
      } as ListCondition;
      if (searchText.value) {
        res.search = {
          name: {
            mode: 'Fuzzy',
            value: searchText.value,
          },
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

    const router = useRouter();

    function detailProblem(row: SimpleProblem) {
      router.push({ name: 'ProblemDetail', params: { id: row.id } });
    }

    onBeforeMount(() => {
      fetchProblemList('Refresh');
      fetchTags();
    });

    function searchProblem() {
      lastFetchCondition = undefined;
      fetchProblemList('Refresh');
    }

    function fetchTags() {
      problemApi.tags({
        limit: 99999,
      }, {
        ignoreError: true,
      }).then(res => {
        tags.value = res.record;
        fetchingTagList.value = false;
      }).catch(() => {
        setTimeout(() => {
          fetchTags();
        }, 100);
      });
    }

    function fetchProblemList(fetchMode: FetchMode) {
      if (fetchMode === 'Refresh') {
        problemList.value = undefined;
      }
      fetchingProblemList.value = true;
      fetchingProblemListError.value = '';
      const condition = buildListCondition(fetchMode);
      problemApi.problems(tags.value.filter(it => it.selected).map((it) => it.name), condition).then((data) => {
        if (data.record.length) {
          lastFetchCondition = condition;
          problemList.value = data;
          if (data.record.length < pageLimit) {
            if (fetchMode === 'Next') {
              haveNext.value = false;
              havePrev.value = true;
            }
            if (fetchMode === 'Prev') {
              havePrev.value = false;
              haveNext.value = true;
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
      }).catch((err) => {
        if (fetchMode === 'Refresh') {
          fetchingProblemListError.value = err.message;
        }
      }).finally(() => {
        fetchingProblemList.value = false;
      });
    }

    function addProblem(simpleProblem: SimpleProblem) {
      if (problemList.value!.totalCount < pageLimit) {
        if (currentSortMode == 'Asc') {
          problemList.value!.record.unshift(simpleProblem);
        } else {
          problemList.value!.record.push(simpleProblem);
        }
      }else{
        if (currentSortMode == 'Asc') {
          havePrev.value = true;
        } else {
          haveNext.value = true;
        }
      }
    }

    function deleteProblem(simpleProblem:SimpleProblem){
      ElMessageBox.confirm('确定要删除该题目吗？', '警告', {
        confirmButtonText: '删除',
        confirmButtonClass: 'el-button--danger',
        cancelButtonText: '算了',
        type: 'warning'
      }).then(() => {
        problemApi.deleteProblem(simpleProblem.id).then(() => {
          ElMessage.success('删除成功');
          fetchProblemList('Refresh');
        });
      });
    }

    return {
      deleteProblem,
      user,
      addProblem,
      showAdd,
      detailProblem,
      fetchingProblemListError,
      fetchingTagList,
      tags,
      tagOnChange,
      searchProblem,
      searchText,
      Search,
      havePrev,
      haveNext,
      ArrowLeft,
      RefreshRight,
      problemList,
      fetchingProblemList,
      fetchProblemList,
    };
  },
});
</script>

<style scoped>
.problem-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 30px;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.el-table :deep(.cell) {
  display: flex !important;
  justify-content: center;
}

.problem-container .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.problem-container .el-aside {
  color: var(--el-text-color-primary);
}

.problem-container .toolbar {
  align-items: center;
  justify-content: center;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

#tag-container .el-check-tag {
  margin: 5px;
  height: 17px;
  font-size: 18px;
  padding: 13px 19px;
  border: 1px solid black;
}

#tag-container .el-card {
  margin-top: 10px;
}

.toolbar .el-row {
  padding-top: 10px;
}

.el-table__body .el-table__row {
  cursor: pointer;
}

</style>
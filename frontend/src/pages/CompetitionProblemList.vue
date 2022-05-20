<template>
  <el-container class='problem-container'>
    <el-header style='text-align: right; font-size: 12px'>
      <div class='toolbar'>
        <el-row :gutter='20'>
          <el-col :span='14'></el-col>
          <el-col :span='10'>
            <el-button-group>
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
              <el-table
                :data='problemList?.record' :stripe='true' style='width: 100%;margin-top:10px;'
                :row-style='{cursor: "pointer"}' max-height='100%' :border='true' @row-click='detailProblem'>
                <el-table-column prop='idx' label='ID' width='180' />
                <el-table-column prop='name' label='题目' />
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
      </el-container>
    </el-main>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, Ref, ref, watch } from 'vue';
import api from '~/api';
import { SimpleProblem } from '~/apiDeclaration';
import { Plus, RefreshRight } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';

export default defineComponent({
  name: 'CompetitionProblemList',
  components: {
    Plus,
  },
  setup() {
    const controller = new AbortController();
    const problemApi = api.withConfig({ signal: controller.signal });
    const problemList: Ref<SimpleProblem[] | undefined> = ref();
    const fetchingProblemList = ref(false);
    const fetchingProblemListError = ref('');
    const instance = getCurrentInstance()!;
    const user = getGlobalUser(instance.appContext);

    let competitionId = '';

    const router = useRouter();
    const route = useRoute();
    const watchHandler = watch(() => route.fullPath, () => {
      if (route.name === 'CompetitionProblemDetail') {
        const id = route.params.id;
        if (typeof id === 'string') {
          competitionId = id;
        } else {
          competitionId = id[0];
        }
        fetchProblemList();
      }
    });

    onBeforeMount(() => {
      if (route.name === 'CompetitionProblemDetail') {
        const id = route.params.id;
        if (typeof id === 'string') {
          competitionId = id;
        } else {
          competitionId = id[0];
        }
        fetchProblemList();
      }
    });

    onBeforeUnmount(() => {
      watchHandler();
      controller.abort();
    });

    function detailProblem(row: SimpleProblem) {
      router.push({ name: 'ProblemDetail', params: { id: row.id } });
    }

    onBeforeMount(() => {
      fetchProblemList();
    });

    function fetchProblemList() {
      problemList.value = undefined;
      fetchingProblemList.value = true;
      fetchingProblemListError.value = '';
      problemApi.competitionProblems(competitionId).then((data) => {
        problemList.value = data;
      }).catch((err) => {
        fetchingProblemListError.value = err.message;
      }).finally(() => {
        fetchingProblemList.value = false;
      });
    }

    function addProblem(simpleProblem: SimpleProblem) {
      problemList.value!.push(simpleProblem);
    }

    function deleteProblem(simpleProblem: SimpleProblem) {
      ElMessageBox.confirm('确定要删除该题目吗？', '警告', {
        confirmButtonText: '删除',
        confirmButtonClass: 'el-button--danger',
        cancelButtonText: '算了',
        type: 'warning',
      }).then(() => {
        problemApi.deleteCompetitionProblem(competitionId,simpleProblem.id).then(() => {
          ElMessage.success('删除成功');
          fetchProblemList();
        });
      });
    }

    return {
      deleteProblem,
      user,
      addProblem,
      detailProblem,
      fetchingProblemListError,
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
<template>
  <CompetitionDialog
    v-model:show='showDialog'
    v-model:base-instance='model'
    @add-competition='addCompetition'
  />
  <el-container class='competition-container'>
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
              @keyup.enter='searchCompetition'
            >
              <template #append>
                <el-button :icon='Search' @click='searchCompetition' />
              </template>
            </el-input>
          </el-col>
          <el-col :span='10'>
            <el-button-group>
              <el-button
                type='primary' size='large' :loading='fetchingCompetitionList' :icon='ArrowLeft'
                :disabled='!havePrev'
                @click='fetchCompetitionList("Prev")'>
                {{ havePrev ? '上一页' : '没有更多了' }}
              </el-button>
              <el-button
                type='primary' size='large' :loading='fetchingCompetitionList' :disabled='!haveNext'
                @click='fetchCompetitionList("Next")'>
                {{ haveNext ? '下一页' : '没有更多了' }}
                <el-icon class='el-icon--right'>
                  <ArrowRight />
                </el-icon>
              </el-button>
              <el-button
                size='large' :loading='fetchingCompetitionList' :icon='RefreshRight'
                @click='fetchCompetitionList("Refresh")'>
                {{ fetchingCompetitionList ? '请求中' : '刷新' }}
              </el-button>
            </el-button-group>
          </el-col>
        </el-row>
      </div>
    </el-header>

    <el-main style='padding: 0'>
      <el-container style='max-height: 100%'>
        <el-main style='padding: 0'>
          <el-scrollbar v-loading='fetchingCompetitionList'>
            <template v-if='fetchingCompetitionListError.length'>
              <el-result
                icon='error'
                title='请求出错'
                :sub-title='fetchingCompetitionListError'
              >
                <template #extra>
                  <el-button type='primary' @click='fetchCompetitionList("Refresh")'>刷新</el-button>
                </template>
              </el-result>
            </template>
            <template v-if='competitionList?.record'>
              <el-table
                :data='competitionList?.record' style='width: 100%;margin-top:10px;'
                :row-style='{cursor: "pointer"}' max-height='100%' :border='true'
                :row-class-name='tableRowClassName'
                @row-click='detailCompetition'>
                <el-table-column prop='id' label='ID' width='180'>
                  <template #default='scope'>
                    <Lock v-if='scope.row.pwd!==undefined && scope.row.pwd!==null' />
                    <Unlock v-else />
                    <el-badge v-if='scope.row.joined' is-dot>
                      <span>{{ scope.row.id }}</span>
                    </el-badge>
                    <template v-else>
                      <span>{{ scope.row.id }}</span>
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop='name' label='名称' />
                <el-table-column prop='startTime' label='时间'>
                  <template #default='scope'>
                    <span>{{ `${scope.row.startTime.format('YYYY/MM/DD HH:mm:ss')} - ${scope.row.endTime.format('YYYY/MM/DD HH:mm:ss')}`
                      }}</span>
                  </template>
                </el-table-column>
                <template v-if='user.user?.type===0'>
                  <el-table-column prop='type' label='操作'>
                    <template #default='scope'>
                      <el-button
                        v-if='competitionNotStart(scope.row)' type='primary'
                        @click.stop='showEdit(scope.row)'>
                        更新
                      </el-button>
                      <el-button type='danger' @click.stop='deleteCompetition(scope.row)'>
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </template>
                <template v-if='user.user?.type===0' #append>
                  <tr
                    class='el-table__row el-table__row--striped'
                    style='display: flex;justify-content: center;'>
                    <td
                      class='el-table_1_column_1 el-table__cell' rowspan='1'
                      :colspan='user.user?.type===0?"4":"3"'>
                      <div class='cell'>
                        <el-button type='primary' @click='showAdd'>
                          <el-icon style='margin-right: 5px'>
                            <plus></plus>
                          </el-icon>
                          创 建 比 赛
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
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api from '~/api';
import {
  getCompetitionState,
  ListCondition,
  ManageCompetition,
  PageData,
  SimpleCompetition,
  UserType,
} from '~/apiDeclaration';
import { ArrowLeft, ArrowRight, Lock, Plus, RefreshRight, Search, Unlock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';
import CompetitionDialog from '../components/CompetitionDialog.vue';
import moment from 'moment';

export default defineComponent({
  name: 'CompetitionList',
  components: {
    Unlock,
    Lock,
    CompetitionDialog,
    Plus,
    ArrowRight,
  },
  setup() {
    const controller = new AbortController();
    const competitionApi = api.withConfig({ signal: controller.signal });
    let lastFetchCondition: ListCondition | undefined;
    const competitionList: Ref<PageData<SimpleCompetition> | undefined> = ref();
    const fetchingCompetitionList = ref(false);
    const fetchingCompetitionListError = ref('');
    const havePrev = ref(false);
    const haveNext = ref(false);
    const showDialog = ref(false);
    const instance = getCurrentInstance()!;
    const user = getGlobalUser(instance.appContext);
    const model = ref({
      name: '',
      pwd: '',
      startTime: moment(),
      endTime: moment(),
    } as unknown as ManageCompetition);

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
      if (competitionList.value?.record?.length) {
        if (fetchMode === 'Next') {
          res.seek = [competitionList.value.record[competitionList.value.record.length - 1].id];
        } else if (fetchMode === 'Prev') {
          res.seek = [competitionList.value.record[0].id];
        }
      }
      return res;
    }

    const router = useRouter();

    function detailCompetition(row: SimpleCompetition) {
      if (!user.user) {
        ElMessage.error('请先登录');
        return;
      }
      if (!row.joined && user.user?.type !== UserType.ADMIN) {
        if (row.pwd !== undefined && row.pwd !== null) {
          ElMessageBox.prompt('请输入密码', '加入比赛', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确定',
            beforeClose: (action, instance, done) => {
              if (action === 'confirm') {
                instance.confirmButtonLoading = true;
                instance.confirmButtonText = '提交中...';
                console.log(instance.inputValue);
                competitionApi.joinCompetition(row.id, instance.inputValue).then(() => {
                  fetchCompetitionList('Refresh');
                  done();
                }).finally(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '确定';
                });
              } else {
                done();
              }
            },
          });
          return;
        } else {
          ElMessageBox.confirm('还未加入比赛,要加入比赛吗？', '加入比赛', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            beforeClose: (action, instance, done) => {
              if (action === 'confirm') {
                instance.confirmButtonLoading = true;
                instance.confirmButtonText = '提交中...';
                competitionApi.joinCompetition(row.id).then(() => {
                  fetchCompetitionList('Refresh');
                  done();
                }).finally(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '确定';
                });
              } else {
                done();
              }
            },
          });
          return;
        }
      }
      const state = getCompetitionState(row);
      if (user.user?.type !== UserType.ADMIN && state == 'NOT_STARTED') {
        ElMessage({
          message: '该比赛还未开始,请勿着急',
          type: 'info',
        });
        return;
      }
      router.push({ name: 'CompetitionProblemList', params: { id: row.id } });
    }

    onBeforeMount(() => {
      fetchCompetitionList('Refresh');
    });

    function searchCompetition() {
      lastFetchCondition = undefined;
      fetchCompetitionList('Refresh');
    }

    function fetchCompetitionList(fetchMode: FetchMode) {
      if (fetchMode === 'Refresh') {
        competitionList.value = undefined;
      }
      fetchingCompetitionList.value = true;
      fetchingCompetitionListError.value = '';
      const condition = buildListCondition(fetchMode);
      competitionApi.getCompetitionList(condition).then((data) => {
        lastFetchCondition = condition;
        competitionList.value = data;
        if (data.record.length) {
          if (fetchMode == 'Prev') {
            competitionList.value!.record = competitionList.value?.record?.reverse();
          }
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
          fetchingCompetitionListError.value = err.message;
        }
      }).finally(() => {
        fetchingCompetitionList.value = false;
      });
    }

    function addCompetition(simpleCompetition: SimpleCompetition & { new: boolean }) {
      if (simpleCompetition.new) {
        if (competitionList.value!.totalCount < pageLimit) {
          if (currentSortMode == 'Desc') {
            competitionList.value!.record.unshift(simpleCompetition);
          } else {
            competitionList.value!.record.push(simpleCompetition);
          }
        } else {
          if (currentSortMode == 'Desc') {
            havePrev.value = true;
          } else {
            haveNext.value = true;
          }
        }
      } else {
        const idx = competitionList.value?.record.findIndex((item) => item.id == simpleCompetition.id);
        if (idx !== undefined && idx !== null) {
          competitionList.value!.record[idx] = simpleCompetition;
        }
      }
      showDialog.value = false;
    }

    function deleteCompetition(simpleCompetition: SimpleCompetition) {
      ElMessageBox.confirm('确定要删除该比赛吗？', '警告', {
        confirmButtonText: '删除',
        confirmButtonClass: 'el-button--danger',
        cancelButtonText: '算了',
        type: 'warning',
      }).then(() => {
        competitionApi.deleteCompetition(simpleCompetition.id).then(() => {
          ElMessage.success('删除成功');
          fetchCompetitionList('Refresh');
        });
      });
    }

    function tableRowClassName({ row }: { row: SimpleCompetition }) {
      const state = getCompetitionState(row);
      switch (state) {
        case 'STARTED':
          return 'competition-active';
        case 'NOT_STARTED':
          return '';
        case 'ENDED':
          return 'competition-end';
      }
    }

    function competitionNotStart(manageCompetition: ManageCompetition) {
      return manageCompetition.startTime.isSameOrAfter(moment.now());
    }

    function showAdd() {
      model.value = {
        name: '',
        pwd: '',
        startTime: moment(),
        endTime: moment().add(2, 'hour'),
      } as unknown as ManageCompetition;
      showDialog.value = true;
    }

    function showEdit(manageCompetition: ManageCompetition) {
      model.value = manageCompetition;
      showDialog.value = true;
    }

    return {
      model,
      showEdit,
      competitionNotStart,
      tableRowClassName,
      showAdd,
      showDialog,
      deleteCompetition,
      user,
      addCompetition,
      detailCompetition,
      fetchingCompetitionListError,
      searchCompetition,
      searchText,
      Search,
      havePrev,
      haveNext,
      ArrowLeft,
      RefreshRight,
      competitionList,
      fetchingCompetitionList,
      fetchCompetitionList,
    };
  },
});
</script>

<style scoped>
.el-table :deep(.competition-end) {
  --el-table-tr-bg-color: var(--el-color-error-light-7);
}

.el-table :deep(.competition-active) {
  --el-table-tr-bg-color: var(--el-color-success-light-7);
}

.competition-item {
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
  overflow: visible;
}

.competition-container .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.competition-container .el-aside {
  color: var(--el-text-color-primary);
}

.competition-container .toolbar {
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
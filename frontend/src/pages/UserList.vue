<template>
  <el-container class='user-container'>
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
              @keyup.enter='searchUser'
            >
              <template #append>
                <el-button :icon='Search' @click='searchUser' />
              </template>
            </el-input>
          </el-col>
          <el-col :span='10'>
            <el-button-group>
              <el-button
                type='primary' size='large' :loading='fetchingUserList' :icon='ArrowLeft'
                :disabled='!havePrev'
                @click='fetchUserList("Prev")'>
                {{ havePrev ? '上一页' : '没有更多了' }}
              </el-button>
              <el-button
                type='primary' size='large' :loading='fetchingUserList' :disabled='!haveNext'
                @click='fetchUserList("Next")'>
                {{ haveNext ? '下一页' : '没有更多了' }}
                <el-icon class='el-icon--right'>
                  <ArrowRight />
                </el-icon>
              </el-button>
              <el-button
                size='large' :loading='fetchingUserList' :icon='RefreshRight'
                @click='fetchUserList("Refresh")'>
                {{ fetchingUserList ? '请求中' : '刷新' }}
              </el-button>
            </el-button-group>
          </el-col>
        </el-row>
      </div>
    </el-header>

    <el-main style='padding: 0'>
      <el-container style='max-height: 100%'>
        <el-main style='padding: 0'>
          <el-scrollbar v-loading='fetchingUserList'>
            <template v-if='fetchingUserListError.length'>
              <el-result
                icon='error'
                title='请求出错'
                :sub-title='fetchingUserListError'
              >
                <template #extra>
                  <el-button type='primary' @click='fetchUserList("Refresh")'>刷新</el-button>
                </template>
              </el-result>
            </template>
            <template v-if='userList'>
              <el-table
                :data='userList?.record' style='width: 100%;margin-top:10px;'
                max-height='100%' :border='true'>
                <el-table-column prop='id' label='用户ID' width='180' />
                <el-table-column prop='username' label='用户名称' />
                <el-table-column prop='email' label='邮箱' />
                <el-table-column prop='type' label='类型'>
                  <template #default='scope'>
                    <el-tag :type='scope.row.type===0?"danger":"success"'>
                      {{ scope.row.type === 0 ? '管理' : '用户' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop='createTime' label='注册时间'>
                  <template #default='scope'>
                    <span>{{ scope.row.createTime.fromNow() }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop='updateTime' label='信息更新时间'>
                  <template #default='scope'>
                    <span>{{ scope.row.updateTime.fromNow() }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop='blocked' label='账户状态'>
                  <template #default='scope'>
                    <el-switch
                      v-model='scope.row.blockedBoolean'
                      size='large'
                      inline-prompt
                      active-color='#ff4949'
                      inactive-color='#13ce66'
                      active-text='禁用'
                      inactive-text='启用'
                      :loading='scope.row.updating===true'
                      :disabled='scope.row.type===0'
                      :before-change='changeUserStatus(scope.row)'
                    />
                  </template>
                </el-table-column>
              </el-table>
            </template>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-main>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api from '~/api';
import { ListCondition, PageData, UserManageDetail } from '~/apiDeclaration';
import { ArrowLeft, ArrowRight, RefreshRight, Search } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'UserList',
  components: {
    ArrowRight,
  },
  setup() {
    const controller = new AbortController();
    const userApi = api.withConfig({ signal: controller.signal });
    let lastFetchCondition: ListCondition | undefined;
    const userList: Ref<PageData<UserManageDetail> | undefined> = ref();
    const fetchingUserList = ref(false);
    const fetchingUserListError = ref('');
    const havePrev = ref(false);
    const haveNext = ref(false);

    type SortMode = 'Asc' | 'Desc';
    type FetchMode = 'Refresh' | 'Next' | 'Prev';

    let currentSortMode: SortMode = 'Asc';
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
          username: {
            mode: 'Fuzzy',
            value: searchText.value,
          },
        };
      }
      if (userList.value?.record?.length) {
        if (fetchMode === 'Next') {
          res.seek = [userList.value.record[userList.value.record.length - 1].id];
        } else if (fetchMode === 'Prev') {
          res.seek = [userList.value.record[0].id];
        }
      }
      return res;
    }

    const router = useRouter();

    function goToProblem(id: string) {
      router.push({ name: 'ProblemDetail', params: { id } });
    }

    onBeforeMount(() => {
      fetchUserList('Refresh');
    });

    function searchUser() {
      lastFetchCondition = undefined;
      fetchUserList('Refresh');
    }

    function fetchUserList(fetchMode: FetchMode) {
      if (fetchMode === 'Refresh') {
        userList.value = undefined;
      }
      fetchingUserList.value = true;
      fetchingUserListError.value = '';
      const condition = buildListCondition(fetchMode);
      userApi.userList(condition).then((data) => {
        if (data.record.length) {
          (data.record as Array<UserManageDetail & { blockedBoolean: Boolean }>).forEach(it => {
            if (it.blocked === 0) {
              it.blockedBoolean = false;
            } else {
              it.blockedBoolean = true;
            }
          });
          lastFetchCondition = condition;
          userList.value = data;
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
      }).catch((err) => {
        if (fetchMode === 'Refresh') {
          fetchingUserListError.value = err.message;
        }
      }).finally(() => {
        fetchingUserList.value = false;
      });
    }

    let problemDetailController = new AbortController();

    function cancelProblemDetail(done: () => void) {
      problemDetailController.abort();
      problemDetailController = new AbortController();
      done();
    }

    function changeUserStatus(user: UserManageDetail & { updating?: boolean, blockedBoolean: boolean }) {
      return () => {
        user.updating = true;
        const target = !user.blockedBoolean;
        userApi.manageUpdateUser(user.id, { blocked: target }).then((resp) => {
          if (resp.status === 304) {
            user.blockedBoolean = !target;
          } else {
            user.blockedBoolean = target;
          }
        }).catch(() => {
          user.blockedBoolean = !target;
        }).finally(() => {
          user.updating = false;
        });
        return false;
      };
    }

    return {
      changeUserStatus,
      cancelProblemDetail,
      goToProblem,
      fetchingUserListError,
      searchUser,
      searchText,
      Search,
      havePrev,
      haveNext,
      ArrowLeft,
      RefreshRight,
      userList,
      fetchingUserList,
      fetchUserList,
    };
  },
});
</script>

<style scoped>
.el-table :deep(.error-user) {
  --el-table-tr-bg-color: var(--el-color-error-light-7);
}

.el-table :deep(.accept-user) {
  --el-table-tr-bg-color: var(--el-color-success-light-7);
}

.el-table :deep(.cell) {
  display: flex !important;
  justify-content: center;
}

.el-table :deep(.myself-user) {
  color: blue;
  text-decoration: underline;
  cursor: pointer;
}

.user-item {
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

.user-container .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.user-container .el-aside {
  color: var(--el-text-color-primary);
}

.user-container .toolbar {
  align-items: center;
  justify-content: center;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar .el-row {
  padding-top: 10px;
}

.el-table__body .el-table__row {
  cursor: pointer;
}

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

.my-editor {
  background-color: #f8f8f9;
  /* you must provide font-family font-size line-height. Example: */
  font-family: Fira code, Fira Mono, Consolas, Menlo, Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  padding: 5px;
}

.my-editor :deep(.prism-editor__textarea):focus {
  outline: none;
}
</style>
<template>
    <el-container class='problem-container'>
        <el-header style='text-align: right; font-size: 12px'>
            <div class='toolbar'>
                <el-row :gutter='20'>
                    <el-col :span='14'>
                        <span style='height: 100%;font-size: 20px;display: flex;align-items: center;'>
                            {{ `${competition?.id ?? ''} : ${competition?.name ?? '请求中'}` }}
                        </span>
                    </el-col>
                    <el-col :span='10'>
                        <el-button-group>
                            <el-button type='primary' size='large' :loading='fetchingProblemList' @click='gotoSubmit'>
                                查看提交
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
                            <el-table
                                :data='problemList' :stripe='true' style='width: 100%;margin-top:10px;'
                                :row-style='{cursor: "pointer"}' max-height='100%' :border='true'
                                @row-click='detailProblem'>
                                <el-table-column prop='idx' label='ID' width='180' />
                                <el-table-column prop='name' label='题目' />
                                <template v-if='(user.user?.type===0) && competitionState==="NOT_STARTED"'>
                                    <el-table-column prop='type' label='操作'>
                                        <template #default='scope'>
                                            <el-button type='danger' @click.stop='deleteProblem(scope.row)'>
                                                删除
                                            </el-button>
                                        </template>
                                    </el-table-column>
                                </template>
                                <template v-if='(user.user?.type===0) && competitionState==="NOT_STARTED"' #append>
                                    <tr
                                        class='el-table__row el-table__row--striped'
                                        style='display: flex;justify-content: center;'>
                                        <td
                                            class='el-table_1_column_1 el-table__cell' rowspan='1'
                                            :colspan='user.user?.type===0?"4":"3"'>
                                            <div class='cell'>
                                                <el-button type='primary' @click='addProblem'>
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
import { CompetitionState, getCompetitionState, ManageCompetition, SimpleProblem, UserType } from '~/apiDeclaration';
import { Plus, RefreshRight } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';

export default defineComponent({
    name: 'CompetitionProblemList',
    components: {
        Plus
    },
    setup() {
        const controller = new AbortController();
        const problemApi = api.withConfig({ signal: controller.signal });
        const problemList: Ref<SimpleProblem[] | undefined> = ref();
        const fetchingProblemList = ref(false);
        const fetchingProblemListError = ref('');
        const instance = getCurrentInstance()!;
        const user = getGlobalUser(instance.appContext);
        const competitionState: Ref<CompetitionState> = ref('ENDED');
        const competition: Ref<ManageCompetition | undefined> = ref();

        let competitionId = '';

        const router = useRouter();
        const route = useRoute();
        const watchHandler = watch(() => route.fullPath, () => {
            if (route.name === 'CompetitionProblemList') {
                const id = route.params.id;
                if (typeof id === 'string') {
                    competitionId = id;
                } else {
                    competitionId = id[0];
                }
                fetchProblemList();
            }
        });
        const userWatchHandler = watch(() => user.user, () => {
            if (route.name === 'CompetitionProblemList') {
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
            if (route.name === 'CompetitionProblemList') {
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
            userWatchHandler();
            controller.abort();
        });

        function detailProblem(row: SimpleProblem) {
            router.push({ name: 'ProblemDetail', params: { id: row.id }, query: { competitionId: competitionId } });
        }

        function fetchCompetitionState() {
            problemApi.getCompetition(competitionId).then(res => {
                competition.value = res;
                competitionState.value = getCompetitionState(res);
            }).catch(() => {
                setTimeout(fetchCompetitionState, 1000);
            });
        }

        function gotoSubmit() {
            router.push({ name: 'SubmitList', query: { competitionId: competitionId } });
        }

        function fetchProblemList() {
            if (user.user?.type == UserType.ADMIN) {
                fetchCompetitionState();
            }
            if (competitionId === '') {
                const id = route.params.id;
                if (typeof id === 'string') {
                    competitionId = id;
                } else {
                    competitionId = id[0];
                }
            }
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

        function addProblem() {
            ElMessageBox.prompt('添加题目', '请输入题目id', {
                inputPattern: /^\d*$/,
                inputErrorMessage: '题目id必须为数字',
                showCancelButton: true,
                beforeClose: (action, instance, done) => {
                    if (action === 'confirm') {
                        instance.confirmButtonLoading = true;
                        instance.confirmButtonText = '提交中...';
                        problemApi.addCompetitionProblem(competitionId, instance.inputValue).then((res) => {
                            if (res.status === 304) {
                                ElMessage({
                                    message: '题目id错误或不存在',
                                    type: 'info',
                                    duration: 3000
                                });
                            } else {
                                fetchProblemList();
                            }
                            done();
                        }).catch((err) => {
                            ElMessage({
                                message: err.message,
                                type: 'error',
                                duration: 3000
                            });
                        }).finally(() => {
                            instance.confirmButtonLoading = false;
                            instance.confirmButtonText = '确定';
                        });
                    } else {
                        done();
                    }
                }
            });
        }

        function deleteProblem(simpleProblem: SimpleProblem) {
            ElMessageBox.confirm('确定要删除该题目吗？', '警告', {
                confirmButtonText: '删除',
                confirmButtonClass: 'el-button--danger',
                cancelButtonText: '算了',
                type: 'warning'
            }).then(() => {
                problemApi.deleteCompetitionProblem(competitionId, simpleProblem.id).then(() => {
                    ElMessage.success('删除成功');
                    fetchProblemList();
                });
            });
        }

        return {
            gotoSubmit,
            competition,
            competitionState,
            deleteProblem,
            user,
            addProblem,
            detailProblem,
            fetchingProblemListError,
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
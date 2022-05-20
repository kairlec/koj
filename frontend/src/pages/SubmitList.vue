<template>
    <el-drawer
        v-model='showDetail' direction='rtl' size='60%'
        :before-close='cancelProblemDetail'>
        <template v-if='submitDetail!==undefined&&submitDetail!==null' #title>
            <h4>{{ `提交: ${submitDetail.id}` }}</h4>
            <el-tag style='margin-left: auto' effect='dark' :type='stateType(submitDetail.state)'>
                状态: {{ submitStateToString(submitDetail.state) }}
            </el-tag>
        </template>
        <template #default>
            <el-card
                v-loading='submitDetail===undefined||submitDetail===null' class='box-card'
                style='padding: 5px;margin: 0'>
                <template v-if='submitDetail!==undefined&&submitDetail!==null' #default>
                    <el-descriptions
                        :column='1'
                        size='large'
                        direction='vertical'
                        style='margin-top: 32px;'
                    >
                        <el-descriptions-item
                            label='题目Id' label-class-name='content-label'>
              <span
                  style='cursor: pointer; color: blue; text-decoration: underline'
                  @click='goToProblem(submitDetail.problemId)'>{{ submitDetail.problemId }}</span>
                        </el-descriptions-item>
                        <el-descriptions-item label='耗时(ms)' label-class-name='content-label'>
                            {{ /\d+/.test(submitDetail.castTime) ? submitDetail.castTime : '--' }}
                        </el-descriptions-item>
                        <el-descriptions-item label='消耗内存' label-class-name='content-label'>
                            {{ /\d+/.test(submitDetail.castMemory) ? submitDetail.castMemory / 1024 : '--' }}
                        </el-descriptions-item>
                        <el-descriptions-item label='语言' label-class-name='content-label'>
                            {{ submitDetail.languageId }}
                        </el-descriptions-item>
                        <el-descriptions-item label='提交时间' label-class-name='content-label'>
                            {{ submitDetail.createTime.format('YYYY-MM-DD HH:mm:ss') }}
                        </el-descriptions-item>
                        <el-descriptions-item label='代码' label-class-name='content-label'>
                            <prism-editor
                                v-model='submitDetail.code' class='my-editor' :highlight='highlighterCodeContent'
                                line-numbers :readonly='true'></prism-editor>
                        </el-descriptions-item>
                        <el-descriptions-item
                            v-if='submitDetail.stderr?.length??0>0' label='错误信息'
                            label-class-name='content-label'>
                            {{ submitDetail.stderr }}
                        </el-descriptions-item>
                    </el-descriptions>
                </template>
            </el-card>
        </template>
    </el-drawer>
    <el-container class='submit-container'>
        <el-header style='text-align: right; font-size: 12px'>
            <div class='toolbar'>
                <el-row :gutter='20'>
                    <el-col :span='6'></el-col>
                    <el-col :span='8'>
                        <el-input
                            v-model='searchText'
                            class='w-50 m-2'
                            size='large'
                            placeholder='输入id搜索'
                            :prefix-icon='Search'
                            clearable
                            maxlength='20'
                            @keyup.enter='searchSubmit'
                        >
                            <template #append>
                                <el-button :icon='Search' @click='searchSubmit' />
                            </template>
                        </el-input>
                    </el-col>
                    <el-col :span='10'>
                        <el-button-group>
                            <el-button
                                type='primary' size='large' :loading='fetchingSubmitList' :icon='ArrowLeft'
                                :disabled='!havePrev'
                                @click='fetchSubmitList("Prev")'>
                                {{ havePrev ? '上一页' : '没有更多了' }}
                            </el-button>
                            <el-button
                                type='primary' size='large' :loading='fetchingSubmitList' :disabled='!haveNext'
                                @click='fetchSubmitList("Next")'>
                                {{ haveNext ? '下一页' : '没有更多了' }}
                                <el-icon class='el-icon--right'>
                                    <ArrowRight />
                                </el-icon>
                            </el-button>
                            <el-button
                                size='large' :loading='fetchingSubmitList' :icon='RefreshRight'
                                @click='fetchSubmitList("Refresh")'>
                                {{ fetchingSubmitList ? '请求中' : '刷新' }}
                            </el-button>
                        </el-button-group>
                    </el-col>
                </el-row>
            </div>
        </el-header>

        <el-main style='padding: 0'>
            <el-container style='max-height: 100%'>
                <el-main style='padding: 0'>
                    <el-scrollbar v-loading='fetchingSubmitList'>
                        <template v-if='fetchingSubmitListError.length'>
                            <el-result
                                icon='error'
                                title='请求出错'
                                :sub-title='fetchingSubmitListError'
                            >
                                <template #extra>
                                    <el-button type='primary' @click='fetchSubmitList("Refresh")'>刷新</el-button>
                                </template>
                            </el-result>
                        </template>
                        <template v-if='submitList'>
                            <el-table
                                :data='submitList?.record' style='width: 100%;margin-top:10px;'
                                max-height='100%' :border='true' :row-class-name='tableRowClassName'>
                                <el-table-column prop='id' label='提交ID' width='180' />
                                <el-table-column prop='problemId' label='题目ID'>
                                    <template #default='scope'>
                    <span
                        style='cursor: pointer; color: blue; text-decoration: underline'
                        @click='goToProblem(scope.row.problemId)'
                    >{{ scope.row.problemId }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop='state' label='状态'>
                                    <template #default='scope'>
                    <span
                        :class='stateClassName(scope.row)'
                        @click='showSubmitDetail(scope.row)'
                    >{{ submitStateToString(scope.row.state) }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop='castMemory' label='消耗内存(KB)'>
                                    <template #default='scope'>
                                        <span>{{ /\d+/.test(scope.row.castMemory) ? scope.row.castMemory / 1024 : '--'
                                            }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop='castTime' label='消耗时间(ms)'>
                                    <template #default='scope'>
                                        <span>{{ /\d+/.test(scope.row.castTime) ? scope.row.castTime : '--' }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop='languageId' label='语言' />
                                <el-table-column prop='username' label='提交人' />
                                <el-table-column prop='createTime' label='提交时间'>
                                    <template #default='scope'>
                                        <span>{{ scope.row.createTime.fromNow() }}</span>
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
import { defineComponent, getCurrentInstance, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api from '~/api';
import { ListCondition, PageData, SimpleSubmit, SubmitDetail, submitStateToString } from '~/apiDeclaration';
import { ArrowLeft, ArrowRight, RefreshRight, Search } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { getGlobalUser } from '~/hooks/globalUser';
import { PrismEditor } from 'vue-prism-editor';
import Prism, { Grammar, highlight } from 'prismjs';
import 'vue-prism-editor/dist/prismeditor.min.css';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-kotlin';
import 'prismjs/components/prism-textile';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-json';
import 'prismjs/themes/prism.css';

function getLanguage(languageId: string): { language: Grammar, name: string } {
    if (/kotlin/i.test(languageId)) {
        return { language: Prism.languages.kotlin, name: 'kotlin' };
    }
    if (/java/i.test(languageId)) {
        return { language: Prism.languages.java, name: 'java' };
    }
    if (/python/i.test(languageId)) {
        return { language: Prism.languages.python, name: 'python' };
    }
    if (/c\+\+/i.test(languageId)) {
        return { language: Prism.languages.cpp, name: 'cpp' };
    }
    if (/c/i.test(languageId)) {
        return { language: Prism.languages.c, name: 'c' };
    }
    return { language: Prism.languages.textile, name: 'textile' };
}


export default defineComponent({
    name: 'SubmitList',
    components: {
        ArrowRight,
        PrismEditor
    },
    setup() {
        const controller = new AbortController();
        const submitApi = api.withConfig({ signal: controller.signal });
        let lastFetchCondition: ListCondition | undefined;
        const instance = getCurrentInstance();
        const user = getGlobalUser(instance!.appContext);
        const submitList: Ref<PageData<SimpleSubmit> | undefined> = ref();
        const fetchingSubmitList = ref(false);
        const fetchingSubmitListError = ref('');
        const havePrev = ref(false);
        const haveNext = ref(false);
        const showDetail = ref(false);
        const showDetailId: Ref<string> = ref('');
        const submitDetail: Ref<SubmitDetail | undefined> = ref();
        const submitDetailMap = new Map<string, SubmitDetail>();

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
                    id: {
                        mode: 'Fuzzy',
                        value: searchText.value
                    }
                };
            }
            if (submitList.value?.record?.length) {
                if (fetchMode === 'Next') {
                    res.seek = [submitList.value.record[submitList.value.record.length - 1].id];
                } else if (fetchMode === 'Prev') {
                    res.seek = [submitList.value.record[0].id];
                }
            }
            return res;
        }

        const router = useRouter();

        function goToProblem(id: string) {
            router.push({ name: 'ProblemDetail', params: { id } });
        }

        onBeforeMount(() => {
            fetchSubmitList('Refresh');
        });

        function searchSubmit() {
            lastFetchCondition = undefined;
            fetchSubmitList('Refresh');
        }

        const route = useRoute();

        function fetchSubmitList(fetchMode: FetchMode) {
            if (fetchMode === 'Refresh') {
                submitList.value = undefined;
            }
            fetchingSubmitList.value = true;
            fetchingSubmitListError.value = '';

            ///
            const competitionIds = route.query['competitionId'];
            let competitionId: string | undefined;
            if (Array.isArray(competitionIds)) {
                competitionId = competitionIds[0]!;
            } else {
                if (competitionIds == null) {
                    competitionId = undefined;
                } else {
                    competitionId = competitionIds;
                }
            }
            ///

            if (competitionId) {
                submitApi.competitionSubmits(competitionId).then((data) => {
                    submitList.value = {
                        totalCount: data.length,
                        record: data
                    };
                    haveNext.value = false;
                    havePrev.value = false;
                }).catch((err) => {
                    if (fetchMode === 'Refresh') {
                        fetchingSubmitListError.value = err.message;
                    }
                }).finally(() => {
                    fetchingSubmitList.value = false;
                });
            } else {
                const condition = buildListCondition(fetchMode);
                submitApi.submits(condition).then((data) => {
                    if (data.record.length) {
                        lastFetchCondition = condition;
                        if (fetchMode == 'Prev') {
                            data.record = data.record.reverse();
                        }
                        submitList.value = data;
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
                        fetchingSubmitListError.value = err.message;
                    }
                }).finally(() => {
                    fetchingSubmitList.value = false;
                });
            }
        }

        function tableRowClassName({ row }: { row: SimpleSubmit }) {
            if (row.state.startsWith('IN')) {
                return '';
            }
            if (row.state === 'ACCEPTED') {
                return 'accept-submit';
            }
            return 'error-submit';
        }

        function stateType(state: string) {
            if (state.startsWith('IN')) {
                return '';
            }
            if (state === 'ACCEPTED') {
                return 'success';
            }
            return 'danger';
        }

        function stateClassName(submit: SimpleSubmit) {
            if (submit.belongUserId == user.user?.id) {
                return 'myself-submit';
            } else {
                return '';
            }
        }

        function showSubmitDetail(submit: SimpleSubmit) {
            if (submit.belongUserId === user.user?.id) {
                getSubmitDetail(submit.id).then((data) => {
                    submitDetail.value = data;
                });
                showDetailId.value = submit.id;
                showDetail.value = true;
            }
        }

        let problemDetailController = new AbortController();

        function cancelProblemDetail(done: () => void) {
            problemDetailController.abort();
            problemDetailController = new AbortController();
            done();
        }

        function getSubmitDetail(id: string): Promise<SubmitDetail> {
            const cacheSubmit = submitDetailMap.get(id);
            if (cacheSubmit !== undefined && cacheSubmit !== null) {
                return Promise.resolve(cacheSubmit);
            }
            return submitApi.submitDetail(id, { signal: problemDetailController.signal }).then((data) => {
                submitDetailMap.set(id, data);
                return data;
            }).catch((err) => {
                if (err?.message !== 'canceled') {
                    getSubmitDetail(id);
                }
                return Promise.reject(err);
            });
        }

        function highlighterCodeContent(code: string) {
            const { language, name } = getLanguage(submitDetail.value!.languageId);
            return highlight(code, language, name);
        }

        return {
            stateType,
            highlighterCodeContent,
            cancelProblemDetail,
            submitDetail,
            showDetail,
            stateClassName,
            goToProblem,
            tableRowClassName,
            submitStateToString,
            showSubmitDetail,
            fetchingSubmitListError,
            searchSubmit,
            searchText,
            Search,
            havePrev,
            haveNext,
            ArrowLeft,
            RefreshRight,
            submitList,
            fetchingSubmitList,
            fetchSubmitList
        };
    }
});
</script>

<style scoped>
.el-table :deep(.error-submit) {
    --el-table-tr-bg-color: var(--el-color-error-light-7);
}

.el-table :deep(.accept-submit) {
    --el-table-tr-bg-color: var(--el-color-success-light-7);
}

.el-table :deep(.cell) {
    display: flex !important;
    justify-content: center;
}

.el-table :deep(.myself-submit) {
    color: blue;
    text-decoration: underline;
    cursor: pointer;
}

.submit-item {
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

.submit-container .el-header {
    position: relative;
    background-color: var(--el-color-primary-light-7);
    color: var(--el-text-color-primary);
}

.submit-container .el-aside {
    color: var(--el-text-color-primary);
}

.submit-container .toolbar {
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
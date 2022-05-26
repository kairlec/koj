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
                    </el-col>
                    <el-col :span='10'>
                        <el-button-group>
                            <el-button
                                size='large' :loading='rankList===undefined' :icon='View'
                                @click='showRank = !showRank'>
                                {{ rankList === undefined ? '请求中' : (showRank ? '查看提交' : '查看排行') }}
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
                        <template v-if='submitList!==undefined && !showRank'>
                            <el-table
                                :data='submitList' style='width: 100%;margin-top:10px;'
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
                        <template v-if='rankList!==undefined && showRank'>
                            <el-table
                                :data='rankList' style='width: 100%;margin-top:10px;'
                                :cell-class-name='getSubmitClass'
                                :row-class-name='getRowClass'
                                max-height='100%' :border='true'>
                                <el-table-column label='排名' width='60'>
                                    <template #default='scope'>
                                        <span>{{ scope.$index + 1 }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column label='用户' width='160'>
                                    <template #default='scope'>
                                        <div>
                                            <span style='display: flex;justify-content: center;'>
                                                {{ scope.row.username }}
                                            </span>
                                            <span style='display: flex;justify-content: center;'>
                                                {{ timeAsHms(scope.row.castTime) }}
                                            </span>
                                        </div>
                                    </template>
                                </el-table-column>
                                <template v-for='(problem,idx) in simpleProblems' :key='idx'>
                                    <el-table-column
                                        :label='problem.idx<=26?String.fromCharCode(65 + problem.idx):problem.idx.toString()'>
                                        <template #default='scope'>
                                            <div v-if='scope.row.submits[problem.id]!==undefined'>
                                                <span
                                                    v-if='scope.row.submits[problem.id].submitMinTime!==Infinity'
                                                    style='display: flex;justify-content: center;'>
                                                    {{ timeAsHms(scope.row.submits[problem.id].submitMinTime) }}</span>
                                                <span
                                                    v-if='scope.row.submits[problem.id].submitFailedCount>0'
                                                    style='display: flex;justify-content: center;'>
                                                    (-{{ scope.row.submits[problem.id].submitFailedCount }})
                                                </span>
                                            </div>
                                        </template>
                                    </el-table-column>
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
import { ManageCompetition, SimpleProblem, SimpleSubmit, SubmitDetail, submitStateToString } from '~/apiDeclaration';
import { RefreshRight, Search, View } from '@element-plus/icons-vue';
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
import moment from 'moment';

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

interface CompetitionSubmitRank {
    submitFailedCount: number;
    submitMinTime: number;
    isAccepted: boolean;
    isFirst: boolean;

    update(simpleSubmit: SimpleSubmit, competition: ManageCompetition): void;
}

function createCompetitionSubmitRank(): CompetitionSubmitRank {
    return {
        submitFailedCount: 0,
        submitMinTime: Infinity,
        isAccepted: false,
        isFirst: false,
        update(simpleSubmit: SimpleSubmit, competition: ManageCompetition) {
            switch (simpleSubmit.state) {
                case 'WRONG_ANSWER':
                case 'TIME_LIMIT_EXCEEDED':
                case 'MEMORY_LIMIT_EXCEEDED':
                case 'OUTPUT_LIMIT_EXCEEDED':
                case 'RUNTIME_ERROR':
                case 'COMPILATION_ERROR':
                case 'PRESENTATION_ERROR':
                    this.submitFailedCount++;
                    break;
                case 'ACCEPTED':
                    this.submitMinTime = Math.min(this.submitMinTime, simpleSubmit.createTime.diff(competition.startTime, 'millisecond'));
                    this.isAccepted = true;
                    break;
            }
        }
    };
}

interface CompetitionRank {
    userid: string;
    username: string;
    castTime: number;
    submits: Record<string, CompetitionSubmitRank>;
    isAk: boolean;
}

type ObjectKey = string | number | symbol

export const groupBy = <K extends ObjectKey, TItem extends Record<K, ObjectKey>>(
    items: TItem[],
    key: K
): Record<ObjectKey, TItem[]> =>
    items.reduce(
        (result, item) => ({
            ...result,
            [item[key]]: [...(result[item[key]] || []), item]
        }),
        {} as Record<ObjectKey, TItem[]>
    );

function createCompetitionRank(submitList: SimpleSubmit[], simpleCompetition: ManageCompetition, allProblem: SimpleProblem[]): CompetitionRank[] {
    const submitGroup = groupBy(submitList, 'belongUserId');
    const ranks: CompetitionRank[] = [];
    for (const submitGroupKey in submitGroup) {
        const submitGroupValue = submitGroup[submitGroupKey];
        const submits: Record<string, CompetitionSubmitRank> = {};
        for (const simpleSubmit of submitGroupValue) {
            const submitRank = submits[simpleSubmit.problemId];
            if (submitRank) {
                submitRank.update(simpleSubmit, simpleCompetition);
            } else {
                const submitRank = createCompetitionSubmitRank();
                submitRank.update(simpleSubmit, simpleCompetition);
                submits[simpleSubmit.problemId] = submitRank;
            }
        }
        let castTime = 0;
        let acCount = 0;
        for (const submitsKey in submits) {
            const submitRank = submits[submitsKey];
            if (submitRank.isAccepted) {
                acCount++;
                castTime += submitRank.submitFailedCount * 20 * 60 * 1000 + submitRank.submitMinTime;
            }
        }
        ranks.push({
            userid: submitGroupKey,
            username: submitGroupValue[0].username,
            castTime: castTime,
            submits,
            isAk: allProblem.length === acCount
        });
    }

    const sortedRanks = ranks.sort((a, b) => a.castTime - b.castTime);
    allProblem.forEach((problem) => {
        const minTime = Math.min(...sortedRanks.map(it => {
            return it.submits[problem.id]?.submitMinTime ?? Infinity;
        }));
        if (minTime !== Infinity) {
            sortedRanks.forEach((rank) => {
                const submitRank = rank.submits[problem.id];
                if (submitRank) {
                    submitRank.isFirst = minTime === submitRank.submitMinTime;
                }
            });
        }
    });
    return sortedRanks;
}

export default defineComponent({
    name: 'SubmitList',
    components: {
        PrismEditor
    },
    setup() {
        const controller = new AbortController();
        const submitApi = api.withConfig({ signal: controller.signal });
        const instance = getCurrentInstance();
        const user = getGlobalUser(instance!.appContext);
        const submitList: Ref<SimpleSubmit[] | undefined> = ref();
        const fetchingSubmitList = ref(false);
        const fetchingSubmitListError = ref('');
        const showRank = ref(false);
        const showDetail = ref(false);
        const showDetailId: Ref<string> = ref('');
        const submitDetail: Ref<SubmitDetail | undefined> = ref();
        const submitDetailMap = new Map<string, SubmitDetail>();

        const simpleProblems: Ref<SimpleProblem[] | undefined> = ref();
        const rankList: Ref<CompetitionRank[] | undefined> = ref();
        const simpleCompetition: Ref<ManageCompetition | undefined> = ref();

        const searchText = ref('');

        const router = useRouter();
        const route = useRoute();

        function goToProblem(id: string) {
            router.push({ name: 'ProblemDetail', params: { id } });
        }

        function searchSubmit() {
            fetchSubmitList();
        }

        const watchHandler = watch(() => route.fullPath, () => {
            if (route.name === 'CompetitionSubmitList') {
                fetchProblemList();
                fetchCompetitionState();
                fetchSubmitList();
            }
        });

        onBeforeMount(() => {
            if (route.name === 'CompetitionSubmitList') {
                fetchProblemList();
                fetchCompetitionState();
                fetchSubmitList();
            }
        });

        onBeforeUnmount(() => {
            watchHandler();
            controller.abort();
        });

        function tryCalc() {
            if (submitList.value !== undefined && simpleCompetition.value !== undefined && simpleProblems.value !== undefined) {
                rankList.value = createCompetitionRank(submitList.value, simpleCompetition.value, simpleProblems.value);
            }
        }

        function getCompetitionId(): string {
            const competitionIds = route.params['id'];
            if (Array.isArray(competitionIds)) {
                return competitionIds[0]!;
            } else {
                return competitionIds;
            }
        }

        function fetchSubmitList() {
            submitList.value = undefined;
            fetchingSubmitList.value = true;
            fetchingSubmitListError.value = '';

            submitApi.competitionSubmits(getCompetitionId()).then((data) => {
                submitList.value = data;
                tryCalc();
            }).catch((err) => {
                fetchingSubmitListError.value = err.message;
            }).finally(() => {
                fetchingSubmitList.value = false;
            });
        }

        function fetchCompetitionState() {
            submitApi.getCompetition(getCompetitionId(), { ignoreError: true }).then(res => {
                simpleCompetition.value = res;
                tryCalc();
            }).catch(() => {
                setTimeout(fetchCompetitionState, 500);
            });
        }


        function fetchProblemList() {
            submitApi.competitionProblems(getCompetitionId(), { ignoreError: true }).then((data) => {
                simpleProblems.value = data;
                tryCalc();
            }).catch(() => {
                setTimeout(fetchProblemList, 500);
            });
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

        function getSubmitClass({ row, columnIndex }: { row: CompetitionRank, columnIndex: number }) {
            if (columnIndex < 2 || row.isAk) {
                return '';
            }
            const problem = simpleProblems.value![columnIndex - 2];
            const submitRank = row.submits[problem.id];
            if (submitRank.isFirst) {
                return 'first-rank';
            }
            if (submitRank.isAccepted) {
                return 'accept-rank';
            }
            if (submitRank.submitFailedCount > 0) {
                return 'error-rank';
            }
            return '';
        }

        function getRowClass({ row }: { row: CompetitionRank }) {
            if (row.isAk) {
                return 'ak';
            } else {
                return '';
            }
        }

        return {
            getRowClass,
            getSubmitClass,
            simpleProblems,
            View,
            showRank,
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
            rankList,
            RefreshRight,
            submitList,
            fetchingSubmitList,
            fetchSubmitList,
            timeAsHms(mills: number) {
                const tm = moment.duration(mills, 'milliseconds');
                return moment({ h: tm.hours(), m: tm.minutes(), s: tm.seconds() }).format('HH:mm:ss');
            }
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

.el-table :deep(.ak) {
    --el-table-tr-bg-color: #FFDF00;
}

.el-table :deep(.cell) {
    display: flex !important;
    justify-content: center;
}

.el-table :deep(.first-rank) {
    background-color: #6fcafb;
}

.el-table :deep(.accept-rank) {
    background-color: var(--el-color-success-light-7);
}

.el-table :deep(.error-rank) {
    background-color: var(--el-color-error-light-7);
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
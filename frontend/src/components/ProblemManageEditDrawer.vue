<template>
  <el-drawer
    v-model='showDrawer' direction='rtl' size='100%'
    :before-close='handleDrawerClose'>
    <template #title>
      <h4>{{ `${problemDetail?.id} : ${problemDetail?.name}` }}</h4>
    </template>
    <template #default>
      <ProblemLanguageConfig
        v-if='showLanguageConfig' :problem-detail='problemDetail'
        :language-ids='languageIds'></ProblemLanguageConfig>
      <ProblemRunConfig
        v-if='showRunConfig' ref='childRef' :problem-detail='problemDetail'
        @close='showDrawer=false'></ProblemRunConfig>
    </template>
    <template v-if='showRunConfig' #footer>
      <div style='flex: auto'>
        <el-button type='primary' :loading='saving' @click='confirmClick'>确定</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang='ts' setup>
import { computed, onBeforeUnmount, PropType, ref } from 'vue';
import { ProblemDetail } from '~/apiDeclaration';

const props = defineProps({
  problemDetail: {
    type: Object as PropType<ProblemDetail>,
    required: true,
  },
  show: {
    type: Boolean,
    required: true,
    default: true,
  },
  languageIds: {
    type: Array as PropType<Array<string>>,
    required: true,
  },
});

const emit = defineEmits(['update:problemDetail', 'update:show']);

const showDrawer = computed<boolean>({
  get() {
    return props.show;
  },
  set(value) {
    emit('update:show', value);
  },
});


const showLanguageConfig = ref(false);
const showRunConfig = ref(false);

function switchToLanguageConfig() {
  showRunConfig.value = false;
  showLanguageConfig.value = true;
}

function switchToRunConfig() {
  showLanguageConfig.value = false;
  showRunConfig.value = true;
}

const saving = ref(false);
const controller = new AbortController();

function handleDrawerClose(done: () => void) {
  if (showRunConfig.value) {
    ElMessageBox.confirm('是否保存数据?', {
      confirmButtonText: '保存',
      cancelButtonText: '退出',
    })
      .then(() => {
        confirmClick().then(() => {
          done();
        }).catch(() => {
          Promise.resolve();
        });
      }).catch(() => {
      done();
    });
  }else{
    done()
  }
}

onBeforeUnmount(() => {
  controller.abort();
});

const childRef = ref(null);

function confirmClick(): Promise<any> {
  return (childRef.value as any).saveRunConfig();
}

defineExpose({
  switchToLanguageConfig,
  switchToRunConfig,
});

</script>

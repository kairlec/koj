<template>
  <el-drawer
    v-model='showDrawer' direction='rtl' size='100%'
    :before-close='handleDrawerClose'>
    <template #default>
      <el-card class='box-card' style='padding: 5px;margin: 0'>
          <el-form
            label-position='top'
            label-width='100px'
            :model='competitionContentObjEdit'
            :disabled='saving'
          >
            <el-form-item label='比赛标题'>
              <el-input v-model='competitionContentObjEdit.description' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='比赛密码(可选)'>
              <el-input v-model='competitionContentObjEdit.description' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='比赛开始时间'>
              <el-input v-model='competitionContentObjEdit.input' type='textarea' :autosize='true' />
            </el-form-item>
            <el-form-item label='比赛结束时间'>
              <el-input v-model='competitionContentObjEdit.output' type='textarea' :autosize='true' />
            </el-form-item>
          </el-form>
      </el-card>
    </template>
<!--    <template #footer>
      <div
        style='text-align: center'>
        <el-transfer
          v-model='rightValue'
          :disabled='saving'
          style='text-align: left; display: inline-block'
          filterable
          :titles="['可选标签', '题目标签']"
          :button-texts="['取消', '选中']"
          :format="{
          noChecked: '${total}',
          hasChecked: '${checked}/${total}',
        }"
          :data='tagData'
        >
          <template #default='{ option }'>
            <span>{{ option.label }}</span>
          </template>
        </el-transfer>
      </div>

      <div style='flex: auto'>
        <el-button type='primary' :loading='saving' @click='confirmClick'>确定</el-button>
      </div>
    </template>-->
  </el-drawer>
</template>

<script lang='ts' setup>
import { computed, onBeforeUnmount, PropType, reactive, ref } from 'vue';
import api from '~/api';
import { ManageCompetition, SimpleCompetition, Tag } from '~/apiDeclaration';
// import { CirclePlus, Delete } from '@element-plus/icons-vue';

const props = defineProps({
  show: {
    type: Boolean,
    required: true,
    default: true,
  },
  // tagData: {
  //   type: Array as PropType<Array<Tag>>,
  //   required: true,
  // },
});

// const tagData = props.tagData.map((tag) => {
//   return {
//     key: tag.id,
//     label: tag.name,
//     disabled: false,
//   };
// });

const emit = defineEmits(['addCompetition', 'update:show']);

function emitAddCompetition(competition: SimpleCompetition) {
  emit('addCompetition', competition);
}

const showDrawer = computed<boolean>({
  get() {
    return props.show;
  },
  set(value) {
    emit('update:show', value);
  },
});

const competitionNameEdit = ref('');
const saving = ref(false);
const controller = new AbortController();
const rightValue = ref(Array<string>());
const competitionEditDrawerApi = api.withConfig({ signal: controller.signal });
const showRawMode = ref(true);

function flushDetail(): boolean {
  if (showRawMode.value) {
    try {
      Object.assign(competitionContentObjEdit, JSON.parse(competitionContentEdit.value));
      return true;
    } catch (e) {
      ElMessage({
        type: 'error',
        message: 'json格式错误,请修正后切换视图',
      });
      return false;
    }
  } else {
    competitionContentEdit.value = JSON.stringify(competitionContentObjEdit, null, 2);
    return true;
  }
}

function handleDrawerClose(done: () => void) {
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
}

onBeforeUnmount(() => {
  controller.abort();
});

function highlighterJson(code: string) {
  return highlight(code, Prism.languages.json, 'json');
}

function saveCompetitionDetail(): Promise<any> {
  if(competitionNameEdit.value.length===0){
    ElMessage({
      type: 'error',
      message: '题目名称不能为空',
    });
    return Promise.reject();
  }
  let obj: CompetitionContent;
  try {
    if (!showRawMode.value) {
      obj = competitionContentObjEdit;
    } else {
      obj = JSON.parse(competitionContentEdit.value);
    }
  } catch (e) {
    console.log(e);
    ElMessageBox.alert('内容不是合法的json', {
      type: 'error',
    });
    return Promise.reject(e);
  }
  saving.value = true;
  const realContent = JSON.stringify(obj);
  return competitionEditDrawerApi.addCompetition({
    name: competitionNameEdit.value,
    content: realContent,
    spj: competitionDetail.spj,
    tags: rightValue.value,
  }).then((id) => {
    ElMessage({
      type: 'success',
      message: '添加成功',
    });
    competitionDetail.tags = tagData.filter(tag => {
      return rightValue.value.includes(tag.key);
    }).map(tag => tag.label);
    competitionDetail.content = realContent;
    competitionDetail.name = competitionNameEdit.value;
    competitionDetail.contentObj = obj;
    emitAddCompetition({
      id: id,
      name: competitionNameEdit.value,
      spj: competitionDetail.spj,
      tags: rightValue.value
    });
    showDrawer.value = false;
  }).finally(() => {
    saving.value = false;
  });
}

function confirmClick(): Promise<any> {
  return saveCompetitionDetail();
}

</script>

<style scoped>
.my-editor {
  background-color: #f8f8f9;
  font-family: Fira code, Fira Mono, Consolas, Menlo, Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  padding: 5px;
}

.my-editor :deep(.prism-editor__textarea):focus {
  outline: none;
}
</style>
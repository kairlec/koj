<template>
  <el-container>
    <el-main>
      <el-scrollbar>
        <p v-for='item in 20' :key='item' class='problem-item'>{{ item }}</p>
      </el-scrollbar>
    </el-main>
    <el-aside>

    </el-aside>
  </el-container>
</template>

<script lang='ts'>
import { defineComponent, onBeforeMount, onBeforeUnmount, Ref, ref } from 'vue';
import api, { PageData, SimpleProblem } from '~/api';

export default defineComponent({
  setup() {
    const controller = new AbortController();
    const problemApi = api.withConfig({ signal: controller.signal });
    const problemList: Ref<PageData<SimpleProblem> | undefined> = ref();

    onBeforeUnmount(() => {
      controller.abort();
    });

    onBeforeMount(() => {
      problemApi.problems().then((data) => {
        problemList.value = data;
      });
    });

    return {};
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
</style>
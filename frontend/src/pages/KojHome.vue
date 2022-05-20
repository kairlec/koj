<template>
  <div style='padding: 0 20px;width: 100%'>
    <el-table v-loading='rankLoading' :data='rankData' style='width: 100%'>
      <el-table-column prop='userId' label='用户Id' />
      <el-table-column prop='username' label='用户名' />
      <el-table-column prop='ac' label='ac数' />
      <el-table-column prop='rank' label='排名' />
    </el-table>
  </div>
</template>
<script lang='ts'>

import { defineComponent, onBeforeMount, onBeforeUnmount, ref, Ref } from 'vue';
import { UserRankInfo } from '~/apiDeclaration';
import api from '~/api';

export default defineComponent({
  name: 'KojHome',
  setup() {
    const rankData: Ref<UserRankInfo[]> = ref([]);
    const controller = new AbortController();
    const signal = controller.signal;
    const homeApi = api.withConfig({ signal: signal });

    const rankLoading = ref(true);

    onBeforeMount(() => {
      fetchRank();
    });

    onBeforeUnmount(() => {
      controller.abort();
    });

    function fetchRank() {
      rankLoading.value = true;
      homeApi.rank().then(res => {
        rankData.value = res;
      }).finally(() => {
        rankLoading.value = false;
      });
    }

    return {
      rankData,
      rankLoading,
    };
  },
});

</script>

<template>
  <el-container>
    <el-header>
      <MenuBar></MenuBar>
    </el-header>
    <el-main style='padding: 0'>
      <el-container :style='bodyStyle'>
        <router-view v-slot="{ Component }">
          <keep-alive include='ProblemList'>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-container>
    </el-main>
  </el-container>
</template>

<script lang='ts' setup>
import { onBeforeMount, onBeforeUnmount, onMounted, ref } from 'vue';

//region 刷新body的最大高度,flex搞了半天,没搞好,还是直接resize,开摆!

const bodyStyle = ref('');

function useResize(onResize: (this: Window, ev: UIEvent) => any) {
  onMounted(() => {
    window.addEventListener('resize', onResize);
  });
  onBeforeUnmount(() => {
    window.removeEventListener('resize', onResize);
  });
}

function freshBodySize() {
  bodyStyle.value = `max-height: ${window.innerHeight - 76}px`;
}

onBeforeMount(() => {
  freshBodySize();
});

useResize(() => {
  freshBodySize();
});

//endregion

</script>
<script setup lang="ts">
import {ref} from 'vue'

defineProps({
  msg: String
})

const activeIndex = ref('1')
const handleSelect = (key: string) => {
  console.log(key)
}

</script>
<template>
  <el-menu :default-active="activeIndex" mode="horizontal" @select="handleSelect" router="true">
    <el-image src="/src/assets/logo.svg" class="logo"></el-image>
    <el-menu-item index="1" route="/home">首页</el-menu-item>
    <el-menu-item index="2" route="/problem">题目</el-menu-item>
    <el-menu-item index="3" route="/submit">提交</el-menu-item>
    <el-menu-item index="4" route="/contents">比赛</el-menu-item>
    <el-menu-item index="5" route="/rank">排行</el-menu-item>
  </el-menu>
</template>

<script lang="ts">
export default {
  name: "MenuBar"
}
</script>

<style scoped>
.el-menu-item {
  width: 16%;
}

.logo {
  margin-right: 30px;
}
</style>
<style>
.logo .el-image__inner {
  max-height: 58px;
}
</style>
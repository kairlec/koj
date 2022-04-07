<script setup lang="ts">
import {ref} from 'vue'

defineProps({
  msg: String
})

const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>
<template>
  <el-menu :default-active="activeIndex" mode="horizontal" @select="handleSelect">
    <el-image src="/src/assets/logo.svg" class="logo"></el-image>
    <el-menu-item index="1">首页</el-menu-item>
    <el-menu-item index="2">题目</el-menu-item>
    <el-menu-item index="3">提交</el-menu-item>
    <el-menu-item index="4">比赛</el-menu-item>
    <el-menu-item index="5">排行</el-menu-item>
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
.logo{
  margin-right: 30px;
}
</style>
<style>
.el-image__inner{
  max-height: 58px;
}
</style>
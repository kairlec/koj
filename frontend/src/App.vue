<script lang='ts' setup>
import Home from './components/Home.vue';</script>

<template>
  <Home></Home>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
<script lang='ts'>
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    return {
      goto() {
        useRouter().push('/home');
      },
    };
  },
});
</script>

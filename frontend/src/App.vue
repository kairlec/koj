<template>
  <el-container>
    <el-header>
      <MenuBar id='menu'></MenuBar>
    </el-header>
    <el-main id='body-main'>
      <router-view />
    </el-main>
  </el-container>
</template>

<style>
a {
  color: #42b983;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

/*#menu{*/
/*  position:fixed;*/
/*}*/
</style>

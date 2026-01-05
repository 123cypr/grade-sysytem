<template>
  <el-container class="layout">
    <el-header class="nav">
      <el-space wrap>
        <router-link to="/login">登录</router-link>
        <router-link to="/score-entry">成绩录入</router-link>
        <router-link to="/score-query">成绩查询</router-link>
        <router-link to="/score-analysis">成绩分析</router-link>
      </el-space>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}
.nav {
  display: flex;
  gap: 16px;
  align-items: center;
}
.nav a {
  color: var(--el-color-primary);
}
</style>

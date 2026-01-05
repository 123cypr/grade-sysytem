<template>
  <el-container class="layout">
    <el-header>初中成绩管理系统（骨架）</el-header>
    <el-main>
      <el-card>
        <p>后端健康检查：{{ health }}</p>
        <el-button type="primary" @click="checkHealth">检查</el-button>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue';
import http from '../api/http';

const health = ref('未检查');
const checkHealth = async () => {
  try {
    const res = await http.get('/health');
    health.value = res?.data ?? 'ok';
  } catch (e) {
    health.value = 'failed';
  }
};
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
</style>

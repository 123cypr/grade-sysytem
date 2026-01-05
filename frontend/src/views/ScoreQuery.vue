<template>
  <el-container class="page">
    <el-header>成绩查询</el-header>
    <el-main>
      <el-card>
        <el-form :inline="true" :model="filters" @submit.prevent="load">
          <el-form-item label="学生ID">
            <el-input v-model="filters.studentId" />
          </el-form-item>
          <el-form-item label="考试ID">
            <el-input v-model="filters.examId" />
          </el-form-item>
          <el-form-item label="学科ID">
            <el-input v-model="filters.subjectId" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="load">查询</el-button>
          </el-form-item>
          <span class="tip">{{ message }}</span>
        </el-form>
      </el-card>

      <el-table :data="rows" style="margin-top: 16px">
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="examId" label="考试ID" width="100" />
        <el-table-column prop="subjectId" label="学科ID" width="100" />
        <el-table-column prop="score" label="分数" width="100" />
        <el-table-column label="体育分项">
          <template #default="{ row }">
            <pre class="json">{{ formatJson(row.sportsItems) }}</pre>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue';
import http from '../api/http';

const filters = reactive({
  studentId: '',
  examId: '',
  subjectId: ''
});
const rows = ref([]);
const loading = ref(false);
const message = ref('');

const load = async () => {
  loading.value = true;
  message.value = '';
  try {
    const res = await http.get('/scores', { params: filters });
    rows.value = res || [];
    if (!rows.value.length) {
      message.value = '暂无数据';
    }
  } catch (e) {
    message.value = e?.message || '查询失败';
  } finally {
    loading.value = false;
  }
};

const formatJson = (obj) => {
  if (!obj) return '';
  try {
    return typeof obj === 'string' ? obj : JSON.stringify(obj);
  } catch (e) {
    return '';
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
}
.json {
  white-space: pre-wrap;
  margin: 0;
}
.tip {
  margin-left: 12px;
  color: #999;
}
</style>

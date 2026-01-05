<template>
  <el-container class="page">
    <el-header>成绩分析</el-header>
    <el-main>
      <el-card>
        <el-form :inline="true" :model="form" @submit.prevent="load">
          <el-form-item label="考试ID">
            <el-input v-model="form.examId" />
          </el-form-item>
          <el-form-item label="学科ID">
            <el-input v-model="form.subjectId" />
          </el-form-item>
          <el-form-item label="及格线">
            <el-input v-model="form.passLine" />
          </el-form-item>
          <el-form-item label="优秀线">
            <el-input v-model="form.excellentLine" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="load">统计</el-button>
          </el-form-item>
          <span class="tip">{{ message }}</span>
        </el-form>
      </el-card>

      <el-card style="margin-top: 16px">
        <p>平均分：{{ stats.average ?? '-' }}</p>
        <p>及格率：{{ stats.passRate ?? '-' }}%</p>
        <p>优秀率：{{ stats.excellentRate ?? '-' }}%</p>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue';
import http from '../api/http';

const form = reactive({
  examId: '',
  subjectId: '',
  passLine: 60,
  excellentLine: 90
});
const stats = ref({});
const message = ref('');
const loading = ref(false);

const load = async () => {
  if (!form.passLine || !form.excellentLine) {
    message.value = '请输入及格线和优秀线';
    return;
  }
  loading.value = true;
  message.value = '';
  try {
    const res = await http.get('/stats/score', {
      params: {
        examId: form.examId || undefined,
        subjectId: form.subjectId || undefined,
        passLine: form.passLine,
        excellentLine: form.excellentLine
      }
    });
    stats.value = res || {};
  } catch (e) {
    message.value = e?.message || '统计失败';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
}
.tip {
  margin-left: 12px;
  color: #999;
}
</style>

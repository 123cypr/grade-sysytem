<template>
  <el-container class="page">
    <el-header>成绩录入</el-header>
    <el-main>
      <el-card>
        <el-form :model="form" label-width="110px" @submit.prevent="onSubmit">
          <el-form-item label="学生ID">
            <el-input v-model="form.studentId" />
          </el-form-item>
          <el-form-item label="考试ID">
            <el-input v-model="form.examId" />
          </el-form-item>
          <el-form-item label="学科ID">
            <el-input v-model="form.subjectId" />
          </el-form-item>
          <el-form-item label="分数">
            <el-input v-model="form.score" />
          </el-form-item>
          <el-form-item label="体育分项(JSON)">
            <el-input
              type="textarea"
              :rows="3"
              v-model="sportsItemsText"
              placeholder='例如 {"跑步":30,"跳绳":15}'
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="onSubmit">提交</el-button>
            <span class="tip">{{ message }}</span>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue';
import http from '../api/http';

const form = reactive({
  studentId: '',
  examId: '',
  subjectId: '',
  score: ''
});
const sportsItemsText = ref('');
const loading = ref(false);
const message = ref('');

const onSubmit = async () => {
  loading.value = true;
  message.value = '';
  try {
    let sportsItems = undefined;
    if (sportsItemsText.value) {
      sportsItems = JSON.parse(sportsItemsText.value);
    }
    await http.post('/scores', {
      studentId: Number(form.studentId),
      examId: Number(form.examId),
      subjectId: Number(form.subjectId),
      score: form.score ? Number(form.score) : null,
      sportsItems
    });
    message.value = '提交成功';
  } catch (e) {
    message.value = e?.message || '提交失败';
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

<template>
  <el-container class="page">
    <el-header>登录</el-header>
    <el-main>
      <el-card>
        <el-form :model="form" label-width="80px" @submit.prevent="onLogin">
          <el-form-item label="账号">
            <el-input v-model="form.username" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" autocomplete="current-password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="onLogin">登录</el-button>
            <span class="tip">{{ message }}</span>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import http from '../api/http';
import { useUserStore } from '../store';

const form = reactive({
  username: '',
  password: ''
});
const message = ref('');
const loading = ref(false);
const store = useUserStore();
const router = useRouter();

const onLogin = async () => {
  if (!form.username || !form.password) {
    message.value = '请输入账号密码';
    return;
  }
  loading.value = true;
  try {
    const token = await http.post('/auth/login', { ...form });
    store.setToken(token);
    // 角色信息留待 profile，先跳转
    message.value = '登录成功';
    router.push('/score-entry');
  } catch (e) {
    message.value = e?.message || '登录失败';
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

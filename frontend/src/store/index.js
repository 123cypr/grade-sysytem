import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const token = ref('');
  const role = ref('');

  function setToken(val) {
    token.value = val;
    localStorage.setItem('token', val);
  }

  function setRole(val) {
    role.value = val;
  }

  return { token, role, setToken, setRole };
});

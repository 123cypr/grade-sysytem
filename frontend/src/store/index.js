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
    localStorage.setItem('role', val);
  }

  function restore() {
    token.value = localStorage.getItem('token') || '';
    role.value = localStorage.getItem('role') || '';
  }

  restore();

  return { token, role, setToken, setRole, restore };
});

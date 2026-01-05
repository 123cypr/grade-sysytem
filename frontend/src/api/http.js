import axios from 'axios';

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data && data.code && data.code !== 200) {
      return Promise.reject(new Error(data.msg || '请求失败'));
    }
    return data;
  },
  (error) => Promise.reject(error)
);

export default http;

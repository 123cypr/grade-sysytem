import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import ScoreEntry from '../views/ScoreEntry.vue';
import ScoreQuery from '../views/ScoreQuery.vue';
import ScoreAnalysis from '../views/ScoreAnalysis.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    component: Home
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/score-entry',
    component: ScoreEntry
  },
  {
    path: '/score-query',
    component: ScoreQuery
  },
  {
    path: '/score-analysis',
    component: ScoreAnalysis
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;

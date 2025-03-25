import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 }, // Ramp up to 10 users
    { duration: '1m', target: 10 },  // Stay at 10 users for 1 minute
    { duration: '30s', target: 0 },  // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests should complete within 500ms
    http_req_failed: ['rate<0.1'],    // Less than 10% of requests can fail
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // Test check-in endpoint
  const checkInRes = http.post(`${BASE_URL}/attendance/check-in/1`);
  check(checkInRes, {
    'check-in status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test check-out endpoint
  const checkOutRes = http.post(`${BASE_URL}/attendance/check-out/1`);
  check(checkOutRes, {
    'check-out status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test work-from-home endpoint
  const wfhRes = http.post(`${BASE_URL}/attendance/work-from-home/1?notes=Working from home`);
  check(wfhRes, {
    'work-from-home status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test get employee attendance endpoint
  const now = new Date();
  const start = new Date(now.getTime() - 24 * 60 * 60 * 1000); // 24 hours ago
  const getAttendanceRes = http.get(
    `${BASE_URL}/attendance/employee/1?start=${start.toISOString()}&end=${now.toISOString()}`
  );
  check(getAttendanceRes, {
    'get attendance status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test AI summary endpoint
  const aiSummaryRes = http.get(`${BASE_URL}/ai/attendance/summary?date=${now.toISOString()}`);
  check(aiSummaryRes, {
    'AI summary status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test AI question endpoint
  const aiQuestionRes = http.get(`${BASE_URL}/ai/attendance/question?question=Who was absent the most this month?`);
  check(aiQuestionRes, {
    'AI question status is 200': (r) => r.status === 200,
  });
} 
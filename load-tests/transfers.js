import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m', target: 50 },
    { duration: '30s', target: 0 },
  ],
};

export default function () {
  // Assuming wallet1 and wallet2 UUIDs exist in the DB
  const payload = JSON.stringify({
    sourceWalletId: 'ef280499-3887-4362-ad98-11060d4af64e',
    destinationWalletId: '39b1361d-aa62-4b5f-92b6-d628b100a805',
    amount: 1,
    currency: 'USD',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Idempotency-Key': `k6-test-${__VU}-${__ITER}`,
    },
  };

  const res = http.post('http://localhost:8080/api/v1/transfers', payload, params);

  check(res, {
    'status is 201': (r) => r.status === 201,
  });

  sleep(1);
}

import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    executor: 'ramping-vus',  // 점진적으로 VU 증가 및 감소
    startVUs: 0,  // 시작 VU 수
    stages: [
        { duration: '15s', target: 250 },  // 15초 동안 250 VU까지 증가
        { duration: '15s', target: 500 }, // 15초 동안 500 VU까지 증가
        { duration: '30s', target: 1000 }, // 30초 동안 최대 1000 VU 유지 (Peak 부하)
        { duration: '15s', target: 500 },  // 15초 동안 500 VU까지 감소
        { duration: '15s', target: 0 },    // 15초 동안 0 VU까지 감소 (테스트 종료)
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내에 완료
        http_req_failed: ['rate<0.001'], // 실패율이 0.1% 미만
    },
};

export default function () {
    const url = 'http://host.docker.internal:8080/queue/token';
    const userId = __VU;
    const payload = JSON.stringify({ userId: userId, concertId: 1 });
    const headers = { 'Content-Type': 'application/json' };

    let res = http.post(url, payload, { headers });

    check(res, {
        'status is 201': (r) => r.status === 201,
        'response time is < 2000ms': (r) => r.timings.duration < 2000,
    });

    sleep(1); // 요청 간 간격을 두어 TPS 조정
};


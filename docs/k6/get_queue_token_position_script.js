import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080';
const TOKEN_ISSUE_ENDPOINT = `${BASE_URL}/queue/token`;
const POSITION_CHECK_ENDPOINT = `${BASE_URL}/queue/position`;

export function setup() {
    let tokens = [];

    // 1,000명의 사용자에게 대기열 토큰 발급 요청 (병렬 요청)
    for (let i = 0; i < 1000; i++) {
        let res = http.post(TOKEN_ISSUE_ENDPOINT, JSON.stringify({ userId: i, concertId: 1 }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (res.status === 201) {
            let responseBody = JSON.parse(res.body);
            let token = responseBody?.data?.token;
            if (token) {
                tokens.push(token);
            }
        }
    }

    return tokens;
}


// 대기열 토큰 순번 조회 부하 테스트 (Polling 10초 간격 + 10초마다 30명씩 polling 중단)
export let options = {
    scenarios: {
        queue_position_check: {
            executor: 'ramping-arrival-rate', // 점진적인 부하 감소 반영
            startRate: 1000, // 초당 1000건 요청 (초기 TPS)
            timeUnit: '1s',
            stages: [
                { duration: '10s', target: 997 }, // 10초 후 997 TPS
                { duration: '10s', target: 994 }, // 20초 후 994 TPS
                { duration: '10s', target: 991 }, // 30초 후 991 TPS
                { duration: '10s', target: 988 }, // 40초 후 988 TPS
                { duration: '10s', target: 985 }, // 50초 후 985 TPS
                { duration: '10s', target: 982 }, // 1분 후 982 TPS
                { duration: '1m', target: 964 }, // 이후 점진적으로 감소
                { duration: '1m', target: 946 },
                { duration: '1m', target: 928 },
                { duration: '1m', target: 910 }, // 최종 5분 후 910 TPS
            ],
            preAllocatedVUs: 1500, // 초반 부하를 감당할 수 있도록 미리 1500명의 VU 할당
            maxVUs: 5000, // 최대 5000명의 VU까지 확장 가능
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.01'],
    },
};

export default function (tokens) {
    let vuIndex = __VU % tokens.length;
    let iterationTime = (__ITER * 10); // 10초 단위로 polling 중단

    // 10초마다 30명씩 polling 중단
    if (vuIndex < (tokens.length - (iterationTime / 10) * 30)) {
        let token = tokens[vuIndex];
        let res = http.get(`${POSITION_CHECK_ENDPOINT}?token=${token}`);

        check(res, {
            'status is 200': (r) => r.status === 200,
            'response time is < 1000ms': (r) => r.timings.duration < 1000,
        });

        sleep(10); // Polling 간격을 10초로 설정
    }
}

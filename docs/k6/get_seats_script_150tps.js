import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080';
const TOKEN_ISSUE_ENDPOINT = `${BASE_URL}/queue/token`; // 대기열 토큰 발급 API
const SEAT_LOOKUP_ENDPOINT = `${BASE_URL}/concerts/1/dates/2025-03-30/seats`; // 좌석 조회 API

// 대기열 토큰 발급 API를 호출하여 토큰을 가져옴
export function setup() {
    let tokens = [];

    // 1,000명의 사용자에게 대기열 토큰 발급 요청 (병렬 요청)
    for (let i = 0; i < 500; i++) {
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

export let options = {
    scenarios: {
        seat_lookup: {
            executor: 'ramping-arrival-rate', // 점진적인 부하 증가 방식 사용
            startRate: 50, // 초당 50개 요청 시작
            timeUnit: '1s', // 초 단위로 TPS 유지
            preAllocatedVUs: 100, // 미리 100명의 VU 할당
            maxVUs: 300, // 최대 300명의 VU까지 확장 가능
            startTime: '15s', // setup() 실행 후 15초 뒤에 시작
            stages: [
                { duration: '30s', target: 100 }, // 30초 동안 100 TPS 유지
                { duration: '1m', target: 150 },  // 1분 동안 150 TPS 유지
                { duration: '30s', target: 120 }, // 30초 동안 120 TPS 유지
                { duration: '30s', target: 0 },   // 30초 동안 부하 제거
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내에 완료
        http_req_failed: ['rate<0.01'], // 실패율이 1% 미만
    },
};

export default function (tokens) {
    let vuIndex = __VU % tokens.length; // VU 인덱스를 사용하여 토큰 할당
    let token = tokens[vuIndex]; // 해당 사용자에 맞는 토큰 선택

    let headers = { 'X-Queue-Token': token };
    let res = http.get(SEAT_LOOKUP_ENDPOINT, { headers });

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time is < 2000ms': (r) => r.timings.duration < 2000,
    });

    sleep(1); // 요청 간격 조절
}

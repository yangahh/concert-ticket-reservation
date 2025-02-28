import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 환경 설정
const BASE_URL = 'http://host.docker.internal:8080';
const TOKEN_ISSUE_ENDPOINT = `${BASE_URL}/queue/token`; // 대기열 토큰 발급 API
const RESERVATION_ENDPOINT = `${BASE_URL}/reservations`; // 좌석 예약 API

export function setup() {
    let tokens = [];

    // 300명의 사용자가 대기열 토큰을 요청 (병렬 요청 가능)
    for (let i = 0; i < 300; i++) {
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
        seat_reservation: {
            executor: 'ramping-arrival-rate', // 점진적으로 부하 증가
            startRate: 20, // 초당 20개 요청 시작
            timeUnit: '1s', // 초 단위로 TPS 유지
            preAllocatedVUs: 50, // 미리 50명의 VU 할당
            maxVUs: 200, // 최대 200명의 VU까지 확장 가능
            startTime: '15s', // setup() 실행 후 10초 뒤에 실행됨 (대기열 토큰 발급 후 active로 전환하기 위해)
            stages: [
                { duration: '30s', target: 50 }, // 30초 동안 50 TPS 유지
                { duration: '30s', target: 70 }, // 30초 동안 70 TPS 유지
                { duration: '1m', target: 70 },  // 1분 동안 70 TPS 유지
                { duration: '30s', target: 50 }, // 30초 동안 다시 50 TPS로 감소
                { duration: '30s', target: 0 },  // 30초 동안 부하 제거
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내에 완료
    },
};

export default function (tokens) {
    let vuIndex = __VU % tokens.length;
    let token = tokens[vuIndex]; // 해당 사용자에 맞는 토큰 선택
    let seatId = Math.floor(Math.random() * 50) + 1; // 1~50 사이의 랜덤 좌석 선택

    let headers = {
        'Content-Type': 'application/json',
        'X-Queue-Token': token
    };

    let payload = JSON.stringify({ userId: __VU, seatId: seatId });

    let res = http.post(RESERVATION_ENDPOINT, payload, { headers });

    check(res, {
        'status is 200 or 422': (r) => r.status === 200 || r.status === 422, // 422: 좌석 이미 예약됨
        'response time is < 2000ms': (r) => r.timings.duration < 2000,
    });

    sleep(1); // 요청 간격 조절
}

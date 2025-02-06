import http from 'k6/http';
import { check } from 'k6';


// 고정된 사용자 수와 테스트 지속 시간 설정
export const options = {
    vus: 30, // 동시에 실행할 가상 사용자 수
    // iterations: 100 // 총 요청 횟수
    duration: '30s', // 테스트 지속 시간
};

export default function () {
    // 요청 URL
    const url = "http://host.docker.internal:8080/concerts?date=2025-02-07";
    const res = http.get(url);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    if (!success) {
        console.error(`Request failed! Status: ${res.status}`);
        console.error(`Response body: ${res.body}`);
    }
}

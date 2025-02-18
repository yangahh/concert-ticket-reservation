import http from 'k6/http';
import { check } from 'k6';


// 고정된 사용자 수와 테스트 지속 시간 설정
export const options = {
    vus: 30, // 동시에 실행할 가상 사용자 수
    duration: '30s', // 테스트 지속 시간
};

export default function () {
    // 요청 URL
    const url = "http://host.docker.internal:8080/concerts/95336/dates/2025-02-26/seats";
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Queue-Token': 'eyJjb25jZXJ0SWQiOjEsInRva2VuVXVpZCI6IjJjZDg5ZGY3LWI4MzMtNDgwMS1iNDFhLTZjN2Q4NmU1ZjI0YyJ9'
        }
    };

    const res = http.get(url, params);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    if (!success) {
        console.error(`Request failed! Status: ${res.status}`);
        console.error(`Response body: ${res.body}`);
    }
}

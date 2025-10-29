import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';


export const options = {
    vus: 5,
    duration: '1m'
};

var BASE_URL = "http://localhost:3000"
var PAGE_SIZE = 20

// The default exported function is gonna be picked up by k6 as the entry point for the test script. It will be executed repeatedly in "iterations" for the whole duration of the test.
export default function () {
    const url = `${BASE_URL}/api/v1/products?page=0&size=${PAGE_SIZE}`;

    const params = {
        headers: {
            'Content-Type' : 'application/json',
            'Authorization' : 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsYXVyZW4iLCJpYXQiOjE3NjE3MTMwNjgsImV4cCI6MTc2MTk3MjI2OH0.VLiFIapPJb_vy22qKvMSnWHKL4ELNyh6ZnNRR6anWsi1ATsqfZGRv1p02i3Q0YbBVQZyfyX_eH558_h5x2eFuA',
        }
    }
    const response = http.get(url, params);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'rate limit not exceeded' : (r) => r.status !== 429
    })

    console.log(`Status: ${response.status}, Response time: ${response.timings.duration} ms`)

    sleep(0.1)
}
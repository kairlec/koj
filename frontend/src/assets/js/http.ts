import axios, {AxiosInstance} from "axios";

function http(baseURL: string = '/api'): AxiosInstance {
    const _http = axios.create({
        baseURL: baseURL,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        timeout: 50000
    })

    _http.interceptors.request.use((config) => {
        return config;
    }, (error) => {
        return Promise.reject(error);
    });

    _http.interceptors.response.use((res) => {
        if (res.status / 100 == 2 || res.status == 304) {
            return Promise.resolve(res.data);
        }
    }, (error) => {
        if (error.config.ignoreError) {
            return Promise.reject(error)
        }
        const data = error.response.data
        if (data?.code) {
            ElMessage({
                type: 'error',
                message: `[${data.code}] ${data.message}`,
            })
            return Promise.reject(error)
        }
        switch (error.response.status) {
            case 404:
                ElMessage({
                    type: 'error',
                    message: '资源不存在',
                });
                break;
            case 500:
                ElMessage({
                    type: 'error',
                    message: '服务器故障',
                });
                break;
            case 502:
                ElMessage({
                    type: 'error',
                    message: '服务器正在维护',
                });
                break;
            default:
                ElMessage({
                    type: 'error',
                    message: '请求出错',
                });
                break;
        }
        return Promise.reject(error);
    });
    return _http
}

export default http;

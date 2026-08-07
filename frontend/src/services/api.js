import axios from "axios";
import { clearAuthStorage, getAccessToken } from "./auth/authStorage";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

api.interceptors.request.use((config) => {
    const requestConfig = config;
    const token = getAccessToken();

    if (token && !requestConfig.skipAuth) {
        requestConfig.headers = requestConfig.headers || {};
        requestConfig.headers.Authorization = `Bearer ${token}`;
    }

    return requestConfig;
});

let isRedirectingToLogin = false;

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error?.response?.status;
        const requestConfig = error?.config;
        const shouldHandleAuthError =
            status === 401 && requestConfig && !requestConfig.skipAuth && requestConfig.headers?.Authorization;

        if (shouldHandleAuthError) {
            clearAuthStorage();

            if (!isRedirectingToLogin && window.location.pathname !== "/login") {
                isRedirectingToLogin = true;
                window.location.replace("/login");
            }
        }

        return Promise.reject(error);
    },
);

export default api;

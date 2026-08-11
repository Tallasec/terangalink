import axios from "axios";
import {
    getAccessToken,
    handleSessionExpired,
    isAccessTokenExpired,
} from "./auth/authStorage";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

api.interceptors.request.use((config) => {
    const requestConfig = config;

    if (requestConfig.skipAuth) {
        return requestConfig;
    }

    const token = getAccessToken();

    if (!token) {
        return requestConfig;
    }

    if (isAccessTokenExpired(token)) {
        handleSessionExpired();
        return Promise.reject(new Error("Session expired"));
    }

    requestConfig.headers = requestConfig.headers || {};
    requestConfig.headers.Authorization = `Bearer ${token}`;

    return requestConfig;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error?.response?.status;
        const requestConfig = error?.config;
        const shouldHandleAuthError =
            status === 401 && requestConfig && !requestConfig.skipAuth && requestConfig.headers?.Authorization;

        if (shouldHandleAuthError) {
            handleSessionExpired();
        }

        return Promise.reject(error);
    },
);

export default api;

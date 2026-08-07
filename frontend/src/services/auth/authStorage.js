import { PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";

const ACCESS_TOKEN_KEY = "accessToken";

export function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function setAccessToken(token) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function clearAccessToken() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
}

export function hasAccessToken() {
    return Boolean(getAccessToken());
}

function decodeJwtPayload(token) {
    const parts = token.split(".");

    if (parts.length !== 3) {
        return null;
    }

    try {
        const base64Payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
        const paddedPayload = base64Payload.padEnd(
            base64Payload.length + ((4 - (base64Payload.length % 4)) % 4),
            "=",
        );

        return JSON.parse(atob(paddedPayload));
    } catch {
        return null;
    }
}

export function isAccessTokenExpired(token = getAccessToken()) {
    if (!token) {
        return true;
    }

    const payload = decodeJwtPayload(token);

    if (!payload || typeof payload.exp !== "number") {
        return true;
    }

    return Date.now() >= payload.exp * 1000;
}

export function hasValidAccessToken() {
    const token = getAccessToken();
    return Boolean(token) && !isAccessTokenExpired(token);
}

export function clearAuthStorage() {
    clearAccessToken();
    sessionStorage.removeItem(PENDING_VERIFICATION_EMAIL_KEY);
}

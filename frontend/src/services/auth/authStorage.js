import { PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";

const ACCESS_TOKEN_KEY = "accessToken";

export function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function setAccessToken(token) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
    isRedirectingToLogin = false;
    window.dispatchEvent(new CustomEvent("auth-token-changed"));
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

export function getAccessTokenExpiresAtMs(token = getAccessToken()) {
    if (!token) {
        return null;
    }

    const payload = decodeJwtPayload(token);

    if (!payload || typeof payload.exp !== "number") {
        return null;
    }

    return payload.exp * 1000;
}

export function hasValidAccessToken() {
    const token = getAccessToken();
    return Boolean(token) && !isAccessTokenExpired(token);
}

export function clearAuthStorage() {
    clearAccessToken();
    sessionStorage.removeItem(PENDING_VERIFICATION_EMAIL_KEY);
}

let isRedirectingToLogin = false;

export function handleSessionExpired() {
    clearAuthStorage();

    if (isRedirectingToLogin || window.location.pathname === "/login") {
        return;
    }

    isRedirectingToLogin = true;

    const loginUrl = new URL("/login", window.location.origin);
    loginUrl.searchParams.set("session", "expired");
    window.location.replace(`${loginUrl.pathname}${loginUrl.search}`);
}

import { useCallback, useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

import {
    getAccessToken,
    getAccessTokenExpiresAtMs,
    handleSessionExpired,
    hasValidAccessToken,
    isAccessTokenExpired,
} from "../../services/auth/authStorage";

const SESSION_CHECK_INTERVAL_MS = 1000;

function AuthSessionWatcher() {
    const location = useLocation();
    const [tokenVersion, setTokenVersion] = useState(0);

    const checkSession = useCallback(() => {
        const token = getAccessToken();

        if (!token) {
            return;
        }

        if (isAccessTokenExpired(token)) {
            handleSessionExpired();
        }
    }, []);

    useEffect(() => {
        function handleTokenChanged() {
            setTokenVersion((currentVersion) => currentVersion + 1);
        }

        window.addEventListener("auth-token-changed", handleTokenChanged);

        return () => {
            window.removeEventListener("auth-token-changed", handleTokenChanged);
        };
    }, []);

    useEffect(() => {
        if (!hasValidAccessToken()) {
            return undefined;
        }

        const expiresAtMs = getAccessTokenExpiresAtMs();

        if (!expiresAtMs) {
            handleSessionExpired();
            return undefined;
        }

        const delayMs = expiresAtMs - Date.now();

        if (delayMs <= 0) {
            handleSessionExpired();
            return undefined;
        }

        const timeoutId = window.setTimeout(handleSessionExpired, delayMs);
        const intervalId = window.setInterval(checkSession, SESSION_CHECK_INTERVAL_MS);

        return () => {
            window.clearTimeout(timeoutId);
            window.clearInterval(intervalId);
        };
    }, [location.pathname, tokenVersion, checkSession]);

    return null;
}

export default AuthSessionWatcher;

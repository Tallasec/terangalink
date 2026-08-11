import { Navigate, useLocation } from "react-router-dom";

import {
    clearAuthStorage,
    getAccessToken,
    hasValidAccessToken,
} from "../../services/auth/authStorage";

const SESSION_EXPIRED_MESSAGE = "Votre session a expiré. Veuillez vous reconnecter.";

function ProtectedRoute({ children }) {
    const location = useLocation();

    if (!hasValidAccessToken()) {
        const hadExpiredToken = Boolean(getAccessToken());
        clearAuthStorage();

        return (
            <Navigate
                to="/login"
                replace
                state={{
                    from: location,
                    message: hadExpiredToken ? SESSION_EXPIRED_MESSAGE : undefined,
                }}
            />
        );
    }

    return children;
}

export default ProtectedRoute;

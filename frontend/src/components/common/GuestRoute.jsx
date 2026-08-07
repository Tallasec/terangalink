import { Navigate, useLocation } from "react-router-dom";

import { hasValidAccessToken } from "../../services/auth/authStorage";

function GuestRoute({ children }) {
    const location = useLocation();

    if (hasValidAccessToken()) {
        return <Navigate to="/dashboard" replace state={{ from: location }} />;
    }

    return children;
}

export default GuestRoute;

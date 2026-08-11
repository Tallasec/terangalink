import { Routes, Route } from "react-router-dom";

import LandingPage from "./pages/landing/LandingPage";
import LoginPage from "./pages/login/LoginPage";
import RegisterPage from "./pages/register/RegisterPage";
import VerifyEmailPage from "./pages/register/VerifyEmailPage";
import ProfilePage from "./pages/profile/ProfilePage";
import DashboardPage from "./pages/dashboard/DashboardPage";
import HousingPage from "./pages/housing/HousingPage";
import HousingDetailPage from "./pages/housing/HousingDetailPage";
import ProtectedRoute from "./components/common/ProtectedRoute";
import GuestRoute from "./components/common/GuestRoute";
import AuthSessionWatcher from "./components/common/AuthSessionWatcher";

function App() {
    return (
        <>
            <AuthSessionWatcher />
            <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <DashboardPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/housing"
                element={
                    <ProtectedRoute>
                        <HousingPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/housing/:id"
                element={
                    <ProtectedRoute>
                        <HousingDetailPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/profile"
                element={
                    <ProtectedRoute>
                        <ProfilePage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/profile/verify-email"
                element={
                    <ProtectedRoute>
                        <VerifyEmailPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/login"
                element={
                    <GuestRoute>
                        <LoginPage />
                    </GuestRoute>
                }
            />
            <Route
                path="/register"
                element={
                    <GuestRoute>
                        <RegisterPage />
                    </GuestRoute>
                }
            />
            <Route
                path="/verify-email"
                element={
                    <GuestRoute>
                        <VerifyEmailPage />
                    </GuestRoute>
                }
            />
        </Routes>
        </>
    );
}

export default App;

import { Routes, Route } from "react-router-dom";

import LandingPage from "./pages/landing/LandingPage";
import LoginPage from "./pages/login/LoginPage";
import RegisterPage from "./pages/register/RegisterPage";
import VerifyEmailPage from "./pages/register/VerifyEmailPage";
import ProfilePage from "./pages/profile/ProfilePage";
import DashboardPage from "./pages/dashboard/DashboardPage";
import HousingPage from "./pages/housing/HousingPage";
import HousingDetailPage from "./pages/housing/HousingDetailPage";
import JobsPage from "./pages/jobs/JobsPage";
import JobDetailPage from "./pages/jobs/JobDetailPage";
import StudyGroupsPage from "./pages/study-groups/StudyGroupsPage";
import StudyGroupDetailPage from "./pages/study-groups/StudyGroupDetailPage";
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
                    path="/jobs"
                    element={
                        <ProtectedRoute>
                            <JobsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/jobs/:id"
                    element={
                        <ProtectedRoute>
                            <JobDetailPage />
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
                    path="/study-groups"
                    element={
                        <ProtectedRoute>
                            <StudyGroupsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/study-groups/:id"
                    element={
                        <ProtectedRoute>
                            <StudyGroupDetailPage />
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

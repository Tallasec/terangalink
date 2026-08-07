import { useEffect, useState } from "react";

import DashboardHeader from "../../components/dashboard/DashboardHeader";
import DashboardWelcomeCard from "../../components/dashboard/DashboardWelcomeCard";
import DashboardQuickActions from "../../components/dashboard/DashboardQuickActions";
import DashboardCommunitySection from "../../components/dashboard/DashboardCommunitySection";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import Loader from "../../components/common/ui/Loader";
import Alert from "../../components/common/ui/Alert";
import { getCurrentUser } from "../../services/user/userService";

function DashboardPage() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let isActive = true;

        async function loadUser() {
            try {
                setLoading(true);
                setError("");

                const currentUser = await getCurrentUser();

                if (isActive) {
                    setUser(currentUser);
                }
            } catch (requestError) {
                if (isActive && requestError?.response?.status !== 401) {
                    setError(
                        requestError?.response?.data?.message ||
                            "Impossible de charger les informations de votre compte.",
                    );
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadUser();

        return () => {
            isActive = false;
        };
    }, []);

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-[#f7fafb] px-4">
                <div className="flex items-center gap-3 rounded-full border border-[#bfc8ca]/40 bg-white px-5 py-3 text-[14px] leading-5 text-[#40484a] shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
                    <Loader size="sm" />
                    Chargement de votre tableau de bord...
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} />

            <main className="mx-auto max-w-[1200px] px-4 py-12 md:px-12">
                {error ? (
                    <Alert type="error" className="mb-12">
                        {error}
                    </Alert>
                ) : null}

                <DashboardWelcomeCard user={user} />
                <DashboardQuickActions user={user} />
                <DashboardCommunitySection />
            </main>

            <DashboardFooter />
        </div>
    );
}

export default DashboardPage;

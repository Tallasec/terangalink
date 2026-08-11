import { useCallback, useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import Loader from "../../components/common/ui/Loader";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import HousingDetailContent from "../../components/housing/details/HousingDetailContent";
import HousingDetailOwner from "../../components/housing/details/HousingDetailOwner";
import HousingDetailPhotoManager from "../../components/housing/details/HousingDetailPhotoManager";
import HousingDetailReservations from "../../components/housing/details/HousingDetailReservations";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import { isHousingOwner } from "../../services/housing/housingHelpers";
import {
    getHousingById,
    getHousingErrorMessage,
    getMyHousingReservation,
} from "../../services/housing/housingService";
import { getCurrentUser } from "../../services/user/userService";

function HousingDetailPage() {
    const { id } = useParams();
    const location = useLocation();
    const [user, setUser] = useState(null);
    const [housing, setHousing] = useState(null);
    const [reservation, setReservation] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [successMessage] = useState(location.state?.message || "");

    const loadHousingData = useCallback(async () => {
        const housingDetails = await getHousingById(id);
        setHousing(housingDetails);

        try {
            const myReservation = await getMyHousingReservation(id);
            setReservation(myReservation);
        } catch {
            setReservation(null);
        }

        return housingDetails;
    }, [id]);

    const loadHousing = useCallback(async (nextHousing) => {
        if (nextHousing) {
            setHousing(nextHousing);
            return nextHousing;
        }

        return loadHousingData();
    }, [loadHousingData]);

    const handleReservationChange = useCallback(async () => {
        await loadHousingData();
    }, [loadHousingData]);

    useEffect(() => {
        let isActive = true;

        async function loadUser() {
            try {
                const currentUser = await getCurrentUser();
                if (isActive) {
                    setUser(currentUser);
                }
            } catch {
                if (isActive) {
                    setUser(null);
                }
            }
        }

        loadUser();

        return () => {
            isActive = false;
        };
    }, []);

    useEffect(() => {
        let isActive = true;

        async function loadHousingDetails() {
            try {
                setLoading(true);
                setError("");

                const housingDetails = await loadHousingData();

                if (isActive) {
                    setHousing(housingDetails);
                }
            } catch (requestError) {
                if (isActive && requestError?.response?.status !== 401) {
                    setError(
                        getHousingErrorMessage(
                            requestError,
                            "Impossible de charger les details de ce logement.",
                        ),
                    );
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        if (id) {
            loadHousingDetails();
        }

        return () => {
            isActive = false;
        };
    }, [id, loadHousingData]);

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-[#f7fafb] px-4">
                <div className="flex items-center gap-3 rounded-full border border-[#bfc8ca]/40 bg-white px-5 py-3 text-[14px] leading-5 text-[#40484a] shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
                    <Loader size="sm" />
                    Chargement du logement...
                </div>
            </div>
        );
    }

    const isOwner = isHousingOwner(user, housing);

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Logement" />

            <main className="mx-auto max-w-[1200px] px-4 py-8 md:px-12 md:py-12">
                <Link
                    className="mb-8 inline-flex items-center gap-2 text-[14px] font-semibold leading-5 text-[#00343a] transition-colors hover:text-[#004851]"
                    to="/housing"
                >
                    <MaterialSymbol icon="arrow_back" />
                    Retour aux logements
                </Link>

                {successMessage ? (
                    <Alert type="success" className="mb-6">
                        {successMessage}
                    </Alert>
                ) : null}

                {error ? (
                    <Alert type="error">{error}</Alert>
                ) : housing ? (
                    <div className="space-y-8">
                        <div className="grid gap-8 lg:grid-cols-[1fr_320px]">
                            <HousingDetailContent housing={housing} />
                            <HousingDetailOwner
                                housing={housing}
                                reservation={reservation}
                                user={user}
                                onReservationChange={handleReservationChange}
                            />
                        </div>

                        {isOwner ? (
                            <>
                                <HousingDetailReservations housingId={housing.id} />
                                <HousingDetailPhotoManager
                                    housing={housing}
                                    onHousingUpdated={loadHousing}
                                />
                            </>
                        ) : null}
                    </div>
                ) : null}
            </main>

            <DashboardFooter />
        </div>
    );
}

export default HousingDetailPage;

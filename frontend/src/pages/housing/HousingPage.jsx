import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import HousingCardGrid from "../../components/housing/cards/HousingCardGrid";
import HousingCardSkeleton from "../../components/housing/cards/HousingCardSkeleton";
import HousingEmptyState from "../../components/housing/cards/HousingEmptyState";
import HousingResultsHeader from "../../components/housing/cards/HousingResultsHeader";
import HousingFilters from "../../components/housing/filters/HousingFilters";
import HousingHeader from "../../components/housing/header/HousingHeader";
import HousingPagination from "../../components/housing/pagination/HousingPagination";
import HousingSearchBar from "../../components/housing/search/HousingSearchBar";
import HousingCreateModal from "../../components/housing/publish/HousingCreateModal";
import {
    DEFAULT_HOUSING_FILTERS,
    hasActiveHousingFilters,
} from "../../services/housing/housingHelpers";
import { fetchHousings, getHousingErrorMessage } from "../../services/housing/housingService";
import { getCurrentUser } from "../../services/user/userService";

function HousingPage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [filters, setFilters] = useState(DEFAULT_HOUSING_FILTERS);
    const [searchValues, setSearchValues] = useState({
        city: "",
        maxPrice: "",
        housingType: "",
    });
    const [housings, setHousings] = useState([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

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

    const loadHousings = useCallback(async (nextFilters) => {
        try {
            setLoading(true);
            setError("");

            const pageData = await fetchHousings(nextFilters);

            setHousings(pageData.content || []);
            setTotalElements(pageData.totalElements || 0);
            setTotalPages(pageData.totalPages || 0);
        } catch (requestError) {
            if (requestError?.response?.status !== 401) {
                setError(getHousingErrorMessage(requestError));
            }
            setHousings([]);
            setTotalElements(0);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        // Chargement des annonces a chaque changement de filtres ou pagination.
        // eslint-disable-next-line react-hooks/set-state-in-effect -- fetch API intentionnel
        loadHousings(filters);
    }, [filters, loadHousings]);

    function handleSearchValueChange(field, value) {
        setSearchValues((previousValues) => ({
            ...previousValues,
            [field]: value,
        }));
    }

    function handleSearchSubmit() {
        setFilters((previousFilters) => ({
            ...previousFilters,
            city: searchValues.city,
            maxPrice: searchValues.maxPrice,
            housingType: searchValues.housingType,
            page: 0,
        }));
    }

    function handleFilterChange(field, value) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            [field]: value,
            page: 0,
        }));

        if (field === "city" || field === "maxPrice" || field === "housingType") {
            setSearchValues((previousValues) => ({
                ...previousValues,
                [field]: value,
            }));
        }
    }

    function handleClearFilters() {
        setFilters(DEFAULT_HOUSING_FILTERS);
        setSearchValues({
            city: "",
            maxPrice: "",
            housingType: "",
        });
    }

    function handleSortChange(sort) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            sort,
            page: 0,
        }));
    }

    function handlePageChange(page) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            page,
        }));
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    const activeFilters = hasActiveHousingFilters(filters);

    function handleHousingCreated(createdHousing) {
        navigate(`/housing/${createdHousing.id}`, {
            state: {
                message:
                    "Annonce publiee. Ajoutez des photos pour completer votre annonce.",
            },
        });
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Logement" />

            <HousingHeader onPublishClick={() => setIsCreateModalOpen(true)} />
            <HousingSearchBar
                loading={loading}
                values={searchValues}
                onChange={handleSearchValueChange}
                onSubmit={handleSearchSubmit}
            />

            <main className="mx-auto max-w-[1200px] px-4 py-10 md:px-12 md:py-12">
                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                <div className="grid gap-8 lg:grid-cols-[280px_1fr]">
                    <div className="lg:sticky lg:top-28 lg:self-start">
                        <HousingFilters
                            filters={filters}
                            hasActiveFilters={activeFilters}
                            onChange={handleFilterChange}
                            onClear={handleClearFilters}
                        />
                    </div>

                    <section>
                        <HousingResultsHeader
                            sort={filters.sort}
                            totalElements={totalElements}
                            onSortChange={handleSortChange}
                        />

                        {loading ? (
                            <HousingCardSkeleton count={filters.size} />
                        ) : housings.length === 0 ? (
                            <HousingEmptyState
                                hasActiveFilters={activeFilters}
                                onClearFilters={handleClearFilters}
                            />
                        ) : (
                            <HousingCardGrid housings={housings} />
                        )}

                        <HousingPagination
                            disabled={loading}
                            page={filters.page}
                            totalPages={totalPages}
                            onPageChange={handlePageChange}
                        />
                    </section>
                </div>
            </main>

            <DashboardFooter />

            <HousingCreateModal
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onCreated={handleHousingCreated}
            />
        </div>
    );
}

export default HousingPage;

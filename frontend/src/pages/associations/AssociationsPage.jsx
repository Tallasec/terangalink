import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import AssociationCardGrid from "../../components/associations/cards/AssociationCardGrid";
import AssociationCardSkeleton from "../../components/associations/cards/AssociationCardSkeleton";
import AssociationEmptyState from "../../components/associations/cards/AssociationEmptyState";
import AssociationFilters from "../../components/associations/filters/AssociationFilters";
import AssociationPagination from "../../components/associations/pagination/AssociationPagination";
import AssociationResultsHeader from "../../components/associations/cards/AssociationResultsHeader";
import AssociationSearchBar from "../../components/associations/search/AssociationSearchBar";
import {
    fetchAssociations,
    getAssociationErrorMessage,
} from "../../services/associations/associationService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_ASSOCIATION_FILTERS = {
    page: 0,
    size: 8,
    sort: "createdAt,desc",
    title: "",
    city: "",
    associationType: "",
    available: "",
};

function AssociationsPage() {
    const [user, setUser] = useState(null);
    const [filters, setFilters] = useState(DEFAULT_ASSOCIATION_FILTERS);
    const [searchValues, setSearchValues] = useState({
        title: "",
        city: "",
    });
    const [associations, setAssociations] = useState([]);
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

    const loadAssociations = useCallback(async (nextFilters) => {
        try {
            setLoading(true);
            setError("");

            const pageData = await fetchAssociations(nextFilters);

            setAssociations(pageData.content || []);
            setTotalElements(pageData.totalElements || 0);
            setTotalPages(pageData.totalPages || 0);
        } catch (requestError) {
            if (requestError?.response?.status !== 401) {
                setError(getAssociationErrorMessage(requestError));
            }
            setAssociations([]);
            setTotalElements(0);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        let isActive = true;

        async function run() {
            if (!isActive) {
                return;
            }

            await loadAssociations(filters);
        }

        run();

        return () => {
            isActive = false;
        };
    }, [filters, loadAssociations]);

    function handleSearchValueChange(field, value) {
        setSearchValues((previousValues) => ({
            ...previousValues,
            [field]: value,
        }));
    }

    function handleSearchSubmit(event) {
        event.preventDefault();

        setFilters((previousFilters) => ({
            ...previousFilters,
            title: searchValues.title,
            city: searchValues.city,
            page: 0,
        }));
    }

    function handleFilterChange(field, value) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            [field]: value,
            page: 0,
        }));

        if (field === "city") {
            setSearchValues((previousValues) => ({
                ...previousValues,
                city: value,
            }));
        }
    }

    function handleClearFilters() {
        setFilters(DEFAULT_ASSOCIATION_FILTERS);
        setSearchValues({
            title: "",
            city: "",
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

    const activeFilters = Boolean(
        filters.title || filters.city || filters.associationType || filters.available !== "",
    );

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Associations" />

            <main className="mx-auto max-w-[1200px] px-4 py-10 md:px-12 md:py-12">
                <section className="rounded-[36px] border border-[#dbe6e6] bg-white px-6 py-8 shadow-[0px_12px_40px_rgba(0,52,58,0.04)] md:px-8 md:py-10">
                    <div className="flex flex-col gap-8 lg:flex-row lg:items-start lg:justify-between">
                        <div className="max-w-[760px]">
                            <h1 className="mt-3 text-[44px] font-semibold leading-[0.98] tracking-[-0.06em] text-[#00343a] md:text-[64px]">
                                Associations et dahiras
                            </h1>
                            <p className="mt-5 max-w-[720px] text-[18px] leading-8 text-[#526062]">
                                Parcourez les structures du reseau, filtrez par type ou
                                ville, puis ouvrez une fiche pour voir les details et les contacts.
                            </p>
                        </div>

                        <Link
                            className="inline-flex shrink-0 items-center justify-center gap-2 rounded-[20px] bg-[#00343a] px-6 py-4 text-[15px] font-semibold text-white shadow-[0px_10px_30px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5 lg:mt-2"
                            to="/associations/create"
                        >
                            <span className="material-symbols-outlined text-[20px]">add</span>
                            Publier une organisation
                        </Link>
                    </div>
                </section>

                <div className="mt-6">
                    <AssociationSearchBar
                        loading={loading}
                        values={searchValues}
                        onChange={handleSearchValueChange}
                        onSubmit={handleSearchSubmit}
                    />
                </div>

                <div className="mt-8 space-y-8">
                    {error ? (
                        <Alert type="error">
                            {error}
                        </Alert>
                    ) : null}

                    <div className="grid gap-8 lg:grid-cols-[300px_minmax(0,1fr)]">
                        <div className="lg:sticky lg:top-28 lg:self-start">
                            <AssociationFilters
                                filters={filters}
                                onChange={handleFilterChange}
                                onClear={handleClearFilters}
                            />
                        </div>

                        <section>
                            <AssociationResultsHeader
                                activeFilters={activeFilters}
                                sort={filters.sort}
                                totalElements={totalElements}
                                onSortChange={handleSortChange}
                            />

                            {loading ? (
                                <AssociationCardSkeleton count={filters.size} />
                            ) : associations.length === 0 ? (
                                <AssociationEmptyState
                                    hasActiveFilters={activeFilters}
                                    onClearFilters={handleClearFilters}
                                />
                            ) : (
                                <AssociationCardGrid associations={associations} />
                            )}

                            <AssociationPagination
                                disabled={loading}
                                page={filters.page}
                                totalPages={totalPages}
                                onPageChange={handlePageChange}
                            />
                        </section>
                    </div>
                </div>
            </main>

            <DashboardFooter />
        </div>
    );
}

export default AssociationsPage;

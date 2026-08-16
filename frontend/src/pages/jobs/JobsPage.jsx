import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import JobCardGrid from "../../components/jobs/cards/JobCardGrid";
import JobCardSkeleton from "../../components/jobs/cards/JobCardSkeleton";
import JobEmptyState from "../../components/jobs/cards/JobEmptyState";
import JobFilters from "../../components/jobs/filters/JobFilters";
import JobHeader from "../../components/jobs/header/JobHeader";
import JobPagination from "../../components/jobs/pagination/JobPagination";
import JobResultsHeader from "../../components/jobs/cards/JobResultsHeader";
import JobSearchBar from "../../components/jobs/search/JobSearchBar";
import JobCreateModal from "../../components/jobs/publish/JobCreateModal";
import { fetchJobs, getJobErrorMessage } from "../../services/jobs/jobService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_JOB_FILTERS = {
    page: 0,
    size: 8,
    sort: "createdAt,desc",
    title: "",
    city: "",
    companyName: "",
    contractType: "",
    salaryMin: "",
    salaryMax: "",
    available: undefined,
};

function JobsPage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [filters, setFilters] = useState(DEFAULT_JOB_FILTERS);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [searchValues, setSearchValues] = useState({
        title: "",
        city: "",
        companyName: "",
    });
    const [jobs, setJobs] = useState([]);
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

    const loadJobs = useCallback(async (nextFilters) => {
        try {
            setLoading(true);
            setError("");

            const pageData = await fetchJobs(nextFilters);

            setJobs(pageData.content || []);
            setTotalElements(pageData.totalElements || 0);
            setTotalPages(pageData.totalPages || 0);
        } catch (requestError) {
            if (requestError?.response?.status !== 401) {
                setError(getJobErrorMessage(requestError));
            }
            setJobs([]);
            setTotalElements(0);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        // Chargement des offres a chaque changement de filtres ou pagination.
        // eslint-disable-next-line react-hooks/set-state-in-effect -- fetch API intentionnel
        loadJobs(filters);
    }, [filters, loadJobs]);

    function handleSearchValueChange(field, value) {
        setSearchValues((previousValues) => ({
            ...previousValues,
            [field]: value,
        }));
    }

    function handleSearchSubmit() {
        setFilters((previousFilters) => ({
            ...previousFilters,
            title: searchValues.title,
            city: searchValues.city,
            companyName: searchValues.companyName,
            page: 0,
        }));
    }

    function handleFilterChange(field, value) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            [field]: value,
            page: 0,
        }));

        if (field === "title" || field === "city" || field === "companyName") {
            setSearchValues((previousValues) => ({
                ...previousValues,
                [field]: value,
            }));
        }
    }

    function handleClearFilters() {
        setFilters(DEFAULT_JOB_FILTERS);
        setSearchValues({
            title: "",
            city: "",
            companyName: "",
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

    function handleJobCreated(createdJob) {
        navigate(`/jobs/${createdJob.id}`);
    }

    const activeFilters = Boolean(
        filters.title ||
            filters.city ||
            filters.companyName ||
            filters.contractType ||
            filters.salaryMin !== "" ||
            filters.salaryMax !== "" ||
            filters.available !== undefined,
    );

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Emplois" />

            <JobHeader
                activeFilters={activeFilters}
                onPublishClick={() => setIsCreateModalOpen(true)}
                totalElements={totalElements}
            />
            <JobSearchBar
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

                <div className="grid gap-8 lg:grid-cols-[300px_minmax(0,1fr)]">
                    <div className="lg:sticky lg:top-28 lg:self-start">
                        <JobFilters
                            filters={filters}
                            onChange={handleFilterChange}
                            onClear={handleClearFilters}
                        />
                    </div>

                    <section>
                        <JobResultsHeader
                            activeFilters={activeFilters}
                            sort={filters.sort}
                            totalElements={totalElements}
                            onSortChange={handleSortChange}
                        />

                        {loading ? (
                            <JobCardSkeleton count={filters.size} />
                        ) : jobs.length === 0 ? (
                            <JobEmptyState onClearFilters={handleClearFilters} />
                        ) : (
                            <JobCardGrid jobs={jobs} />
                        )}

                        <JobPagination
                            disabled={loading}
                            page={filters.page}
                            totalPages={totalPages}
                            onPageChange={handlePageChange}
                        />
                    </section>
                </div>
            </main>

            <DashboardFooter />

            <JobCreateModal
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onCreated={handleJobCreated}
            />
        </div>
    );
}

export default JobsPage;

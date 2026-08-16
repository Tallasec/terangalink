import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import StudyGroupCardGrid from "../../components/study-groups/cards/StudyGroupCardGrid";
import StudyGroupCardSkeleton from "../../components/study-groups/cards/StudyGroupCardSkeleton";
import StudyGroupEmptyState from "../../components/study-groups/cards/StudyGroupEmptyState";
import StudyGroupFilters from "../../components/study-groups/filters/StudyGroupFilters";
import StudyGroupPagination from "../../components/study-groups/pagination/StudyGroupPagination";
import StudyGroupResultsHeader from "../../components/study-groups/cards/StudyGroupResultsHeader";
import StudyGroupSearchBar from "../../components/study-groups/search/StudyGroupSearchBar";
import StudyGroupCreateModal from "../../components/study-groups/publish/StudyGroupCreateModal";
import {
    createStudyGroup,
    fetchStudyGroups,
    getStudyGroupErrorMessage,
} from "../../services/study-groups/studyGroupService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_FILTERS = {
    page: 0,
    size: 8,
    sort: "createdAt,desc",
    title: "",
    subject: "",
    city: "",
    meetingType: "",
    available: "",
    meetingDate: "",
};

function StudyGroupsPage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [filters, setFilters] = useState(DEFAULT_FILTERS);
    const [searchValues, setSearchValues] = useState({
        title: "",
        subject: "",
        city: "",
    });
    const [groups, setGroups] = useState([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

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

    const loadGroups = useCallback(async (nextFilters) => {
        try {
            setLoading(true);
            setError("");

            const pageData = await fetchStudyGroups(nextFilters);

            setGroups(pageData.content || []);
            setTotalElements(pageData.totalElements || 0);
            setTotalPages(pageData.totalPages || 0);
        } catch (requestError) {
            if (requestError?.response?.status !== 401) {
                setError(getStudyGroupErrorMessage(requestError));
            }
            setGroups([]);
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

            await loadGroups(filters);
        }

        run();

        return () => {
            isActive = false;
        };
    }, [filters, loadGroups]);

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
            ...searchValues,
            page: 0,
        }));
    }

    function handleFilterChange(field, value) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            [field]: value,
            page: 0,
        }));
    }

    function handleClearFilters() {
        setFilters(DEFAULT_FILTERS);
        setSearchValues({
            title: "",
            subject: "",
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

    async function handleCreateGroup(payload) {
        const createdGroup = await createStudyGroup(payload);
        setIsCreateModalOpen(false);
        navigate(`/study-groups/${createdGroup.id}`);
        return createdGroup;
    }

    const activeFilters = Boolean(
        filters.title ||
            filters.subject ||
            filters.city ||
            filters.meetingType ||
            filters.available !== "" ||
            filters.meetingDate,
    );

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Groupes" />

            <StudyGroupSearchBar
                values={searchValues}
                onChange={handleSearchValueChange}
                onSubmit={handleSearchSubmit}
                onCreateClick={() => setIsCreateModalOpen(true)}
            />

            <main className="mx-auto max-w-[1200px] px-4 py-10 md:px-12 md:py-12">
                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                <div className="grid gap-8 lg:grid-cols-[300px_minmax(0,1fr)]">
                    <div className="lg:sticky lg:top-28 lg:self-start">
                        <StudyGroupFilters
                            filters={filters}
                            onChange={handleFilterChange}
                            onClear={handleClearFilters}
                        />
                    </div>

                    <section>
                        <StudyGroupResultsHeader
                            activeFilters={activeFilters}
                            sort={filters.sort}
                            totalElements={totalElements}
                            onSortChange={handleSortChange}
                        />

                        {loading ? (
                            <StudyGroupCardSkeleton count={filters.size} />
                        ) : groups.length === 0 ? (
                            <StudyGroupEmptyState onClearFilters={handleClearFilters} />
                        ) : (
                            <StudyGroupCardGrid groups={groups} />
                        )}

                        <StudyGroupPagination
                            disabled={loading}
                            page={filters.page}
                            totalPages={totalPages}
                            onPageChange={handlePageChange}
                        />
                    </section>
                </div>
            </main>

            <DashboardFooter />

            <StudyGroupCreateModal
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onCreated={(createdGroup) => createdGroup}
                onSubmit={handleCreateGroup}
            />
        </div>
    );
}

export default StudyGroupsPage;

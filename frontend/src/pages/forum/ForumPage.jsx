import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import ForumCategoryCard from "../../components/forum/ForumCategoryCard";
import ForumSidebar from "../../components/forum/ForumSidebar";
import ForumTopicCard from "../../components/forum/ForumTopicCard";
import {
    fetchForumAnswers,
    fetchForumTopics,
    getForumErrorMessage,
} from "../../services/forum/forumService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_FORUM_FILTERS = {
    page: 0,
    size: 6,
    sort: "createdAt,desc",
    title: "",
    category: "",
};

const FORUM_CATEGORY_CARDS = [
    {
        category: "ADMINISTRATIF",
        description: "Campus France, titre de séjour, CAF",
    },
    {
        category: "LOGEMENT",
        description: "Colocations, bailleurs, quartiers",
    },
    {
        category: "ETUDES",
        description: "Licence, master, révisions",
    },
    {
        category: "VIE_ETUDIANTE",
        description: "Événements, cuisine, entraide",
    },
];

const FORUM_TABS = [
    { id: "recent", label: "Récentes", sort: "createdAt,desc" },
    { id: "popular", label: "Populaires", sort: "views,desc" },
    { id: "unanswered", label: "Sans réponse", sort: "createdAt,desc" },
    { id: "pinned", label: "Épinglées", disabled: true },
];

function ForumPage() {
    const [user, setUser] = useState(null);
    const [filters, setFilters] = useState(DEFAULT_FORUM_FILTERS);
    const [activeTab, setActiveTab] = useState("recent");
    const [searchValues, setSearchValues] = useState({ title: "" });
    const [topics, setTopics] = useState([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [categoryCounts, setCategoryCounts] = useState({});
    const [loading, setLoading] = useState(true);
    const [categoryLoading, setCategoryLoading] = useState(true);
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

    const loadTopics = useCallback(async (nextFilters, nextTab) => {
        try {
            setLoading(true);
            setError("");

            const resolvedSort =
                nextTab === "popular" ? "views,desc" : nextFilters.sort || "createdAt,desc";

            const pageData = await fetchForumTopics({
                ...nextFilters,
                sort: resolvedSort,
            });

            const content = pageData.content || [];
            const topicsWithStats = await Promise.all(
                content.map(async (topic) => {
                    try {
                        const answers = await fetchForumAnswers(topic.id);
                        return {
                            ...topic,
                            answerCount: answers?.length || 0,
                        };
                    } catch {
                        return {
                            ...topic,
                            answerCount: 0,
                        };
                    }
                }),
            );

            const visibleTopics =
                nextTab === "unanswered"
                    ? topicsWithStats.filter((topic) => (topic.answerCount || 0) === 0)
                    : topicsWithStats;

            setTopics(visibleTopics);
            setTotalElements(pageData.totalElements || 0);
            setTotalPages(pageData.totalPages || 0);
        } catch (requestError) {
            if (requestError?.response?.status !== 401) {
                setError(getForumErrorMessage(requestError));
            }
            setTopics([]);
            setTotalElements(0);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    }, []);

    const loadCategoryCounts = useCallback(async () => {
        try {
            setCategoryLoading(true);

            const results = await Promise.all(
                FORUM_CATEGORY_CARDS.map(async ({ category }) => {
                    try {
                        const pageData = await fetchForumTopics({
                            category,
                            page: 0,
                            size: 1,
                            sort: "createdAt,desc",
                        });

                        return [category, pageData.totalElements || 0];
                    } catch {
                        return [category, 0];
                    }
                }),
            );

            setCategoryCounts(Object.fromEntries(results));
        } catch {
            setCategoryCounts({});
        } finally {
            setCategoryLoading(false);
        }
    }, []);

    useEffect(() => {
        // Chargement intentionnel des compteurs par catégorie.
        // eslint-disable-next-line react-hooks/set-state-in-effect -- fetch API intentionnel
        loadCategoryCounts();
    }, [loadCategoryCounts]);

    useEffect(() => {
        // Chargement volontaire des sujets du forum selon la recherche et le tri actifs.
        // eslint-disable-next-line react-hooks/set-state-in-effect -- fetch API intentionnel
        loadTopics(filters, activeTab);
    }, [filters, activeTab, loadTopics]);

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
            page: 0,
        }));
        setActiveTab("recent");
    }

    function handleCategoryClick(category) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            category: previousFilters.category === category ? "" : category,
            page: 0,
        }));
        setActiveTab("recent");
    }

    function handleTabChange(tabId) {
        const tab = FORUM_TABS.find((item) => item.id === tabId);

        if (!tab || tab.disabled) {
            return;
        }

        setActiveTab(tabId);
        setFilters((previousFilters) => ({
            ...previousFilters,
            sort: tab.sort,
            page: 0,
        }));
    }

    function handleClearFilters() {
        setFilters(DEFAULT_FORUM_FILTERS);
        setSearchValues({ title: "" });
        setActiveTab("recent");
    }

    function handlePageChange(page) {
        setFilters((previousFilters) => ({
            ...previousFilters,
            page,
        }));
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    const activeFilters = Boolean(filters.title || filters.category);
    const showPagination = totalPages > 1;

    const topAuthors = useMemo(() => {
        const authors = new Map();

        topics.forEach((topic) => {
            const key = topic.authorId || `${topic.authorFirstName}-${topic.authorLastName}`;
            const current = authors.get(key) || {
                key,
                authorFirstName: topic.authorFirstName,
                authorLastName: topic.authorLastName,
                count: 0,
            };

            current.count += 1;
            authors.set(key, current);
        });

        return Array.from(authors.values())
            .sort((left, right) => right.count - left.count)
            .slice(0, 3);
    }, [topics]);

    const topicCountLabel = `${totalElements} discussion${totalElements > 1 ? "s" : ""}`;

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Forum" />

            <main className="mx-auto max-w-[1200px] px-4 py-8 md:px-12 md:py-12">
                <section className="grid gap-6 lg:grid-cols-[minmax(0,1.2fr)_320px] lg:items-end">
                    <div>
                        <p className="text-[12px] font-semibold uppercase tracking-[0.22em] text-[#8a661f]">
                            La parole au reseau
                        </p>
                        <h1 className="mt-3 text-[44px] font-semibold leading-[0.98] tracking-[-0.06em] text-[#00343a] md:text-[64px]">
                            Forum TerangaLink
                        </h1>
                        <p className="mt-5 max-w-[740px] text-[18px] leading-8 text-[#526062] md:text-[20px]">
                            Posez vos questions sur les visas, le logement, les études ou la vie
                            quotidienne en France. Les membres répondent, partagent et s&apos;entraident.
                        </p>
                    </div>

                    <div className="lg:justify-self-end">
                        <Link
                            to="/forum/create"
                            className="inline-flex w-full items-center justify-center gap-2 rounded-[20px] bg-[#f8cf86] px-6 py-4 text-[18px] font-medium text-[#7a5a1b] shadow-[0px_10px_30px_rgba(122,90,27,0.12)] transition-transform duration-150 hover:-translate-y-0.5 md:w-[290px]"
                        >
                            <MaterialSymbol icon="add" />
                            Nouvelle discussion
                        </Link>
                    </div>
                </section>

                <section className="mt-10">
                    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                        {FORUM_CATEGORY_CARDS.map(({ category }) => (
                            <ForumCategoryCard
                                key={category}
                                category={category}
                                count={categoryLoading ? "..." : categoryCounts[category] || 0}
                                active={filters.category === category}
                                onClick={() => handleCategoryClick(category)}
                            />
                        ))}
                    </div>
                </section>

                <section className="mt-10">
                    <form onSubmit={handleSearchSubmit} className="flex flex-col gap-4 lg:flex-row">
                        <div className="relative flex-1">
                            <MaterialSymbol
                                icon="search"
                                className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-[20px] border border-[#dbe6e6] bg-white py-4 pl-12 pr-4 text-[15px] text-[#181c1d] shadow-[0px_10px_30px_rgba(0,52,58,0.05)] outline-none transition focus:border-[#00343a]"
                                name="title"
                                placeholder="Rechercher un sujet..."
                                value={searchValues.title}
                                onChange={(event) => handleSearchValueChange("title", event.target.value)}
                            />
                        </div>

                        <button
                            className="inline-flex items-center justify-center rounded-[20px] bg-[#00343a] px-6 py-4 text-[15px] font-semibold text-white transition-transform duration-150 hover:-translate-y-0.5"
                            type="submit"
                        >
                            Rechercher
                        </button>

                        {activeFilters ? (
                            <button
                                className="inline-flex items-center justify-center rounded-[20px] border border-[#dbe6e6] bg-white px-6 py-4 text-[15px] font-semibold text-[#00343a] transition-colors hover:border-[#00343a]"
                                type="button"
                                onClick={handleClearFilters}
                            >
                                Réinitialiser
                            </button>
                        ) : null}
                    </form>
                </section>

                <section className="mt-10 flex flex-wrap items-center gap-3">
                    {FORUM_TABS.map((tab) => (
                        <button
                            key={tab.id}
                            type="button"
                            disabled={tab.disabled}
                            onClick={() => handleTabChange(tab.id)}
                            className={`rounded-full px-5 py-2.5 text-[14px] font-semibold transition-colors ${
                                activeTab === tab.id
                                    ? "bg-[#00343a] text-white"
                                    : tab.disabled
                                        ? "cursor-not-allowed bg-[#e5e8e9] text-[#8b9495]"
                                        : "bg-[#e5e8e9] text-[#526062] hover:bg-[#d7dfdf]"
                            }`}
                        >
                            {tab.label}
                        </button>
                    ))}
                    <span className="ml-auto text-[14px] font-medium text-[#70797a]">
                        {topicCountLabel}
                    </span>
                </section>

                {error ? (
                    <Alert type="error" className="mt-8">
                        {error}
                    </Alert>
                ) : null}

                <section className="mt-8 grid gap-6 lg:grid-cols-[minmax(0,1fr)_330px]">
                    <div className="space-y-4">
                        {loading ? (
                            <ForumTopicListSkeleton count={filters.size} />
                        ) : topics.length === 0 ? (
                            <ForumEmptyState onReset={handleClearFilters} />
                        ) : (
                            topics.map((topic) => <ForumTopicCard key={topic.id} topic={topic} />)
                        )}

                        {showPagination ? (
                            <ForumPagination
                                disabled={loading}
                                page={filters.page}
                                totalPages={totalPages}
                                onPageChange={handlePageChange}
                            />
                        ) : null}
                    </div>

                    <div id="forum">
                        <ForumSidebar topAuthors={topAuthors} />
                    </div>
                </section>
            </main>

            <DashboardFooter />
        </div>
    );
}

function ForumEmptyState({ onReset }) {
    return (
        <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
            <h2 className="text-[28px] font-semibold text-[#00343a]">Aucune discussion trouvée</h2>
            <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                Essayez une autre recherche, changez de catégorie ou lancez la première discussion.
            </p>
            <div className="mt-6 flex flex-wrap justify-center gap-3">
                <button
                    className="rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                    type="button"
                    onClick={onReset}
                >
                    Réinitialiser
                </button>
                <Link
                    className="rounded-full border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                    to="/forum/create"
                >
                    Nouvelle discussion
                </Link>
            </div>
        </div>
    );
}

function ForumPagination({ disabled, page, totalPages, onPageChange }) {
    if (totalPages <= 1) {
        return null;
    }

    return (
        <nav
            aria-label="Pagination du forum"
            className="mt-10 flex items-center justify-center gap-2"
        >
            <button
                aria-label="Page précédente"
                className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#dce7e8] bg-white text-[#40484a] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page <= 0}
                onClick={() => onPageChange(page - 1)}
            >
                <MaterialSymbol icon="chevron_left" />
            </button>

            {getVisiblePages(page, totalPages).map((pageNumber, index) =>
                pageNumber === "ellipsis" ? (
                    <span
                        key={`ellipsis-${index}`}
                        className="flex h-10 min-w-10 items-center justify-center text-[#70797a]"
                    >
                        ...
                    </span>
                ) : (
                    <button
                        key={pageNumber}
                        aria-current={pageNumber === page ? "page" : undefined}
                        className={`flex h-10 min-w-10 items-center justify-center rounded-xl px-3 text-[14px] font-semibold transition-colors ${
                            pageNumber === page
                                ? "bg-[#00343a] text-white"
                                : "border border-[#dce7e8] bg-white text-[#40484a] hover:border-[#00343a] hover:text-[#00343a]"
                        }`}
                        type="button"
                        disabled={disabled}
                        onClick={() => onPageChange(pageNumber)}
                    >
                        {pageNumber + 1}
                    </button>
                ),
            )}

            <button
                aria-label="Page suivante"
                className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#dce7e8] bg-white text-[#40484a] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page >= totalPages - 1}
                onClick={() => onPageChange(page + 1)}
            >
                <MaterialSymbol icon="chevron_right" />
            </button>
        </nav>
    );
}

function getVisiblePages(currentPage, totalPages) {
    if (totalPages <= 5) {
        return Array.from({ length: totalPages }, (_, index) => index);
    }

    const pages = new Set([0, totalPages - 1, currentPage]);

    if (currentPage > 0) {
        pages.add(currentPage - 1);
    }

    if (currentPage < totalPages - 1) {
        pages.add(currentPage + 1);
    }

    const sortedPages = [...pages].sort((left, right) => left - right);
    const visiblePages = [];

    sortedPages.forEach((pageNumber, index) => {
        if (index > 0 && pageNumber - sortedPages[index - 1] > 1) {
            visiblePages.push("ellipsis");
        }

        visiblePages.push(pageNumber);
    });

    return visiblePages;
}

function ForumTopicListSkeleton({ count }) {
    return (
        <div className="space-y-4">
            {Array.from({ length: count }).map((_, index) => (
                <div
                    key={index}
                    className="rounded-[28px] border border-[#edf3f3] bg-white p-5 shadow-[0px_10px_32px_rgba(0,52,58,0.05)]"
                >
                    <div className="flex gap-4">
                        <div className="h-12 w-12 animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="flex-1 space-y-3">
                            <div className="h-5 w-32 animate-pulse rounded-full bg-[#edf3f3]" />
                            <div className="h-7 w-4/5 animate-pulse rounded-full bg-[#edf3f3]" />
                            <div className="h-5 w-full animate-pulse rounded-full bg-[#edf3f3]" />
                            <div className="h-5 w-2/3 animate-pulse rounded-full bg-[#edf3f3]" />
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}

export default ForumPage;

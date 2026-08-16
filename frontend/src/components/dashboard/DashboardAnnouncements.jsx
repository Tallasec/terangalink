import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import Alert from "../common/ui/Alert";
import DashboardAnnouncementCard from "./DashboardAnnouncementCard";
import { fetchForumAnswers, fetchForumTopics, getForumErrorMessage } from "../../services/forum/forumService";

function DashboardAnnouncements() {
    const [topics, setTopics] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let isActive = true;

        async function loadTopics() {
            try {
                setLoading(true);
                setError("");

                const pageData = await fetchForumTopics({
                    page: 0,
                    size: 2,
                    sort: "createdAt,desc",
                });

                const topicsWithStats = await Promise.all(
                    (pageData.content || []).map(async (topic) => {
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

                if (isActive) {
                    setTopics(topicsWithStats);
                }
            } catch (requestError) {
                if (isActive && requestError?.response?.status !== 401) {
                    setError(getForumErrorMessage(requestError, "Impossible de charger le forum."));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadTopics();

        return () => {
            isActive = false;
        };
    }, []);

    return (
        <section className="lg:col-span-2">
            <div className="mb-6 flex items-center justify-between">
                <div>
                    <h2 className="text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                        Mises a jour recentes
                    </h2>
                    <p className="mt-1 text-[14px] text-[#526062]">
                        Les dernieres discussions du forum apparaissent ici.
                    </p>
                </div>
                <Link
                    className="text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a] hover:underline"
                    to="/forum"
                >
                    Tout voir
                </Link>
            </div>

            {error ? (
                <Alert type="warning" className="mb-4">
                    {error}
                </Alert>
            ) : null}

            <div className="space-y-4">
                {loading ? (
                    <DashboardAnnouncementSkeleton />
                ) : topics.length === 0 ? (
                    <div className="rounded-xl border border-dashed border-[#bfc8ca]/30 bg-white p-6 text-center text-[14px] text-[#526062]">
                        Aucune discussion recente pour le moment.
                    </div>
                ) : (
                    topics.map((topic) => (
                        <DashboardAnnouncementCard
                            key={topic.id}
                            topic={topic}
                        />
                    ))
                )}
            </div>
        </section>
    );
}

function DashboardAnnouncementSkeleton() {
    return (
        <div className="space-y-4">
            <div className="h-32 animate-pulse rounded-xl border border-[#bfc8ca]/30 bg-white" />
            <div className="h-32 animate-pulse rounded-xl border border-[#bfc8ca]/30 bg-white" />
        </div>
    );
}

export default DashboardAnnouncements;

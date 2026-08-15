import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import StudyGroupMembersPanel from "../../components/study-groups/details/StudyGroupMembersPanel";
import {
    formatStudyGroupDate,
    getStudyGroupBadgeLabel,
    getStudyGroupErrorMessage,
    getStudyGroupLocationLabel,
    getStudyGroupMembers,
    getStudyGroupById,
    joinStudyGroup,
    leaveStudyGroup,
} from "../../services/study-groups/studyGroupService";
import { getCurrentUser } from "../../services/user/userService";

function StudyGroupDetailPage() {
    const { id } = useParams();
    const [group, setGroup] = useState(null);
    const [members, setMembers] = useState([]);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [membersLoading, setMembersLoading] = useState(false);
    const [actionLoading, setActionLoading] = useState(false);
    const [error, setError] = useState("");
    const [actionError, setActionError] = useState("");

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

        async function loadGroup() {
            try {
                setLoading(true);
                setError("");
                const data = await getStudyGroupById(id);
                if (isActive) {
                    setGroup(data);
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setError(getStudyGroupErrorMessage(requestError));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadGroup();

        return () => {
            isActive = false;
        };
    }, [id]);

    useEffect(() => {
        if (!group || !user) {
            return;
        }

        const canViewMembers =
            user.role === "ADMIN" ||
            user.id === group.creatorId ||
            Boolean(group.currentUserMember);

        if (!canViewMembers) {
            return;
        }

        let isActive = true;

        async function loadMembers() {
            try {
                setMembersLoading(true);
                const data = await getStudyGroupMembers(id);
                if (isActive) {
                    setMembers(data || []);
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setActionError(getStudyGroupErrorMessage(requestError, "Impossible de charger les membres."));
                }
            } finally {
                if (isActive) {
                    setMembersLoading(false);
                }
            }
        }

        loadMembers();

        return () => {
            isActive = false;
        };
    }, [group, id, user]);

    const isOwner = Boolean(user && group && (user.role === "ADMIN" || user.id === group.creatorId));
    const isMember = Boolean(group?.currentUserMember);
    const canJoin = Boolean(group && user && !isOwner && !isMember && group.available && !group.full);
    const canLeave = Boolean(group && user && !isOwner && isMember);

    async function handleJoin() {
        try {
            setActionLoading(true);
            setActionError("");
            const updatedGroup = await joinStudyGroup(id);
            setGroup(updatedGroup);
        } catch (requestError) {
            setActionError(getStudyGroupErrorMessage(requestError, "Impossible de rejoindre ce groupe."));
        } finally {
            setActionLoading(false);
        }
    }

    async function handleLeave() {
        try {
            setActionLoading(true);
            setActionError("");
            const updatedGroup = await leaveStudyGroup(id);
            setGroup(updatedGroup);
        } catch (requestError) {
            setActionError(getStudyGroupErrorMessage(requestError, "Impossible de quitter ce groupe."));
        } finally {
            setActionLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Groupes" />

            <main className="mx-auto max-w-[1200px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to="/study-groups"
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour aux groupes
                    </Link>
                </div>

                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                {loading ? (
                    <div className="space-y-4">
                        <div className="h-6 w-1/2 animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="h-4 w-1/3 animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="h-[420px] animate-pulse rounded-[28px] bg-white shadow-[0px_12px_40px_rgba(0,52,58,0.05)]" />
                    </div>
                ) : !group ? (
                    <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
                        <h1 className="text-[28px] font-semibold text-[#00343a]">Aucun groupe trouvé</h1>
                        <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                            Ce groupe n'existe plus ou a été supprimé.
                        </p>
                        <Link
                            className="mt-6 inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                            to="/study-groups"
                        >
                            Explorer les groupes
                        </Link>
                    </div>
                ) : (
                    <div className="grid gap-6 lg:grid-cols-[minmax(0,1.45fr)_minmax(320px,0.85fr)]">
                        <article className="overflow-hidden rounded-[32px] border border-[#dbe6e6] bg-white shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                            <div className="border-b border-[#edf3f3] bg-[linear-gradient(180deg,#f7fafb_0%,#ffffff_100%)] px-6 py-6 md:px-8">
                                <div className="flex flex-wrap items-center gap-2">
                                    <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                                        {getStudyGroupBadgeLabel(group)}
                                    </span>
                                    <span className="rounded-full bg-[#f7fafb] px-3 py-1 text-[12px] font-semibold text-[#526062]">
                                        {group.available ? "Disponible" : "Fermé"}
                                    </span>
                                </div>

                                <h1 className="mt-4 text-[34px] font-semibold leading-[1.04] tracking-[-0.04em] text-[#00343a] md:text-[46px]">
                                    {group.title}
                                </h1>
                                <p className="mt-3 text-[16px] text-[#526062] md:text-[18px]">
                                    {group.subject} • {getStudyGroupLocationLabel(group)}
                                </p>

                                <div className="mt-5 flex flex-wrap gap-3">
                                    <DetailPill icon="groups" value={`${group.memberCount || 0}/${group.maxMembers} membres`} />
                                    <DetailPill icon="location_on" value={group.city} />
                                    <DetailPill icon="schedule" value={formatStudyGroupDate(group.meetingDate)} />
                                </div>
                            </div>

                            <div className="px-6 py-6 md:px-8">
                                <section>
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Description du groupe
                                    </h2>
                                    <p className="mt-4 whitespace-pre-line text-[15px] leading-7 text-[#526062]">
                                        {group.description}
                                    </p>
                                </section>

                                <section className="mt-8">
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Informations pratiques
                                    </h2>

                                    <div className="mt-4 grid gap-4 sm:grid-cols-2">
                                        <InfoCard icon="event" label="Rencontre" value={group.meetingType} />
                                        <InfoCard icon="location_city" label="Ville" value={group.city} />
                                        <InfoCard icon="place" label="Lieu" value={group.location || "Non renseigné"} />
                                        <InfoCard icon="groups" label="Capacité" value={`${group.memberCount || 0}/${group.maxMembers}`} />
                                    </div>
                                </section>
                            </div>
                        </article>

                        <aside className="space-y-5">
                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Actions
                                </p>
                                <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                                    Rejoindre le groupe
                                </h2>

                                {actionError ? (
                                    <Alert type="warning" className="mt-4">
                                        {actionError}
                                    </Alert>
                                ) : null}

                                <div className="mt-4">
                                    {isOwner ? (
                                        <div className="rounded-[24px] border border-[#dbe6e6] bg-[#f7fafb] p-4">
                                            <p className="text-[14px] font-semibold text-[#00343a]">
                                                Vous gérez ce groupe
                                            </p>
                                            <p className="mt-2 text-[13px] leading-6 text-[#526062]">
                                                En tant que créateur, vous pouvez consulter la liste des membres.
                                            </p>
                                        </div>
                                    ) : canJoin ? (
                                        <button
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white shadow-[0px_10px_24px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-70"
                                            type="button"
                                            onClick={handleJoin}
                                            disabled={actionLoading}
                                        >
                                            <MaterialSymbol icon="group_add" filled />
                                            {actionLoading ? "Connexion..." : "Rejoindre"}
                                        </button>
                                    ) : canLeave ? (
                                        <button
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a] shadow-[0px_10px_24px_rgba(0,52,58,0.08)] ring-1 ring-[#dbe6e6] transition-transform duration-150 hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-70"
                                            type="button"
                                            onClick={handleLeave}
                                            disabled={actionLoading}
                                        >
                                            <MaterialSymbol icon="logout" />
                                            {actionLoading ? "Traitement..." : "Quitter"}
                                        </button>
                                    ) : (
                                        <div className="rounded-[24px] border border-[#dbe6e6] bg-[#f7fafb] p-4">
                                            <p className="text-[14px] font-semibold text-[#00343a]">
                                                {isMember ? "Vous êtes déjà membre" : group.full ? "Groupe complet" : "Inscription fermée"}
                                            </p>
                                            <p className="mt-2 text-[13px] leading-6 text-[#526062]">
                                                {isMember
                                                    ? "Vous participez déjà à ce groupe."
                                                    : group.full
                                                        ? "La capacité maximale a été atteinte."
                                                        : "Ce groupe n'accepte plus de nouvelles inscriptions."}
                                            </p>
                                        </div>
                                    )}
                                </div>
                            </div>

                            {isOwner ? (
                                membersLoading ? (
                                    <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                        <div className="h-5 w-1/3 animate-pulse rounded-full bg-[#edf3f3]" />
                                        <div className="mt-4 space-y-3">
                                            <div className="h-14 animate-pulse rounded-[22px] bg-[#edf3f3]" />
                                            <div className="h-14 animate-pulse rounded-[22px] bg-[#edf3f3]" />
                                        </div>
                                    </div>
                                ) : (
                                    <StudyGroupMembersPanel members={members} />
                                )
                            ) : group?.currentUserMember ? (
                                membersLoading ? (
                                    <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                        <div className="h-5 w-1/3 animate-pulse rounded-full bg-[#edf3f3]" />
                                        <div className="mt-4 space-y-3">
                                            <div className="h-14 animate-pulse rounded-[22px] bg-[#edf3f3]" />
                                            <div className="h-14 animate-pulse rounded-[22px] bg-[#edf3f3]" />
                                        </div>
                                    </div>
                                ) : (
                                    <StudyGroupMembersPanel members={members} />
                                )
                            ) : null}
                        </aside>
                    </div>
                )}
            </main>

            <DashboardFooter />
        </div>
    );
}

function DetailPill({ icon, value }) {
    return (
        <div className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white">
            <MaterialSymbol icon={icon} />
            {value}
        </div>
    );
}

function InfoCard({ icon, label, value }) {
    return (
        <div className="rounded-[24px] border border-[#edf3f3] bg-[#f7fafb] p-4">
            <div className="flex items-center gap-2 text-[#00343a]">
                <MaterialSymbol icon={icon} />
                <span className="text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                    {label}
                </span>
            </div>
            <div className="mt-3 text-[15px] font-semibold text-[#181c1d]">{value}</div>
        </div>
    );
}

export default StudyGroupDetailPage;

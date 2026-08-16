import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import {
    deleteAssociation,
    formatAssociationDate,
    formatRelativeAssociationDate,
    getAssociationAuthorLabel,
    getAssociationErrorMessage,
    getAssociationLocationLabel,
    getAssociationStatusLabel,
    getAssociationTypeLabel,
    getAssociationById,
} from "../../services/associations/associationService";
import { getCurrentUser } from "../../services/user/userService";

function AssociationDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [association, setAssociation] = useState(null);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
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

        async function loadAssociation() {
            try {
                setLoading(true);
                setError("");
                const data = await getAssociationById(id);
                if (isActive) {
                    setAssociation(data);
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setError(getAssociationErrorMessage(requestError));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadAssociation();

        return () => {
            isActive = false;
        };
    }, [id]);

    const isOwner = Boolean(user && association && (user.role === "ADMIN" || user.id === association.creatorId));
    const canManage = Boolean(association && isOwner);

    async function handleDelete() {
        const confirmed = window.confirm("Supprimer cette organisation ?");

        if (!confirmed) {
            return;
        }

        try {
            setActionLoading(true);
            setActionError("");
            await deleteAssociation(id);
            navigate("/associations");
        } catch (requestError) {
            setActionError(getAssociationErrorMessage(requestError, "Impossible de supprimer cette organisation."));
        } finally {
            setActionLoading(false);
        }
    }

    const logoUrl = association?.logoUrl || "";

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Associations" />

            <main className="mx-auto max-w-[1200px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to="/associations"
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour aux associations
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
                ) : !association ? (
                    <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
                        <h1 className="text-[28px] font-semibold text-[#00343a]">Aucune organisation trouvee</h1>
                        <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                            Cette organisation n&apos;existe plus ou a ete supprimee.
                        </p>
                        <Link
                            className="mt-6 inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                            to="/associations"
                        >
                            Explorer les associations
                        </Link>
                    </div>
                ) : (
                    <div className="grid gap-6 lg:grid-cols-[minmax(0,1.45fr)_minmax(320px,0.85fr)]">
                        <article className="overflow-hidden rounded-[32px] border border-[#dbe6e6] bg-white shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                            <div className="border-b border-[#edf3f3] bg-[linear-gradient(180deg,#f7fafb_0%,#ffffff_100%)] px-6 py-6 md:px-8">
                                <div className="flex flex-wrap items-center gap-2">
                                    <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                                        {getAssociationTypeLabel(association.associationType)}
                                    </span>
                                    <span className="rounded-full bg-[#f7fafb] px-3 py-1 text-[12px] font-semibold text-[#526062]">
                                        {getAssociationStatusLabel(association)}
                                    </span>
                                </div>

                                <div className="mt-5 flex flex-col gap-5 md:flex-row md:items-start md:justify-between">
                                    <div className="min-w-0">
                                        <h1 className="text-[34px] font-semibold leading-[1.04] tracking-[-0.04em] text-[#00343a] md:text-[46px]">
                                            {association.title}
                                        </h1>
                                        <p className="mt-3 text-[16px] text-[#526062] md:text-[18px]">
                                            {getAssociationLocationLabel(association)}
                                        </p>
                                    </div>

                                    <div className="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-[28px] bg-[linear-gradient(135deg,#00343a_0%,#0d5960_100%)] text-[24px] font-semibold tracking-[0.08em] text-white">
                                        {logoUrl ? (
                                            <img
                                                alt={association.title}
                                                className="h-full w-full object-cover"
                                                src={logoUrl}
                                            />
                                        ) : (
                                            getAssociationInitials(association)
                                        )}
                                    </div>
                                </div>

                                <div className="mt-5 flex flex-wrap gap-3">
                                    <DetailPill icon="location_on" value={association.city} />
                                    <DetailPill icon="schedule" value={formatRelativeAssociationDate(association.createdAt)} />
                                    <DetailPill icon="groups" value={getAssociationAuthorLabel(association)} />
                                </div>
                            </div>

                            <div className="px-6 py-6 md:px-8">
                                <section>
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Description de l&apos;organisation
                                    </h2>
                                    <p className="mt-4 whitespace-pre-line text-[15px] leading-7 text-[#526062]">
                                        {association.description}
                                    </p>
                                </section>

                                <section className="mt-8">
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Informations pratiques
                                    </h2>

                                    <div className="mt-4 grid gap-4 sm:grid-cols-2">
                                        <InfoCard icon="location_city" label="Ville" value={association.city} />
                                        <InfoCard icon="place" label="Adresse" value={association.address || "Non renseignee"} />
                                        <InfoCard icon="mail" label="Email" value={association.contactEmail || "Non renseigne"} />
                                        <InfoCard icon="call" label="Telephone" value={association.phone || "Non renseigne"} />
                                        <InfoCard icon="public" label="Site web" value={association.website || "Non renseigne"} />
                                        <InfoCard icon="badge" label="Type" value={getAssociationTypeLabel(association.associationType)} />
                                    </div>
                                </section>
                            </div>
                        </article>

                        <aside className="space-y-5">
                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Contacts
                                </p>
                                <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                                    Rejoindre ou contacter
                                </h2>

                                <div className="mt-4 space-y-3">
                                    {association.website ? (
                                        <a
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                                            href={association.website}
                                            rel="noreferrer"
                                            target="_blank"
                                        >
                                            <MaterialSymbol icon="language" filled />
                                            Ouvrir le site
                                        </a>
                                    ) : null}

                                    {association.contactEmail ? (
                                        <a
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                                            href={`mailto:${association.contactEmail}`}
                                        >
                                            <MaterialSymbol icon="mail" />
                                            Ecrire un email
                                        </a>
                                    ) : null}

                                    {association.phone ? (
                                        <a
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                                            href={`tel:${association.phone}`}
                                        >
                                            <MaterialSymbol icon="call" />
                                            Appeler
                                        </a>
                                    ) : null}
                                </div>
                            </div>

                            {canManage ? (
                                <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                    <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                        Gestion
                                    </p>
                                    <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                                        Modifier cette fiche
                                    </h2>

                                    {actionError ? (
                                        <Alert type="warning" className="mt-4">
                                            {actionError}
                                        </Alert>
                                    ) : null}

                                    <div className="mt-4 space-y-3">
                                        <Link
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                                            to={`/associations/${association.id}/edit`}
                                        >
                                            <MaterialSymbol icon="edit" />
                                            Modifier
                                        </Link>

                                        <button
                                            className="inline-flex w-full items-center justify-center gap-2 rounded-2xl border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#9b3d3d] disabled:cursor-not-allowed disabled:opacity-70"
                                            type="button"
                                            onClick={handleDelete}
                                            disabled={actionLoading}
                                        >
                                            <MaterialSymbol icon="delete" />
                                            {actionLoading ? "Suppression..." : "Supprimer"}
                                        </button>
                                    </div>
                                </div>
                            ) : null}

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Details
                                </p>
                                <div className="mt-4 space-y-4 text-[14px] text-[#526062]">
                                    <InfoRow label="Creee le" value={formatAssociationDate(association.createdAt)} />
                                    <InfoRow label="Mise a jour" value={formatAssociationDate(association.updatedAt)} />
                                    <InfoRow label="Reference" value={`#${association.id}`} />
                                    <InfoRow
                                        label="Auteur"
                                        value={getAssociationAuthorLabel(association)}
                                    />
                                </div>
                            </div>
                        </aside>
                    </div>
                )}
            </main>

            <DashboardFooter />
        </div>
    );
}

function getAssociationInitials(association) {
    const first = association?.title?.trim()?.[0] || "";
    const second = association?.city?.trim()?.[0] || "";

    return `${first}${second}`.trim() || "TL";
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

function InfoRow({ label, value }) {
    return (
        <div className="flex items-start justify-between gap-4 border-b border-[#edf3f3] pb-3 last:border-b-0 last:pb-0">
            <span className="text-[#70797a]">{label}</span>
            <span className="text-right font-semibold text-[#181c1d]">{value}</span>
        </div>
    );
}

export default AssociationDetailPage;


import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import JobApplicationModal from "../../components/jobs/application/JobApplicationModal";
import {
    formatJobDate,
    formatJobSalary,
    formatRelativeJobDate,
    getJobApplicationStatusLabel,
    getJobApplicationStatusTone,
    getJobBadgeLabel,
    getJobErrorMessage,
    getJobLocationLabel,
    getJobById,
    getJobApplications,
    getMyJobApplication,
} from "../../services/jobs/jobService";
import { getCurrentUser } from "../../services/user/userService";

function JobDetailPage() {
    const { id } = useParams();
    const [job, setJob] = useState(null);
    const [user, setUser] = useState(null);
    const [application, setApplication] = useState(null);
    const [receivedApplications, setReceivedApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [applicationLoading, setApplicationLoading] = useState(false);
    const [receivedApplicationsLoading, setReceivedApplicationsLoading] = useState(false);
    const [error, setError] = useState("");
    const [applicationError, setApplicationError] = useState("");
    const [receivedApplicationsError, setReceivedApplicationsError] = useState("");
    const [isApplicationModalOpen, setIsApplicationModalOpen] = useState(false);

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

        async function loadJob() {
            try {
                setLoading(true);
                setError("");
                const data = await getJobById(id);
                if (isActive) {
                    setJob(data);
                }
            } catch (err) {
                if (err?.response?.status !== 401) {
                    setError(getJobErrorMessage(err));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadJob();

        return () => {
            isActive = false;
        };
    }, [id]);

    useEffect(() => {
        if (!job || !user) {
            return;
        }

        let isActive = true;
        const canViewReceivedApplications = user.role === "ADMIN" || user.id === job.ownerId;

        async function loadApplications() {
            try {
                if (canViewReceivedApplications) {
                    setReceivedApplicationsLoading(true);
                    setReceivedApplicationsError("");
                    const response = await getJobApplications(id);
                    if (isActive) {
                        setReceivedApplications(response || []);
                        setApplication(null);
                    }
                } else {
                    setApplicationLoading(true);
                    setApplicationError("");
                    const response = await getMyJobApplication(id);
                    if (isActive) {
                        setApplication(response || null);
                        setReceivedApplications([]);
                    }
                }
            } catch (err) {
                if (err?.response?.status !== 401) {
                    const message = getJobErrorMessage(
                        err,
                        canViewReceivedApplications
                            ? "Impossible de charger les candidatures."
                            : "Impossible de charger votre candidature.",
                    );

                    if (canViewReceivedApplications) {
                        setReceivedApplicationsError(message);
                    } else {
                        setApplicationError(message);
                    }
                }
            } finally {
                if (isActive) {
                    setApplicationLoading(false);
                    setReceivedApplicationsLoading(false);
                }
            }
        }

        loadApplications();

        return () => {
            isActive = false;
        };
    }, [id, job, user]);

    const displayedApplication = application && job && application.jobPostId === job.id ? application : null;
    const isOwner = Boolean(user && job?.ownerId && user.id === job.ownerId);
    const canViewReceivedApplications = Boolean(user && job && (user.role === "ADMIN" || user.id === job.ownerId));
    const canApply = Boolean(job && !isOwner && job.available && !displayedApplication);
    const existingApplicationLabel = displayedApplication
        ? getJobApplicationStatusLabel(displayedApplication.status)
        : "";
    const existingApplicationTone = displayedApplication
        ? getJobApplicationStatusTone(displayedApplication.status)
        : "neutral";

    function handleApplied(nextApplication) {
        setApplication(nextApplication);
        setIsApplicationModalOpen(false);
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Emplois" />

            <main className="mx-auto max-w-[1100px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to="/jobs"
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour aux offres
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
                ) : !job ? (
                    <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
                        <h1 className="text-[28px] font-semibold text-[#00343a]">Aucune offre trouvée</h1>
                        <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                            Cette offre n'existe plus ou a été supprimée.
                        </p>
                        <Link
                            className="mt-6 inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                            to="/jobs"
                        >
                            Explorer les offres
                        </Link>
                    </div>
                ) : (
                    <div className="grid gap-6 lg:grid-cols-[minmax(0,1.45fr)_minmax(320px,0.85fr)]">
                        <article className="overflow-hidden rounded-[32px] border border-[#dbe6e6] bg-white shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                            <div className="border-b border-[#edf3f3] bg-[linear-gradient(180deg,#f7fafb_0%,#ffffff_100%)] px-6 py-6 md:px-8">
                                <div className="flex flex-wrap items-center gap-2">
                                    <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                                        {getJobBadgeLabel(job)}
                                    </span>
                                    <span className="rounded-full bg-[#f7fafb] px-3 py-1 text-[12px] font-semibold text-[#526062]">
                                        {job.available ? "Disponible" : "Indisponible"}
                                    </span>
                                </div>

                                <h1 className="mt-4 text-[34px] font-semibold leading-[1.04] tracking-[-0.04em] text-[#00343a] md:text-[46px]">
                                    {job.title}
                                </h1>
                                <p className="mt-3 text-[16px] text-[#526062] md:text-[18px]">
                                    {job.companyName} • {getJobLocationLabel(job)}
                                </p>

                                <div className="mt-5 flex flex-wrap gap-3">
                                    <div className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white">
                                        <MaterialSymbol icon="payments" />
                                        {formatJobSalary(job.salary)}
                                    </div>
                                    <div className="inline-flex items-center gap-2 rounded-full bg-[#f7fafb] px-4 py-2 text-[13px] font-semibold text-[#526062]">
                                        <MaterialSymbol icon="location_on" />
                                        {job.city}
                                    </div>
                                    <div className="inline-flex items-center gap-2 rounded-full bg-[#f7fafb] px-4 py-2 text-[13px] font-semibold text-[#526062]">
                                        <MaterialSymbol icon="schedule" />
                                        {formatRelativeJobDate(job.createdAt)}
                                    </div>
                                </div>
                            </div>

                            <div className="px-6 py-6 md:px-8">
                                <section>
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Description du poste
                                    </h2>
                                    <p className="mt-4 whitespace-pre-line text-[15px] leading-7 text-[#526062]">
                                        {job.description}
                                    </p>
                                </section>

                                <section className="mt-8">
                                    <h2 className="text-[18px] font-semibold text-[#00343a]">
                                        Informations pratiques
                                    </h2>

                                    <div className="mt-4 grid gap-4 sm:grid-cols-2">
                                        <DetailCard
                                            icon="business"
                                            label="Entreprise"
                                            value={job.companyName}
                                        />
                                        <DetailCard icon="location_city" label="Ville" value={job.city} />
                                        <DetailCard
                                            icon="contract"
                                            label="Contrat"
                                            value={job.contractType || "Non renseigné"}
                                        />
                                        <DetailCard
                                            icon="payments"
                                            label="Salaire"
                                            value={formatJobSalary(job.salary)}
                                        />
                                    </div>
                                </section>
                            </div>
                        </article>

                        <aside className="space-y-5">
                            {canViewReceivedApplications ? (
                                <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                    <div className="flex items-start justify-between gap-4">
                                        <div>
                                            <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                                Recrutement
                                            </p>
                                            <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                                                Candidatures reçues
                                            </h2>
                                        </div>
                                        <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                                            {receivedApplications.length}
                                        </span>
                                    </div>

                                    {receivedApplicationsLoading ? (
                                        <div className="mt-4 space-y-3">
                                            <div className="h-4 w-full animate-pulse rounded-full bg-[#edf3f3]" />
                                            <div className="h-4 w-2/3 animate-pulse rounded-full bg-[#edf3f3]" />
                                        </div>
                                    ) : receivedApplicationsError ? (
                                        <Alert type="warning" className="mt-4">
                                            {receivedApplicationsError}
                                        </Alert>
                                    ) : receivedApplications.length === 0 ? (
                                        <p className="mt-4 text-[14px] leading-6 text-[#526062]">
                                            Aucune candidature n’a encore été déposée pour cette offre.
                                        </p>
                                    ) : (
                                        <div className="mt-4 space-y-4">
                                            {receivedApplications.map((applicationItem) => (
                                                <ApplicationCard
                                                    key={applicationItem.id}
                                                    application={applicationItem}
                                                />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            ) : null}

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Actions
                                </p>
                                <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                                    Postuler
                                </h2>

                                {applicationLoading ? (
                                    <div className="mt-4 space-y-3">
                                        <div className="h-4 w-full animate-pulse rounded-full bg-[#edf3f3]" />
                                        <div className="h-4 w-3/4 animate-pulse rounded-full bg-[#edf3f3]" />
                                    </div>
                                ) : displayedApplication ? (
                                    <div className="mt-4 rounded-[24px] border border-[#dbe6e6] bg-[#f7fafb] p-4">
                                        <div className={`inline-flex rounded-full px-3 py-1 text-[12px] font-semibold ${getApplicationToneClassName(existingApplicationTone)}`}>
                                            {existingApplicationLabel}
                                        </div>
                                        <p className="mt-3 text-[14px] leading-6 text-[#526062]">
                                            Votre candidature a bien été enregistrée.
                                        </p>
                                        <div className="mt-4 space-y-2 text-[13px] text-[#526062]">
                                            <div>
                                                <span className="font-semibold text-[#181c1d]">Téléphone: </span>
                                                {displayedApplication.phoneNumber}
                                            </div>
                                            <div>
                                                <span className="font-semibold text-[#181c1d]">Envoyée le: </span>
                                                {formatJobDate(displayedApplication.createdAt)}
                                            </div>
                                        </div>
                                        <div className="mt-4">
                                            <a
                                                className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white"
                                                href={displayedApplication.cvUrl}
                                                rel="noreferrer"
                                                target="_blank"
                                            >
                                                <MaterialSymbol icon="description" filled />
                                                Voir le CV
                                            </a>
                                            <p className="mt-2 text-[12px] text-[#70797a]">
                                                {displayedApplication.cvOriginalFilename}
                                            </p>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="mt-4">
                                        <p className="text-[14px] leading-6 text-[#526062]">
                                            Renseignez un numéro de téléphone, ajoutez votre CV et
                                            laissez un message si vous voulez préciser votre profil.
                                        </p>

                                        <button
                                            className="mt-5 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white shadow-[0px_10px_24px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-70"
                                            type="button"
                                            onClick={() => setIsApplicationModalOpen(true)}
                                            disabled={!canApply}
                                        >
                                            <MaterialSymbol icon="send" filled />
                                            {isOwner
                                                ? "Vous êtes l'auteur"
                                                : !job.available
                                                    ? "Offre indisponible"
                                                    : "Postuler maintenant"}
                                        </button>

                                        <p className="mt-3 text-[13px] text-[#70797a]">
                                            Le message est facultatif. Le CV est obligatoire.
                                        </p>

                                        {!canApply && !isOwner ? (
                                            <p className="mt-3 text-[13px] text-[#70797a]">
                                                {job.available
                                                    ? "Vous avez déjà postulé à cette offre."
                                                    : "Cette offre n'est plus disponible."}
                                            </p>
                                        ) : null}
                                    </div>
                                )}

                                {applicationError ? (
                                    <Alert type="warning" className="mt-4">
                                        {applicationError}
                                    </Alert>
                                ) : null}
                            </div>

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Détails
                                </p>

                                <div className="mt-4 space-y-4 text-[14px] text-[#526062]">
                                    <InfoRow label="Créée le" value={formatJobDate(job.createdAt)} />
                                    <InfoRow label="Mise à jour" value={formatJobDate(job.updatedAt)} />
                                    <InfoRow label="Référence" value={`#${job.id}`} />
                                    <InfoRow
                                        label="Auteur"
                                        value={
                                            job.ownerFirstName || job.ownerLastName
                                                ? `${job.ownerFirstName || ""} ${job.ownerLastName || ""}`.trim()
                                                : "Non renseigné"
                                        }
                                    />
                                </div>
                            </div>

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-[linear-gradient(135deg,#00343a_0%,#0d5960_100%)] p-6 text-white shadow-[0px_16px_40px_rgba(0,52,58,0.16)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-white/70">
                                    Conseil
                                </p>
                                <h2 className="mt-2 text-[22px] font-semibold">Restez visible</h2>
                                <p className="mt-3 text-[14px] leading-6 text-white/85">
                                    Combinez recherche par ville et type de contrat pour trouver
                                    plus vite les offres pertinentes.
                                </p>
                            </div>
                        </aside>
                    </div>
                )}
            </main>

            <DashboardFooter />

            {job && canApply ? (
                <JobApplicationModal
                    isOpen={isApplicationModalOpen}
                    job={job}
                    onApplied={handleApplied}
                    onClose={() => setIsApplicationModalOpen(false)}
                />
            ) : null}
        </div>
    );
}

function DetailCard({ icon, label, value }) {
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

function ApplicationCard({ application }) {
    return (
        <div className="rounded-[24px] border border-[#edf3f3] bg-[#f7fafb] p-4">
            <div className="flex items-start justify-between gap-3">
                <div>
                    <p className="text-[15px] font-semibold text-[#181c1d]">
                        {application.applicantFirstName} {application.applicantLastName}
                    </p>
                    <p className="mt-1 text-[13px] text-[#70797a]">
                        {formatJobDate(application.createdAt)}
                    </p>
                </div>
                <span className={`rounded-full px-3 py-1 text-[12px] font-semibold ${getApplicationToneClassName(getJobApplicationStatusTone(application.status))}`}>
                    {getJobApplicationStatusLabel(application.status)}
                </span>
            </div>

            <div className="mt-4 space-y-2 text-[13px] text-[#526062]">
                <div>
                    <span className="font-semibold text-[#181c1d]">Téléphone: </span>
                    {application.phoneNumber}
                </div>
                {application.message ? (
                    <div>
                        <span className="font-semibold text-[#181c1d]">Message: </span>
                        {application.message}
                    </div>
                ) : null}
                <div>
                    <span className="font-semibold text-[#181c1d]">CV: </span>
                    <a
                        className="text-[#00343a] underline"
                        href={application.cvUrl}
                        rel="noreferrer"
                        target="_blank"
                    >
                        {application.cvOriginalFilename}
                    </a>
                </div>
            </div>
        </div>
    );
}

function getApplicationToneClassName(tone) {
    switch (tone) {
        case "primary":
            return "bg-[#e4f5f4] text-[#00514a]";
        case "accent":
            return "bg-[#fff2db] text-[#8c5a06]";
        case "secondary":
            return "bg-[#e7f5e8] text-[#2f6b31]";
        default:
            return "bg-[#edf3f3] text-[#526062]";
    }
}

export default JobDetailPage;

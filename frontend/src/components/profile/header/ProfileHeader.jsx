import { getUserFullName } from "../../../services/user/userHelpers";

function formatCreatedAt(createdAt) {
    if (!createdAt) {
        return "Date d'inscription indisponible";
    }

    const date = new Date(createdAt);

    if (Number.isNaN(date.getTime())) {
        return "Date d'inscription indisponible";
    }

    return new Intl.DateTimeFormat("fr-FR", {
        day: "numeric",
        month: "long",
        year: "numeric",
    }).format(date);
}

function ProfileHeader({ user, isEditing, onEdit }) {
    const fullName = getUserFullName(user);
    const roleLabel =
        user?.role === "ADMIN" ? "Administrateur" : user?.role === "USER" ? "Étudiant" : "Membre";

    return (
        <section className="overflow-hidden rounded-[28px] border border-[#dce7e8] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.08)]">
            <div className="h-2 bg-gradient-to-r from-[#00343a] via-[#0f5b60] to-[#fdd798]" />

            <div className="grid gap-0 lg:grid-cols-[1.2fr_0.8fr]">
                <div className="p-8 sm:p-10">
                    <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-[#f8fbfb] px-4 py-2 text-sm font-medium text-[#00343a]">
                        <span className="material-symbols-outlined text-[18px]">person</span>
                        Mon profil
                    </div>

                    <h1 className="max-w-2xl text-[34px] font-bold leading-[42px] tracking-[-0.02em] text-[#181c1d] sm:text-[44px] sm:leading-[52px]">
                        {fullName || "Profil TerangaLink"}
                    </h1>

                    <p className="mt-4 max-w-2xl text-[16px] leading-7 text-[#40484a]">
                        Consultez et modifiez votre identité TerangaLink depuis un espace pensé pour
                        suivre votre parcours académique et collectif.
                    </p>

                    <div className="mt-8 flex flex-wrap gap-3">
                        <span className="rounded-full bg-[#00343a] px-4 py-2 text-[12px] font-semibold uppercase tracking-[0.08em] text-white">
                            {roleLabel}
                        </span>
                        <span className="rounded-full bg-[#f1f4f5] px-4 py-2 text-[12px] font-semibold uppercase tracking-[0.08em] text-[#40484a]">
                            Inscrit le {formatCreatedAt(user?.createdAt)}
                        </span>
                        <span className="rounded-full bg-[#fdd798] px-4 py-2 text-[12px] font-semibold uppercase tracking-[0.08em] text-[#785c29]">
                            {isEditing ? "Mode édition" : "Lecture seule"}
                        </span>
                    </div>
                </div>

                <div className="flex items-stretch bg-[#00343a] p-8 sm:p-10">
                    <div className="flex w-full flex-col justify-between rounded-[24px] border border-white/10 bg-white/5 p-6 text-white backdrop-blur-sm">
                        <div>
                            <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-white/70">
                                Adresse e-mail
                            </p>
                            <p className="mt-2 break-words text-[20px] font-semibold leading-8">
                                {user?.email || "Aucune adresse e-mail"}
                            </p>
                            <p className="mt-3 text-[14px] leading-6 text-white/75">
                                Les modifications sont synchronisées avec votre compte principal.
                            </p>
                        </div>

                        <button
                            className="mt-8 inline-flex items-center justify-center gap-2 rounded-xl bg-white px-4 py-3 text-[12px] font-semibold uppercase tracking-[0.08em] text-[#00343a] transition-transform duration-150 hover:scale-[0.99]"
                            type="button"
                            onClick={onEdit}
                        >
                            <span className="material-symbols-outlined text-[18px]">
                                {isEditing ? "edit_note" : "edit"}
                            </span>
                            {isEditing ? "Poursuivre l'édition" : "Modifier mon profil"}
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default ProfileHeader;

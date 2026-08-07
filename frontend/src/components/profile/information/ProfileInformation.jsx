function formatCreatedAt(createdAt) {
    if (!createdAt) {
        return "Indisponible";
    }

    const date = new Date(createdAt);

    if (Number.isNaN(date.getTime())) {
        return "Indisponible";
    }

    return new Intl.DateTimeFormat("fr-FR", {
        day: "2-digit",
        month: "long",
        year: "numeric",
    }).format(date);
}

function InfoRow({ label, value }) {
    return (
        <div className="rounded-2xl border border-[#dce7e8] bg-[#f8fbfb] p-4">
            <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#6a7375]">
                {label}
            </p>
            <p className="mt-2 break-words text-[15px] font-medium leading-6 text-[#181c1d]">
                {value || "Indisponible"}
            </p>
        </div>
    );
}

function ProfileInformation({ user }) {
    const roleLabel =
        user?.role === "ADMIN" ? "Administrateur" : user?.role === "USER" ? "Étudiant" : "Membre";

    return (
        <section className="overflow-hidden rounded-[28px] border border-[#dce7e8] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.08)]">
            <div className="border-b border-[#dce7e8] px-8 py-6">
                <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#6a7375]">
                    Consultation
                </p>
                <h2 className="mt-2 text-[24px] font-semibold leading-8 text-[#00343a]">
                    Informations du profil
                </h2>
            </div>

            <div className="grid gap-8 p-8 lg:grid-cols-2">
                <div className="space-y-4">
                    <div className="grid gap-4 sm:grid-cols-2">
                        <InfoRow label="Prénom" value={user?.firstName} />
                        <InfoRow label="Nom" value={user?.lastName} />
                    </div>
                    <InfoRow label="Adresse e-mail" value={user?.email} />
                    <InfoRow label="Rôle" value={roleLabel} />
                </div>

                <div className="space-y-4">
                    <div className="grid gap-4 sm:grid-cols-2">
                        <InfoRow label="Université" value={user?.university} />
                        <InfoRow label="Ville" value={user?.city} />
                    </div>
                    <InfoRow label="Domaine d’étude" value={user?.fieldOfStudy} />
                    <InfoRow label="Date d'inscription" value={formatCreatedAt(user?.createdAt)} />
                </div>
            </div>
        </section>
    );
}

export default ProfileInformation;

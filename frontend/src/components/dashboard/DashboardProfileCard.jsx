import DashboardCard from "./DashboardCard";
import { Link } from "react-router-dom";

import { getUserFullName, getUserInitials } from "../../services/user/userHelpers";

function DashboardProfileCard({ user }) {
    const fullName = getUserFullName(user);
    const initials = getUserInitials(user);

    return (
        <DashboardCard
            as={Link}
            to="/profile"
            className="flex flex-col gap-8 rounded-xl bg-[#f1f4f5] p-8 md:col-span-8 md:flex-row md:items-center"
        >
            <div className="flex h-32 w-32 flex-shrink-0 items-center justify-center rounded-full border-4 border-white bg-[#00343a] text-[28px] font-semibold leading-none tracking-[0.05em] text-white shadow-sm">
                {initials}
            </div>

            <div className="flex-grow">
                <h3 className="mb-1 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                    Mon profil
                </h3>
                <p className="mb-2 text-[16px] leading-6 text-[#40484a]">
                    {fullName || "Profil connecté"}
                </p>
                <p className="mb-4 text-[14px] leading-5 text-[#6a7375]">
                    {user?.email || "Aucune adresse e-mail disponible"}
                </p>
                <div className="flex flex-wrap items-center gap-3">
                    <span className="rounded-full bg-white px-3 py-1 text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a]">
                        Compte actif
                    </span>
                    <span className="rounded-full bg-[#bfc8ca]/20 px-3 py-1 text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#40484a]">
                        Accès sécurisé
                    </span>
                </div>
            </div>
        </DashboardCard>
    );
}

export default DashboardProfileCard;

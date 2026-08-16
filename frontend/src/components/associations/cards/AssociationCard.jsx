import { Link } from "react-router-dom";

import MaterialSymbol from "../../common/MaterialSymbol";
import {
    formatRelativeAssociationDate,
    getAssociationAuthorLabel,
    getAssociationLocationLabel,
    getAssociationStatusLabel,
    getAssociationTypeClassName,
    getAssociationTypeLabel,
} from "../../../services/associations/associationService";

function getAssociationInitials(association) {
    const first = association?.title?.trim()?.[0] || "";
    const second = association?.city?.trim()?.[0] || "";

    return `${first}${second}`.trim() || "TL";
}

function AssociationCard({ association }) {
    return (
        <Link
            className="group flex h-full flex-col overflow-hidden rounded-[28px] border border-[#dbe6e6] bg-white shadow-[0px_10px_34px_rgba(0,52,58,0.05)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_18px_48px_rgba(0,52,58,0.12)]"
            to={`/associations/${association.id}`}
        >
            <div className="flex items-start justify-between gap-4 p-5">
                <div className="flex min-w-0 items-center gap-3">
                    <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,#00343a_0%,#0d5960_100%)] text-[13px] font-semibold tracking-[0.08em] text-white">
                        {getAssociationInitials(association)}
                    </div>
                    <div className="min-w-0">
                        <p className="truncate text-[13px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                            {getAssociationTypeLabel(association.associationType)}
                        </p>
                        <p className="mt-1 text-[13px] text-[#839194]">
                            {getAssociationLocationLabel(association)}
                        </p>
                    </div>
                </div>

                <span
                    className={`rounded-full px-3 py-2 text-[12px] font-semibold ${getAssociationTypeClassName(
                        association.associationType,
                    )}`}
                >
                    {getAssociationStatusLabel(association)}
                </span>
            </div>

            <div className="flex flex-1 flex-col px-5 pb-5">
                <h3 className="text-[21px] font-semibold leading-7 tracking-[-0.02em] text-[#00343a] transition-colors group-hover:text-[#0d5960]">
                    {association.title}
                </h3>

                <p className="mt-3 line-clamp-3 text-[14px] leading-6 text-[#526062]">
                    {association.description}
                </p>

                <div className="mt-5 grid gap-2 text-[13px] text-[#526062] sm:grid-cols-2">
                    <div className="inline-flex items-center gap-2 rounded-2xl bg-[#f7fafb] px-3 py-2">
                        <MaterialSymbol icon="location_on" className="text-[#00343a]" />
                        <span>{association.city}</span>
                    </div>
                    <div className="inline-flex items-center gap-2 rounded-2xl bg-[#f7fafb] px-3 py-2">
                        <MaterialSymbol icon="schedule" className="text-[#00343a]" />
                        <span>{formatRelativeAssociationDate(association.createdAt)}</span>
                    </div>
                </div>

                <div className="mt-4 flex flex-wrap gap-2">
                    <span className="inline-flex rounded-full bg-[#eef8f8] px-3 py-2 text-[12px] font-semibold text-[#00343a]">
                        {getAssociationAuthorLabel(association)}
                    </span>
                    <span className="inline-flex rounded-full bg-[#f7fafb] px-3 py-2 text-[12px] font-semibold text-[#526062]">
                        {association.available ? "Disponible" : "Non disponible"}
                    </span>
                </div>
            </div>

            <div className="flex items-center justify-between gap-3 border-t border-[#edf3f3] px-5 py-4">
                <span className="text-[12px] font-semibold uppercase tracking-[0.14em] text-[#8b9496]">
                    {association.website ? "Site disponible" : "Contact direct"}
                </span>
                <span className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white transition-transform duration-150 group-hover:translate-x-0.5">
                    Détails
                    <MaterialSymbol icon="arrow_forward" className="text-[18px]" />
                </span>
            </div>
        </Link>
    );
}

export default AssociationCard;

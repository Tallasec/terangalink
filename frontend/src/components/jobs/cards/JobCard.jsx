import { Link } from "react-router-dom";

import MaterialSymbol from "../../common/MaterialSymbol";
import {
    formatJobSalary,
    formatRelativeJobDate,
    getJobBadgeLabel,
    getJobBadgeTone,
    getJobLocationLabel,
} from "../../../services/jobs/jobService";

function getToneClassName(tone) {
    switch (tone) {
        case "accent":
            return "bg-[#fff2db] text-[#8c5a06]";
        case "secondary":
            return "bg-[#e4f5f4] text-[#00514a]";
        case "warm":
            return "bg-[#f4e8d3] text-[#7c5700]";
        case "neutral":
            return "bg-[#edf3f3] text-[#526062]";
        default:
            return "bg-[#e4f5f4] text-[#00514a]";
    }
}

function getCompanyInitials(companyName = "") {
    return companyName
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0]?.toUpperCase())
        .join("")
        || "TL";
}

function JobCard({ job }) {
    const tone = getJobBadgeTone(job);

    return (
        <Link
            className="group flex h-full flex-col overflow-hidden rounded-[28px] border border-[#dbe6e6] bg-white shadow-[0px_10px_34px_rgba(0,52,58,0.05)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_18px_48px_rgba(0,52,58,0.12)]"
            to={`/jobs/${job.id}`}
        >
            <div className="flex items-start justify-between gap-4 p-5">
                <div className="flex min-w-0 items-center gap-3">
                    <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl border border-[#dce7e8] bg-[linear-gradient(135deg,#00343a_0%,#0d5960_100%)] text-[13px] font-semibold tracking-[0.08em] text-white">
                        {getCompanyInitials(job.companyName)}
                    </div>

                    <div className="min-w-0">
                        <p className="truncate text-[13px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                            {job.companyName}
                        </p>
                        <p className="mt-1 text-[13px] text-[#839194]">
                            {getJobLocationLabel(job)}
                        </p>
                    </div>
                </div>

                <button
                    aria-label="Sauvegarder l'offre"
                    className="rounded-full p-2 text-[#839194] transition-colors hover:bg-[#f7fafb] hover:text-[#00343a]"
                    type="button"
                >
                    <MaterialSymbol icon="bookmark" />
                </button>
            </div>

            <div className="flex flex-1 flex-col px-5 pb-5">
                <h3 className="text-[21px] font-semibold leading-7 tracking-[-0.02em] text-[#00343a] transition-colors group-hover:text-[#0d5960]">
                    {job.title}
                </h3>

                <p className="mt-3 line-clamp-3 text-[14px] leading-6 text-[#526062]">
                    {job.description}
                </p>

                <div className="mt-5 flex flex-wrap gap-2">
                    <span className={`inline-flex items-center gap-1 rounded-full px-3 py-2 text-[12px] font-semibold ${getToneClassName(tone)}`}>
                        <MaterialSymbol icon="payments" className="text-[18px]" />
                        {formatJobSalary(job.salary)}
                    </span>
                    <span className="inline-flex items-center gap-1 rounded-full bg-[#f7fafb] px-3 py-2 text-[12px] font-semibold text-[#526062]">
                        <MaterialSymbol icon="location_on" className="text-[18px]" />
                        {job.city}
                    </span>
                </div>

                <div className="mt-5 flex flex-wrap gap-2">
                    <span className="inline-flex rounded-full bg-[#eef8f8] px-3 py-2 text-[12px] font-semibold text-[#00343a]">
                        {getJobBadgeLabel(job)}
                    </span>
                    <span className="inline-flex rounded-full bg-[#f7fafb] px-3 py-2 text-[12px] font-semibold text-[#526062]">
                        {job.available ? "Disponible" : "Non disponible"}
                    </span>
                </div>
            </div>

            <div className="flex items-center justify-between gap-3 border-t border-[#edf3f3] px-5 py-4">
                <span className="text-[12px] font-semibold uppercase tracking-[0.14em] text-[#8b9496]">
                    {formatRelativeJobDate(job.createdAt)}
                </span>
                <span className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white transition-transform duration-150 group-hover:translate-x-0.5">
                    Détails
                    <MaterialSymbol icon="arrow_forward" className="text-[18px]" />
                </span>
            </div>
        </Link>
    );
}

export default JobCard;

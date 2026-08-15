import { Link } from "react-router-dom";

import MaterialSymbol from "../../common/MaterialSymbol";
import {
    formatRelativeStudyGroupDate,
    getStudyGroupBadgeLabel,
    getStudyGroupBadgeTone,
    getStudyGroupLocationLabel,
    getMeetingTypeLabel,
} from "../../../services/study-groups/studyGroupService";

function toneClassName(tone) {
    switch (tone) {
        case "accent":
            return "bg-[#fff2db] text-[#8c5a06]";
        case "secondary":
            return "bg-[#e4f5f4] text-[#00514a]";
        case "warm":
            return "bg-[#f4e8d3] text-[#7c5700]";
        default:
            return "bg-[#edf3f3] text-[#526062]";
    }
}

function StudyGroupCard({ group }) {
    const tone = getStudyGroupBadgeTone(group);

    return (
        <Link
            className="group flex h-full flex-col overflow-hidden rounded-[28px] border border-[#dbe6e6] bg-white shadow-[0px_10px_34px_rgba(0,52,58,0.05)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_18px_48px_rgba(0,52,58,0.12)]"
            to={`/study-groups/${group.id}`}
        >
            <div className="flex items-start justify-between gap-4 p-5">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.18em] text-[#70797a]">
                        {group.subject}
                    </p>
                    <h3 className="mt-2 text-[22px] font-semibold leading-7 tracking-[-0.02em] text-[#00343a] transition-colors group-hover:text-[#0d5960]">
                        {group.title}
                    </h3>
                </div>
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,#00343a_0%,#0d5960_100%)] text-[13px] font-semibold tracking-[0.1em] text-white">
                    {getMeetingTypeLabel(group.meetingType).slice(0, 2).toUpperCase()}
                </div>
            </div>

            <div className="flex flex-1 flex-col px-5 pb-5">
                <p className="line-clamp-3 text-[14px] leading-6 text-[#526062]">
                    {group.description}
                </p>

                <div className="mt-5 flex flex-wrap gap-2">
                    <span className={`inline-flex items-center gap-1 rounded-full px-3 py-2 text-[12px] font-semibold ${toneClassName(tone)}`}>
                        <MaterialSymbol icon="event" className="text-[18px]" />
                        {getStudyGroupBadgeLabel(group)}
                    </span>
                    <span className="inline-flex items-center gap-1 rounded-full bg-[#f7fafb] px-3 py-2 text-[12px] font-semibold text-[#526062]">
                        <MaterialSymbol icon="location_on" className="text-[18px]" />
                        {group.city}
                    </span>
                </div>

                <div className="mt-4 grid gap-2 text-[13px] text-[#526062] sm:grid-cols-2">
                    <div className="inline-flex items-center gap-2 rounded-2xl bg-[#f7fafb] px-3 py-2">
                        <MaterialSymbol icon="groups" className="text-[#00343a]" />
                        <span>
                            {group.memberCount || 0}/{group.maxMembers} membres
                        </span>
                    </div>
                    <div className="inline-flex items-center gap-2 rounded-2xl bg-[#f7fafb] px-3 py-2">
                        <MaterialSymbol icon="schedule" className="text-[#00343a]" />
                        <span>{formatRelativeStudyGroupDate(group.createdAt)}</span>
                    </div>
                </div>
            </div>

            <div className="flex items-center justify-between gap-3 border-t border-[#edf3f3] px-5 py-4">
                <span className="text-[12px] font-semibold uppercase tracking-[0.14em] text-[#8b9496]">
                    {getStudyGroupLocationLabel(group)}
                </span>
                <span className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white transition-transform duration-150 group-hover:translate-x-0.5">
                    Voir
                    <MaterialSymbol icon="arrow_forward" className="text-[18px]" />
                </span>
            </div>
        </Link>
    );
}

export default StudyGroupCard;

import { Link } from "react-router-dom";

import DashboardCard from "./DashboardCard";
import MaterialSymbol from "../common/MaterialSymbol";
import {
    formatRelativeForumDate,
    getForumAuthorInitials,
    getForumAuthorLabel,
    getForumCategoryAccentClassName,
    getForumCategoryLabel,
    getForumTopicExcerpt,
} from "../../services/forum/forumService";

function DashboardAnnouncementCard({ topic }) {
    return (
        <DashboardCard className="rounded-xl border border-[#bfc8ca]/30 bg-white p-6">
            <Link to={`/forum/${topic.id}`} className="block">
                <div className="flex gap-4">
                    <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-[#00343a] text-[14px] font-semibold text-white">
                        {getForumAuthorInitials(topic)}
                    </div>

                    <div className="min-w-0 flex-1">
                        <div className="mb-2 flex flex-wrap items-center gap-2">
                            <span
                                className={`rounded-full px-3 py-1 text-[12px] font-semibold ${getForumCategoryAccentClassName(
                                    topic.category,
                                )}`}
                            >
                                {getForumCategoryLabel(topic.category)}
                            </span>
                            <span className="text-[13px] text-[#70797a]">
                                {formatRelativeForumDate(topic.createdAt)}
                            </span>
                        </div>

                        <h4 className="text-[20px] font-medium leading-7 text-[#181c1d]">
                            {topic.title}
                        </h4>
                        <p className="mt-2 text-[15px] leading-6 text-[#40484a]">
                            {getForumTopicExcerpt(topic, 120)}
                        </p>

                        <div className="mt-4 flex flex-wrap items-center gap-4 text-[13px] text-[#70797a]">
                            <span className="font-semibold text-[#526062]">{getForumAuthorLabel(topic)}</span>
                            <span className="inline-flex items-center gap-1">
                                <MaterialSymbol icon="forum" className="text-[18px]" />
                                {topic.answerCount || 0}
                            </span>
                            <span className="inline-flex items-center gap-1">
                                <MaterialSymbol icon="visibility" className="text-[18px]" />
                                {topic.views || 0}
                            </span>
                        </div>
                    </div>
                </div>
            </Link>
        </DashboardCard>
    );
}

export default DashboardAnnouncementCard;

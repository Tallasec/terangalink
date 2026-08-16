import { Link } from "react-router-dom";

import MaterialSymbol from "../common/MaterialSymbol";
import {
    formatRelativeForumDate,
    getForumAuthorInitials,
    getForumAuthorLabel,
    getForumCategoryAccentClassName,
    getForumCategoryLabel,
    getForumTopicExcerpt,
} from "../../services/forum/forumService";

function ForumTopicCard({ topic }) {
    const authorLabel = getForumAuthorLabel(topic);
    const initials = getForumAuthorInitials(topic);
    const replyCount = topic.answerCount || 0;

    return (
        <Link
            to={`/forum/${topic.id}`}
            className="group block rounded-[28px] border border-[#edf3f3] bg-white p-5 shadow-[0px_10px_32px_rgba(0,52,58,0.05)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_18px_48px_rgba(0,52,58,0.12)]"
        >
            <div className="flex gap-4">
                <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-[#00343a] text-[14px] font-semibold text-white">
                    {initials}
                </div>

                <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center gap-2">
                        <span
                            className={`rounded-full px-3 py-1 text-[12px] font-semibold ${getForumCategoryAccentClassName(
                                topic.category,
                            )}`}
                        >
                            {getForumCategoryLabel(topic.category)}
                        </span>

                        {replyCount === 0 ? (
                            <span className="rounded-full bg-[#fdf1ef] px-3 py-1 text-[12px] font-semibold text-[#8e362c]">
                                Sans réponse
                            </span>
                        ) : null}
                    </div>

                    <h3 className="mt-3 text-[22px] font-medium leading-7 tracking-[-0.02em] text-[#181c1d] transition-colors group-hover:text-[#00343a]">
                        {topic.title}
                    </h3>

                    <p className="mt-2 line-clamp-2 text-[15px] leading-7 text-[#526062]">
                        {getForumTopicExcerpt(topic, 180)}
                    </p>

                    <div className="mt-4 flex flex-wrap items-center gap-4 text-[13px] text-[#70797a]">
                        <span className="font-semibold text-[#526062]">{authorLabel}</span>

                        <span className="inline-flex items-center gap-1">
                            <MaterialSymbol icon="forum" className="text-[18px]" />
                            {replyCount} réponse{replyCount > 1 ? "s" : ""}
                        </span>

                        <span className="inline-flex items-center gap-1">
                            <MaterialSymbol icon="visibility" className="text-[18px]" />
                            {topic.views || 0}
                        </span>

                        <span>{formatRelativeForumDate(topic.createdAt)}</span>
                    </div>
                </div>
            </div>
        </Link>
    );
}

export default ForumTopicCard;

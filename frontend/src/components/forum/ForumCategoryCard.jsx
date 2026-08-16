import MaterialSymbol from "../common/MaterialSymbol";
import {
    getForumCategoryAccentClassName,
    getForumCategoryDescription,
    getForumCategoryIcon,
    getForumCategoryLabel,
} from "../../services/forum/forumService";

function ForumCategoryCard({ category, count, onClick, active = false }) {
    const label = getForumCategoryLabel(category);
    const description = getForumCategoryDescription(category);
    const icon = getForumCategoryIcon(category);
    const accentClassName = getForumCategoryAccentClassName(category);
    const countLabel =
        typeof count === "number" ? `${count} discussion${count > 1 ? "s" : ""}` : count;

    return (
        <button
            type="button"
            onClick={onClick}
            className={`group text-left rounded-[24px] border bg-white p-5 text-[#181c1d] shadow-[0px_10px_30px_rgba(0,52,58,0.06)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_16px_40px_rgba(0,52,58,0.12)] ${
                active ? "border-[#00343a]" : "border-[#edf3f3]"
            }`}
        >
            <div
                className={`flex h-11 w-11 items-center justify-center rounded-2xl ${accentClassName}`}
            >
                <MaterialSymbol icon={icon} />
            </div>
            <h3 className="mt-4 text-[18px] font-semibold leading-6 tracking-[-0.02em] text-[#181c1d]">
                {label}
            </h3>
            <p className="mt-2 text-[14px] leading-6 text-[#526062]">{description}</p>
            <p className="mt-4 text-[13px] font-semibold text-[#8a661f]">{countLabel}</p>
        </button>
    );
}

export default ForumCategoryCard;

import { Link } from "react-router-dom";

import MaterialSymbol from "../../common/MaterialSymbol";
import {
    formatHousingPrice,
    getHousingTypeBadgeClassName,
    getHousingTypeLabel,
    getOwnerFullName,
    getPrimaryImageUrl,
    truncateDescription,
} from "../../../services/housing/housingHelpers";

function HousingCard({ housing }) {
    const imageUrl = getPrimaryImageUrl(housing);
    const ownerName = getOwnerFullName(housing);
    const typeLabel = getHousingTypeLabel(housing.housingType);
    const badgeClassName = getHousingTypeBadgeClassName(housing.housingType);

    return (
        <Link
            className="group flex h-full flex-col overflow-hidden rounded-2xl border border-[#bfc8ca]/40 bg-white shadow-[0px_4px_20px_rgba(0,0,0,0.04)] transition-all duration-200 hover:-translate-y-1 hover:shadow-[0px_12px_30px_rgba(0,52,58,0.12)]"
            to={`/housing/${housing.id}`}
        >
            <div className="relative aspect-[4/3] overflow-hidden">
                <img
                    alt={housing.title}
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
                    src={imageUrl}
                />
                <div className="absolute left-4 top-4">
                    <span
                        className={`inline-flex rounded-full px-3 py-1 text-[12px] font-semibold leading-4 tracking-[0.05em] ${badgeClassName}`}
                    >
                        {typeLabel}
                    </span>
                </div>
                <div className="absolute right-4 top-4">
                    <span
                        className={`inline-flex rounded-full px-3 py-1 text-[12px] font-semibold leading-4 ${
                            housing.available
                                ? "bg-white/95 text-[#00343a]"
                                : "bg-[#181c1d]/80 text-white"
                        }`}
                    >
                        {housing.available ? "Disponible" : "Indisponible"}
                    </span>
                </div>
            </div>

            <div className="flex flex-1 flex-col p-5">
                <div className="flex items-start justify-between gap-4">
                    <h3 className="text-[18px] font-semibold leading-6 text-[#181c1d] transition-colors group-hover:text-[#00343a]">
                        {housing.title}
                    </h3>
                    <p className="shrink-0 text-[18px] font-bold leading-6 text-[#00343a]">
                        {formatHousingPrice(housing.price)}
                        <span className="text-[12px] font-medium text-[#70797a]"> / mois</span>
                    </p>
                </div>

                <div className="mt-3 flex items-center gap-2 text-[14px] leading-5 text-[#70797a]">
                    <MaterialSymbol icon="location_on" className="text-[#00343a]" />
                    <span>{housing.city}</span>
                </div>

                {housing.description ? (
                    <p className="mt-3 line-clamp-2 text-[14px] leading-6 text-[#40484a]">
                        {truncateDescription(housing.description, 140)}
                    </p>
                ) : null}

                <div className="mt-4 border-t border-[#ebeeef] pt-4">
                    <div className="flex items-center justify-between gap-3 text-[12px] leading-4 text-[#70797a]">
                        <div className="flex items-center gap-2">
                            <MaterialSymbol icon="person" className="text-[#00343a]" />
                            <span>{ownerName}</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <MaterialSymbol icon="home" className="text-[#00343a]" />
                            <span>{typeLabel}</span>
                        </div>
                    </div>
                </div>
            </div>
        </Link>
    );
}

export default HousingCard;

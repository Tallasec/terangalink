import MaterialSymbol from "../../common/MaterialSymbol";
import HousingDetailActions from "./HousingDetailActions";
import { getOwnerFullName } from "../../../services/housing/housingHelpers";

function HousingDetailOwner({ housing, user, reservation, onReservationChange }) {
    const ownerName = getOwnerFullName(housing);
    const initials = [housing?.ownerFirstName, housing?.ownerLastName]
        .filter(Boolean)
        .map((part) => part.charAt(0).toUpperCase())
        .join("")
        .slice(0, 2);

    return (
        <aside className="rounded-[28px] border border-[#bfc8ca]/40 bg-white p-6 shadow-[0px_8px_30px_rgba(0,0,0,0.06)]">
            <h2 className="text-[18px] font-semibold leading-6 text-[#00343a]">Proprietaire</h2>

            <div className="mt-5 flex items-center gap-4">
                <div className="flex h-14 w-14 items-center justify-center rounded-full bg-[#00343a] text-[16px] font-semibold text-white">
                    {initials || "TL"}
                </div>
                <div>
                    <p className="text-[16px] font-semibold leading-6 text-[#181c1d]">{ownerName}</p>
                    <p className="text-[14px] leading-5 text-[#70797a]">Membre TerangaLink</p>
                </div>
            </div>

            <div className="mt-6 space-y-3 border-t border-[#ebeeef] pt-6 text-[14px] leading-6 text-[#40484a]">
                <div className="flex items-center gap-3">
                    <MaterialSymbol icon="verified" className="text-[#00343a]" />
                    <span>Annonce publiee sur la plateforme</span>
                </div>
                <div className="flex items-center gap-3">
                    <MaterialSymbol icon="location_city" className="text-[#00343a]" />
                    <span>{housing.city}</span>
                </div>
            </div>

            <HousingDetailActions
                key={`${housing.id}-${reservation?.id ?? "none"}`}
                housing={housing}
                reservation={reservation}
                user={user}
                onReservationChange={onReservationChange}
            />
        </aside>
    );
}

export default HousingDetailOwner;

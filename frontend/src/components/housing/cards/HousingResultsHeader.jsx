import MaterialSymbol from "../../common/MaterialSymbol";
import { HOUSING_SORT_OPTIONS } from "../../../services/housing/housingHelpers";

function HousingResultsHeader({ totalElements, sort, onSortChange }) {
    const offerLabel =
        totalElements > 1
            ? `${totalElements} offres disponibles`
            : `${totalElements} offre disponible`;

    return (
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <p className="text-[14px] font-medium leading-5 text-[#40484a]">{offerLabel}</p>

            <label className="flex items-center gap-3">
                <span className="text-[14px] leading-5 text-[#70797a]">Trier par :</span>
                <div className="relative">
                    <select
                        className="appearance-none rounded-xl border border-[#dce7e8] bg-white py-2 pl-4 pr-10 text-[14px] leading-5 text-[#181c1d] outline-none focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                        value={sort}
                        onChange={(event) => {
                            onSortChange(event.target.value);
                        }}
                    >
                        {HOUSING_SORT_OPTIONS.map((option) => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                    <MaterialSymbol
                        icon="expand_more"
                        className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                    />
                </div>
            </label>
        </div>
    );
}

export default HousingResultsHeader;

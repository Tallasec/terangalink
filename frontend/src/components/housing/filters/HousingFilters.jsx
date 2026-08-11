import HousingFilterPriceRange from "./HousingFilterPriceRange";
import HousingFilterType from "./HousingFilterType";
import HousingFilterAvailability from "./HousingFilterAvailability";

function HousingFilters({ filters, onChange, onClear, hasActiveFilters }) {
    return (
        <aside className="space-y-6 rounded-2xl border border-[#bfc8ca]/40 bg-white p-5 shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="flex items-center justify-between gap-3">
                <h2 className="text-[18px] font-semibold leading-6 text-[#00343a]">Filtres</h2>
                {hasActiveFilters ? (
                    <button
                        className="text-[12px] font-semibold uppercase tracking-[0.05em] text-[#70797a] transition-colors hover:text-[#00343a]"
                        type="button"
                        onClick={onClear}
                    >
                        Tout effacer
                    </button>
                ) : null}
            </div>

            <HousingFilterPriceRange
                minPrice={filters.minPrice}
                maxPrice={filters.maxPrice}
                onChange={onChange}
            />
            <HousingFilterType value={filters.housingType} onChange={onChange} />
            <HousingFilterAvailability value={filters.available} onChange={onChange} />
        </aside>
    );
}

export default HousingFilters;

import MaterialSymbol from "../../common/MaterialSymbol";

function JobResultsHeader({ sort, totalElements, onSortChange, activeFilters = false }) {
    return (
        <div className="mb-6 flex flex-col gap-4 rounded-[24px] border border-[#dbe6e6] bg-white px-5 py-4 shadow-[0px_12px_40px_rgba(0,52,58,0.06)] md:flex-row md:items-center md:justify-between">
            <div>
                <div className="flex items-center gap-2 text-[13px] font-semibold text-[#70797a]">
                    <MaterialSymbol icon="fact_check" className="text-[#00343a]" />
                    {totalElements} résultat{totalElements > 1 ? "s" : ""}
                </div>
                <p className="mt-1 text-[14px] text-[#526062]">
                    {activeFilters
                        ? "Vos filtres sont appliqués à la liste ci-dessous."
                        : "Les offres les plus récentes sont affichées en premier."}
                </p>
            </div>

            <label className="flex items-center gap-3">
                <span className="text-[13px] font-semibold text-[#3d484a]">Trier par</span>
                <div className="relative">
                    <select
                        className="h-11 appearance-none rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 pr-10 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                        value={sort}
                        onChange={(event) => onSortChange(event.target.value)}
                    >
                        <option value="createdAt,desc">Plus récentes</option>
                        <option value="createdAt,asc">Plus anciennes</option>
                        <option value="salary,desc">Salaire décroissant</option>
                        <option value="salary,asc">Salaire croissant</option>
                    </select>
                    <MaterialSymbol
                        icon="expand_more"
                        className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-[#839194]"
                    />
                </div>
            </label>
        </div>
    );
}

export default JobResultsHeader;

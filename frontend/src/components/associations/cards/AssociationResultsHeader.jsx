import MaterialSymbol from "../../common/MaterialSymbol";

const SORT_OPTIONS = [
    { value: "createdAt,desc", label: "Plus récentes" },
    { value: "createdAt,asc", label: "Plus anciennes" },
    { value: "title,asc", label: "Titre A-Z" },
    { value: "city,asc", label: "Ville A-Z" },
];

function AssociationResultsHeader({ sort, totalElements, onSortChange, activeFilters = false }) {
    return (
        <div className="mb-5 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div className="max-w-[720px]">
                <h2 className="text-[26px] font-semibold tracking-[-0.03em] text-[#00343a]">
                    Trouver des organisations
                </h2>
                <p className="mt-2 text-[15px] leading-7 text-[#526062]">
                    {activeFilters
                        ? `${totalElements} résultat${totalElements > 1 ? "s" : ""} filtré${
                              totalElements > 1 ? "s" : ""
                          }`
                        : `${totalElements} organisation${totalElements > 1 ? "s" : ""} référencée${
                              totalElements > 1 ? "s" : ""
                          }`}
                </p>
            </div>

            <label className="inline-flex items-center gap-3 self-start rounded-full border border-[#dbe6e6] bg-white px-4 py-3 lg:self-auto">
                <MaterialSymbol icon="sort" className="text-[#00343a]" />
                <select
                    className="bg-transparent text-[14px] font-semibold text-[#181c1d] outline-none"
                    value={sort}
                    onChange={(event) => onSortChange(event.target.value)}
                >
                    {SORT_OPTIONS.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </label>
        </div>
    );
}

export default AssociationResultsHeader;

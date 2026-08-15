import MaterialSymbol from "../../common/MaterialSymbol";

function StudyGroupResultsHeader({ activeFilters, sort, totalElements, onSortChange }) {
    return (
        <div className="mb-6 flex flex-col gap-4 rounded-[28px] border border-[#dbe6e6] bg-white p-5 shadow-[0px_10px_34px_rgba(0,52,58,0.05)] md:flex-row md:items-center md:justify-between">
            <div>
                <div className="flex items-center gap-2 text-[#00343a]">
                    <MaterialSymbol icon="assignment" />
                    <span className="text-[15px] font-semibold">{totalElements} résultat{totalElements > 1 ? "s" : ""}</span>
                </div>
                <p className="mt-2 text-[14px] leading-6 text-[#526062]">
                    {activeFilters
                        ? "Votre recherche est filtrée selon les critères choisis."
                        : "Les groupes les plus récents sont affichés en premier."}
                </p>
            </div>

            <div className="flex items-center gap-3">
                <label className="text-[14px] font-semibold text-[#526062]">Trier par</label>
                <select
                    className="rounded-full border border-[#dce7e8] bg-white px-5 py-3 text-[14px] text-[#00343a] outline-none"
                    value={sort}
                    onChange={(event) => onSortChange(event.target.value)}
                >
                    <option value="createdAt,desc">Plus récents</option>
                    <option value="createdAt,asc">Plus anciens</option>
                    <option value="meetingDate,asc">Date de réunion</option>
                    <option value="title,asc">Titre</option>
                </select>
            </div>
        </div>
    );
}

export default StudyGroupResultsHeader;

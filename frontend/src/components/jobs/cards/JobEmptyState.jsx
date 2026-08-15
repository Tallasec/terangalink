import MaterialSymbol from "../../common/MaterialSymbol";

function JobEmptyState({ onClearFilters }) {
    return (
        <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-14 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
            <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-[#eef8f8] text-[#00343a]">
                <MaterialSymbol icon="search_off" className="text-[30px]" />
            </div>

            <h3 className="mt-5 text-[22px] font-semibold text-[#00343a]">
                Aucune offre trouvée
            </h3>
            <p className="mx-auto mt-3 max-w-lg text-[15px] leading-7 text-[#526062]">
                Essayez un autre mot-clé, changez de ville ou réinitialisez les filtres pour
                élargir la sélection.
            </p>

            <button
                className="mt-6 inline-flex items-center justify-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white shadow-[0px_10px_24px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5"
                type="button"
                onClick={onClearFilters}
            >
                Réinitialiser les filtres
            </button>
        </div>
    );
}

export default JobEmptyState;

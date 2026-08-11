import MaterialSymbol from "../../common/MaterialSymbol";

function HousingEmptyState({ hasActiveFilters, onClearFilters }) {
    return (
        <div className="rounded-2xl border border-dashed border-[#bfc8ca] bg-white px-6 py-16 text-center shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-[#ebeeef] text-[#00343a]">
                <MaterialSymbol icon="home_work" className="text-[32px]" />
            </div>
            <h3 className="mt-6 text-[20px] font-semibold leading-7 text-[#00343a]">
                Aucun logement disponible
            </h3>
            <p className="mx-auto mt-2 max-w-md text-[14px] leading-6 text-[#70797a]">
                {hasActiveFilters
                    ? "Aucune annonce ne correspond a vos criteres. Essayez d'elargir votre recherche."
                    : "Il n'y a pas encore d'annonces de logement. Revenez bientot pour decouvrir de nouvelles offres."}
            </p>
            {hasActiveFilters ? (
                <button
                    className="mt-6 inline-flex items-center justify-center rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white transition-colors hover:bg-[#004851]"
                    type="button"
                    onClick={onClearFilters}
                >
                    Reinitialiser les filtres
                </button>
            ) : null}
        </div>
    );
}

export default HousingEmptyState;

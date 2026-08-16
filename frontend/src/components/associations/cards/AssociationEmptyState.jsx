import { Link } from "react-router-dom";

function AssociationEmptyState({ hasActiveFilters, onClearFilters }) {
    return (
        <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-14 text-center">
            <h3 className="text-[24px] font-semibold text-[#00343a]">
                {hasActiveFilters ? "Aucune organisation trouvée" : "Aucune association disponible"}
            </h3>
            <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                {hasActiveFilters
                    ? "Essayez d’élargir vos filtres pour voir davantage de structures."
                    : "Revenez plus tard ou publiez une nouvelle organisation si vous avez les droits."}
            </p>

            <div className="mt-6 flex flex-wrap justify-center gap-3">
                {hasActiveFilters ? (
                    <button
                        className="inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                        type="button"
                        onClick={onClearFilters}
                    >
                        Réinitialiser les filtres
                    </button>
                ) : null}
                <Link
                    className="inline-flex items-center rounded-full border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                    to="/associations/create"
                >
                    Publier une organisation
                </Link>
            </div>
        </div>
    );
}

export default AssociationEmptyState;

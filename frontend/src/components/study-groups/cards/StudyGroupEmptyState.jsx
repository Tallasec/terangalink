import { Link } from "react-router-dom";

function StudyGroupEmptyState({ onClearFilters }) {
    return (
        <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
            <h2 className="text-[28px] font-semibold text-[#00343a]">Aucun groupe trouvé</h2>
            <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                Essayez d'ajuster vos filtres ou créez votre propre groupe de révision.
            </p>
            <div className="mt-6 flex flex-wrap items-center justify-center gap-3">
                <button
                    className="inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                    type="button"
                    onClick={onClearFilters}
                >
                    Réinitialiser les filtres
                </button>
                <Link
                    className="inline-flex items-center rounded-full border border-[#dce7e8] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                    to="/study-groups"
                >
                    Revenir à la liste
                </Link>
            </div>
        </div>
    );
}

export default StudyGroupEmptyState;

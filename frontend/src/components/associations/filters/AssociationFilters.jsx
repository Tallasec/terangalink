import MaterialSymbol from "../../common/MaterialSymbol";
import { getAssociationTypeLabel } from "../../../services/associations/associationService";

const TYPE_OPTIONS = [
    "",
    "ETUDIANTE",
    "DAHIRA",
    "CULTURELLE",
    "SPORTIVE",
    "HUMANITAIRE",
    "AUTRE",
];

function AssociationFilters({ filters, onChange, onClear }) {
    return (
        <aside className="rounded-[28px] border border-[#dbe6e6] bg-white p-5 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
            <div className="flex items-center justify-between gap-3">
                <h3 className="text-[18px] font-semibold text-[#181c1d]">Filtres</h3>
                <button
                    className="text-[13px] font-semibold text-[#00343a] hover:underline"
                    type="button"
                    onClick={onClear}
                >
                    Réinitialiser
                </button>
            </div>

            <div className="mt-5 space-y-4">
                <Field label="Type">
                    <select
                        name="associationType"
                        value={filters.associationType}
                        onChange={(event) => onChange("associationType", event.target.value)}
                    >
                        {TYPE_OPTIONS.map((type) => (
                            <option key={type || "all"} value={type}>
                                {type ? getAssociationTypeLabel(type) : "Tous les types"}
                            </option>
                        ))}
                    </select>
                </Field>

                <Field label="Disponibilité">
                    <select
                        name="available"
                        value={filters.available}
                        onChange={(event) => onChange("available", event.target.value)}
                    >
                        <option value="">Toutes</option>
                        <option value="true">Ouvertes</option>
                        <option value="false">Fermées</option>
                    </select>
                </Field>

                <Field label="Ville">
                    <input
                        name="city"
                        value={filters.city}
                        onChange={(event) => onChange("city", event.target.value)}
                        placeholder="Dakar, Paris..."
                    />
                </Field>
            </div>
        </aside>
    );
}

function Field({ label, children }) {
    return (
        <label className="block">
            <span className="mb-2 block text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                {label}
            </span>
            {children}
            <style>{`
                select, input {
                    width: 100%;
                    border-radius: 1rem;
                    border: 1px solid #dbe6e6;
                    background: #f7fafb;
                    padding: 0.8rem 1rem;
                    font-size: 14px;
                    color: #181c1d;
                    outline: none;
                    transition: border-color .2s ease, background-color .2s ease;
                }
                select:focus, input:focus {
                    border-color: #00343a;
                    background: #fff;
                }
            `}</style>
        </label>
    );
}

export default AssociationFilters;

import MaterialSymbol from "../../common/MaterialSymbol";

function StudyGroupFilters({ filters, onChange, onClear }) {
    return (
        <aside className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_10px_34px_rgba(0,52,58,0.05)]">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.18em] text-[#70797a]">
                        Filtres rapides
                    </p>
                    <h2 className="mt-2 text-[24px] font-semibold tracking-[-0.02em] text-[#00343a]">
                        Affinez votre recherche
                    </h2>
                </div>
                <button
                    className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]"
                    type="button"
                    onClick={onClear}
                >
                    Effacer
                </button>
            </div>

            <div className="mt-6 space-y-5">
                <FilterField label="Type de rencontre">
                    <select
                        className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                        value={filters.meetingType}
                        onChange={(event) => onChange("meetingType", event.target.value)}
                    >
                        <option value="">Tous les types</option>
                        <option value="ONLINE">En ligne</option>
                        <option value="PHYSICAL">Présentiel</option>
                        <option value="HYBRID">Hybride</option>
                    </select>
                </FilterField>

                <FilterField label="Disponibilité">
                    <select
                        className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                        value={filters.available}
                        onChange={(event) => onChange("available", event.target.value)}
                    >
                        <option value="">Tous</option>
                        <option value="true">Ouverts</option>
                        <option value="false">Fermés</option>
                    </select>
                </FilterField>

                <FilterField label="Date de réunion">
                    <input
                        className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                        type="datetime-local"
                        value={filters.meetingDate}
                        onChange={(event) => onChange("meetingDate", event.target.value)}
                    />
                </FilterField>

                <button
                    className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                    type="button"
                    onClick={onClear}
                >
                    <MaterialSymbol icon="restart_alt" />
                    Réinitialiser
                </button>
            </div>
        </aside>
    );
}

function FilterField({ label, children }) {
    return (
        <label className="block">
            <span className="mb-2 block text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                {label}
            </span>
            {children}
        </label>
    );
}

export default StudyGroupFilters;

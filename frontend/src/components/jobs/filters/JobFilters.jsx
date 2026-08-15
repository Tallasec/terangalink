import { ContractType, contractTypeLabels } from "../../../services/jobs/contractTypes";
import MaterialSymbol from "../../common/MaterialSymbol";

const QUICK_FILTERS = [
    { label: "Tous", value: "" },
    { label: "CDI", value: ContractType.CDI },
    { label: "CDD", value: ContractType.CDD },
    { label: "Stage", value: ContractType.STAGE },
    { label: "Alternance", value: ContractType.ALTERNANCE },
    { label: "Intérim", value: ContractType.INTERIM },
    { label: "Freelance", value: ContractType.FREELANCE },
];

function JobFilters({ filters, onChange, onClear }) {
    return (
        <aside className="rounded-[28px] border border-[#dbe6e6] bg-white p-5 shadow-[0px_18px_60px_rgba(0,52,58,0.08)]">
            <div className="flex items-center justify-between gap-3">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                        Filtres rapides
                    </p>
                    <h2 className="mt-1 text-[20px] font-semibold text-[#00343a]">Affinez votre recherche</h2>
                </div>
                <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                    Explore
                </span>
            </div>

            <div className="mt-5 flex flex-wrap gap-2">
                {QUICK_FILTERS.map((filter) => {
                    const isActive = (filters.contractType || "") === filter.value;

                    return (
                        <button
                            key={filter.label}
                            className={`rounded-full px-4 py-2 text-[13px] font-semibold transition-all ${
                                isActive
                                    ? "bg-[#00343a] text-white shadow-[0px_10px_24px_rgba(0,52,58,0.18)]"
                                    : "bg-[#f7fafb] text-[#526062] hover:bg-[#eef8f8] hover:text-[#00343a]"
                            }`}
                            type="button"
                            onClick={() => onChange("contractType", filter.value)}
                        >
                            {filter.label}
                        </button>
                    );
                })}
            </div>

            <div className="mt-6 space-y-5">
                <label className="block">
                    <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                        Type de contrat
                    </span>
                    <select
                        className="h-12 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                        value={filters.contractType || ""}
                        onChange={(event) => onChange("contractType", event.target.value)}
                    >
                        <option value="">Tous les contrats</option>
                        {Object.values(ContractType).map((type) => (
                            <option key={type} value={type}>
                                {contractTypeLabels[type] || type}
                            </option>
                        ))}
                    </select>
                </label>

                <div className="grid gap-4 sm:grid-cols-2">
                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                            Salaire min
                        </span>
                        <input
                            className="h-12 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                            inputMode="decimal"
                            placeholder="Ex. 1200"
                            type="number"
                            value={filters.salaryMin}
                            onChange={(event) => onChange("salaryMin", event.target.value)}
                        />
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                            Salaire max
                        </span>
                        <input
                            className="h-12 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                            inputMode="decimal"
                            placeholder="Ex. 2500"
                            type="number"
                            value={filters.salaryMax}
                            onChange={(event) => onChange("salaryMax", event.target.value)}
                        />
                    </label>
                </div>

                <label className="block">
                    <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                        Disponibilité
                    </span>
                    <select
                        className="h-12 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                        value={filters.available === undefined ? "" : String(filters.available)}
                        onChange={(event) =>
                            onChange(
                                "available",
                                event.target.value === ""
                                    ? undefined
                                    : event.target.value === "true",
                            )
                        }
                    >
                        <option value="">Toutes</option>
                        <option value="true">Disponibles</option>
                        <option value="false">Indisponibles</option>
                    </select>
                </label>
            </div>

            <div className="mt-6 flex gap-3">
                <button
                    className="inline-flex flex-1 items-center justify-center gap-2 rounded-2xl border border-[#dce7e8] px-4 py-3 text-[14px] font-semibold text-[#3d484a] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
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

export default JobFilters;

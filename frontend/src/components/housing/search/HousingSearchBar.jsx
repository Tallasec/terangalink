import MaterialSymbol from "../../common/MaterialSymbol";
import { HOUSING_TYPE_OPTIONS } from "../../../services/housing/housingHelpers";

function HousingSearchBar({ values, onChange, onSubmit, loading = false }) {
    function handleSubmit(event) {
        event.preventDefault();
        onSubmit();
    }

    return (
        <section className="relative z-10 -mt-8 px-4 md:px-12">
            <form
                className="mx-auto max-w-[1200px] rounded-2xl border border-[#bfc8ca]/40 bg-white p-4 shadow-[0px_8px_30px_rgba(0,0,0,0.06)] md:p-6"
                onSubmit={handleSubmit}
            >
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                    <label className="block">
                        <span className="mb-2 block text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                            Localisation
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="location_on"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-4 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                placeholder="Ville ou arrondissement"
                                type="text"
                                value={values.city}
                                onChange={(event) => {
                                    onChange("city", event.target.value);
                                }}
                                disabled={loading}
                            />
                        </div>
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                            Budget max
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="euro"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-4 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                placeholder="ex. 800"
                                type="number"
                                min="0"
                                value={values.maxPrice}
                                onChange={(event) => {
                                    onChange("maxPrice", event.target.value);
                                }}
                                disabled={loading}
                            />
                        </div>
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                            Type de logement
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="home"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <select
                                className="w-full appearance-none rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-10 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                value={values.housingType}
                                onChange={(event) => {
                                    onChange("housingType", event.target.value);
                                }}
                                disabled={loading}
                            >
                                {HOUSING_TYPE_OPTIONS.map((option) => (
                                    <option key={option.value || "all"} value={option.value}>
                                        {option.label}
                                    </option>
                                ))}
                            </select>
                            <MaterialSymbol
                                icon="expand_more"
                                className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                        </div>
                    </label>

                    <div className="flex items-end">
                        <button
                            className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold leading-5 text-white transition-colors hover:bg-[#004851] disabled:cursor-not-allowed disabled:opacity-70"
                            type="submit"
                            disabled={loading}
                        >
                            <MaterialSymbol icon="search" className="text-white" />
                            Trouver un logement
                        </button>
                    </div>
                </div>
            </form>
        </section>
    );
}

export default HousingSearchBar;

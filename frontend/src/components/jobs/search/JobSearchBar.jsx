import MaterialSymbol from "../../common/MaterialSymbol";

function JobSearchBar({ loading, values, onChange, onSubmit }) {
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
                            Poste
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="search"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-4 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                placeholder="Intitulé du poste, entreprise ou mot-clé"
                                type="text"
                                value={values.title}
                                onChange={(event) => {
                                    onChange("title", event.target.value);
                                }}
                                disabled={loading}
                            />
                        </div>
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                            Ville
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="location_on"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-4 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                placeholder="Paris, Lyon..."
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
                            Entreprise
                        </span>
                        <div className="relative">
                            <MaterialSymbol
                                icon="domain"
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                            />
                            <input
                                className="w-full rounded-xl border border-[#dce7e8] bg-white py-3 pl-10 pr-4 text-[14px] leading-5 text-[#181c1d] outline-none transition-all focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                                placeholder="Nom de l'entreprise"
                                type="text"
                                value={values.companyName}
                                onChange={(event) => {
                                    onChange("companyName", event.target.value);
                                }}
                                disabled={loading}
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
                            Trouver un job
                        </button>
                    </div>
                </div>
            </form>
        </section>
    );
}

export default JobSearchBar;

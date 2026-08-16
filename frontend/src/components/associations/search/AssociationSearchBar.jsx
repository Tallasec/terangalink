import MaterialSymbol from "../../common/MaterialSymbol";

function AssociationSearchBar({ values, onChange, onSubmit, loading }) {
    return (
        <section className="border-b border-[#dbe6e6] bg-white">
            <div className="mx-auto max-w-[1200px] px-4 py-8 md:px-12">
                <form
                    className="grid gap-4 rounded-[32px] border border-[#dbe6e6] bg-[#f7fafb] p-5 shadow-[0px_12px_40px_rgba(0,52,58,0.04)] md:grid-cols-[minmax(0,1.25fr)_minmax(0,0.75fr)_auto] md:items-end md:p-6"
                    onSubmit={onSubmit}
                >
                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                            Chercher
                        </span>
                        <div className="flex h-14 items-center rounded-full border border-[#dbe6e6] bg-white px-5">
                            <MaterialSymbol
                                icon="search"
                                className="shrink-0 text-[20px] leading-none text-[#70797a]"
                            />
                            <input
                                className="ml-4 h-full w-full border-0 bg-transparent p-0 text-[15px] outline-none ring-0 placeholder:text-[#8e9597] focus:border-0 focus:ring-0"
                                value={values.title}
                                onChange={(event) => onChange("title", event.target.value)}
                                placeholder="Nom d'une association, dahira..."
                            />
                        </div>
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                            Ville
                        </span>
                        <input
                            className="h-14 w-full rounded-full border border-[#dbe6e6] bg-white px-4 text-[15px] outline-none placeholder:text-[#8e9597] focus:border-[#00343a]"
                            value={values.city}
                            onChange={(event) => onChange("city", event.target.value)}
                            placeholder="Paris, Lyon..."
                        />
                    </label>

                    <button
                        className="inline-flex h-14 items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-6 text-[14px] font-semibold text-white disabled:cursor-not-allowed disabled:opacity-70 md:justify-self-end"
                        type="submit"
                        disabled={loading}
                    >
                        <MaterialSymbol icon="search" filled />
                        {loading ? "Recherche..." : "Rechercher"}
                    </button>
                </form>
            </div>
        </section>
    );
}

export default AssociationSearchBar;

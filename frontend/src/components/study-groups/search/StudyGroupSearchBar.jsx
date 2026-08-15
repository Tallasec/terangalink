import MaterialSymbol from "../../common/MaterialSymbol";

function StudyGroupSearchBar({ values, onChange, onSubmit, onCreateClick }) {
    return (
        <section className="border-b border-[#dbe6e6] bg-[linear-gradient(180deg,#eef5f6_0%,#f7fafb_100%)]">
            <div className="mx-auto max-w-[1200px] px-4 py-10 md:px-12 md:py-14">
                <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
                    <div className="max-w-3xl">
                        <p className="text-[12px] font-semibold uppercase tracking-[0.18em] text-[#70797a]">
                            TerangaLink groups
                        </p>
                        <h1 className="mt-3 text-[42px] font-semibold leading-[1.03] tracking-[-0.05em] text-[#00343a] md:text-[60px]">
                            Trouvez votre groupe de révision en France.
                        </h1>
                        <p className="mt-4 max-w-2xl text-[16px] leading-8 text-[#526062] md:text-[18px]">
                            Recherchez des groupes par matière, ville ou type de rencontre et
                            rejoignez des étudiants qui révisent déjà ensemble.
                        </p>
                    </div>

                    <button
                        className="inline-flex items-center justify-center gap-3 rounded-full bg-[#00343a] px-6 py-4 text-[15px] font-semibold text-white shadow-[0px_14px_30px_rgba(0,52,58,0.18)]"
                        type="button"
                        onClick={onCreateClick}
                    >
                        <MaterialSymbol icon="group_add" filled />
                        Créer un groupe
                    </button>
                </div>

                <form
                    className="mt-8 grid gap-3 rounded-[28px] border border-[#dbe6e6] bg-white p-3 shadow-[0px_12px_40px_rgba(0,52,58,0.05)] lg:grid-cols-[1.1fr_0.9fr_0.8fr_auto]"
                    onSubmit={onSubmit}
                >
                    <SearchField
                        icon="search"
                        placeholder="Nom du groupe ou mot-clé"
                        value={values.title}
                        onChange={(value) => onChange("title", value)}
                    />
                    <SearchField
                        icon="menu_book"
                        placeholder="Matière"
                        value={values.subject}
                        onChange={(value) => onChange("subject", value)}
                    />
                    <SearchField
                        icon="location_on"
                        placeholder="Ville"
                        value={values.city}
                        onChange={(value) => onChange("city", value)}
                    />
                    <button
                        className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-6 py-4 text-[15px] font-semibold text-white"
                        type="submit"
                    >
                        <MaterialSymbol icon="search" />
                        Trouver
                    </button>
                </form>
            </div>
        </section>
    );
}

function SearchField({ icon, placeholder, value, onChange }) {
    return (
        <label className="flex items-center gap-3 rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3">
            <MaterialSymbol icon={icon} className="text-[#839194]" />
            <input
                className="w-full bg-transparent text-[14px] outline-none placeholder:text-[#839194]"
                placeholder={placeholder}
                type="text"
                value={value}
                onChange={(event) => onChange(event.target.value)}
            />
        </label>
    );
}

export default StudyGroupSearchBar;

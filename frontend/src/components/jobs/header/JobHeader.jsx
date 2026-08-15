import MaterialSymbol from "../../common/MaterialSymbol";

function JobHeader({ onPublishClick, totalElements = 0, activeFilters = false }) {
    return (
        <section className="border-b border-[#bfc8ca]/40 bg-[#f7fafb] px-4 py-10 md:px-12 md:py-12">
            <div className="mx-auto max-w-[1200px]">
                <div className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
                    <div className="text-center md:text-left">
                        <h1 className="text-[32px] font-bold leading-10 tracking-[-0.02em] text-[#00343a] md:text-[40px] md:leading-[48px]">
                            Boostez votre carrière en France
                        </h1>
                        <p className="mx-auto mt-3 max-w-2xl text-[16px] leading-6 text-[#40484a] md:mx-0">
                            Trouvez des opportunités d'emploi adaptées aux étudiants et jeunes diplômés.
                        </p>
                    </div>

                    <button
                        className="inline-flex items-center justify-center gap-2 self-center rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white transition-colors hover:bg-[#004851] md:self-auto"
                        type="button"
                        onClick={onPublishClick}
                    >
                        <MaterialSymbol icon="add" filled />
                        Publier une offre
                    </button>
                </div>

                <div className="mt-4 flex items-center justify-center md:justify-end">
                    <div
                        className={`rounded-full border px-4 py-2 text-[13px] font-semibold shadow-[0px_6px_24px_rgba(0,52,58,0.06)] ${
                            activeFilters
                                ? "border-[#f0d8a2] bg-[#fff8ea] text-[#8b6510]"
                                : "border-[#dce7e8] bg-white text-[#00343a]"
                        }`}
                    >
                        <div className="flex items-center gap-2">
                            <MaterialSymbol icon="work" className="text-[#e0a93f]" />
                            {totalElements} offre{totalElements > 1 ? "s" : ""} disponibles
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default JobHeader;

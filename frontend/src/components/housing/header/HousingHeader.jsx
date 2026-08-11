import MaterialSymbol from "../../common/MaterialSymbol";

function HousingHeader({ onPublishClick }) {
    return (
        <section className="border-b border-[#bfc8ca]/40 bg-[#f7fafb] px-4 py-10 md:px-12 md:py-12">
            <div className="mx-auto max-w-[1200px]">
                <div className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
                    <div className="text-center md:text-left">
                        <h1 className="text-[32px] font-bold leading-10 tracking-[-0.02em] text-[#00343a] md:text-[40px] md:leading-[48px]">
                            Trouvez votre chez-vous en France
                        </h1>
                        <p className="mx-auto mt-3 max-w-2xl text-[16px] leading-6 text-[#40484a] md:mx-0">
                            Des logements verifies par la communaute TerangaLink pour les etudiants
                            senegalais.
                        </p>
                    </div>

                    <button
                        className="inline-flex items-center justify-center gap-2 self-center rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white transition-colors hover:bg-[#004851] md:self-auto"
                        type="button"
                        onClick={onPublishClick}
                    >
                        <MaterialSymbol icon="add_home" className="text-white" />
                        Publier une annonce
                    </button>
                </div>
            </div>
        </section>
    );
}

export default HousingHeader;

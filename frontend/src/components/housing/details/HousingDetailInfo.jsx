import {
    formatHousingPrice,
    getHousingTypeLabel,
} from "../../../services/housing/housingHelpers";

function HousingDetailInfo({ housing }) {
    return (
        <section className="rounded-[28px] border border-[#bfc8ca]/40 bg-white p-6 shadow-[0px_8px_30px_rgba(0,0,0,0.06)] md:p-8">
            <div className="flex flex-col gap-4 border-b border-[#ebeeef] pb-6 md:flex-row md:items-start md:justify-between">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#70797a]">
                        {getHousingTypeLabel(housing.housingType)}
                    </p>
                    <h1 className="mt-2 text-[28px] font-bold leading-9 tracking-[-0.02em] text-[#00343a] md:text-[36px] md:leading-[44px]">
                        {housing.title}
                    </h1>
                    <p className="mt-2 text-[16px] leading-6 text-[#40484a]">{housing.city}</p>
                </div>

                <div className="text-left md:text-right">
                    <p className="text-[28px] font-bold leading-9 text-[#00343a]">
                        {formatHousingPrice(housing.price)}
                    </p>
                    <p className="text-[14px] leading-5 text-[#70797a]">par mois</p>
                    <span
                        className={`mt-3 inline-flex rounded-full px-3 py-1 text-[12px] font-semibold leading-4 ${
                            housing.available
                                ? "bg-[#b5ecf5] text-[#001f24]"
                                : "bg-[#ebeeef] text-[#70797a]"
                        }`}
                    >
                        {housing.available ? "Disponible" : "Indisponible"}
                    </span>
                </div>
            </div>

            {housing.description ? (
                <div className="mt-6">
                    <h2 className="text-[18px] font-semibold leading-6 text-[#00343a]">Description</h2>
                    <p className="mt-3 whitespace-pre-line text-[15px] leading-7 text-[#40484a]">
                        {housing.description}
                    </p>
                </div>
            ) : null}

            {housing.address ? (
                <div className="mt-6 rounded-2xl bg-[#f7fafb] px-4 py-4">
                    <h2 className="text-[14px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                        Adresse
                    </h2>
                    <p className="mt-2 text-[15px] leading-6 text-[#181c1d]">{housing.address}</p>
                </div>
            ) : null}
        </section>
    );
}

export default HousingDetailInfo;

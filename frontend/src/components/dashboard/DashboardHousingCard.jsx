import DashboardCard from "./DashboardCard";

import { dashboardQuickAccessCards } from "../../services/dashboard/dashboardService";

function DashboardHousingCard() {
    const { category, title, description, image } = dashboardQuickAccessCards.housing;

    return (
        <DashboardCard
            as="a"
            href="#"
            className="group relative h-64 overflow-hidden rounded-xl border border-[#bfc8ca]/30 bg-white hover:-translate-y-1 md:col-span-8 md:h-auto"
        >
            <img
                alt="Intérieur d'un appartement parisien lumineux et minimaliste."
                className="absolute inset-0 h-full w-full object-cover opacity-80 transition-transform duration-700 group-hover:scale-105"
                src={image}
            />
            <div className="absolute inset-0 bg-gradient-to-t from-[#00343a]/80 to-transparent" />
            <div className="absolute bottom-0 left-0 p-8">
                <span className="mb-3 inline-block rounded-full bg-[#fdd798] px-3 py-1 text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#271900]">
                    {category}
                </span>
                <h3 className="mb-2 text-[32px] font-semibold leading-10 tracking-[-0.01em] text-white">
                    {title}
                </h3>
                <p className="text-[16px] leading-6 text-white/80">{description}</p>
            </div>
        </DashboardCard>
    );
}

export default DashboardHousingCard;

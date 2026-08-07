import { dashboardSupportCard } from "../../services/dashboard/dashboardService";

function DashboardSupportCard() {
    return (
        <div className="group relative overflow-hidden rounded-xl bg-[#00343a]">
            <img
                alt="Étudiants partageant un repas dans une cafétéria."
                className="absolute inset-0 h-full w-full object-cover opacity-50 transition-transform duration-500 group-hover:scale-105"
                src={dashboardSupportCard.image}
            />
            <div className="absolute inset-0 flex flex-col items-center justify-center p-6 text-center">
                <p className="mb-2 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-white">
                    {dashboardSupportCard.title}
                </p>
                <p className="mb-4 text-[14px] leading-5 text-white/80">
                    {dashboardSupportCard.description}
                </p>
                <button
                    className="rounded-lg bg-white px-4 py-2 text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a]"
                    type="button"
                >
                    {dashboardSupportCard.cta}
                </button>
            </div>
        </div>
    );
}

export default DashboardSupportCard;

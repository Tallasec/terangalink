import { Link } from "react-router-dom";

import DashboardCard from "./DashboardCard";
import MaterialSymbol from "../common/MaterialSymbol";

import { dashboardQuickAccessCards } from "../../services/dashboard/dashboardService";

function DashboardJobsCard() {
    const { title, description, cta, href } = dashboardQuickAccessCards.jobs;

    return (
        <DashboardCard
            as={Link}
            to={href || "/jobs"}
            className="group flex flex-col justify-between rounded-xl bg-[#00343a] p-8 text-white hover:-translate-y-1 md:col-span-4"
        >
            <div>
                <div className="mb-6 flex h-12 w-12 items-center justify-center rounded-lg bg-[#0d4c54]">
                    <MaterialSymbol
                        filled
                        icon="work"
                        className="text-[#85bbc4]"
                    />
                </div>
                <h3 className="mb-2 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-white">
                    {title}
                </h3>
                <p className="text-[14px] leading-5 text-[#85bbc4]">{description}</p>
            </div>
            <div className="mt-4 flex items-center gap-2 text-[12px] font-semibold leading-4 tracking-[0.05em] text-white">
                <span>{cta}</span>
                <MaterialSymbol icon="arrow_forward" className="text-sm" />
            </div>
        </DashboardCard>
    );
}

export default DashboardJobsCard;

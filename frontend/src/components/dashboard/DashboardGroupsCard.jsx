import { Link } from "react-router-dom";

import DashboardCard from "./DashboardCard";
import MaterialSymbol from "../common/MaterialSymbol";

import { dashboardQuickAccessCards } from "../../services/dashboard/dashboardService";

function DashboardGroupsCard() {
    const { title, description, cta, href } = dashboardQuickAccessCards.groups;

    return (
        <DashboardCard
            as={Link}
            to={href}
            className="group flex flex-col justify-between rounded-xl border border-[#bfc8ca]/50 bg-white p-8 hover:-translate-y-1 md:col-span-4"
        >
            <div>
                <div className="mb-6 flex h-12 w-12 items-center justify-center rounded-lg bg-[#ebeeef]">
                    <MaterialSymbol icon="groups" className="text-[#00343a]" />
                </div>
                <h3 className="mb-2 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                    {title}
                </h3>
                <p className="text-[14px] leading-5 text-[#40484a]">{description}</p>
            </div>
            <div className="mt-4 flex items-center gap-2 text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a]">
                <span>{cta}</span>
                <MaterialSymbol icon="chevron_right" className="text-sm" />
            </div>
        </DashboardCard>
    );
}

export default DashboardGroupsCard;

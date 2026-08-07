import MaterialSymbol from "../common/MaterialSymbol";

import { dashboardQuickLinks } from "../../services/dashboard/dashboardService";

function DashboardQuickLinks() {
    return (
        <div className="rounded-xl bg-[#ebeeef] p-8">
            <h3 className="mb-4 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                Liens rapides
            </h3>
            <ul className="space-y-4">
                {dashboardQuickLinks.map((link) => (
                    <li key={link}>
                        <a
                            className="group flex items-center justify-between"
                            href="#"
                        >
                            <span className="text-[16px] leading-6 text-[#40484a] transition-colors group-hover:text-[#00343a]">
                                {link}
                            </span>
                            <MaterialSymbol
                                icon="chevron_right"
                                className="text-[#70797a] transition-transform group-hover:translate-x-1"
                            />
                        </a>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default DashboardQuickLinks;

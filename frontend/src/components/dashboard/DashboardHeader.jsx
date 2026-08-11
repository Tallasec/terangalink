import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import MaterialSymbol from "../common/MaterialSymbol";
import DashboardUserMenu from "./DashboardUserMenu";
import { dashboardNavigationItems } from "../../services/dashboard/dashboardService";

function DashboardHeader({ user, activeNav = "" }) {
    const [isElevated, setIsElevated] = useState(false);

    useEffect(() => {
        function handleScroll() {
            setIsElevated(window.scrollY > 10);
        }

        handleScroll();
        window.addEventListener("scroll", handleScroll, { passive: true });

        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    return (
        <header
            className={`sticky top-0 z-50 border-b border-[#bfc8ca] bg-[#f7fafb] ${
                isElevated ? "shadow-md" : "shadow-sm"
            }`}
        >
            <div className="mx-auto flex w-full max-w-[1200px] items-center justify-between px-4 py-4 md:px-12">
                <Link
                    className="text-[24px] font-bold leading-8 tracking-[-0.01em] text-[#00343a]"
                    to="/dashboard"
                >
                    TerangaLink
                </Link>

                <nav className="hidden items-center gap-8 md:flex">
                    {dashboardNavigationItems.map((item) => {
                        const isActive = activeNav === item.label;

                        return (
                            <Link
                                key={item.label}
                                className={`text-[12px] font-semibold leading-4 tracking-[0.05em] transition-colors duration-200 ${
                                    isActive
                                        ? "border-b-2 border-[#00343a] pb-1 text-[#00343a]"
                                        : "text-[#40484a] hover:text-[#00343a]"
                                }`}
                                to={item.href}
                            >
                                {item.label}
                            </Link>
                        );
                    })}
                </nav>

                <div className="flex items-center gap-4">
                    <div className="relative hidden sm:block">
                        <MaterialSymbol
                            icon="search"
                            className="absolute left-3 top-1/2 -translate-y-1/2 text-[#70797a]"
                        />
                        <input
                            className="w-48 rounded-full border-none bg-[#ebeeef] py-2 pl-10 pr-4 text-[14px] leading-5 outline-none transition-all focus:ring-2 focus:ring-[#00343a]"
                            placeholder="Rechercher..."
                            type="text"
                        />
                    </div>

                    <DashboardUserMenu user={user} />
                </div>
            </div>
        </header>
    );
}

export default DashboardHeader;

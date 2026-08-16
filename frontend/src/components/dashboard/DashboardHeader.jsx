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
            <div className="mx-auto grid h-24 w-full max-w-[1320px] grid-cols-[auto_minmax(0,1fr)_auto] items-center gap-8 px-4 md:px-8">
                <Link
                    className="whitespace-nowrap text-[28px] font-bold leading-none tracking-[-0.02em] text-[#00343a]"
                    to="/dashboard"
                >
                    TerangaLink
                </Link>

                <nav className="hidden items-center justify-center gap-10 md:flex">
                    {dashboardNavigationItems.map((item) => {
                        const isActive = activeNav === item.label;

                        return (
                            <Link
                                key={item.label}
                                className={`pb-1 text-[14px] font-semibold leading-none tracking-[0.01em] transition-colors duration-200 ${
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

                <div className="flex items-center gap-4 justify-self-end">
                    <label className="hidden h-14 w-[300px] items-center gap-3 overflow-hidden rounded-full border border-[#dbe6e6] bg-white px-5 transition-all focus-within:border-[#00343a] sm:flex lg:w-[320px]">
                        <MaterialSymbol
                            icon="search"
                            className="shrink-0 text-[22px] leading-none text-[#70797a]"
                        />
                        <input
                            className="w-full min-w-0 bg-transparent text-[16px] leading-none text-[#181c1d] outline-none placeholder:text-[#8e9597]"
                            style={{
                                all: "unset",
                                display: "block",
                                width: "100%",
                                minWidth: 0,
                                flex: 1,
                                fontSize: "16px",
                                lineHeight: 1,
                                color: "#181c1d",
                                boxShadow: "none",
                                WebkitAppearance: "none",
                                MozAppearance: "none",
                                background: "transparent",
                                outline: "none",
                                border: "none",
                                padding: 0,
                                margin: 0,
                                caretColor: "#181c1d",
                            }}
                            placeholder="Rechercher..."
                            type="text"
                        />
                    </label>

                    <DashboardUserMenu user={user} />
                </div>
            </div>
        </header>
    );
}

export default DashboardHeader;

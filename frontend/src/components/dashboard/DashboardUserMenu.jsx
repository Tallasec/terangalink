import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import MaterialSymbol from "../common/MaterialSymbol";
import { logoutUser } from "../../services/auth/authService";
import { getUserFullName, getUserInitials } from "../../services/user/userHelpers";

function DashboardUserMenu({ user }) {
    const navigate = useNavigate();
    const menuRef = useRef(null);
    const [isOpen, setIsOpen] = useState(false);

    const fullName = getUserFullName(user);
    const initials = getUserInitials(user);
    const profileImageUrl = user?.profileImageUrl || "";

    useEffect(() => {
        function handleDocumentClick(event) {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        }

        function handleEscape(event) {
            if (event.key === "Escape") {
                setIsOpen(false);
            }
        }

        document.addEventListener("mousedown", handleDocumentClick);
        document.addEventListener("keydown", handleEscape);

        return () => {
            document.removeEventListener("mousedown", handleDocumentClick);
            document.removeEventListener("keydown", handleEscape);
        };
    }, []);

    function handleLogout() {
        logoutUser();
        setIsOpen(false);
        navigate("/login", { replace: true });
    }

    return (
        <div className="relative" ref={menuRef}>
            <button
                aria-expanded={isOpen}
                aria-haspopup="menu"
                className="flex items-center gap-3 rounded-full border border-[#bfc8ca]/40 bg-white px-3 py-2 text-left shadow-[0px_4px_20px_rgba(0,0,0,0.04)] transition-all duration-150 hover:border-[#00343a]/20 hover:shadow-md"
                type="button"
                onClick={() => {
                    setIsOpen((previous) => !previous);
                }}
            >
                {profileImageUrl ? (
                    <img
                        src={profileImageUrl}
                        alt={fullName || "Photo de profil"}
                        className="h-9 w-9 rounded-full object-cover"
                    />
                ) : (
                    <span className="flex h-9 w-9 items-center justify-center rounded-full bg-[#00343a] text-[12px] font-semibold leading-none tracking-[0.05em] text-white">
                        {initials}
                    </span>
                )}
                <span className="hidden min-w-0 flex-col text-left sm:flex">
                    <span className="max-w-[180px] truncate text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a]">
                        {fullName || "Mon compte"}
                    </span>
                    <span className="max-w-[180px] truncate text-[12px] leading-4 text-[#6a7375]">
                        Menu utilisateur
                    </span>
                </span>
                <MaterialSymbol icon="expand_more" className="text-[#40484a]" />
            </button>

            {isOpen && (
                <div
                    className="absolute right-0 z-50 mt-3 w-72 overflow-hidden rounded-2xl border border-[#e4e8e8] bg-white p-2 shadow-[0_20px_50px_rgba(0,52,58,0.16)]"
                    role="menu"
                >
                    <div className="rounded-xl bg-[#f8fbfb] px-4 py-3">
                        <p className="text-[14px] font-semibold leading-5 text-[#181c1d]">
                            {fullName || "Utilisateur connecté"}
                        </p>
                        <p className="mt-1 truncate text-[12px] leading-4 text-[#6a7375]">
                            {user?.email || "Aucune adresse e-mail"}
                        </p>
                    </div>

                    <div className="mt-2 space-y-1">
                        <Link
                            className="flex items-center gap-3 rounded-xl px-4 py-3 text-[14px] leading-5 text-[#181c1d] transition-colors hover:bg-[#f8fbfb] hover:text-[#00343a]"
                            role="menuitem"
                            to="/dashboard"
                            onClick={() => {
                                setIsOpen(false);
                            }}
                        >
                            <MaterialSymbol icon="dashboard" className="text-[#00343a]" />
                            Dashboard
                        </Link>

                        <Link
                            className="flex items-center gap-3 rounded-xl px-4 py-3 text-[14px] leading-5 text-[#181c1d] transition-colors hover:bg-[#f8fbfb] hover:text-[#00343a]"
                            role="menuitem"
                            to="/profile"
                            onClick={() => {
                                setIsOpen(false);
                            }}
                        >
                            <MaterialSymbol icon="person" className="text-[#00343a]" />
                            Mon profil
                        </Link>

                        <button
                            className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left text-[14px] leading-5 text-[#6a7375] transition-colors hover:bg-[#f8fbfb] hover:text-[#00343a]"
                            role="menuitem"
                            type="button"
                            onClick={() => {
                                setIsOpen(false);
                            }}
                        >
                            <MaterialSymbol icon="settings" className="text-[#6a7375]" />
                            Paramètres
                            <span className="ml-auto text-[12px] text-[#b0b7b8]">Bientôt</span>
                        </button>

                        <button
                            className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left text-[14px] leading-5 text-[#9b3d3d] transition-colors hover:bg-[#fff5f5]"
                            role="menuitem"
                            type="button"
                            onClick={handleLogout}
                        >
                            <MaterialSymbol icon="logout" className="text-[#9b3d3d]" />
                            Déconnexion
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default DashboardUserMenu;

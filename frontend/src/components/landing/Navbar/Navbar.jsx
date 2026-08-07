import { Link } from "react-router-dom";

import Logo from "../../common/Logo";
import LandingNavLinks from "./LandingNavLinks";

function Navbar() {
    return (
        <header className="fixed top-0 z-50 w-full border-b border-[#bfc8ca] bg-[#f7fafb] shadow-sm transition-all duration-300">
            <div className="mx-auto flex w-full max-w-[1200px] items-center justify-between px-4 py-4 md:px-12">
                <div className="flex items-center gap-8">
                    <Logo className="text-[24px] font-bold leading-[32px] text-[#00343a]" />
                    <LandingNavLinks />
                </div>

                <div className="flex items-center gap-4">
                    <Link
                        to="/login"
                        className="hidden rounded-lg px-4 py-2 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a] transition-colors duration-200 hover:bg-[#00343a]/10 md:block"
                    >
                        Connexion
                    </Link>

                    <Link
                        to="/register"
                        className="rounded-lg bg-[#00343a] px-6 py-2.5 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-white shadow-sm transition-transform duration-150 hover:scale-[0.98] hover:bg-[#002b30]"
                    >
                        S&apos;inscrire
                    </Link>

                    <button
                        type="button"
                        aria-label="Ouvrir le menu"
                        className="p-2 text-[#40484a] md:hidden"
                    >
                        <span className="material-symbols-outlined text-[24px]">menu</span>
                    </button>
                </div>
            </div>
        </header>
    );
}

export default Navbar;

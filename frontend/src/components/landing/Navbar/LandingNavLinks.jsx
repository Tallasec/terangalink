import { Link } from "react-router-dom";

const navLinks = [
    { href: "/housing", label: "Logement" },
    { href: "/jobs", label: "Emplois" },
    { href: "/associations", label: "Associations" },
    { href: "#groups", label: "Groupes" },
    { href: "/forum", label: "Forum" },
];

function LandingNavLinks() {
    return (
        <nav className="hidden items-center gap-6 md:flex">
            {navLinks.map((link) =>
                link.href.startsWith("/") ? (
                    <Link
                        key={link.label}
                        to={link.href}
                        className="text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#40484a] transition-colors duration-200 hover:text-[#00343a]"
                    >
                        {link.label}
                    </Link>
                ) : (
                    <a
                        key={link.label}
                        href={link.href}
                        className="text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#40484a] transition-colors duration-200 hover:text-[#00343a]"
                    >
                        {link.label}
                    </a>
                ),
            )}
        </nav>
    );
}

export default LandingNavLinks;

import { Link } from "react-router-dom";

function Logo({ className = "" }) {
    const baseClassName = "text-3xl font-bold tracking-tight text-emerald-600";

    return (
        <Link to="/" className={`${baseClassName} ${className}`}>
            TerangaLink
        </Link>
    );
}

export default Logo;

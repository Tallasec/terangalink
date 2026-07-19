function Logo({ className = "" }) {
    const baseClassName = "text-3xl font-bold tracking-tight text-emerald-600";

    return (
        <div className={`${baseClassName} ${className}`}>
            TerangaLink
        </div>
    );
}

export default Logo;

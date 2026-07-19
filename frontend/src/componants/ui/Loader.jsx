const sizeStyles = {
    sm: "h-4 w-4 border-2",
    md: "h-6 w-6 border-2",
    lg: "h-8 w-8 border-[3px]",
};

function Loader({ size = "md", className = "" }) {
    const baseClassName = "inline-block animate-spin rounded-full border-emerald-600 border-t-transparent";
    const sizeClassName = sizeStyles[size] || sizeStyles.md;

    return (
        <span
            role="status"
            aria-label="Chargement"
            className={`${baseClassName} ${sizeClassName} ${className}`}
        />
    );
}

export default Loader;

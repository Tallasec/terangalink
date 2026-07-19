const alertStyles = {
    success: "border-emerald-200 bg-emerald-50 text-emerald-800",
    error: "border-rose-200 bg-rose-50 text-rose-800",
    warning: "border-amber-200 bg-amber-50 text-amber-800",
    info: "border-sky-200 bg-sky-50 text-sky-800",
};

function Alert({ type = "info", children, className = "" }) {
    const baseClassName = "rounded-xl border px-4 py-3 text-sm font-medium";
    const typeClassName = alertStyles[type] || alertStyles.info;

    return (
        <div role="alert" className={`${baseClassName} ${typeClassName} ${className}`}>
            {children}
        </div>
    );
}

export default Alert;

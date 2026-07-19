function Card({ children, className = "" }) {
    const baseClassName = "rounded-2xl bg-white p-6 shadow-sm ring-1 ring-slate-200/60";

    return (
        <div className={`${baseClassName} ${className}`}>
            {children}
        </div>
    );
}

export default Card;

function PageLayout({ children, className = "" }) {
    const baseClassName = "flex min-h-screen items-center justify-center bg-slate-50 px-4 py-8";

    return (
        <div className={`${baseClassName} ${className}`}>
            {children}
        </div>
    );
}

export default PageLayout;

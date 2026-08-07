function AuthSplitLayout({ children, className = "" }) {
    return (
        <div
            className={`grid w-full grid-cols-1 overflow-hidden rounded-xl bg-white shadow-[0_4px_20px_rgba(0,0,0,0.04)] ring-1 ring-[#e6e9ea] md:grid-cols-2 ${className}`}
        >
            {children}
        </div>
    );
}

export default AuthSplitLayout;

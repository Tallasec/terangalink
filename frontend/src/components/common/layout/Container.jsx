function Container({ children, className = "" }) {
    const baseClassName = "mx-auto w-full max-w-[1320px] px-4 sm:px-6 lg:px-8";

    return (
        <div className={`${baseClassName} ${className}`}>
            {children}
        </div>
    );
}

export default Container;

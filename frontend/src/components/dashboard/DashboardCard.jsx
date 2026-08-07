function DashboardCard({
    as: Component = "div",
    className = "",
    children,
    style,
    ...props
}) {
    return (
        <Component
            className={`soft-elevation transition-all duration-300 ${className}`.trim()}
            style={style}
            {...props}
        >
            {children}
        </Component>
    );
}

export default DashboardCard;

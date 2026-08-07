function MaterialSymbol({ icon, className = "", filled = false, style, ...props }) {
    const resolvedStyle = filled
        ? {
              fontVariationSettings: "'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24",
              ...style,
          }
        : style;

    return (
        <span
            className={`material-symbols-outlined ${className}`.trim()}
            style={resolvedStyle}
            {...props}
        >
            {icon}
        </span>
    );
}

export default MaterialSymbol;

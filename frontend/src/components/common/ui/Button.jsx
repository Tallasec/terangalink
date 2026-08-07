function Button({
  children,
  variant = "primary",
  size = "md",
  disabled = false,
  loading = false,
  type = "button",
  className = "",
  onClick,
}) {
  const variants = {
    primary:
      "bg-green-600 hover:bg-green-700 text-white",
    secondary:
      "bg-white border border-gray-300 text-gray-800 hover:bg-gray-100",
    danger:
      "bg-red-600 hover:bg-red-700 text-white",
    outline:
      "border border-green-600 text-green-600 hover:bg-green-50",
  };

  const sizes = {
    sm: "px-3 py-2 text-sm",
    md: "px-4 py-2 text-base",
    lg: "px-6 py-3 text-lg",
  };

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`
        inline-flex
        items-center
        justify-center
        rounded-lg
        font-medium
        transition-all
        duration-200
        cursor-pointer
        disabled:opacity-50
        disabled:cursor-not-allowed
        ${variants[variant]}
        ${sizes[size]}
        ${className}
      `}
    >
      {loading ? "Chargement..." : children}
    </button>
  );
}

export default Button;

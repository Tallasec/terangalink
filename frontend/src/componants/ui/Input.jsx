function Input({
  label,
  type = "text",
  placeholder = "",
  value,
  onChange,
  name,
  required = false,
  disabled = false,
  error = "",
  className = "",
}) {
  return (
    <div className="flex flex-col gap-2">
      {label && (
        <label className="font-medium text-gray-700">
          {label}
          {required && <span className="text-red-500"> *</span>}
        </label>
      )}

      <input
        type={type}
        name={name}
        value={value}
        placeholder={placeholder}
        onChange={onChange}
        disabled={disabled}
        required={required}
        className={`
          w-full
          rounded-lg
          border
          border-gray-300
          px-4
          py-2
          outline-none
          transition
          focus:border-green-600
          focus:ring-2
          focus:ring-green-200
          disabled:bg-gray-100
          disabled:cursor-not-allowed
          ${error ? "border-red-500 focus:border-red-500 focus:ring-red-200" : ""}
          ${className}
        `}
      />

      {error && (
        <p className="text-sm text-red-500">
          {error}
        </p>
      )}
    </div>
  );
}

export default Input;
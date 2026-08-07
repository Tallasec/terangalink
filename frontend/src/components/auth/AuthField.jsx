import { useState } from "react";
import {
    BookOpen,
    Building2,
    Eye,
    EyeOff,
    Lock,
    Mail,
    MapPin,
    User,
} from "lucide-react";

const iconMap = {
    user: User,
    mail: Mail,
    lock: Lock,

    // Université
    school: Building2,

    // Domaine d'étude
    study: BookOpen,

    // Ville
    city: MapPin,

    eye: Eye,
    eyeoff: EyeOff,
};

function AuthField({
    label,
    type = "text",
    name,
    placeholder = "",
    icon = "",
    actionIcon = "",
    actionLabel = "",
    togglePassword = false,
    required = false,
    autoComplete = "",
    value,
    onChange,
    className = "",
}) {
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);

    const IconComponent = iconMap[icon] || null;
    const ActionIconComponent = iconMap[actionIcon] || null;

    const isPasswordField = type === "password";
    const hasPasswordToggle = togglePassword && isPasswordField;
    const hasActionIcon = !hasPasswordToggle && actionIcon !== "";

    const inputPaddingLeft = IconComponent ? "pl-12" : "pl-4";
    const inputPaddingRight = hasPasswordToggle || hasActionIcon ? "pr-12" : "pr-4";
    // Si l'utilisateur clique sur l'œil,
    // le mot de passe devient visible
    const inputType = hasPasswordToggle && isPasswordVisible ? "text" : type;

    function handleTogglePassword() {
    // Inverse la valeur précédente
    setIsPasswordVisible((previous) => !previous);
    }

    return (
        <div className={`space-y-2 ${className}`}>
            <label
                htmlFor={name}
                className="block text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]"
            >
                {label}
            </label>

            <div className="relative">
                {IconComponent && (
                    <IconComponent
                        size={20}
                        strokeWidth={1.9}
                        className="absolute left-4 top-1/2 -translate-y-1/2 text-[#bfc8ca]"
                    />
                )}

                <input
                    id={name}
                    name={name}
                    type={inputType}
                    placeholder={placeholder}
                    value={value}
                    onChange={onChange}
                    required={required}
                    autoComplete={autoComplete}
                    className={`w-full rounded-lg border border-[#bfc8ca] bg-white py-3 ${inputPaddingLeft} ${inputPaddingRight} text-[16px] leading-[24px] text-[#181c1d] outline-none transition-all duration-200 focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10`}
                />

                {hasPasswordToggle && (
                    <button
                        type="button"
                        aria-label={
                            isPasswordVisible
                                ? "Masquer le mot de passe"
                                : "Afficher le mot de passe"
                        }
                        onClick={handleTogglePassword}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-[#bfc8ca] transition-colors hover:text-[#00343a]"
                    >
                        {isPasswordVisible ? (
                            <EyeOff size={20} strokeWidth={1.9} />
                        ) : (
                            <Eye size={20} strokeWidth={1.9} />
                        )}
                    </button>
                )}

                {hasActionIcon && ActionIconComponent && (
                    <button
                        type="button"
                        aria-label={actionLabel}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-[#bfc8ca] transition-colors hover:text-[#00343a]"
                    >
                        <ActionIconComponent size={20} strokeWidth={1.9} />
                    </button>
                )}
            </div>
        </div>
    );
}

export default AuthField;

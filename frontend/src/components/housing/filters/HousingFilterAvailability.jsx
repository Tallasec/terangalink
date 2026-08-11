import { HOUSING_AVAILABILITY_OPTIONS } from "../../../services/housing/housingHelpers";

function HousingFilterAvailability({ value, onChange }) {
    return (
        <div>
            <p className="mb-3 text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                Disponibilite
            </p>

            <div className="space-y-2">
                {HOUSING_AVAILABILITY_OPTIONS.map((option) => (
                    <label
                        key={option.value || "all"}
                        className="flex cursor-pointer items-center gap-3 rounded-xl px-2 py-2 transition-colors hover:bg-[#f7fafb]"
                    >
                        <input
                            checked={value === option.value}
                            className="h-4 w-4 accent-[#00343a]"
                            name="availability"
                            type="radio"
                            value={option.value}
                            onChange={() => {
                                onChange("available", option.value);
                            }}
                        />
                        <span className="text-[14px] leading-5 text-[#181c1d]">{option.label}</span>
                    </label>
                ))}
            </div>
        </div>
    );
}

export default HousingFilterAvailability;

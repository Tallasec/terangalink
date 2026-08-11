import { HOUSING_TYPE_OPTIONS } from "../../../services/housing/housingHelpers";

function HousingFilterType({ value, onChange }) {
    const options = HOUSING_TYPE_OPTIONS.filter((option) => option.value !== "");

    return (
        <div>
            <p className="mb-3 text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                Type de logement
            </p>

            <div className="space-y-2">
                <label className="flex cursor-pointer items-center gap-3 rounded-xl px-2 py-2 transition-colors hover:bg-[#f7fafb]">
                    <input
                        checked={!value}
                        className="h-4 w-4 accent-[#00343a]"
                        name="housingType"
                        type="radio"
                        value=""
                        onChange={() => {
                            onChange("housingType", "");
                        }}
                    />
                    <span className="text-[14px] leading-5 text-[#181c1d]">Tous les types</span>
                </label>

                {options.map((option) => (
                    <label
                        key={option.value}
                        className="flex cursor-pointer items-center gap-3 rounded-xl px-2 py-2 transition-colors hover:bg-[#f7fafb]"
                    >
                        <input
                            checked={value === option.value}
                            className="h-4 w-4 accent-[#00343a]"
                            name="housingType"
                            type="radio"
                            value={option.value}
                            onChange={() => {
                                onChange("housingType", option.value);
                            }}
                        />
                        <span className="text-[14px] leading-5 text-[#181c1d]">{option.label}</span>
                    </label>
                ))}
            </div>
        </div>
    );
}

export default HousingFilterType;

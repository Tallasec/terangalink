function HousingFilterPriceRange({ minPrice, maxPrice, onChange }) {
    return (
        <div>
            <p className="mb-3 text-[12px] font-semibold uppercase tracking-[0.05em] text-[#40484a]">
                Fourchette de prix
            </p>

            <div className="grid grid-cols-2 gap-3">
                <label className="block">
                    <span className="mb-1 block text-[12px] text-[#70797a]">Min (EUR)</span>
                    <input
                        className="w-full rounded-xl border border-[#dce7e8] bg-[#f7fafb] px-3 py-2 text-[14px] leading-5 text-[#181c1d] outline-none focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                        type="number"
                        min="0"
                        placeholder="0"
                        value={minPrice}
                        onChange={(event) => {
                            onChange("minPrice", event.target.value);
                        }}
                    />
                </label>

                <label className="block">
                    <span className="mb-1 block text-[12px] text-[#70797a]">Max (EUR)</span>
                    <input
                        className="w-full rounded-xl border border-[#dce7e8] bg-[#f7fafb] px-3 py-2 text-[14px] leading-5 text-[#181c1d] outline-none focus:border-[#00343a] focus:ring-2 focus:ring-[#00343a]/10"
                        type="number"
                        min="0"
                        placeholder="2000"
                        value={maxPrice}
                        onChange={(event) => {
                            onChange("maxPrice", event.target.value);
                        }}
                    />
                </label>
            </div>
        </div>
    );
}

export default HousingFilterPriceRange;

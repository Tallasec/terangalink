function HeroStatCard() {
    return (
        <div className="absolute bottom-8 left-8 right-8 rounded-2xl border border-white/20 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <div className="flex items-center gap-4">
                <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-[#00343a]/10 text-[#00343a]">
                    <span className="material-symbols-outlined text-[24px]">home</span>
                </span>

                <div>
                    <p className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                        Logement trouvé à Lyon
                    </p>
                    <p className="text-[14px] font-normal leading-[20px] text-[#40484a]">
                        "Grâce à TerangaLink, j&apos;ai trouvé une colocation solidaire en seulement 3 jours."
                        {" "}
                        — Modou S.
                    </p>
                </div>
            </div>
        </div>
    );
}

export default HeroStatCard;

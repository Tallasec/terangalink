function HowItWorksSection() {
    return (
        <section className="px-4 py-20 md:px-12">
            <div className="mx-auto max-w-[1200px]">
                <div className="grid gap-10 lg:grid-cols-[0.95fr_1.05fr]">
                    <div>
                        <h2 className="max-w-[460px] text-[32px] font-semibold leading-[40px] tracking-[-0.01em] text-[#00343a]">
                            S&apos;installer ne devrait pas être une épreuve solitaire.
                        </h2>

                        <div className="mt-10 space-y-8">
                            <div className="flex gap-4">
                                <div className="flex h-10 w-10 flex-none items-center justify-center rounded-full bg-[#00343a] text-[14px] font-bold text-white">
                                    1
                                </div>
                                <div>
                                    <h3 className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                        Créer votre profil
                                    </h3>
                                    <p className="mt-2 max-w-lg text-[16px] font-normal leading-[24px] text-[#40484a]">
                                        Renseignez votre université, votre ville et ce que vous
                                        recherchez.
                                    </p>
                                </div>
                            </div>

                            <div className="flex gap-4">
                                <div className="flex h-10 w-10 flex-none items-center justify-center rounded-full bg-[#fdd798] text-[14px] font-bold text-[#785c29]">
                                    2
                                </div>
                                <div>
                                    <h3 className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                        Trouvez vos matchs
                                    </h3>
                                    <p className="mt-2 max-w-lg text-[16px] font-normal leading-[24px] text-[#40484a]">
                                        Notre algorithme trouve les meilleurs logements, jobs et
                                        partenaires de révision près de vous.
                                    </p>
                                </div>
                            </div>

                            <div className="flex gap-4">
                                <div className="flex h-10 w-10 flex-none items-center justify-center rounded-full bg-[#00343a] text-[14px] font-bold text-white">
                                    3
                                </div>
                                <div>
                                    <h3 className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                        Réussissez ensemble
                                    </h3>
                                    <p className="mt-2 max-w-lg text-[16px] font-normal leading-[24px] text-[#40484a]">
                                        Accédez à un support continu 24/7 et à des conseils
                                        administratifs.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-rows-[160px_84px_160px] md:gap-6">
                        <div className="col-span-1 rounded-2xl bg-white p-6 soft-elevation md:row-span-2">
                            <div className="flex h-full flex-col items-center justify-center text-center">
                                <span className="material-symbols-outlined mb-4 text-[40px] text-[#00343a]">
                                    description
                                </span>
                                <p className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                    Guide Visa
                                </p>
                            </div>
                        </div>

                        <div className="col-span-1 rounded-2xl bg-[#00343a] p-6 text-white soft-elevation">
                            <div className="flex h-full flex-col items-center justify-center text-center">
                                <span className="material-symbols-outlined mb-4 text-[40px] text-[#99d0d9]">
                                    savings
                                </span>
                                <p className="text-[20px] font-medium leading-[28px]">
                                    Budget
                                </p>
                            </div>
                        </div>

                        <div className="col-span-1 rounded-2xl bg-white p-6 soft-elevation">
                            <div className="flex h-full flex-col items-center justify-center text-center">
                                <span className="material-symbols-outlined mb-4 text-[40px] text-[#00343a]">
                                    chat
                                </span>
                                <p className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                    Chat 24/7
                                </p>
                            </div>
                        </div>

                        <div className="col-span-1 rounded-2xl bg-white p-6 soft-elevation md:row-span-2">
                            <div className="flex h-full flex-col items-center justify-center text-center">
                                <span className="material-symbols-outlined mb-4 text-[40px] text-[#755a26]">
                                    celebration
                                </span>
                                <p className="text-[20px] font-medium leading-[28px] text-[#00343a]">
                                    Événements
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default HowItWorksSection;

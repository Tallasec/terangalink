import MainImage from "../../../assets/img/Main2.PNG";
import CommunityImage from "../../../assets/img/Main3.PNG";

function FeaturesSection() {
    return (
        <section id="housing" className="bg-white px-4 py-16 md:px-12 md:py-20">
            <div className="mx-auto max-w-[1200px]">
                <div className="mb-16 text-center">
                    <h2 className="mb-4 text-[28px] font-semibold leading-[36px] tracking-[-0.01em] text-[#00343a] sm:text-[32px] sm:leading-[40px]">
                        Tout ce dont vous avez besoin
                    </h2>
                    <p className="mx-auto max-w-2xl text-[16px] font-normal leading-[24px] text-[#40484a] sm:text-[18px] sm:leading-[28px]">
                        Une plateforme unique pour simplifier chaque étape de votre vie
                        étudiante en France.
                    </p>
                </div>

                <div className="grid grid-cols-1 gap-6 md:grid-cols-12">
                    <div className="group relative overflow-hidden rounded-3xl bg-[#f1f4f5] p-6 shadow-[0_4px_20px_rgba(0,0,0,0.04)] transition-transform duration-300 hover:scale-[1.01] sm:p-8 md:col-span-8">
                        <div className="flex h-full flex-col items-center gap-8 md:flex-row">
                            <div className="flex-1">
                                <span className="mb-4 block leading-none text-[#00343a]">
                                    <span className="material-symbols-outlined text-[32px] sm:text-[40px]">
                                        real_estate_agent
                                    </span>
                                </span>
                                <h3 className="mb-4 text-[22px] font-semibold leading-[30px] text-[#00343a] sm:text-[24px] sm:leading-[32px]">
                                    Logements sûrs
                                </h3>
                                <p className="mb-6 text-[15px] font-normal leading-[23px] text-[#40484a] sm:text-[16px] sm:leading-[24px]">
                                    Accédez à des offres vérifiées par la communauté, de la
                                    chambre chez l&apos;habitant à la colocation entre étudiants.
                                </p>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-3 text-[14px] font-normal leading-[20px] text-[#181c1d]">
                                        <span className="material-symbols-outlined text-[20px] text-[#755a26]">
                                            check_circle
                                        </span>
                                        Dossiers facilités
                                    </li>
                                    <li className="flex items-center gap-3 text-[14px] font-normal leading-[20px] text-[#181c1d]">
                                        <span className="material-symbols-outlined text-[20px] text-[#755a26]">
                                            check_circle
                                        </span>
                                        Propriétaires bienveillants
                                    </li>
                                </ul>
                            </div>

                            <div className="h-64 w-full overflow-hidden rounded-2xl md:h-full md:w-[46%]">
                                <img
                                    src={MainImage}
                                    alt="Logement étudiant"
                                    className="h-full w-full object-cover object-center"
                                />
                            </div>
                        </div>
                    </div>

                    <div
                        id="jobs"
                        className="rounded-3xl bg-[#00343a] p-6 text-white shadow-[0_4px_20px_rgba(0,0,0,0.04)] transition-transform duration-300 hover:scale-[1.01] sm:p-8 md:col-span-4"
                    >
                        <span className="mb-4 block leading-none text-[#99d0d9]">
                            <span className="material-symbols-outlined text-[32px] sm:text-[40px]">
                                work_history
                            </span>
                        </span>
                        <h3 className="mb-4 text-[22px] font-semibold leading-[30px] sm:text-[24px] sm:leading-[32px]">
                            Jobs étudiants
                        </h3>
                        <p className="mb-8 text-[15px] font-normal leading-[23px] text-white/75 sm:text-[16px] sm:leading-[24px]">
                            Des opportunités adaptées à votre emploi du temps universitaire
                            pour financer vos projets sereinement.
                        </p>
                        <button
                            type="button"
                            className="w-full rounded-xl border border-white/20 bg-white/10 py-3 text-[12px] font-semibold leading-[16px] tracking-[0.05em] transition-all hover:bg-white/20"
                        >
                            Consulter les offres
                        </button>
                    </div>

                    <div
                        id="groups"
                        className="rounded-3xl bg-[#fdd798] p-6 text-[#785c29] shadow-[0_4px_20px_rgba(0,0,0,0.04)] transition-transform duration-300 hover:scale-[1.01] sm:p-8 md:col-span-4"
                    >
                        <span className="mb-4 block leading-none text-[#755a26]">
                            <span className="material-symbols-outlined text-[32px] sm:text-[40px]">
                                groups_3
                            </span>
                        </span>
                        <h3 className="mb-4 text-[22px] font-semibold leading-[30px] sm:text-[24px] sm:leading-[32px]">
                            Groupes de révision
                        </h3>
                        <p className="mb-6 text-[15px] font-normal leading-[23px] text-[#5b4311] sm:text-[16px] sm:leading-[24px]">
                            Ne révisez plus seul. Rejoignez des groupes par spécialité pour
                            partager vos fiches et réussir vos examens.
                        </p>
                        <div className="flex flex-wrap items-center gap-2">
                            <span className="rounded-full bg-white/50 px-3 py-1 text-[12px] font-bold">
                                L1 Droit
                            </span>
                            <span className="rounded-full bg-white/50 px-3 py-1 text-[12px] font-bold">
                                Master Eco
                            </span>
                            <span className="rounded-full bg-white/50 px-3 py-1 text-[12px] font-bold">
                                Ingénierie
                            </span>
                        </div>
                    </div>

                    <div
                        id="community"
                        className="overflow-hidden rounded-3xl bg-[#ebeeef] p-6 shadow-[0_4px_20px_rgba(0,0,0,0.04)] transition-transform duration-300 hover:scale-[1.01] sm:p-8 md:col-span-8"
                    >
                        <div className="flex h-full flex-col items-center gap-8 md:flex-row-reverse">
                            <div className="flex-1">
                                <span className="mb-4 block leading-none text-[#00343a]">
                                    <span className="material-symbols-outlined text-[32px] sm:text-[40px]">
                                        diversity_3
                                    </span>
                                </span>
                                <h3 className="mb-4 text-[22px] font-semibold leading-[30px] text-[#00343a] sm:text-[24px] sm:leading-[32px]">
                                    Communauté vibrante
                                </h3>
                                <p className="mb-6 text-[15px] font-normal leading-[23px] text-[#40484a] sm:text-[16px] sm:leading-[24px]">
                                    Échangez avec des aînés qui sont passés par là. Conseils
                                    administratifs, sorties culturelles et entraide quotidienne.
                                </p>
                                <button
                                    type="button"
                                    className="flex items-center gap-2 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a] hover:underline"
                                >
                                    Rejoindre le forum
                                    <span className="material-symbols-outlined text-[20px]">
                                        arrow_forward
                                    </span>
                                </button>
                            </div>

                            <div className="h-64 w-full overflow-hidden rounded-2xl md:h-full md:w-[46%]">
                                <img
                                    src={CommunityImage}
                                    alt="Communauté d&apos;étudiants"
                                    className="h-full w-full object-cover object-center"
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default FeaturesSection;

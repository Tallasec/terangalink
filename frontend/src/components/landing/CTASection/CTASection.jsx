import { Link } from "react-router-dom";

function CTASection() {
    return (
        <section className="overflow-hidden px-4 py-20 md:px-12 md:py-24">
            <div className="relative mx-auto max-w-[1200px] rounded-[40px] bg-[#00343a] p-8 text-center sm:p-12 md:p-20">
                <div className="pointer-events-none absolute inset-0 opacity-10" />

                <div className="relative z-10">
                    <h2 className="mx-auto mb-8 max-w-3xl text-[36px] font-bold leading-[44px] tracking-[-0.02em] text-white sm:text-[48px] sm:leading-[56px]">
                        Pret a ecrire votre propre histoire de succes ?
                    </h2>

                    <p className="mx-auto mb-12 max-w-xl text-[16px] font-normal leading-[24px] text-white/75 sm:text-[18px] sm:leading-[28px]">
                        Rejoignez la premiere plateforme dediee a la reussite des
                        etudiants senegalais en France.
                    </p>

                    <div className="flex justify-center">
                        <Link
                            to="/register"
                            className="w-full rounded-2xl bg-[#755a26] px-6 py-4 text-[18px] font-semibold leading-[24px] text-white shadow-lg transition-transform hover:scale-105 sm:w-auto sm:px-10 sm:py-5 sm:text-[24px] sm:leading-[32px]"
                        >
                            Creer mon compte gratuit
                        </Link>
                    </div>

                    <p className="mt-8 text-[14px] font-normal leading-[20px] text-white/80">
                        Aucun frais cache. Inscription en moins de 2 minutes.
                    </p>
                </div>
            </div>
        </section>
    );
}

export default CTASection;

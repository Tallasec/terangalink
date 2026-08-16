import { Link } from "react-router-dom";

import MainImage from "../../../assets/img/Main2.PNG";
import HeroSocialProof from "./HeroSocialProof";
import HeroStatCard from "./HeroStatCard";

function HeroSection() {
    return (
        <section className="relative flex min-h-[870px] items-center overflow-hidden bg-[linear-gradient(135deg,#f7fafb_0%,#ebeeef_100%)] px-4 md:px-12">
            <div className="mx-auto grid w-full max-w-[1200px] items-center gap-6 lg:grid-cols-[0.92fr_1.08fr]">
                <div className="z-10 py-12">
                    <span className="mb-6 inline-block rounded-full bg-[#fdd798] px-3 py-1 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#785c29]">
                        Bienvenue sur TerangaLink
                    </span>

                    <h1 className="max-w-[540px] text-[36px] font-bold leading-[44px] tracking-[-0.02em] text-[#00343a] text-balance sm:text-[44px] sm:leading-[52px] lg:text-[48px] lg:leading-[56px]">
                        Votre passerelle vers la réussite en France.
                    </h1>

                    <p className="mt-6 max-w-[540px] text-[16px] font-normal leading-[24px] text-[#40484a] sm:text-[18px] sm:leading-[28px]">
                        L&apos;hospitalité sénégalaise au service de votre avenir. Trouvez
                        un logement, un job étudiant et un reseau solidaire pour briller
                        durant vos études.
                    </p>

                    <div className="mt-10 flex flex-col gap-4 sm:flex-row">
                        <Link
                            to="/register"
                            className="w-full rounded-xl bg-[#00343a] px-6 py-4 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-white shadow-lg transition-all duration-150 hover:translate-y-[-2px] hover:bg-[#002b30] sm:w-auto sm:px-8"
                        >
                            Démarrer l&apos;aventure
                        </Link>

                        <a
                            href="#features"
                            className="w-full rounded-xl border border-[#bfc8ca] bg-transparent px-6 py-4 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a] transition-all duration-150 hover:bg-white sm:w-auto sm:px-8"
                        >
                            Découvrir les services
                        </a>
                    </div>

                    <HeroSocialProof />
                </div>

                <div className="relative hidden md:block">
                    <div className="relative h-[600px] w-full overflow-hidden rounded-3xl shadow-[0_4px_20px_rgba(0,0,0,0.04)]">
                        <img
                            src={MainImage}
                            alt="Intérieur moderne et lumineux"
                            className="h-full w-full object-cover object-center"
                        />
                    </div>

                    <HeroStatCard />
                </div>
            </div>
        </section>
    );
}

export default HeroSection;

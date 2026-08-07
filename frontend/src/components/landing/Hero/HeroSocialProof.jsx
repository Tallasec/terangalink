import AvatarImage from "../../../assets/img/Main3.PNG";

function HeroSocialProof() {
    return (
        <div className="mt-12 flex flex-col items-start gap-4 sm:flex-row sm:items-center">
            <div className="flex -space-x-3">
                <div className="h-10 w-10 overflow-hidden rounded-full border-2 border-white">
                    <img
                        src={AvatarImage}
                        alt=""
                        aria-hidden="true"
                        className="h-full w-full object-cover"
                        style={{ objectPosition: "18% 45%" }}
                    />
                </div>
                <div className="h-10 w-10 overflow-hidden rounded-full border-2 border-white">
                    <img
                        src={AvatarImage}
                        alt=""
                        aria-hidden="true"
                        className="h-full w-full object-cover"
                        style={{ objectPosition: "50% 35%" }}
                    />
                </div>
                <div className="h-10 w-10 overflow-hidden rounded-full border-2 border-white">
                    <img
                        src={AvatarImage}
                        alt=""
                        aria-hidden="true"
                        className="h-full w-full object-cover"
                        style={{ objectPosition: "82% 45%" }}
                    />
                </div>
            </div>

            <p className="text-[14px] font-normal leading-[20px] text-[#40484a]">
                Rejoignez <span className="font-semibold">+5,000</span> étudiants déjà installés.
            </p>
        </div>
    );
}

export default HeroSocialProof;

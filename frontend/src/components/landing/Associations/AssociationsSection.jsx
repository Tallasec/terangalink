import { Link } from "react-router-dom";

import MainImage from "../../../assets/img/Main3.PNG";

const CARDS = [
    {
        title: "Associations Étudiantes",
        description:
            "Retrouvez l'AESF et les associations régionales pour vous accompagner dans votre parcours académique et social en France.",
        icon: "school",
    },
    {
        title: "Dahiras",
        description:
            "Groupes d'entraide mutuelle, religieuse. Trouvez un espace de spiritualité et de solidarité active.",
        icon: "mosque",
    },
    {
        title: "Associations Culturelles",
        description:
            "Promouvoir la culture sénégalaise à travers des événements, des ateliers et des rencontres interculturelles en France.",
        icon: "festival",
    },
];

function AssociationsSection() {
    return (
        <section id="associations" className="bg-[#f7fafb] px-4 py-20 md:px-12 md:py-24">
            <div className="mx-auto max-w-[1200px]">
                <div className="grid items-center gap-12 lg:grid-cols-[minmax(0,1fr)_520px]">
                    <div>
                        <span className="inline-flex items-center gap-2 rounded-full bg-[#f7d68e] px-4 py-2 text-[13px] font-semibold text-[#785c29]">
                            <span className="material-symbols-outlined text-[18px]">diversity_3</span>
                            L'esprit d'hospitalité
                        </span>

                        <h2 className="mt-6 max-w-[640px] text-[48px] font-semibold leading-[1.02] tracking-[-0.06em] text-[#181c1d] sm:text-[62px]">
                            Reseau <span className="text-[#00343a]">Teranga</span>
                            <br />
                            Trouvez vos organisations
                        </h2>

                        <p className="mt-6 max-w-[620px] text-[20px] leading-8 text-[#526062]">
                            TerangaLink vous aide à vous connecter avec les associations et dahiras locales.
                            Rejoignez un reseau solidaire d'étudiants et de professionnels sénégalais en
                            France pour construire des liens durables.
                        </p>

                        <div className="mt-10 flex flex-wrap gap-3">
                            <Link
                                className="inline-flex items-center gap-3 rounded-2xl bg-[#00343a] px-7 py-4 text-[15px] font-semibold text-white shadow-[0px_12px_28px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5"
                                to="/associations"
                            >
                                Rejoindre la discussion
                                <span className="material-symbols-outlined text-[20px]">arrow_forward</span>
                            </Link>
                            <Link
                                className="inline-flex items-center gap-3 rounded-2xl border border-[#dbe6e6] bg-white px-7 py-4 text-[15px] font-semibold text-[#00343a] transition-colors hover:border-[#00343a]"
                                to="/forum"
                            >
                                Aller au forum
                            </Link>
                        </div>
                    </div>

                    <div className="overflow-hidden rounded-[28px] shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                        <img
                            src={MainImage}
                            alt="Groupe de membres"
                            className="h-full w-full object-cover"
                        />
                    </div>
                </div>

                <div className="mt-24">
                    <h3 className="text-[32px] font-semibold tracking-[-0.04em] text-[#181c1d]">
                        Trouver des Organisations
                    </h3>
                    <p className="mt-4 max-w-3xl text-[18px] leading-8 text-[#526062]">
                        Découvrez les structures qui animent la vie du reseau sénégalais en France.
                        Trouvez du soutien, de la spiritualité et de la culture près de chez vous.
                    </p>

                    <div className="mt-14 grid gap-6 lg:grid-cols-3">
                        {CARDS.map((card) => (
                            <div
                                key={card.title}
                                className="rounded-[28px] border border-[#dbe6e6] bg-white p-8 shadow-[0px_10px_34px_rgba(0,52,58,0.05)]"
                            >
                                <div className="flex h-14 w-14 items-center justify-center rounded-full bg-[#00343a] text-white">
                                    <span className="material-symbols-outlined text-[24px]">{card.icon}</span>
                                </div>
                                <h4 className="mt-10 text-[24px] font-semibold tracking-[-0.03em] text-[#181c1d]">
                                    {card.title}
                                </h4>
                                <p className="mt-4 text-[16px] leading-7 text-[#526062]">{card.description}</p>
                                <Link
                                    className="mt-8 inline-flex w-full items-center justify-center rounded-2xl bg-[#00343a] px-6 py-4 text-[14px] font-semibold text-white transition-opacity hover:opacity-95"
                                    to="/associations"
                                >
                                    Parcourir la liste
                                </Link>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
}

export default AssociationsSection;

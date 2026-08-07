function Footer() {
    return (
        <footer className="mt-12 bg-[#f1f4f5]">
            <div className="mx-auto flex max-w-[1200px] flex-col gap-10 px-4 py-12 md:flex-row md:items-start md:justify-between md:px-12">
                <div className="max-w-xs">
                    <h3 className="mb-4 text-[24px] font-bold leading-[32px] text-[#00343a]">
                        TerangaLink
                    </h3>
                    <p className="mb-6 text-[14px] font-normal leading-[20px] text-[#40484a]">
                        © 2024 TerangaLink. Reimagining Senegalese Hospitality in France.
                    </p>

                    <div className="flex gap-4">
                        <a
                            href="#"
                            className="flex h-10 w-10 items-center justify-center rounded-full bg-white text-[#00343a] transition-all hover:bg-[#00343a] hover:text-white"
                        >
                            <span className="material-symbols-outlined text-[20px]">
                                public
                            </span>
                        </a>

                        <a
                            href="#"
                            className="flex h-10 w-10 items-center justify-center rounded-full bg-white text-[#00343a] transition-all hover:bg-[#00343a] hover:text-white"
                        >
                            <span className="material-symbols-outlined text-[20px]">
                                share
                            </span>
                        </a>
                    </div>
                </div>

                <div className="grid w-full grid-cols-1 gap-10 sm:grid-cols-2 md:w-auto md:grid-cols-3 md:gap-12">
                    <div>
                        <h4 className="mb-6 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a]">
                            Plateforme
                        </h4>
                        <ul className="space-y-4">
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    À propos
                                </a>
                            </li>
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    Guide étudiant
                                </a>
                            </li>
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    Logement
                                </a>
                            </li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="mb-6 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a]">
                            Support
                        </h4>
                        <ul className="space-y-4">
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    Contacter le support
                                </a>
                            </li>
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    Conditions d&apos;utilisation
                                </a>
                            </li>
                            <li>
                                <a
                                    href="#"
                                    className="text-[14px] font-normal leading-[20px] text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                >
                                    Politique de confidentialité
                                </a>
                            </li>
                        </ul>
                    </div>

                    <div className="sm:col-span-2 md:col-span-1">
                        <h4 className="mb-6 text-[12px] font-semibold leading-[16px] tracking-[0.05em] text-[#00343a]">
                            Newsletter
                        </h4>
                        <p className="mb-4 text-[14px] font-normal leading-[20px] text-[#40484a]">
                            Recevez les meilleures annonces.
                        </p>

                        <div className="flex gap-2">
                            <input
                                type="email"
                                placeholder="Email"
                                className="min-w-0 flex-1 rounded-lg border border-[#bfc8ca] bg-white px-4 py-2 text-[14px] font-normal leading-[20px] outline-none focus:ring-2 focus:ring-[#00343a]"
                            />
                            <button
                                type="button"
                                className="rounded-lg bg-[#00343a] px-4 py-2 text-white"
                            >
                                <span className="material-symbols-outlined text-[20px]">
                                    send
                                </span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;

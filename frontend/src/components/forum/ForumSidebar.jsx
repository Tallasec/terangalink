import { Link } from "react-router-dom";

import MaterialSymbol from "../common/MaterialSymbol";
import DashboardCard from "../dashboard/DashboardCard";
import {
    getForumAuthorInitials,
    getForumAuthorLabel,
} from "../../services/forum/forumService";

function ForumSidebar({ topAuthors = [] }) {
    return (
        <div className="space-y-5">
            <DashboardCard className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                <h3 className="text-[24px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                    Règles du forum
                </h3>
                <ul className="mt-4 space-y-4 text-[15px] leading-6 text-[#526062]">
                    <RuleItem label="Respect et bienveillance avant tout" />
                    <RuleItem label="Pas de démarchage ni d'annonces payantes" />
                    <RuleItem label="Vérifiez avant de partager une info officielle" />
                    <RuleItem label="Une question par discussion" />
                </ul>
            </DashboardCard>

            <DashboardCard className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                <h3 className="text-[24px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                    Membres les plus actifs
                </h3>

                <div className="mt-4 space-y-4">
                    {topAuthors.length > 0 ? (
                        topAuthors.map((author) => (
                            <div key={author.key} className="flex items-center gap-3">
                                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#00343a] text-[13px] font-semibold text-white">
                                    {getForumAuthorInitials(author)}
                                </div>
                                <div className="min-w-0">
                                    <p className="truncate text-[14px] font-semibold text-[#181c1d]">
                                        {getForumAuthorLabel(author)}
                                    </p>
                                    <p className="text-[13px] text-[#70797a]">
                                        {author.count} contribution{author.count > 1 ? "s" : ""}
                                    </p>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p className="text-[14px] leading-6 text-[#526062]">
                            Les profils les plus actifs apparaîtront ici dès que le forum aura chargé des sujets.
                        </p>
                    )}
                </div>
            </DashboardCard>

            <DashboardCard className="overflow-hidden rounded-[28px] bg-[#00343a] p-6 text-white shadow-[0px_16px_40px_rgba(0,52,58,0.18)]">
                <p className="text-[13px] font-semibold uppercase tracking-[0.16em] text-white/70">
                    Besoin d'une réponse rapide ?
                </p>
                <h3 className="mt-3 text-[24px] font-semibold tracking-[-0.02em]">
                    Rejoignez un groupe d'études et échangez en direct.
                </h3>
                <p className="mt-3 text-[15px] leading-7 text-white/75">
                    Les groupes de révision restent le meilleur moyen d'obtenir une réponse rapide avec d'autres étudiants.
                </p>
                <Link
                    to="/study-groups"
                    className="mt-5 inline-flex items-center rounded-full bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a] transition-transform duration-150 hover:-translate-y-0.5"
                >
                    Groupes
                </Link>
            </DashboardCard>
        </div>
    );
}

function RuleItem({ label }) {
    return (
        <li className="flex items-start gap-3">
            <MaterialSymbol icon="check_circle" className="mt-0.5 text-[18px] text-[#00343a]" />
            <span>{label}</span>
        </li>
    );
}

export default ForumSidebar;

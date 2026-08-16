import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import ForumTopicForm from "../../components/forum/ForumTopicForm";
import {
    buildCreateForumTopicPayload,
    createForumTopic,
    getForumErrorMessage,
} from "../../services/forum/forumService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_FORM_VALUES = {
    title: "",
    category: "",
    content: "",
};

function ForumTopicCreatePage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [values, setValues] = useState(DEFAULT_FORM_VALUES);
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        let isActive = true;

        async function loadUser() {
            try {
                const currentUser = await getCurrentUser();
                if (isActive) {
                    setUser(currentUser);
                }
            } catch {
                if (isActive) {
                    setUser(null);
                }
            }
        }

        loadUser();

        return () => {
            isActive = false;
        };
    }, []);

    function handleChange(event) {
        const { name, value } = event.target;

        setValues((previousValues) => ({
            ...previousValues,
            [name]: value,
        }));
    }

    function validate(valuesToValidate) {
        const nextErrors = {};

        if (!valuesToValidate.title.trim()) {
            nextErrors.title = "Le titre est obligatoire.";
        }

        if (!valuesToValidate.category) {
            nextErrors.category = "La catégorie est obligatoire.";
        }

        if (!valuesToValidate.content.trim()) {
            nextErrors.content = "Le contenu est obligatoire.";
        }

        return nextErrors;
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const nextErrors = validate(values);
        setFieldErrors(nextErrors);
        setError("");

        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        try {
            setLoading(true);
            const payload = buildCreateForumTopicPayload(values);
            const createdTopic = await createForumTopic(payload);

            navigate(`/forum/${createdTopic.id}`, {
                state: {
                    message: "Discussion publiée avec succès.",
                },
            });
        } catch (requestError) {
            setError(getForumErrorMessage(requestError, "Impossible de publier la discussion."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Forum" />

            <main className="mx-auto max-w-[1200px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to="/forum"
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour au forum
                    </Link>
                </div>

                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_330px]">
                    <ForumTopicForm
                        values={values}
                        errors={fieldErrors}
                        loading={loading}
                        onChange={handleChange}
                        onSubmit={handleSubmit}
                        submitLabel="Publier la discussion"
                        title="Nouvelle discussion"
                        description="Choisissez une catégorie, détaillez votre question et les membres pourront vous aider plus vite."
                    />

                    <aside className="space-y-5">
                        <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                            <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                Avant de publier
                            </p>
                            <h2 className="mt-2 text-[22px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                                Une bonne question gagne du temps
                            </h2>
                            <ul className="mt-4 space-y-4 text-[15px] leading-7 text-[#526062]">
                                <li className="flex items-start gap-3">
                                    <MaterialSymbol icon="check_circle" className="mt-0.5 text-[#00343a]" />
                                    Soyez précis sur votre situation.
                                </li>
                                <li className="flex items-start gap-3">
                                    <MaterialSymbol icon="check_circle" className="mt-0.5 text-[#00343a]" />
                                    Indiquez la ville, l&apos;école ou le contexte utile.
                                </li>
                                <li className="flex items-start gap-3">
                                    <MaterialSymbol icon="check_circle" className="mt-0.5 text-[#00343a]" />
                                    Restez poli et ouvrez la porte aux réponses utiles.
                                </li>
                            </ul>
                        </div>

                        <div className="rounded-[28px] border border-[#dbe6e6] bg-[#00343a] p-6 text-white shadow-[0px_16px_40px_rgba(0,52,58,0.18)]">
                            <p className="text-[13px] font-semibold uppercase tracking-[0.16em] text-white/70">
                                Besoin d&apos;entraide rapide ?
                            </p>
                            <h2 className="mt-3 text-[22px] font-semibold tracking-[-0.02em]">
                                Les groupes de révision peuvent aider plus vite
                            </h2>
                            <p className="mt-3 text-[15px] leading-7 text-white/75">
                                Retrouvez des étudiants qui vivent les mêmes démarches et échangent en direct.
                            </p>
                            <Link
                                to="/study-groups"
                                className="mt-5 inline-flex items-center rounded-full bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                            >
                                Voir les groupes
                            </Link>
                        </div>
                    </aside>
                </div>
            </main>

            <DashboardFooter />
        </div>
    );
}

export default ForumTopicCreatePage;

import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import ForumReplyForm from "../../components/forum/ForumReplyForm";
import ForumTopicForm from "../../components/forum/ForumTopicForm";
import {
    createForumAnswer,
    deleteForumAnswer,
    deleteForumTopic,
    fetchForumAnswers,
    formatForumDate,
    formatRelativeForumDate,
    getForumAuthorInitials,
    getForumAuthorLabel,
    getForumCategoryAccentClassName,
    getForumCategoryLabel,
    getForumErrorMessage,
    getForumTopicById,
    updateForumAnswer,
    updateForumTopic,
} from "../../services/forum/forumService";
import { getCurrentUser } from "../../services/user/userService";

const DEFAULT_TOPIC_FORM_VALUES = {
    title: "",
    category: "",
    content: "",
};

function ForumTopicDetailPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [user, setUser] = useState(null);
    const [topic, setTopic] = useState(null);
    const [topicValues, setTopicValues] = useState(DEFAULT_TOPIC_FORM_VALUES);
    const [topicFieldErrors, setTopicFieldErrors] = useState({});
    const [isEditingTopic, setIsEditingTopic] = useState(false);
    const [answers, setAnswers] = useState([]);
    const [showAllAnswers, setShowAllAnswers] = useState(false);
    const [replyValue, setReplyValue] = useState("");
    const [editingAnswerId, setEditingAnswerId] = useState(null);
    const [editingAnswerValue, setEditingAnswerValue] = useState("");
    const [loading, setLoading] = useState(true);
    const [answersLoading, setAnswersLoading] = useState(false);
    const [topicSaving, setTopicSaving] = useState(false);
    const [topicActionError, setTopicActionError] = useState("");
    const [replyLoading, setReplyLoading] = useState(false);
    const [error, setError] = useState("");
    const [replyError, setReplyError] = useState("");
    const [answerActionError, setAnswerActionError] = useState("");
    const [answerActionLoading, setAnswerActionLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");

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

    useEffect(() => {
        let isActive = true;

        async function loadTopic() {
            try {
                setLoading(true);
                setError("");
                const data = await getForumTopicById(id);
                if (isActive) {
                    setTopic(data);
                    setTopicValues({
                        title: data?.title || "",
                        category: data?.category || "",
                        content: data?.content || "",
                    });
                    setTopicFieldErrors({});
                    setTopicActionError("");
                    setIsEditingTopic(false);
                    setShowAllAnswers(false);
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setError(getForumErrorMessage(requestError, "Impossible de charger la discussion."));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadTopic();

        return () => {
            isActive = false;
        };
    }, [id]);

    useEffect(() => {
        if (!topic) {
            return;
        }

        let isActive = true;

        async function loadAnswers() {
            try {
                setAnswersLoading(true);
                const data = await fetchForumAnswers(id);
                if (isActive) {
                    setAnswers(data || []);
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setReplyError(getForumErrorMessage(requestError, "Impossible de charger les réponses."));
                }
            } finally {
                if (isActive) {
                    setAnswersLoading(false);
                }
            }
        }

        loadAnswers();

        return () => {
            isActive = false;
        };
    }, [id, topic]);

    async function handleReplySubmit(event) {
        event.preventDefault();

        try {
            setReplyLoading(true);
            setReplyError("");
            setSuccessMessage("");

            const createdAnswer = await createForumAnswer(id, {
                content: replyValue,
            });

            setAnswers((previousAnswers) => [...previousAnswers, createdAnswer]);
            setReplyValue("");
            setSuccessMessage("Votre réponse a bien été publiée.");
        } catch (requestError) {
            setReplyError(getForumErrorMessage(requestError, "Impossible de publier la réponse."));
        } finally {
            setReplyLoading(false);
        }
    }

    function validateTopicForm(valuesToValidate) {
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

    function handleTopicFieldChange(event) {
        const { name, value } = event.target;

        setTopicValues((previousValues) => ({
            ...previousValues,
            [name]: value,
        }));
    }

    async function handleTopicUpdate(event) {
        event.preventDefault();

        const nextErrors = validateTopicForm(topicValues);
        setTopicFieldErrors(nextErrors);
        setTopicActionError("");

        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        try {
            setTopicSaving(true);
            const updatedTopic = await updateForumTopic(id, {
                title: topicValues.title.trim(),
                category: topicValues.category,
                content: topicValues.content.trim(),
            });

            setTopic(updatedTopic);
            setIsEditingTopic(false);
            setSuccessMessage("La discussion a bien été modifiée.");
        } catch (requestError) {
            setTopicActionError(getForumErrorMessage(requestError, "Impossible de modifier la discussion."));
        } finally {
            setTopicSaving(false);
        }
    }

    async function handleTopicDelete() {
        const confirmed = window.confirm(
            "Supprimer cette discussion supprimera aussi toutes ses réponses. Continuer ?",
        );

        if (!confirmed) {
            return;
        }

        try {
            setTopicActionError("");
            await deleteForumTopic(id);
            navigate("/forum", {
                state: {
                    message: "Discussion supprimée avec succès.",
                },
            });
        } catch (requestError) {
            setTopicActionError(getForumErrorMessage(requestError, "Impossible de supprimer la discussion."));
        }
    }

    function startEditingAnswer(answer) {
        setEditingAnswerId(answer.id);
        setEditingAnswerValue(answer.content || "");
        setAnswerActionError("");
        setSuccessMessage("");
    }

    function cancelEditingAnswer() {
        setEditingAnswerId(null);
        setEditingAnswerValue("");
        setAnswerActionError("");
    }

    async function handleAnswerUpdate(answerId) {
        const nextContent = editingAnswerValue.trim();

        if (!nextContent) {
            setAnswerActionError("Le contenu de la réponse est obligatoire.");
            return;
        }

        try {
            setAnswerActionLoading(true);
            setAnswerActionError("");

            const updatedAnswer = await updateForumAnswer(answerId, {
                content: nextContent,
            });

            setAnswers((previousAnswers) =>
                previousAnswers.map((answer) => (answer.id === answerId ? updatedAnswer : answer)),
            );
            cancelEditingAnswer();
            setSuccessMessage("La réponse a bien été modifiée.");
        } catch (requestError) {
            setAnswerActionError(getForumErrorMessage(requestError, "Impossible de modifier la réponse."));
        } finally {
            setAnswerActionLoading(false);
        }
    }

    async function handleAnswerDelete(answerId) {
        const confirmed = window.confirm("Supprimer cette réponse ?");

        if (!confirmed) {
            return;
        }

        try {
            setAnswerActionLoading(true);
            setAnswerActionError("");
            await deleteForumAnswer(answerId);

            setAnswers((previousAnswers) => previousAnswers.filter((answer) => answer.id !== answerId));

            if (editingAnswerId === answerId) {
                cancelEditingAnswer();
            }
        } catch (requestError) {
            setAnswerActionError(getForumErrorMessage(requestError, "Impossible de supprimer la réponse."));
        } finally {
            setAnswerActionLoading(false);
        }
    }

    const authorLabel = getForumAuthorLabel(topic);
    const answersCount = answers.length;
    const sortedAnswers = [...answers].sort((left, right) => {
        const leftDate = new Date(left.createdAt || 0).getTime();
        const rightDate = new Date(right.createdAt || 0).getTime();

        return rightDate - leftDate;
    });
    const visibleAnswers = showAllAnswers ? sortedAnswers : sortedAnswers.slice(0, 4);
    const hiddenAnswerCount = Math.max(0, sortedAnswers.length - visibleAnswers.length);
    const canManageTopic = Boolean(user && topic && (user.role === "ADMIN" || user.id === topic.authorId));

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

                {topicActionError ? (
                    <Alert type="error" className="mb-6">
                        {topicActionError}
                    </Alert>
                ) : null}

                {answerActionError ? (
                    <Alert type="error" className="mb-6">
                        {answerActionError}
                    </Alert>
                ) : null}

                {loading ? (
                    <TopicDetailSkeleton />
                ) : !topic ? (
                    <TopicNotFound />
                ) : (
                    <div className="grid gap-6 lg:grid-cols-[minmax(0,1.2fr)_330px]">
                        <article className="overflow-hidden rounded-[32px] border border-[#dbe6e6] bg-white shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                            <div className="border-b border-[#edf3f3] bg-[linear-gradient(180deg,#f7fafb_0%,#ffffff_100%)] px-6 py-6 md:px-8">
                                <div className="flex flex-wrap items-center gap-2">
                                    <span
                                        className={`rounded-full px-3 py-1 text-[12px] font-semibold ${getForumCategoryAccentClassName(
                                            topic.category,
                                        )}`}
                                    >
                                        {getForumCategoryLabel(topic.category)}
                                    </span>
                                    <span className="rounded-full bg-[#f7fafb] px-3 py-1 text-[12px] font-semibold text-[#526062]">
                                        {answersCount} réponse{answersCount > 1 ? "s" : ""}
                                    </span>
                                </div>

                                <div className="mt-5 flex items-start gap-4">
                                    <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-[#00343a] text-[14px] font-semibold text-white">
                                        {getForumAuthorInitials(topic)}
                                    </div>
                                    <div className="min-w-0 flex-1">
                                        <h1 className="text-[34px] font-semibold leading-[1.04] tracking-[-0.05em] text-[#00343a] md:text-[46px]">
                                            {topic.title}
                                        </h1>
                                        <p className="mt-3 text-[16px] leading-7 text-[#526062]">
                                            Par <span className="font-semibold text-[#181c1d]">{authorLabel}</span> •{" "}
                                            {formatRelativeForumDate(topic.createdAt)}
                                        </p>
                                    </div>
                                </div>

                                <div className="mt-5 flex flex-wrap gap-3 text-[13px] font-semibold">
                                    <MetaPill icon="visibility" value={`${topic.views || 0} vues`} />
                                    <MetaPill icon="schedule" value={formatForumDate(topic.createdAt)} />
                                    <MetaPill icon="forum" value={`${answersCount} réponse${answersCount > 1 ? "s" : ""}`} />
                                </div>

                                {canManageTopic ? (
                                    <div className="mt-5 flex flex-wrap gap-3">
                                        <button
                                            className="inline-flex items-center gap-2 rounded-full border border-[#dbe6e6] bg-white px-4 py-2 text-[13px] font-semibold text-[#00343a] transition-colors hover:border-[#00343a]"
                                            type="button"
                                            onClick={() => setIsEditingTopic((previousValue) => !previousValue)}
                                        >
                                            <MaterialSymbol icon="edit" />
                                            {isEditingTopic ? "Annuler l'édition" : "Modifier"}
                                        </button>
                                        <button
                                            className="inline-flex items-center gap-2 rounded-full border border-[#f3d3d3] bg-[#fff5f5] px-4 py-2 text-[13px] font-semibold text-[#b42318] transition-colors hover:border-[#b42318]"
                                            type="button"
                                            onClick={handleTopicDelete}
                                        >
                                            <MaterialSymbol icon="delete" />
                                            Supprimer
                                        </button>
                                    </div>
                                ) : null}
                            </div>

                            <div className="px-6 py-6 md:px-8">
                                {isEditingTopic ? (
                                    <div className="space-y-4">
                                        <ForumTopicForm
                                            values={topicValues}
                                            errors={topicFieldErrors}
                                            loading={topicSaving}
                                            onChange={handleTopicFieldChange}
                                            onSubmit={handleTopicUpdate}
                                            submitLabel="Enregistrer les modifications"
                                            title="Modifier la discussion"
                                            description="Mettez à jour le titre, la catégorie ou le contenu de votre question."
                                        />
                                        <div className="flex justify-end">
                                            <button
                                                className="inline-flex items-center rounded-full border border-[#dbe6e6] bg-white px-5 py-3 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                                                type="button"
                                                onClick={() => setIsEditingTopic(false)}
                                            >
                                                Annuler
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <>
                                        <section>
                                            <h2 className="text-[18px] font-semibold text-[#00343a]">Discussion</h2>
                                            <p className="mt-4 whitespace-pre-line text-[15px] leading-8 text-[#526062]">
                                                {topic.content}
                                            </p>
                                        </section>

                                        <section className="mt-10">
                                            <div className="flex flex-wrap items-center justify-between gap-3">
                                                <h2 className="text-[22px] font-semibold tracking-[-0.02em] text-[#00343a]">
                                                    Réponses récentes
                                                </h2>
                                                <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                                                    {answersCount}
                                                </span>
                                            </div>

                                            <div className="mt-5 space-y-4">
                                                {answersLoading ? (
                                                    <AnswersSkeleton />
                                                ) : visibleAnswers.length > 0 ? (
                                                    visibleAnswers.map((answer) => (
                                                        <AnswerCard
                                                            key={answer.id}
                                                            answer={answer}
                                                            currentUser={user}
                                                            isEditing={editingAnswerId === answer.id}
                                                            editingValue={editingAnswerValue}
                                                            loading={answerActionLoading}
                                                            onEdit={() => startEditingAnswer(answer)}
                                                            onDelete={() => handleAnswerDelete(answer.id)}
                                                            onCancelEdit={cancelEditingAnswer}
                                                            onChangeEdit={(event) => setEditingAnswerValue(event.target.value)}
                                                            onSaveEdit={() => handleAnswerUpdate(answer.id)}
                                                        />
                                                    ))
                                                ) : (
                                                    <div className="rounded-[24px] border border-dashed border-[#cdd9d9] bg-[#f7fafb] px-5 py-10 text-center">
                                                        <p className="text-[16px] font-semibold text-[#00343a]">
                                                            Aucune réponse pour le moment
                                                        </p>
                                                        <p className="mt-2 text-[14px] leading-6 text-[#526062]">
                                                            Soyez la première personne à aider cette discussion.
                                                        </p>
                                                    </div>
                                                )}
                                            </div>

                                            {!showAllAnswers && hiddenAnswerCount > 0 ? (
                                                <button
                                                    className="mt-4 inline-flex items-center gap-2 rounded-full border border-[#dbe6e6] bg-white px-4 py-2 text-[13px] font-semibold text-[#00343a] transition-colors hover:border-[#00343a]"
                                                    type="button"
                                                    onClick={() => setShowAllAnswers(true)}
                                                >
                                                    Voir les {hiddenAnswerCount} réponses suivantes
                                                    <MaterialSymbol icon="expand_more" />
                                                </button>
                                            ) : null}
                                        </section>

                                        <section className="mt-10">
                                            {successMessage ? (
                                                <Alert type="success" className="mb-5">
                                                    {successMessage}
                                                </Alert>
                                            ) : null}

                                            <ForumReplyForm
                                                value={replyValue}
                                                error={replyError}
                                                loading={replyLoading}
                                                onChange={(event) => setReplyValue(event.target.value)}
                                                onSubmit={handleReplySubmit}
                                            />
                                        </section>
                                    </>
                                )}
                            </div>
                        </article>

                        <aside className="space-y-5">
                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Aperçu
                                </p>
                                <h3 className="mt-2 text-[22px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                                    {topic.category ? getForumCategoryLabel(topic.category) : "Discussion"}
                                </h3>
                                <div className="mt-4 space-y-3">
                                    <TopicInfo icon="visibility" label="Vues" value={`${topic.views || 0}`} />
                                    <TopicInfo icon="forum" label="Réponses" value={`${answersCount}`} />
                                    <TopicInfo icon="person" label="Auteur" value={authorLabel} />
                                </div>
                            </div>

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
                                <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                                    Conseil
                                </p>
                                <h3 className="mt-2 text-[22px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                                    Restez précis
                                </h3>
                                <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                                    Plus votre question est détaillée, plus les membres peuvent vous répondre rapidement et
                                    utilement.
                                </p>
                            </div>

                            <div className="rounded-[28px] border border-[#dbe6e6] bg-[#00343a] p-6 text-white shadow-[0px_16px_40px_rgba(0,52,58,0.18)]">
                                <p className="text-[13px] font-semibold uppercase tracking-[0.16em] text-white/70">
                                    Besoin de plus d&apos;entraide ?
                                </p>
                                <h3 className="mt-3 text-[22px] font-semibold tracking-[-0.02em]">
                                    Explorez les groupes de révision
                                </h3>
                                <p className="mt-3 text-[15px] leading-7 text-white/75">
                                    Rejoignez des étudiants qui révisent dans votre ville ou votre filière.
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
                )}
            </main>

            <DashboardFooter />
        </div>
    );
}

function MetaPill({ icon, value }) {
    return (
        <div className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-white">
            <MaterialSymbol icon={icon} />
            {value}
        </div>
    );
}

function TopicInfo({ icon, label, value }) {
    return (
        <div className="rounded-[22px] border border-[#edf3f3] bg-[#f7fafb] p-4">
            <div className="flex items-center gap-2 text-[#00343a]">
                <MaterialSymbol icon={icon} />
                <span className="text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">{label}</span>
            </div>
            <div className="mt-2 text-[15px] font-semibold text-[#181c1d]">{value}</div>
        </div>
    );
}

function AnswerCard({
    answer,
    currentUser,
    isEditing,
    editingValue,
    loading,
    onEdit,
    onDelete,
    onCancelEdit,
    onChangeEdit,
    onSaveEdit,
}) {
    const authorLabel = getForumAuthorLabel(answer);
    const canManageAnswer = Boolean(
        currentUser && (currentUser.role === "ADMIN" || currentUser.id === answer.authorId),
    );

    return (
        <div className="rounded-[24px] border border-[#edf3f3] bg-[#f7fafb] p-5">
            <div className="flex items-start gap-4">
                <div className="flex h-11 w-11 flex-shrink-0 items-center justify-center rounded-full bg-[#00343a] text-[13px] font-semibold text-white">
                    {getForumAuthorInitials(answer)}
                </div>

                <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center justify-between gap-3">
                        <div className="flex flex-wrap items-center gap-2">
                            <p className="text-[15px] font-semibold text-[#181c1d]">{authorLabel}</p>
                            <span className="text-[12px] text-[#70797a]">{formatRelativeForumDate(answer.createdAt)}</span>
                        </div>

                        {canManageAnswer && !isEditing ? (
                            <div className="flex flex-wrap gap-2">
                                <button
                                    className="inline-flex items-center gap-1 rounded-full border border-[#dbe6e6] bg-white px-3 py-2 text-[12px] font-semibold text-[#00343a] transition-colors hover:border-[#00343a]"
                                    type="button"
                                    onClick={onEdit}
                                >
                                    <MaterialSymbol icon="edit" />
                                    Modifier
                                </button>
                                <button
                                    className="inline-flex items-center gap-1 rounded-full border border-[#f3d3d3] bg-[#fff5f5] px-3 py-2 text-[12px] font-semibold text-[#b42318] transition-colors hover:border-[#b42318]"
                                    type="button"
                                    onClick={onDelete}
                                >
                                    <MaterialSymbol icon="delete" />
                                    Supprimer
                                </button>
                            </div>
                        ) : null}
                    </div>

                    {isEditing ? (
                        <div className="mt-4 space-y-3">
                            <textarea
                                className="min-h-[140px] w-full rounded-3xl border border-[#dce7e8] bg-white px-4 py-3 text-[15px] leading-7 text-[#181c1d] outline-none transition focus:border-[#00343a]"
                                maxLength={10000}
                                value={editingValue}
                                onChange={onChangeEdit}
                            />
                            <div className="flex flex-wrap justify-end gap-3">
                                <button
                                    className="inline-flex items-center rounded-full border border-[#dbe6e6] bg-white px-4 py-2 text-[13px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                                    type="button"
                                    onClick={onCancelEdit}
                                >
                                    Annuler
                                </button>
                                <button
                                    className="inline-flex items-center gap-2 rounded-full bg-[#00343a] px-4 py-2 text-[13px] font-semibold text-white transition-colors hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-70"
                                    type="button"
                                    disabled={loading}
                                    onClick={onSaveEdit}
                                >
                                    <MaterialSymbol icon="save" />
                                    {loading ? "Enregistrement..." : "Enregistrer"}
                                </button>
                            </div>
                        </div>
                    ) : (
                        <p className="mt-3 whitespace-pre-line text-[15px] leading-7 text-[#526062]">
                            {answer.content}
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

function TopicDetailSkeleton() {
    return (
        <div className="grid gap-6 lg:grid-cols-[minmax(0,1.2fr)_330px]">
            <div className="rounded-[32px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_16px_50px_rgba(0,52,58,0.08)]">
                <div className="h-6 w-40 animate-pulse rounded-full bg-[#edf3f3]" />
                <div className="mt-4 h-12 w-3/4 animate-pulse rounded-full bg-[#edf3f3]" />
                <div className="mt-4 h-5 w-1/2 animate-pulse rounded-full bg-[#edf3f3]" />
                <div className="mt-8 h-64 animate-pulse rounded-[28px] bg-[#edf3f3]" />
            </div>

            <div className="space-y-5">
                <div className="h-48 animate-pulse rounded-[28px] bg-white shadow-[0px_12px_40px_rgba(0,52,58,0.06)]" />
                <div className="h-48 animate-pulse rounded-[28px] bg-white shadow-[0px_12px_40px_rgba(0,52,58,0.06)]" />
            </div>
        </div>
    );
}

function AnswersSkeleton() {
    return (
        <div className="space-y-4">
            <div className="h-28 animate-pulse rounded-[24px] bg-[#edf3f3]" />
            <div className="h-28 animate-pulse rounded-[24px] bg-[#edf3f3]" />
        </div>
    );
}

function TopicNotFound() {
    return (
        <div className="rounded-[32px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
            <h1 className="text-[28px] font-semibold text-[#00343a]">Discussion introuvable</h1>
            <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                Ce sujet n&apos;existe plus ou a été supprimé.
            </p>
            <Link
                className="mt-6 inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                to="/forum"
            >
                Retour au forum
            </Link>
        </div>
    );
}

export default ForumTopicDetailPage;

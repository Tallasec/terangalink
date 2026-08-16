import Alert from "../common/ui/Alert";
import MaterialSymbol from "../common/MaterialSymbol";
import {
    getForumCategoryLabel,
} from "../../services/forum/forumService";

const CATEGORY_OPTIONS = [
    "ADMINISTRATIF",
    "LOGEMENT",
    "ETUDES",
    "ALTERNANCE",
    "EMPLOI",
    "VIE_ETUDIANTE",
    "EVENEMENT",
    "AUTRE",
];

function ForumTopicForm({
    values,
    errors,
    loading,
    onChange,
    onSubmit,
    submitLabel = "Publier",
    title = "Nouvelle discussion",
    description = "Partagez une question claire pour obtenir des réponses utiles.",
}) {
    return (
        <form
            onSubmit={onSubmit}
            className="rounded-[32px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_16px_50px_rgba(0,52,58,0.08)] md:p-8"
        >
            <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                        Forum
                    </p>
                    <h1 className="mt-2 text-[30px] font-semibold tracking-[-0.04em] text-[#00343a]">
                        {title}
                    </h1>
                    <p className="mt-3 text-[15px] leading-7 text-[#526062]">{description}</p>
                </div>

                <div className="rounded-full bg-[#fff3dc] px-4 py-2 text-[13px] font-semibold text-[#7a5a1b]">
                    <MaterialSymbol icon="edit_square" className="mr-2 inline-block align-[-3px]" />
                    Sujet public
                </div>
            </div>

            {errors?.form ? (
                <Alert type="error" className="mt-5">
                    {errors.form}
                </Alert>
            ) : null}

            <div className="mt-6 grid gap-5">
                <Field label="Titre" error={errors?.title}>
                    <input
                        className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[15px] text-[#181c1d] outline-none transition focus:border-[#00343a] focus:bg-white"
                        name="title"
                        maxLength={150}
                        placeholder="Ex. Renouvellement du titre de séjour étudiant"
                        value={values.title}
                        onChange={onChange}
                    />
                </Field>

                <Field label="Catégorie" error={errors?.category}>
                    <select
                        className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[15px] text-[#181c1d] outline-none transition focus:border-[#00343a] focus:bg-white"
                        name="category"
                        value={values.category}
                        onChange={onChange}
                    >
                        <option value="">Choisir une catégorie</option>
                        {CATEGORY_OPTIONS.map((category) => (
                            <option key={category} value={category}>
                                {getForumCategoryLabel(category)}
                            </option>
                        ))}
                    </select>
                </Field>

                <Field label="Contenu" error={errors?.content}>
                    <textarea
                        className="min-h-[220px] w-full rounded-3xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[15px] leading-7 text-[#181c1d] outline-none transition focus:border-[#00343a] focus:bg-white"
                        name="content"
                        maxLength={10000}
                        placeholder="Expliquez votre question avec le plus de détails possible."
                        value={values.content}
                        onChange={onChange}
                    />
                </Field>
            </div>

            <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <p className="text-[13px] leading-6 text-[#70797a]">
                    Respectez les autres membres et évitez les informations non vérifiées.
                </p>

                <button
                    className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-6 py-3 text-[14px] font-semibold text-white shadow-[0px_10px_24px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-70"
                    type="submit"
                    disabled={loading}
                >
                    <MaterialSymbol icon="send" filled />
                    {loading ? "Publication..." : submitLabel}
                </button>
            </div>
        </form>
    );
}

function Field({ label, error, children }) {
    return (
        <label className="block">
            <span className="mb-2 block text-[14px] font-semibold text-[#00343a]">{label}</span>
            {children}
            {error ? <span className="mt-2 block text-[13px] text-[#b42318]">{error}</span> : null}
        </label>
    );
}

export default ForumTopicForm;

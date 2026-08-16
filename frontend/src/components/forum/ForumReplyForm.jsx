import MaterialSymbol from "../common/MaterialSymbol";

function ForumReplyForm({
    value,
    error,
    loading,
    onChange,
    onSubmit,
    submitLabel = "Répondre",
    title = "Ajouter une réponse",
    description = "Aidez les membres avec une réponse claire, pratique et respectueuse.",
}) {
    return (
        <form
            onSubmit={onSubmit}
            className="rounded-[28px] border border-[#dbe6e6] bg-white p-5 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]"
        >
            <div className="flex items-start gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#00343a] text-[13px] font-semibold text-white">
                    <MaterialSymbol icon="chat_bubble" filled />
                </div>
                <div>
                    <h3 className="text-[20px] font-semibold tracking-[-0.02em] text-[#181c1d]">
                        {title}
                    </h3>
                    <p className="mt-1 text-[14px] leading-6 text-[#526062]">{description}</p>
                </div>
            </div>

            <div className="mt-5">
                <textarea
                    className="min-h-[150px] w-full rounded-3xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[15px] leading-7 text-[#181c1d] outline-none transition focus:border-[#00343a] focus:bg-white"
                    maxLength={10000}
                    placeholder="Écrivez votre réponse ici..."
                    value={value}
                    onChange={onChange}
                />

                {error ? <p className="mt-2 text-[13px] text-[#b42318]">{error}</p> : null}
            </div>

            <div className="mt-4 flex items-center justify-between gap-4">
                <p className="text-[13px] leading-6 text-[#70797a]">
                    Les réponses utiles sont plus faciles à retrouver par les membres.
                </p>

                <button
                    className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white shadow-[0px_10px_24px_rgba(0,52,58,0.16)] transition-transform duration-150 hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-70"
                    type="submit"
                    disabled={loading}
                >
                    <MaterialSymbol icon="reply" filled />
                    {loading ? "Envoi..." : submitLabel}
                </button>
            </div>
        </form>
    );
}

export default ForumReplyForm;

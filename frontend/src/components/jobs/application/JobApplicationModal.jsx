import { useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import { applyToJob, getJobErrorMessage } from "../../../services/jobs/jobService";

function JobApplicationModal({ isOpen, job, onClose, onApplied }) {
    const [values, setValues] = useState({
        phoneNumber: "",
        message: "",
        cv: null,
    });
    const [fieldErrors, setFieldErrors] = useState({});
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function handleChange(event) {
        const { name, value, files, type } = event.target;

        setValues((previousValues) => ({
            ...previousValues,
            [name]: type === "file" ? files?.[0] || null : value,
        }));

        setFieldErrors((previousErrors) => {
            if (!previousErrors[name]) {
                return previousErrors;
            }

            const nextErrors = { ...previousErrors };
            delete nextErrors[name];
            return nextErrors;
        });
    }

    function validate() {
        const nextErrors = {};

        if (!values.phoneNumber.trim()) {
            nextErrors.phoneNumber = "Le numero de telephone est obligatoire.";
        }

        if (!values.cv) {
            nextErrors.cv = "Le CV est obligatoire.";
        }

        if (values.cv && values.cv.size > 10 * 1024 * 1024) {
            nextErrors.cv = "Le CV ne doit pas depasser 10 MB.";
        }

        const fileName = values.cv?.name?.toLowerCase?.() || "";
        if (values.cv && !fileName.match(/\.(pdf|doc|docx)$/)) {
            nextErrors.cv = "Formats de CV autorises : pdf, doc, docx.";
        }

        return nextErrors;
    }

    async function handleSubmit(event) {
        event.preventDefault();
        const nextErrors = validate();
        setFieldErrors(nextErrors);

        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        try {
            setSubmitting(true);
            setError("");
            const application = await applyToJob(job.id, values);
            onApplied(application);
        } catch (requestError) {
            setError(getJobErrorMessage(requestError, "Impossible d'envoyer la candidature."));
        } finally {
            setSubmitting(false);
        }
    }

    if (!isOpen) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-[120] flex items-end justify-center bg-[#00343a]/55 px-4 py-4 backdrop-blur-[2px] sm:items-center sm:py-8">
            <div className="w-full max-w-2xl overflow-hidden rounded-[30px] border border-white/20 bg-white shadow-[0px_28px_90px_rgba(0,52,58,0.22)]">
                <div className="flex items-start justify-between border-b border-[#edf3f3] px-6 py-5 md:px-8">
                    <div>
                        <p className="text-[12px] font-semibold uppercase tracking-[0.18em] text-[#70797a]">
                            Candidature
                        </p>
                        <h2 className="mt-2 text-[24px] font-semibold leading-8 text-[#00343a]">
                            Postuler à cette offre
                        </h2>
                        <p className="mt-2 text-[14px] leading-6 text-[#526062]">
                            {job.title} chez {job.companyName}
                        </p>
                    </div>

                    <button
                        aria-label="Fermer"
                        className="rounded-full p-2 text-[#70797a] transition-colors hover:bg-[#f7fafb] hover:text-[#00343a]"
                        type="button"
                        onClick={onClose}
                        disabled={submitting}
                    >
                        <MaterialSymbol icon="close" />
                    </button>
                </div>

                <form className="space-y-5 px-6 py-6 md:px-8" onSubmit={handleSubmit}>
                    {error ? (
                        <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800">
                            {error}
                        </div>
                    ) : null}

                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                            Téléphone
                        </span>
                        <input
                            className="h-12 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                            name="phoneNumber"
                            value={values.phoneNumber}
                            onChange={handleChange}
                            disabled={submitting}
                        />
                        {fieldErrors.phoneNumber ? (
                            <p className="mt-1 text-[13px] text-rose-600">{fieldErrors.phoneNumber}</p>
                        ) : null}
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                            Message
                        </span>
                        <textarea
                            className="min-h-32 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] text-[#181c1d] outline-none focus:border-[#00343a]"
                            name="message"
                            placeholder="Optionnel"
                            value={values.message}
                            onChange={handleChange}
                            disabled={submitting}
                        />
                        <p className="mt-1 text-[12px] text-[#70797a]">Vous pouvez laisser ce champ vide.</p>
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-[13px] font-semibold text-[#3d484a]">
                            CV
                        </span>
                        <input
                            className="w-full rounded-2xl border border-dashed border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] text-[#181c1d] outline-none file:mr-4 file:rounded-full file:border-0 file:bg-[#00343a] file:px-4 file:py-2 file:text-[13px] file:font-semibold file:text-white focus:border-[#00343a]"
                            name="cv"
                            accept=".pdf,.doc,.docx"
                            type="file"
                            onChange={handleChange}
                            disabled={submitting}
                        />
                        {values.cv ? (
                            <p className="mt-2 text-[13px] text-[#526062]">
                                Fichier sélectionné: <span className="font-semibold">{values.cv.name}</span>
                            </p>
                        ) : (
                            <p className="mt-2 text-[13px] text-[#70797a]">Formats acceptés: PDF, DOC, DOCX.</p>
                        )}
                        {fieldErrors.cv ? <p className="mt-1 text-[13px] text-rose-600">{fieldErrors.cv}</p> : null}
                    </label>

                    <div className="flex flex-col-reverse gap-3 border-t border-[#edf3f3] pt-5 sm:flex-row sm:justify-end">
                        <button
                            className="inline-flex items-center justify-center rounded-2xl border border-[#dce7e8] px-5 py-3 text-[14px] font-semibold text-[#3d484a]"
                            type="button"
                            onClick={onClose}
                            disabled={submitting}
                        >
                            Annuler
                        </button>
                        <button
                            className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white disabled:cursor-not-allowed disabled:opacity-70"
                            type="submit"
                            disabled={submitting}
                        >
                            <MaterialSymbol icon="send" filled />
                            {submitting ? "Envoi..." : "Envoyer ma candidature"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default JobApplicationModal;

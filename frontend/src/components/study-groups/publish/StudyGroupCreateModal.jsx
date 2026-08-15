import { useState } from "react";

import Alert from "../../common/ui/Alert";
import MaterialSymbol from "../../common/MaterialSymbol";
import { buildCreateStudyGroupPayload } from "../../../services/study-groups/studyGroupService";

const DEFAULT_VALUES = {
    title: "",
    subject: "",
    description: "",
    city: "",
    meetingType: "ONLINE",
    meetingDate: "",
    maxMembers: 4,
};

function StudyGroupCreateModal({ isOpen, onClose, onCreated, onSubmit }) {
    if (!isOpen) {
        return null;
    }

    return (
        <StudyGroupCreateDialog
            onClose={onClose}
            onCreated={onCreated}
            onSubmit={onSubmit}
        />
    );
}

function StudyGroupCreateDialog({ onClose, onCreated, onSubmit }) {
    const [values, setValues] = useState(DEFAULT_VALUES);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    function handleChange(field, value) {
        setValues((previousValues) => ({
            ...previousValues,
            [field]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        try {
            setLoading(true);
            setError("");

            const payload = buildCreateStudyGroupPayload(values);
            const createdGroup = await onSubmit(payload);
            onCreated(createdGroup);
        } catch (submissionError) {
            setError(
                submissionError?.response?.data?.message ||
                    "Impossible de créer le groupe pour le moment.",
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="fixed inset-0 z-[80] flex items-center justify-center bg-[#001d20]/55 px-4 py-8">
            <div className="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-[28px] bg-white shadow-[0px_20px_70px_rgba(0,0,0,0.22)]">
                <div className="flex items-start justify-between gap-4 border-b border-[#edf3f3] px-6 py-5">
                    <div>
                        <p className="text-[12px] font-semibold uppercase tracking-[0.18em] text-[#70797a]">
                            Nouveau groupe
                        </p>
                        <h2 className="mt-2 text-[28px] font-semibold text-[#00343a]">
                            Créer un groupe de révision
                        </h2>
                    </div>
                    <button
                        aria-label="Fermer"
                        className="rounded-full p-2 text-[#70797a] hover:bg-[#f7fafb]"
                        type="button"
                        onClick={onClose}
                    >
                        <MaterialSymbol icon="close" />
                    </button>
                </div>

                <form className="space-y-5 px-6 py-6" onSubmit={handleSubmit}>
                    {error ? (
                        <Alert type="error">{error}</Alert>
                    ) : null}

                    <div className="grid gap-4 md:grid-cols-2">
                        <Field label="Titre">
                            <input
                                className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                                required
                                type="text"
                                value={values.title}
                                onChange={(event) => handleChange("title", event.target.value)}
                            />
                        </Field>
                        <Field label="Matière">
                            <input
                                className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                                required
                                type="text"
                                value={values.subject}
                                onChange={(event) => handleChange("subject", event.target.value)}
                            />
                        </Field>
                    </div>

                    <Field label="Description">
                        <textarea
                            className="min-h-32 w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                            required
                            value={values.description}
                            onChange={(event) => handleChange("description", event.target.value)}
                        />
                    </Field>

                    <Field label="Ville">
                        <input
                            className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                            required
                            type="text"
                            value={values.city}
                            onChange={(event) => handleChange("city", event.target.value)}
                        />
                    </Field>

                    <div className="grid gap-4 md:grid-cols-3">
                        <Field label="Type de rencontre">
                            <select
                                className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                                value={values.meetingType}
                                onChange={(event) => handleChange("meetingType", event.target.value)}
                            >
                                <option value="ONLINE">En ligne</option>
                                <option value="PHYSICAL">Présentiel</option>
                                <option value="HYBRID">Hybride</option>
                                <option value="TEST">Test</option>
                            </select>
                        </Field>
                        <Field label="Date de réunion">
                            <input
                                className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                                required
                                type="datetime-local"
                                value={values.meetingDate}
                                onChange={(event) => handleChange("meetingDate", event.target.value)}
                            />
                        </Field>
                        <Field label="Places max">
                            <input
                                className="w-full rounded-2xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[14px] outline-none"
                                min="2"
                                required
                                type="number"
                                value={values.maxMembers}
                                onChange={(event) => handleChange("maxMembers", event.target.value)}
                            />
                        </Field>
                    </div>

                    <div className="flex flex-col gap-3 border-t border-[#edf3f3] pt-5 sm:flex-row sm:justify-end">
                        <button
                            className="rounded-full border border-[#dce7e8] bg-white px-5 py-3 text-[14px] font-semibold text-[#00343a]"
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                        >
                            Annuler
                        </button>
                        <button
                            className="inline-flex items-center justify-center gap-2 rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white disabled:cursor-not-allowed disabled:opacity-70"
                            type="submit"
                            disabled={loading}
                        >
                            <MaterialSymbol icon="add_circle" filled />
                            {loading ? "Création..." : "Créer le groupe"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function Field({ label, children }) {
    return (
        <label className="block">
            <span className="mb-2 block text-[13px] font-semibold uppercase tracking-[0.12em] text-[#70797a]">
                {label}
            </span>
            {children}
        </label>
    );
}

export default StudyGroupCreateModal;

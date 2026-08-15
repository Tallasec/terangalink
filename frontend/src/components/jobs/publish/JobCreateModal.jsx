import { useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import Input from "../../common/ui/Input";
import { ContractType, contractTypeLabels } from "../../../services/jobs/contractTypes";
import { createJob, buildCreateJobPayload, getJobErrorMessage } from "../../../services/jobs/jobService";

function validateJobForm(values) {
    const errors = {};

    if (!values.title.trim()) {
        errors.title = "Le titre est obligatoire.";
    }

    if (!values.companyName.trim()) {
        errors.companyName = "Le nom de l'entreprise est obligatoire.";
    }

    if (!values.city.trim()) {
        errors.city = "La ville est obligatoire.";
    }

    if (!values.contractType) {
        errors.contractType = "Le type de contrat est obligatoire.";
    }

    if (!values.salary || Number(values.salary) <= 0) {
        errors.salary = "Le salaire doit etre superieur a 0.";
    }

    return errors;
}

function JobCreateModal({ isOpen, onClose, onCreated }) {
    const [values, setValues] = useState({
        title: "",
        description: "",
        companyName: "",
        city: "",
        address: "",
        contractType: "",
        salary: "",
        available: true,
    });
    const [fieldErrors, setFieldErrors] = useState({});
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    if (!isOpen) return null;

    function handleChange(event) {
        const { name, value, type, checked } = event.target;

        setValues((previousValues) => ({
            ...previousValues,
            [name]: type === "checkbox" ? checked : value,
        }));

        setFieldErrors((previousErrors) => {
            if (!previousErrors[name]) return previousErrors;
            const next = { ...previousErrors };
            delete next[name];
            return next;
        });
    }

    function handleClose() {
        if (submitting) return;
        setValues({ title: "", description: "", companyName: "", city: "", address: "", contractType: "", salary: "", available: true });
        setFieldErrors({});
        setError("");
        onClose();
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        const nextErrors = validateJobForm(values);
        setFieldErrors(nextErrors);

        if (Object.keys(nextErrors).length > 0) return;

        try {
            setSubmitting(true);
            const created = await createJob(buildCreateJobPayload(values));
            onCreated(created);
            setValues({ title: "", description: "", companyName: "", city: "", address: "", contractType: "", salary: "", available: true });
            setFieldErrors({});
            onClose();
        } catch (requestError) {
            setError(getJobErrorMessage(requestError, "Impossible de publier cette offre."));
        } finally {
            setSubmitting(false);
        }
    }

    const contractOptions = ["", ...Object.values(ContractType)];

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-[#00343a]/40 px-4 py-8">
            <div className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-[28px] border border-[#bfc8ca]/40 bg-white shadow-[0px_24px_80px_rgba(0,52,58,0.18)]" role="dialog" aria-modal="true">
                <div className="flex items-start justify-between border-b border-[#ebeeef] px-6 py-5 md:px-8">
                    <div>
                        <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#70797a]">Nouvelle offre</p>
                        <h2 className="mt-2 text-[24px] font-semibold leading-8 text-[#00343a]">Publier une offre</h2>
                    </div>
                    <button aria-label="Fermer" className="rounded-full p-2 text-[#70797a]" type="button" onClick={handleClose} disabled={submitting}>
                        <MaterialSymbol icon="close" />
                    </button>
                </div>

                <form className="space-y-5 px-6 py-6 md:px-8" onSubmit={handleSubmit}>
                    {error ? <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800">{error}</div> : null}

                    <Input label="Titre" name="title" value={values.title} onChange={handleChange} required disabled={submitting} error={fieldErrors.title} />

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-700">Description</span>
                        <textarea className="min-h-28 w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none" name="description" value={values.description} onChange={handleChange} disabled={submitting} />
                    </label>

                    <div className="grid gap-5 sm:grid-cols-2">
                        <Input label="Entreprise" name="companyName" value={values.companyName} onChange={handleChange} required disabled={submitting} error={fieldErrors.companyName} />
                        <Input label="Ville" name="city" value={values.city} onChange={handleChange} required disabled={submitting} error={fieldErrors.city} />
                    </div>

                    <Input label="Adresse" name="address" value={values.address} onChange={handleChange} disabled={submitting} placeholder="Optionnel" />

                    <div className="grid gap-5 sm:grid-cols-2">
                        <label className="block">
                            <span className="mb-2 block text-sm font-medium text-slate-700">Type de contrat</span>
                            <select className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900" name="contractType" value={values.contractType} onChange={handleChange} disabled={submitting}>
                                {contractOptions.map((c) => (
                                    <option key={c} value={c}>{contractTypeLabels[c] ?? c ?? "Tous"}</option>
                                ))}
                            </select>
                            {fieldErrors.contractType ? <p className="mt-1 text-sm text-rose-600">{fieldErrors.contractType}</p> : null}
                        </label>

                        <Input label="Salaire (EUR)" name="salary" type="number" min="0" step="0.01" value={values.salary} onChange={handleChange} required disabled={submitting} error={fieldErrors.salary} />
                    </div>

                    <label className="flex items-center gap-3 rounded-2xl border border-[#ebeeef] bg-[#f7fafb] px-4 py-3">
                        <input checked={values.available} className="h-4 w-4 accent-[#00343a]" name="available" type="checkbox" onChange={handleChange} disabled={submitting} />
                        <span className="text-[14px] leading-5 text-[#40484a]">Offre disponible</span>
                    </label>

                    <div className="flex flex-col-reverse gap-3 border-t border-[#ebeeef] pt-5 sm:flex-row sm:justify-end">
                        <button className="inline-flex items-center justify-center rounded-xl border border-[#dce7e8] px-5 py-3 text-[14px] font-semibold text-[#40484a]" type="button" onClick={handleClose} disabled={submitting}>Annuler</button>
                        <button className="inline-flex items-center justify-center gap-2 rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white" type="submit" disabled={submitting}>{submitting ? "Publication..." : "Publier l'offre"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default JobCreateModal;

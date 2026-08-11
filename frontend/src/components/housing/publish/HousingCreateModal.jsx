import { useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import Input from "../../common/ui/Input";
import { HOUSING_TYPE_OPTIONS, createEmptyHousingForm } from "../../../services/housing/housingHelpers";
import {
    buildCreateHousingPayload,
    createHousing,
    getHousingErrorMessage,
} from "../../../services/housing/housingService";

function validateHousingForm(values) {
    const errors = {};

    if (!values.title.trim()) {
        errors.title = "Le titre est obligatoire.";
    }

    if (!values.city.trim()) {
        errors.city = "La ville est obligatoire.";
    }

    if (!values.price || Number(values.price) <= 0) {
        errors.price = "Le prix doit etre superieur a 0.";
    }

    if (!values.housingType) {
        errors.housingType = "Le type de logement est obligatoire.";
    }

    return errors;
}

function HousingCreateModal({ isOpen, onClose, onCreated }) {
    const [values, setValues] = useState(createEmptyHousingForm);
    const [fieldErrors, setFieldErrors] = useState({});
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    if (!isOpen) {
        return null;
    }

    function handleChange(event) {
        const { name, value, type, checked } = event.target;

        setValues((previousValues) => ({
            ...previousValues,
            [name]: type === "checkbox" ? checked : value,
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

    function handleClose() {
        if (submitting) {
            return;
        }

        setValues(createEmptyHousingForm());
        setFieldErrors({});
        setError("");
        onClose();
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");

        const nextErrors = validateHousingForm(values);
        setFieldErrors(nextErrors);

        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        try {
            setSubmitting(true);

            const createdHousing = await createHousing(buildCreateHousingPayload(values));
            onCreated(createdHousing);
            setValues(createEmptyHousingForm());
            setFieldErrors({});
            onClose();
        } catch (requestError) {
            setError(getHousingErrorMessage(requestError, "Impossible de publier cette annonce."));
        } finally {
            setSubmitting(false);
        }
    }

    const typeOptions = HOUSING_TYPE_OPTIONS.filter((option) => option.value);

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-[#00343a]/40 px-4 py-8">
            <div
                className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-[28px] border border-[#bfc8ca]/40 bg-white shadow-[0px_24px_80px_rgba(0,52,58,0.18)]"
                role="dialog"
                aria-modal="true"
                aria-labelledby="housing-create-title"
            >
                <div className="flex items-start justify-between border-b border-[#ebeeef] px-6 py-5 md:px-8">
                    <div>
                        <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#70797a]">
                            Nouvelle annonce
                        </p>
                        <h2
                            id="housing-create-title"
                            className="mt-2 text-[24px] font-semibold leading-8 text-[#00343a]"
                        >
                            Publier un logement
                        </h2>
                    </div>
                    <button
                        aria-label="Fermer"
                        className="rounded-full p-2 text-[#70797a] transition-colors hover:bg-[#f7fafb] hover:text-[#00343a]"
                        type="button"
                        onClick={handleClose}
                        disabled={submitting}
                    >
                        <MaterialSymbol icon="close" />
                    </button>
                </div>

                <form className="space-y-5 px-6 py-6 md:px-8" onSubmit={handleSubmit}>
                    {error ? (
                        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800">
                            {error}
                        </div>
                    ) : null}

                    <Input
                        label="Titre de l'annonce"
                        name="title"
                        value={values.title}
                        onChange={handleChange}
                        required
                        disabled={submitting}
                        error={fieldErrors.title}
                        placeholder="Studio lumineux pres du campus"
                    />

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-700">Description</span>
                        <textarea
                            className="min-h-28 w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-green-600 focus:ring-2 focus:ring-green-100"
                            name="description"
                            value={values.description}
                            onChange={handleChange}
                            disabled={submitting}
                            placeholder="Decrivez le logement, le quartier, les conditions..."
                        />
                    </label>

                    <div className="grid gap-5 sm:grid-cols-2">
                        <Input
                            label="Ville"
                            name="city"
                            value={values.city}
                            onChange={handleChange}
                            required
                            disabled={submitting}
                            error={fieldErrors.city}
                        />
                        <Input
                            label="Adresse"
                            name="address"
                            value={values.address}
                            onChange={handleChange}
                            disabled={submitting}
                            placeholder="Optionnel"
                        />
                    </div>

                    <div className="grid gap-5 sm:grid-cols-2">
                        <Input
                            label="Prix mensuel (EUR)"
                            name="price"
                            type="number"
                            min="1"
                            step="0.01"
                            value={values.price}
                            onChange={handleChange}
                            required
                            disabled={submitting}
                            error={fieldErrors.price}
                        />

                        <label className="block">
                            <span className="mb-2 block text-sm font-medium text-slate-700">
                                Type de logement
                            </span>
                            <select
                                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-green-600 focus:ring-2 focus:ring-green-100"
                                name="housingType"
                                value={values.housingType}
                                onChange={handleChange}
                                disabled={submitting}
                            >
                                {typeOptions.map((option) => (
                                    <option key={option.value} value={option.value}>
                                        {option.label}
                                    </option>
                                ))}
                            </select>
                            {fieldErrors.housingType ? (
                                <p className="mt-1 text-sm text-rose-600">{fieldErrors.housingType}</p>
                            ) : null}
                        </label>
                    </div>

                    <label className="flex items-center gap-3 rounded-2xl border border-[#ebeeef] bg-[#f7fafb] px-4 py-3">
                        <input
                            checked={values.available}
                            className="h-4 w-4 accent-[#00343a]"
                            name="available"
                            type="checkbox"
                            onChange={handleChange}
                            disabled={submitting}
                        />
                        <span className="text-[14px] leading-5 text-[#40484a]">
                            Annonce disponible des la publication
                        </span>
                    </label>

                    <div className="flex flex-col-reverse gap-3 border-t border-[#ebeeef] pt-5 sm:flex-row sm:justify-end">
                        <button
                            className="inline-flex items-center justify-center rounded-xl border border-[#dce7e8] px-5 py-3 text-[14px] font-semibold text-[#40484a] transition-colors hover:bg-[#f7fafb]"
                            type="button"
                            onClick={handleClose}
                            disabled={submitting}
                        >
                            Annuler
                        </button>
                        <button
                            className="inline-flex items-center justify-center gap-2 rounded-xl bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white transition-colors hover:bg-[#004851] disabled:cursor-not-allowed disabled:opacity-70"
                            type="submit"
                            disabled={submitting}
                        >
                            <MaterialSymbol icon="publish" className="text-white" />
                            {submitting ? "Publication..." : "Publier l'annonce"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default HousingCreateModal;

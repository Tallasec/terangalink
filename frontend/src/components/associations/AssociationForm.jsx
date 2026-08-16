import Alert from "../common/ui/Alert";
import MaterialSymbol from "../common/MaterialSymbol";
import { getAssociationTypeLabel } from "../../services/associations/associationService";

const TYPE_OPTIONS = ["ETUDIANTE", "DAHIRA", "CULTURELLE", "SPORTIVE", "HUMANITAIRE", "AUTRE"];

function AssociationForm({
    values,
    errors,
    loading,
    onChange,
    onSubmit,
    submitLabel = "Publier",
    title = "Nouvelle organisation",
    description = "Partagez les informations essentielles pour aider les membres à vous trouver.",
}) {
    return (
        <form
            className="rounded-[32px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_16px_50px_rgba(0,52,58,0.08)] md:p-8"
            onSubmit={onSubmit}
        >
            <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                        Associations
                    </p>
                    <h1 className="mt-2 text-[30px] font-semibold tracking-[-0.04em] text-[#00343a]">
                        {title}
                    </h1>
                    <p className="mt-3 text-[15px] leading-7 text-[#526062]">{description}</p>
                </div>

                <div className="rounded-full bg-[#fff3dc] px-4 py-2 text-[13px] font-semibold text-[#7a5a1b]">
                    <MaterialSymbol icon="groups" className="mr-2 inline-block align-[-3px]" />
                    Organisation publique
                </div>
            </div>

            {errors?.form ? (
                <Alert type="error" className="mt-5">
                    {errors.form}
                </Alert>
            ) : null}

            <div className="mt-6 grid gap-5">
                <Field label="Nom" error={errors?.title}>
                    <input name="title" value={values.title} onChange={onChange} maxLength={150} placeholder="Ex. Association des étudiants sénégalais" />
                </Field>

                <Field label="Type" error={errors?.associationType}>
                    <select name="associationType" value={values.associationType} onChange={onChange}>
                        <option value="">Choisir un type</option>
                        {TYPE_OPTIONS.map((type) => (
                            <option key={type} value={type}>
                                {getAssociationTypeLabel(type)}
                            </option>
                        ))}
                    </select>
                </Field>

                <Field label="Ville" error={errors?.city}>
                    <input name="city" value={values.city} onChange={onChange} maxLength={120} placeholder="Paris, Lyon..." />
                </Field>

                <Field label="Adresse" error={errors?.address}>
                    <input name="address" value={values.address} onChange={onChange} maxLength={255} placeholder="Rue, quartier, bâtiment..." />
                </Field>

                <Field label="Email de contact" error={errors?.contactEmail}>
                    <input name="contactEmail" value={values.contactEmail} onChange={onChange} maxLength={255} placeholder="contact@exemple.org" />
                </Field>

                <Field label="Téléphone" error={errors?.phone}>
                    <input name="phone" value={values.phone} onChange={onChange} maxLength={30} placeholder="+221..." />
                </Field>

                <Field label="Site web" error={errors?.website}>
                    <input name="website" value={values.website} onChange={onChange} maxLength={255} placeholder="https://..." />
                </Field>

                <Field label="Logo" error={errors?.logoUrl}>
                    <input name="logoUrl" value={values.logoUrl} onChange={onChange} maxLength={255} placeholder="https://..." />
                </Field>

                <Field label="Description" error={errors?.description}>
                    <textarea
                        name="description"
                        value={values.description}
                        onChange={onChange}
                        maxLength={10000}
                        className="min-h-[220px] w-full rounded-3xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-3 text-[15px] leading-7 text-[#181c1d] outline-none transition focus:border-[#00343a] focus:bg-white"
                        placeholder="Décrivez la mission, les activités, le public concerné..."
                    />
                </Field>

                <label className="flex items-center gap-3 rounded-3xl border border-[#dce7e8] bg-[#f7fafb] px-4 py-4">
                    <input
                        type="checkbox"
                        name="available"
                        checked={Boolean(values.available)}
                        onChange={onChange}
                        className="h-5 w-5 rounded border-[#bfc8ca] text-[#00343a]"
                    />
                    <span className="text-[14px] font-semibold text-[#00343a]">Organisation actuellement ouverte</span>
                </label>
            </div>

            <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <p className="text-[13px] leading-6 text-[#70797a]">
                    Vérifiez bien les coordonnées avant de publier.
                </p>

                <button
                    className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#00343a] px-6 py-3 text-[14px] font-semibold text-white disabled:cursor-not-allowed disabled:opacity-70"
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
            <style>{`
                input, select {
                    width: 100%;
                    border-radius: 1rem;
                    border: 1px solid #dce7e8;
                    background: #f7fafb;
                    padding: 0.8rem 1rem;
                    font-size: 15px;
                    color: #181c1d;
                    outline: none;
                    transition: border-color .2s ease, background-color .2s ease;
                }
                input:focus, select:focus {
                    border-color: #00343a;
                    background: #fff;
                }
            `}</style>
        </label>
    );
}

export default AssociationForm;

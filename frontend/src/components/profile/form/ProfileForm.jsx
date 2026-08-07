import Input from "../../common/ui/Input";
import ProfileActions from "../actions/ProfileActions";

function ProfileForm({ values, errors, loading, onChange, onSubmit, onCancel }) {
    return (
        <section className="overflow-hidden rounded-[28px] border border-[#dce7e8] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.08)]">
            <div className="border-b border-[#dce7e8] px-8 py-6">
                <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#6a7375]">
                    Edition
                </p>
                <h2 className="mt-2 text-[24px] font-semibold leading-8 text-[#00343a]">
                    Modifier les informations du profil
                </h2>
            </div>

            <form className="space-y-6 p-8" onSubmit={onSubmit}>
                <div className="grid gap-6 sm:grid-cols-2">
                    <Input
                        label="Prénom"
                        name="firstName"
                        value={values.firstName}
                        onChange={onChange}
                        required
                        disabled={loading}
                        error={errors.firstName}
                    />
                    <Input
                        label="Nom"
                        name="lastName"
                        value={values.lastName}
                        onChange={onChange}
                        required
                        disabled={loading}
                        error={errors.lastName}
                    />
                </div>

                <Input
                    label="Adresse e-mail"
                    type="email"
                    name="email"
                    value={values.email}
                    onChange={onChange}
                    required
                    disabled={loading}
                    error={errors.email}
                />

                <div className="grid gap-6 sm:grid-cols-2">
                    <Input
                        label="Université"
                        name="university"
                        value={values.university}
                        onChange={onChange}
                        required
                        disabled={loading}
                        error={errors.university}
                    />
                    <Input
                        label="Ville"
                        name="city"
                        value={values.city}
                        onChange={onChange}
                        required
                        disabled={loading}
                        error={errors.city}
                    />
                </div>

                <Input
                    label="Domaine d'étude"
                    name="fieldOfStudy"
                    value={values.fieldOfStudy}
                    onChange={onChange}
                    required
                    disabled={loading}
                    error={errors.fieldOfStudy}
                />

                <div className="rounded-2xl border border-[#dce7e8] bg-[#f8fbfb] p-4 text-[14px] leading-6 text-[#40484a]">
                    Les champs telephone et biographie seront ajoutés dans une prochaine
                    évolution de l'API.
                </div>

                <ProfileActions isEditing loading={loading} onCancel={onCancel} />
            </form>
        </section>
    );
}

export default ProfileForm;

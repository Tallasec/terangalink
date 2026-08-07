import { getUserFullName, getUserInitials } from "../../../services/user/userHelpers";

function ProfileAvatar({ user, photoPreviewUrl, uploadingPhoto, onPhotoChange }) {
    const fullName = getUserFullName(user);
    const initials = getUserInitials(user);
    const displayedImage = photoPreviewUrl || user?.profileImageUrl || "";

    return (
        <section className="overflow-hidden rounded-[28px] border border-[#dce7e8] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.08)]">
            <div className="bg-gradient-to-br from-[#00343a] via-[#0f5b60] to-[#285d62] px-8 py-8 text-white">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-white/70">
                        Photo de profil
                    </p>
                    <h2 className="mt-2 text-[24px] font-semibold leading-8">
                        {fullName || "Votre identite TerangaLink"}
                    </h2>
                </div>

                <div className="mt-8 flex items-center gap-5">
                    {displayedImage ? (
                        <img
                            src={displayedImage}
                            alt={`Photo de profil de ${fullName || "l'utilisateur"}`}
                            className="h-24 w-24 rounded-[28px] border-4 border-white/15 object-cover shadow-[0_12px_30px_rgba(0,0,0,0.18)]"
                        />
                    ) : (
                        <div className="flex h-24 w-24 items-center justify-center rounded-[28px] border-4 border-white/15 bg-[#fdd798] text-[28px] font-bold tracking-[0.08em] text-[#785c29] shadow-[0_12px_30px_rgba(0,0,0,0.18)]">
                            {initials}
                        </div>
                    )}

                    <div className="space-y-2">
                        <p className="text-[14px] leading-6 text-white/85">
                            {displayedImage
                                ? "Votre photo actuelle est affichee ici."
                                : "Ajoutez une photo pour personnaliser votre profil."}
                        </p>
                        <p className="text-[12px] uppercase tracking-[0.08em] text-white/60">
                            {user?.email || "Aucune adresse e-mail disponible"}
                        </p>
                    </div>
                </div>
            </div>

            <div className="space-y-4 px-8 py-6">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#6a7375]">
                        Mettre a jour
                    </p>
                    <h3 className="mt-2 text-[18px] font-semibold text-[#00343a]">
                        Changer la photo
                    </h3>
                </div>

                <input
                    id="profile-photo-input"
                    type="file"
                    accept="image/png,image/jpeg,image/webp"
                    className="sr-only"
                    onChange={onPhotoChange}
                    disabled={uploadingPhoto}
                />

                <label
                    htmlFor="profile-photo-input"
                    className={`inline-flex w-full cursor-pointer items-center justify-center rounded-xl border px-4 py-3 text-sm font-semibold transition-colors ${
                        uploadingPhoto
                            ? "cursor-not-allowed border-[#dce7e8] bg-[#f8fbfb] text-[#a2a9ab]"
                            : "border-[#dce7e8] bg-white text-[#00343a] hover:bg-[#f8fbfb]"
                    }`}
                    aria-busy={uploadingPhoto}
                >
                    {uploadingPhoto ? "Televersement en cours..." : "Choisir une image"}
                </label>

                <p className="text-sm leading-6 text-[#6a7375]">
                    JPG, PNG ou WEBP. La photo est envoyee des selection.
                </p>
            </div>
        </section>
    );
}

export default ProfileAvatar;

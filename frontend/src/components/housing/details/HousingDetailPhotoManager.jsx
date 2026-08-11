import { useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import Alert from "../../common/ui/Alert";
import {
    deleteHousingImage,
    getHousingErrorMessage,
    updateHousing,
    uploadHousingImages,
} from "../../../services/housing/housingService";

function HousingDetailPhotoManager({ housing, onHousingUpdated }) {
    const [uploading, setUploading] = useState(false);
    const [updatingAvailability, setUpdatingAvailability] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    async function handlePhotoChange(event) {
        const selectedFiles = Array.from(event.target.files || []);

        if (selectedFiles.length === 0) {
            return;
        }

        setError("");
        setSuccessMessage("");

        const invalidFile = selectedFiles.find(
            (file) => !file.type.startsWith("image/") || file.size > 5 * 1024 * 1024,
        );

        if (invalidFile) {
            setError("Chaque photo doit etre une image de 5 Mo maximum (JPG, PNG ou WEBP).");
            event.target.value = "";
            return;
        }

        try {
            setUploading(true);
            await uploadHousingImages(housing.id, selectedFiles);
            const updatedHousing = await onHousingUpdated();
            setSuccessMessage(
                updatedHousing?.images?.length
                    ? "Photos ajoutees avec succes."
                    : "Photos envoyees avec succes.",
            );
        } catch (requestError) {
            setError(getHousingErrorMessage(requestError, "Impossible d'ajouter les photos."));
        } finally {
            setUploading(false);
            event.target.value = "";
        }
    }

    async function handleDeleteImage(imageId) {
        setError("");
        setSuccessMessage("");

        try {
            setUploading(true);
            await deleteHousingImage(imageId);
            await onHousingUpdated();
            setSuccessMessage("Photo supprimee.");
        } catch (requestError) {
            setError(getHousingErrorMessage(requestError, "Impossible de supprimer cette photo."));
        } finally {
            setUploading(false);
        }
    }

    async function handleToggleAvailability() {
        setError("");
        setSuccessMessage("");

        try {
            setUpdatingAvailability(true);
            const updatedHousing = await updateHousing(housing.id, {
                available: !housing.available,
            });
            await onHousingUpdated(updatedHousing);
            setSuccessMessage(
                updatedHousing.available
                    ? "Annonce marquee comme disponible."
                    : "Annonce marquee comme indisponible.",
            );
        } catch (requestError) {
            setError(
                getHousingErrorMessage(
                    requestError,
                    "Impossible de mettre a jour la disponibilite.",
                ),
            );
        } finally {
            setUpdatingAvailability(false);
        }
    }

    return (
        <section
            id="housing-photo-manager"
            className="rounded-[28px] border border-[#bfc8ca]/40 bg-white p-6 shadow-[0px_8px_30px_rgba(0,0,0,0.06)] md:p-8"
        >
            <div className="flex flex-col gap-4 border-b border-[#ebeeef] pb-6 md:flex-row md:items-start md:justify-between">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#70797a]">
                        Gestion de l'annonce
                    </p>
                    <h2 className="mt-2 text-[22px] font-semibold leading-7 text-[#00343a]">
                        Photos et disponibilite
                    </h2>
                    <p className="mt-2 text-[14px] leading-6 text-[#70797a]">
                        Ajoutez jusqu'a 10 photos. Marquez l'annonce indisponible uniquement
                        lorsque le logement n'est plus libre.
                    </p>
                </div>

                <button
                    className="inline-flex items-center justify-center gap-2 rounded-xl border border-[#00343a] px-4 py-3 text-[14px] font-semibold text-[#00343a] transition-colors hover:bg-[#f7fafb] disabled:cursor-not-allowed disabled:opacity-60"
                    type="button"
                    disabled={updatingAvailability}
                    onClick={handleToggleAvailability}
                >
                    <MaterialSymbol icon={housing.available ? "event_busy" : "event_available"} />
                    {updatingAvailability
                        ? "Mise a jour..."
                        : housing.available
                          ? "Marquer comme indisponible"
                          : "Remettre disponible"}
                </button>
            </div>

            {error ? (
                <Alert type="error" className="mt-5">
                    {error}
                </Alert>
            ) : null}

            {successMessage ? (
                <Alert type="success" className="mt-5">
                    {successMessage}
                </Alert>
            ) : null}

            <div className="mt-6">
                <input
                    id="housing-photo-upload"
                    type="file"
                    accept="image/png,image/jpeg,image/webp"
                    multiple
                    className="sr-only"
                    disabled={uploading}
                    onChange={handlePhotoChange}
                />

                <label
                    htmlFor="housing-photo-upload"
                    className={`inline-flex w-full cursor-pointer items-center justify-center gap-2 rounded-xl border px-4 py-3 text-sm font-semibold transition-colors sm:w-auto ${
                        uploading
                            ? "cursor-not-allowed border-[#dce7e8] bg-[#f8fbfb] text-[#a2a9ab]"
                            : "border-[#00343a] bg-[#00343a] text-white hover:bg-[#004851]"
                    }`}
                >
                    <MaterialSymbol icon="add_photo_alternate" className="text-white" />
                    {uploading ? "Envoi en cours..." : "Ajouter des photos"}
                </label>
            </div>

            {housing.images?.length ? (
                <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                    {housing.images.map((image) => (
                        <div
                            key={image.id}
                            className="group relative overflow-hidden rounded-2xl border border-[#bfc8ca]/40 bg-[#f7fafb]"
                        >
                            <img
                                alt="Photo du logement"
                                className="aspect-[4/3] w-full object-cover"
                                src={image.imageUrl}
                            />
                            <button
                                aria-label="Supprimer la photo"
                                className="absolute right-3 top-3 rounded-full bg-white/95 p-2 text-[#9b3d3d] opacity-0 shadow transition-opacity group-hover:opacity-100"
                                type="button"
                                disabled={uploading}
                                onClick={() => {
                                    handleDeleteImage(image.id);
                                }}
                            >
                                <MaterialSymbol icon="delete" />
                            </button>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="mt-6 rounded-2xl border border-dashed border-[#bfc8ca] bg-[#f7fafb] px-4 py-8 text-center text-[14px] leading-6 text-[#70797a]">
                    Aucune photo pour le moment. Ajoutez la premiere photo de votre annonce.
                </div>
            )}
        </section>
    );
}

export default HousingDetailPhotoManager;

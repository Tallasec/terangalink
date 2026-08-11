import { useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import Alert from "../../common/ui/Alert";
import Input from "../../common/ui/Input";
import { isHousingOwner } from "../../../services/housing/housingHelpers";
import {
    cancelHousingReservation,
    createHousingReservation,
    getHousingErrorMessage,
} from "../../../services/housing/housingService";

function validateReservationForm(phoneNumber, message) {
    const errors = {};

    if (!phoneNumber.trim()) {
        errors.phoneNumber = "Votre numero de telephone est obligatoire.";
    }

    if (!message.trim()) {
        errors.message = "Un message pour le proprietaire est obligatoire.";
    }

    return errors;
}

function HousingDetailActions({ user, housing, reservation, onReservationChange }) {
    const [phoneNumber, setPhoneNumber] = useState("");
    const [message, setMessage] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const isOwner = isHousingOwner(user, housing);
    const hasActiveReservation = reservation?.status === "CONFIRMED";

    if (isOwner) {
        return (
            <div className="mt-4 rounded-2xl border border-[#ebeeef] bg-[#f7fafb] p-4 text-[14px] leading-6 text-[#40484a]">
                <p className="font-semibold text-[#00343a]">Vous etes le proprietaire</p>
                <p className="mt-2">
                    Les reservations apparaissent ci-dessous avec le numero et le message du
                    locataire.
                </p>
                <a
                    className="mt-4 inline-flex items-center gap-2 text-[14px] font-semibold text-[#00343a] hover:text-[#004851]"
                    href="#housing-photo-manager"
                >
                    <MaterialSymbol icon="photo_library" />
                    Gerer les photos
                </a>
            </div>
        );
    }

    async function handleReserve() {
        setError("");
        setSuccessMessage("");

        const nextFieldErrors = validateReservationForm(phoneNumber, message);
        setFieldErrors(nextFieldErrors);

        if (Object.keys(nextFieldErrors).length > 0) {
            return;
        }

        try {
            setLoading(true);
            await createHousingReservation(housing.id, {
                phoneNumber: phoneNumber.trim(),
                message: message.trim(),
            });
            setSuccessMessage("Demande de reservation envoyee avec succes.");
            setPhoneNumber("");
            setMessage("");
            await onReservationChange();
        } catch (requestError) {
            setError(getHousingErrorMessage(requestError, "Impossible de reserver ce logement."));
        } finally {
            setLoading(false);
        }
    }

    async function handleCancelReservation() {
        if (!reservation?.id) {
            return;
        }

        setError("");
        setSuccessMessage("");

        try {
            setLoading(true);
            await cancelHousingReservation(reservation.id);
            setSuccessMessage("Reservation annulee.");
            await onReservationChange();
        } catch (requestError) {
            setError(getHousingErrorMessage(requestError, "Impossible d'annuler cette reservation."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="mt-4 space-y-3">
            {error ? <Alert type="error">{error}</Alert> : null}
            {successMessage ? <Alert type="success">{successMessage}</Alert> : null}

            {hasActiveReservation ? (
                <>
                    <div className="rounded-2xl border border-[#b5ecf5] bg-[#f8fbfb] p-4 text-[14px] leading-6 text-[#40484a]">
                        <p className="font-semibold text-[#00343a]">Vous avez envoye une demande</p>
                        <p className="mt-2">
                            Le proprietaire a recu votre numero et votre message. L'annonce reste
                            visible tant qu'il ne la marque pas indisponible.
                        </p>
                        <div className="mt-4 space-y-2 rounded-xl bg-white/70 px-4 py-3">
                            <p>
                                <span className="font-semibold text-[#00343a]">Telephone : </span>
                                {reservation.phoneNumber}
                            </p>
                            <p>
                                <span className="font-semibold text-[#00343a]">Message : </span>
                                {reservation.message}
                            </p>
                        </div>
                    </div>
                    <button
                        className="inline-flex w-full items-center justify-center gap-2 rounded-xl border border-[#dce7e8] bg-white px-4 py-3 text-[14px] font-semibold text-[#00343a] transition-colors hover:bg-[#f7fafb] disabled:cursor-not-allowed disabled:opacity-60"
                        type="button"
                        disabled={loading}
                        onClick={handleCancelReservation}
                    >
                        <MaterialSymbol icon="event_busy" />
                        {loading ? "Annulation..." : "Annuler ma reservation"}
                    </button>
                </>
            ) : (
                <>
                    <Input
                        label="Numero de telephone"
                        name="phoneNumber"
                        type="tel"
                        placeholder="06 12 34 56 78"
                        value={phoneNumber}
                        onChange={(event) => {
                            setPhoneNumber(event.target.value);
                            setFieldErrors((previousErrors) => {
                                if (!previousErrors.phoneNumber) {
                                    return previousErrors;
                                }

                                const nextErrors = { ...previousErrors };
                                delete nextErrors.phoneNumber;
                                return nextErrors;
                            });
                        }}
                        required
                        disabled={loading || !housing.available}
                        error={fieldErrors.phoneNumber}
                    />

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-700">
                            Message au proprietaire
                        </span>
                        <textarea
                            className={`min-h-24 w-full rounded-xl border px-4 py-3 text-[14px] leading-6 text-[#181c1d] outline-none transition focus:ring-2 ${
                                fieldErrors.message
                                    ? "border-rose-300 focus:border-rose-500 focus:ring-rose-100"
                                    : "border-slate-300 focus:border-green-600 focus:ring-green-100"
                            }`}
                            placeholder="Presentez-vous, indiquez vos dates souhaitees..."
                            value={message}
                            disabled={loading || !housing.available}
                            onChange={(event) => {
                                setMessage(event.target.value);
                                setFieldErrors((previousErrors) => {
                                    if (!previousErrors.message) {
                                        return previousErrors;
                                    }

                                    const nextErrors = { ...previousErrors };
                                    delete nextErrors.message;
                                    return nextErrors;
                                });
                            }}
                        />
                        {fieldErrors.message ? (
                            <p className="mt-1 text-sm text-rose-600">{fieldErrors.message}</p>
                        ) : null}
                    </label>

                    <button
                        className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-[#00343a] px-4 py-3 text-[14px] font-semibold text-white transition-colors hover:bg-[#004851] disabled:cursor-not-allowed disabled:bg-[#bfc8ca]"
                        type="button"
                        disabled={loading || !housing.available}
                        onClick={handleReserve}
                    >
                        <MaterialSymbol icon="event_available" className="text-white" />
                        {loading
                            ? "Reservation..."
                            : housing.available
                              ? "Envoyer une demande de reservation"
                              : "Logement indisponible"}
                    </button>

                    {!housing.available ? (
                        <p className="text-[13px] leading-5 text-[#70797a]">
                            Le proprietaire a marque ce logement comme indisponible.
                        </p>
                    ) : null}
                </>
            )}
        </div>
    );
}

export default HousingDetailActions;

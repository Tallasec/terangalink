import { useEffect, useState } from "react";

import MaterialSymbol from "../../common/MaterialSymbol";
import Alert from "../../common/ui/Alert";
import { getHousingReservations, getHousingErrorMessage } from "../../../services/housing/housingService";

function HousingDetailReservations({ housingId }) {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let isActive = true;

        async function loadReservations() {
            try {
                setLoading(true);
                setError("");

                const data = await getHousingReservations(housingId);

                if (isActive) {
                    setReservations(data || []);
                }
            } catch (requestError) {
                if (isActive) {
                    setError(
                        getHousingErrorMessage(
                            requestError,
                            "Impossible de charger les reservations.",
                        ),
                    );
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        if (housingId) {
            loadReservations();
        }

        return () => {
            isActive = false;
        };
    }, [housingId]);

    const activeReservations = reservations.filter(
        (reservation) => reservation.status === "CONFIRMED",
    );

    if (loading) {
        return (
            <section className="rounded-[28px] border border-[#bfc8ca]/40 bg-white p-6 shadow-[0px_8px_30px_rgba(0,0,0,0.06)]">
                <p className="text-[14px] leading-6 text-[#70797a]">Chargement des reservations...</p>
            </section>
        );
    }

    return (
        <section className="rounded-[28px] border border-[#bfc8ca]/40 bg-white p-6 shadow-[0px_8px_30px_rgba(0,0,0,0.06)] md:p-8">
            <div className="border-b border-[#ebeeef] pb-5">
                <p className="text-[12px] font-semibold uppercase tracking-[0.08em] text-[#70797a]">
                    Reservations recues
                </p>
                <h2 className="mt-2 text-[22px] font-semibold leading-7 text-[#00343a]">
                    Demandes des locataires
                </h2>
            </div>

            {error ? (
                <Alert type="error" className="mt-5">
                    {error}
                </Alert>
            ) : null}

            {activeReservations.length === 0 ? (
                <p className="mt-5 text-[14px] leading-6 text-[#70797a]">
                    Aucune reservation confirmee pour le moment.
                </p>
            ) : (
                <div className="mt-5 space-y-4">
                    {activeReservations.map((reservation) => (
                        <article
                            key={reservation.id}
                            className="rounded-2xl border border-[#ebeeef] bg-[#f7fafb] p-4"
                        >
                            <div className="flex items-start justify-between gap-4">
                                <div>
                                    <p className="text-[15px] font-semibold text-[#00343a]">
                                        {[reservation.tenantFirstName, reservation.tenantLastName]
                                            .filter(Boolean)
                                            .join(" ") || "Locataire"}
                                    </p>
                                    <p className="mt-1 text-[13px] text-[#70797a]">
                                        Reservation confirmee
                                    </p>
                                </div>
                                <span className="inline-flex items-center gap-2 rounded-full bg-[#b5ecf5] px-3 py-1 text-[12px] font-semibold text-[#001f24]">
                                    <MaterialSymbol icon="call" className="text-[16px]" />
                                    {reservation.phoneNumber}
                                </span>
                            </div>
                            <p className="mt-4 text-[14px] leading-6 text-[#40484a]">
                                {reservation.message}
                            </p>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}

export default HousingDetailReservations;

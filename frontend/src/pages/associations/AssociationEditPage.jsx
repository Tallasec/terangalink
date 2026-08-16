import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import AssociationForm from "../../components/associations/AssociationForm";
import {
    buildCreateAssociationPayload,
    getAssociationById,
    getAssociationErrorMessage,
    updateAssociation,
} from "../../services/associations/associationService";
import { getCurrentUser } from "../../services/user/userService";
import { createAssociationFormValues } from "./associationFormState";

function AssociationEditPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [values, setValues] = useState(createAssociationFormValues());
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [association, setAssociation] = useState(null);

    useEffect(() => {
        let isActive = true;

        async function loadUser() {
            try {
                const currentUser = await getCurrentUser();
                if (isActive) {
                    setUser(currentUser);
                }
            } catch {
                if (isActive) {
                    setUser(null);
                }
            }
        }

        loadUser();

        return () => {
            isActive = false;
        };
    }, []);

    useEffect(() => {
        let isActive = true;

        async function loadAssociation() {
            try {
                setLoading(true);
                setError("");

                const data = await getAssociationById(id);

                if (isActive) {
                    setAssociation(data);
                    setValues(createAssociationFormValues(data));
                }
            } catch (requestError) {
                if (requestError?.response?.status !== 401) {
                    setError(getAssociationErrorMessage(requestError));
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadAssociation();

        return () => {
            isActive = false;
        };
    }, [id]);

    function handleChange(event) {
        const { name, type, checked, value } = event.target;

        setValues((previousValues) => ({
            ...previousValues,
            [name]: type === "checkbox" ? checked : value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        try {
            setSaving(true);
            setError("");

            const payload = buildCreateAssociationPayload(values);
            const updatedAssociation = await updateAssociation(id, payload);
            setAssociation(updatedAssociation);
            navigate(`/associations/${updatedAssociation.id}`);
        } catch (requestError) {
            setError(getAssociationErrorMessage(requestError, "Impossible de mettre a jour cette organisation."));
        } finally {
            setSaving(false);
        }
    }

    const isOwner = Boolean(user && association && (user.role === "ADMIN" || user.id === association.creatorId));

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Associations" />

            <main className="mx-auto max-w-[980px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to={association ? `/associations/${association.id}` : "/associations"}
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour
                    </Link>
                </div>

                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                {loading ? (
                    <div className="h-[520px] animate-pulse rounded-[32px] border border-[#edf3f3] bg-white shadow-[0px_16px_50px_rgba(0,52,58,0.08)]" />
                ) : !association ? (
                    <div className="rounded-[28px] border border-dashed border-[#cdd9d9] bg-white px-6 py-16 text-center shadow-[0px_12px_40px_rgba(0,52,58,0.04)]">
                        <h1 className="text-[28px] font-semibold text-[#00343a]">Organisation introuvable</h1>
                        <p className="mt-3 text-[15px] leading-7 text-[#526062]">
                            Cette organisation n&apos;existe plus ou a ete supprimee.
                        </p>
                        <Link
                            className="mt-6 inline-flex items-center rounded-full bg-[#00343a] px-5 py-3 text-[14px] font-semibold text-white"
                            to="/associations"
                        >
                            Explorer les associations
                        </Link>
                    </div>
                ) : !isOwner ? (
                    <Alert type="warning">
                        Vous n&apos;avez pas les droits pour modifier cette organisation.
                    </Alert>
                ) : (
                    <AssociationForm
                        description="Mettez a jour les informations publiques de l'organisation."
                        errors={{ form: "" }}
                        loading={saving}
                        submitLabel="Enregistrer"
                        title="Modifier l'organisation"
                        values={values}
                        onChange={handleChange}
                        onSubmit={handleSubmit}
                    />
                )}
            </main>

            <DashboardFooter />
        </div>
    );
}

export default AssociationEditPage;


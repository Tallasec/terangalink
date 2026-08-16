import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import MaterialSymbol from "../../components/common/MaterialSymbol";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import AssociationForm from "../../components/associations/AssociationForm";
import {
    buildCreateAssociationPayload,
    createAssociation,
    getAssociationErrorMessage,
} from "../../services/associations/associationService";
import { getCurrentUser } from "../../services/user/userService";
import { DEFAULT_ASSOCIATION_FORM_VALUES } from "./associationFormState";

function AssociationCreatePage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [values, setValues] = useState(DEFAULT_ASSOCIATION_FORM_VALUES);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

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
            setLoading(true);
            setError("");

            const payload = buildCreateAssociationPayload(values);
            const createdAssociation = await createAssociation(payload);

            navigate(`/associations/${createdAssociation.id}`);
        } catch (requestError) {
            setError(getAssociationErrorMessage(requestError, "Impossible de publier cette organisation."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} activeNav="Associations" />

            <main className="mx-auto max-w-[980px] px-4 py-8 md:px-12 md:py-12">
                <div className="mb-6 flex flex-wrap items-center gap-3">
                    <Link
                        className="inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a]"
                        to="/associations"
                    >
                        <MaterialSymbol icon="arrow_back" />
                        Retour aux associations
                    </Link>
                </div>

                {error ? (
                    <Alert type="error" className="mb-6">
                        {error}
                    </Alert>
                ) : null}

                <AssociationForm
                    description="Partagez les informations essentielles pour aider les membres a vous trouver."
                    errors={{ form: "" }}
                    loading={loading}
                    submitLabel="Publier"
                    title="Nouvelle organisation"
                    values={values}
                    onChange={handleChange}
                    onSubmit={handleSubmit}
                />
            </main>

            <DashboardFooter />
        </div>
    );
}

export default AssociationCreatePage;

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Alert from "../../components/common/ui/Alert";
import DashboardHeader from "../../components/dashboard/DashboardHeader";
import DashboardFooter from "../../components/dashboard/DashboardFooter";
import ProfileAvatar from "../../components/profile/avatar/ProfileAvatar";
import ProfileForm from "../../components/profile/form/ProfileForm";
import ProfileHeader from "../../components/profile/header/ProfileHeader";
import ProfileInformation from "../../components/profile/information/ProfileInformation";
import ProfileSkeleton from "../../components/profile/ProfileSkeleton";
import { PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";
import {
    buildProfileUpdatePayload,
    getCurrentProfile,
    getProfileErrorMessage,
    uploadProfilePhoto,
    updateProfile,
} from "../../services/profile/profileService";

function createFormState(user) {
    return {
        firstName: user?.firstName || "",
        lastName: user?.lastName || "",
        email: user?.email || "",
        university: user?.university || "",
        fieldOfStudy: user?.fieldOfStudy || "",
        city: user?.city || "",
    };
}

function validateProfileForm(values) {
    const errors = {};

    if (!values.firstName.trim()) {
        errors.firstName = "Le prénom est obligatoire.";
    }

    if (!values.lastName.trim()) {
        errors.lastName = "Le nom est obligatoire.";
    }

    if (!values.email.trim()) {
        errors.email = "L’adresse e-mail est obligatoire.";
    } else if (!/^\S+@\S+\.\S+$/.test(values.email.trim())) {
        errors.email = "L’adresse e-mail est invalide.";
    }

    if (!values.university.trim()) {
        errors.university = "L’université est obligatoire.";
    }

    if (!values.fieldOfStudy.trim()) {
        errors.fieldOfStudy = "Le domaine d’étude est obligatoire.";
    }

    if (!values.city.trim()) {
        errors.city = "La ville est obligatoire.";
    }

    return errors;
}

function ProfilePage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [formValues, setFormValues] = useState(createFormState(null));
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [uploadingPhoto, setUploadingPhoto] = useState(false);
    const [photoPreviewUrl, setPhotoPreviewUrl] = useState("");

    useEffect(() => {
        let isActive = true;

        async function loadProfile() {
            try {
                setLoading(true);
                setError("");

                const currentProfile = await getCurrentProfile();

                if (isActive) {
                    setUser(currentProfile);
                    setFormValues(createFormState(currentProfile));
                }
            } catch (requestError) {
                if (isActive && requestError?.response?.status !== 401) {
                    setError(
                        requestError?.response?.data?.message ||
                            "Impossible de charger votre profil.",
                    );
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        loadProfile();

        return () => {
            isActive = false;
        };
    }, []);

    function handleStartEdit() {
        setIsEditing(true);
        setFieldErrors({});
        setError("");
        setSuccessMessage("");
    }

    function handleCancelEdit() {
        setFormValues(createFormState(user));
        setFieldErrors({});
        setError("");
        setSuccessMessage("");
        setIsEditing(false);
    }

    function clearPhotoPreview() {
        setPhotoPreviewUrl((currentPreviewUrl) => {
            if (currentPreviewUrl) {
                URL.revokeObjectURL(currentPreviewUrl);
            }
            return "";
        });
    }

    async function handlePhotoChange(event) {
        const file = event.target.files?.[0];

        if (!file || !user?.id) {
            return;
        }

        setError("");
        setSuccessMessage("");

        if (!file.type.startsWith("image/")) {
            setError("Le fichier sélectionné doit être une image.");
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            setError("La taille de l'image ne doit pas dépasser 5 MB.");
            return;
        }

        clearPhotoPreview();
        const previewUrl = URL.createObjectURL(file);
        setPhotoPreviewUrl(previewUrl);

        try {
            setUploadingPhoto(true);

            const updatedProfile = await uploadProfilePhoto(user.id, file);
            setUser(updatedProfile);
            setFormValues(createFormState(updatedProfile));
            setSuccessMessage("Votre photo de profil a été mise à jour avec succès.");
        } catch (requestError) {
            setError(getProfileErrorMessage(requestError, "Impossible de mettre à jour la photo."));
        } finally {
            setUploadingPhoto(false);
            clearPhotoPreview();
            event.target.value = "";
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;

        setFormValues((previousValues) => ({
            ...previousValues,
            [name]: value,
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

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSuccessMessage("");

        const nextFieldErrors = validateProfileForm(formValues);
        setFieldErrors(nextFieldErrors);

        if (Object.keys(nextFieldErrors).length > 0) {
            return;
        }

        try {
            setSubmitting(true);

            const payload = buildProfileUpdatePayload(formValues);
            const requestedEmail = payload.email.trim().toLowerCase();
            const currentEmail = (user?.email || "").trim().toLowerCase();
            const emailChanged = requestedEmail !== currentEmail;
            const updatedProfile = await updateProfile(user.id, payload);

            setUser(updatedProfile);
            setFormValues(createFormState(updatedProfile));

            if (emailChanged) {
                const pendingEmail = payload.email.trim();
                sessionStorage.setItem(PENDING_VERIFICATION_EMAIL_KEY, pendingEmail);

                navigate("/profile/verify-email", {
                    replace: true,
                    state: {
                        email: pendingEmail,
                        returnTo: "/profile",
                        message:
                            "Un code de verification a ete envoye a votre nouvelle adresse e-mail.",
                    },
                });
                return;
            }

            setIsEditing(false);
            setSuccessMessage("Votre profil a ete mis a jour avec succes.");
        } catch (requestError) {
            setError(getProfileErrorMessage(requestError));
        } finally {
            setSubmitting(false);
        }
    }

    if (loading) {
        return <ProfileSkeleton />;
    }

    return (
        <div className="min-h-screen bg-[#f7fafb] text-[#181c1d]">
            <DashboardHeader user={user} />

            <div className="mx-auto max-w-[1200px] px-4 py-8 md:px-12">
                <ProfileHeader user={user} isEditing={isEditing} onEdit={handleStartEdit} />

                <div className="mt-8 grid gap-6 lg:grid-cols-[0.9fr_1.1fr]">
                    <div className="space-y-6">
                        <ProfileAvatar
                            user={user}
                            photoPreviewUrl={photoPreviewUrl}
                            uploadingPhoto={uploadingPhoto}
                            onPhotoChange={handlePhotoChange}
                        />
                    </div>

                    <div className="space-y-6">
                        {error ? <Alert type="error">{error}</Alert> : null}

                        {successMessage ? <Alert type="success">{successMessage}</Alert> : null}

                        {isEditing ? (
                            <ProfileForm
                                values={formValues}
                                errors={fieldErrors}
                                loading={submitting}
                                onChange={handleChange}
                                onSubmit={handleSubmit}
                                onCancel={handleCancelEdit}
                            />
                        ) : (
                            <ProfileInformation user={user} />
                        )}
                    </div>
                </div>
            </div>

            <DashboardFooter />
        </div>
    );
}

export default ProfilePage;

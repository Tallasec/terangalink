import api from "../api";

function getFirstDetail(details) {
    if (!details || typeof details !== "object") {
        return "";
    }

    for (const value of Object.values(details)) {
        if (typeof value === "string" && value.trim() !== "") {
            return value;
        }
    }

    return "";
}

export async function getCurrentProfile() {
    const response = await api.get("/auth/me");
    return response.data;
}

export async function updateProfile(userId, payload) {
    const response = await api.patch(`/users/${userId}`, payload);
    return response.data;
}

export async function uploadProfilePhoto(userId, file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post(`/users/${userId}/profile-photo`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    });

    return response.data;
}

export function buildProfileUpdatePayload(values) {
    return {
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        email: values.email.trim(),
        university: values.university.trim(),
        fieldOfStudy: values.fieldOfStudy.trim(),
        city: values.city.trim(),
    };
}

export function getProfileErrorMessage(error, fallback = "Impossible de mettre à jour le profil.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || getFirstDetail(responseData.details) || fallback;
}

import api from "../api";

function normalizeTextValue(value) {
    return typeof value === "string" ? value.trim() : value;
}

function buildSearchParams(filters = {}) {
    const params = {
        page: filters.page ?? 0,
        size: filters.size ?? 12,
        sort: filters.sort || "createdAt,desc",
    };

    if (normalizeTextValue(filters.title)) {
        params.title = normalizeTextValue(filters.title);
    }

    if (normalizeTextValue(filters.subject)) {
        params.subject = normalizeTextValue(filters.subject);
    }

    if (normalizeTextValue(filters.city)) {
        params.city = normalizeTextValue(filters.city);
    }

    if (filters.meetingType) {
        params.meetingType = filters.meetingType;
    }

    if (filters.available !== undefined && filters.available !== null && filters.available !== "") {
        params.available = filters.available;
    }

    if (filters.meetingDate) {
        params.meetingDate = filters.meetingDate;
    }

    return params;
}

function shouldUseSearchEndpoint(filters = {}) {
    return Boolean(
        normalizeTextValue(filters.title) ||
            normalizeTextValue(filters.subject) ||
            normalizeTextValue(filters.city) ||
            filters.meetingType ||
            filters.available !== undefined && filters.available !== null && filters.available !== "" ||
            filters.meetingDate,
    );
}

export async function fetchStudyGroups(filters = {}) {
    const params = buildSearchParams(filters);
    const endpoint = shouldUseSearchEndpoint(filters) ? "/study-groups/search" : "/study-groups";
    const response = await api.get(endpoint, { params });
    return response.data;
}

export async function getStudyGroupById(groupId) {
    const response = await api.get(`/study-groups/${groupId}`);
    return response.data;
}

export async function createStudyGroup(payload) {
    const response = await api.post("/study-groups", payload);
    return response.data;
}

export async function updateStudyGroup(groupId, payload) {
    const response = await api.patch(`/study-groups/${groupId}`, payload);
    return response.data;
}

export async function deleteStudyGroup(groupId) {
    await api.delete(`/study-groups/${groupId}`);
}

export async function joinStudyGroup(groupId) {
    const response = await api.post(`/study-groups/${groupId}/join`);
    return response.data;
}

export async function leaveStudyGroup(groupId) {
    const response = await api.delete(`/study-groups/${groupId}/leave`);
    return response.data;
}

export async function getStudyGroupMembers(groupId) {
    const response = await api.get(`/study-groups/${groupId}/members`);
    return response.data;
}

export function getStudyGroupErrorMessage(error, fallback = "Impossible de charger les groupes.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || fallback;
}

export function formatStudyGroupDate(dateValue) {
    if (!dateValue) {
        return "Date inconnue";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return String(dateValue);
    }

    return new Intl.DateTimeFormat("fr-FR", {
        day: "numeric",
        month: "long",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
}

export function formatRelativeStudyGroupDate(dateValue) {
    if (!dateValue) {
        return "Date inconnue";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "Date inconnue";
    }

    const diffMs = Date.now() - date.getTime();
    const diffDays = Math.max(0, Math.floor(diffMs / (1000 * 60 * 60 * 24)));

    if (diffDays === 0) {
        return "Publié aujourd'hui";
    }

    if (diffDays === 1) {
        return "Publié hier";
    }

    if (diffDays < 7) {
        return `Publié il y a ${diffDays} jours`;
    }

    return `Publié le ${formatStudyGroupDate(dateValue)}`;
}

export function getMeetingTypeLabel(meetingType) {
    switch (meetingType) {
        case "ONLINE":
            return "En ligne";
        case "PHYSICAL":
            return "Présentiel";
        case "HYBRID":
            return "Hybride";
        case "TEST":
            return "Test";
        default:
            return meetingType || "Non précisé";
    }
}

export function getStudyGroupBadgeTone(group) {
    if (!group?.available) {
        return "neutral";
    }

    if (group.full) {
        return "warm";
    }

    switch (group.meetingType) {
        case "ONLINE":
            return "secondary";
        case "PHYSICAL":
            return "accent";
        default:
            return "primary";
    }
}

export function getStudyGroupBadgeLabel(group) {
    if (!group?.available) {
        return "Fermé";
    }

    if (group.full) {
        return "Complet";
    }

    return getMeetingTypeLabel(group.meetingType);
}

export function getStudyGroupLocationLabel(group) {
    if (!group) {
        return "";
    }

    return [group.city, group.location].filter(Boolean).join(" • ") || "Localisation non renseignée";
}

export function buildCreateStudyGroupPayload(values) {
    return {
        title: values.title.trim(),
        subject: values.subject.trim(),
        description: values.description.trim(),
        city: values.city.trim(),
        meetingType: values.meetingType,
        meetingDate: normalizeMeetingDate(values.meetingDate),
        maxMembers: Number(values.maxMembers),
    };
}

export function normalizeMeetingDate(value) {
    if (!value) {
        return value;
    }

    if (value.length === 16) {
        return `${value}:00`;
    }

    return value;
}

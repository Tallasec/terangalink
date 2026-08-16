import api from "../api";

function normalizeTextValue(value) {
    return typeof value === "string" ? value.trim() : value;
}

function buildSearchParams(filters = {}) {
    const params = {
        page: filters.page ?? 0,
        size: filters.size ?? 8,
        sort: filters.sort || "createdAt,desc",
    };

    if (normalizeTextValue(filters.title)) {
        params.title = normalizeTextValue(filters.title);
    }

    if (normalizeTextValue(filters.city)) {
        params.city = normalizeTextValue(filters.city);
    }

    if (filters.associationType) {
        params.associationType = filters.associationType;
    }

    if (filters.available !== undefined && filters.available !== null && filters.available !== "") {
        params.available = filters.available;
    }

    return params;
}

function shouldUseSearchEndpoint(filters = {}) {
    return Boolean(
        normalizeTextValue(filters.title) ||
            normalizeTextValue(filters.city) ||
            filters.associationType ||
            filters.available !== undefined && filters.available !== null && filters.available !== "",
    );
}

export async function fetchAssociations(filters = {}) {
    const params = buildSearchParams(filters);
    const endpoint = shouldUseSearchEndpoint(filters) ? "/associations/search" : "/associations";
    const response = await api.get(endpoint, { params });
    return response.data;
}

export async function getAssociationById(associationId) {
    const response = await api.get(`/associations/${associationId}`);
    return response.data;
}

export async function createAssociation(payload) {
    const response = await api.post("/associations", payload);
    return response.data;
}

export async function updateAssociation(associationId, payload) {
    const response = await api.patch(`/associations/${associationId}`, payload);
    return response.data;
}

export async function deleteAssociation(associationId) {
    await api.delete(`/associations/${associationId}`);
}

export function getAssociationErrorMessage(error, fallback = "Impossible de charger les associations.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || fallback;
}

export function formatAssociationDate(dateValue) {
    if (!dateValue) {
        return "";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return new Intl.DateTimeFormat("fr-FR", {
        day: "numeric",
        month: "long",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
}

export function formatRelativeAssociationDate(dateValue) {
    if (!dateValue) {
        return "Date inconnue";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "Date inconnue";
    }

    const diffMs = Date.now() - date.getTime();

    if (diffMs < 0) {
        return `le ${formatAssociationDate(dateValue)}`;
    }

    const diffMinutes = Math.floor(diffMs / (1000 * 60));

    if (diffMinutes < 1) {
        return "à l'instant";
    }

    if (diffMinutes < 60) {
        return `il y a ${diffMinutes}min`;
    }

    const diffHours = Math.floor(diffMinutes / 60);

    if (diffHours < 24) {
        return `il y a ${diffHours}h`;
    }

    const diffDays = Math.floor(diffHours / 24);

    if (diffDays < 7) {
        return `il y a ${diffDays} jour${diffDays > 1 ? "s" : ""}`;
    }

    return `le ${formatAssociationDate(dateValue)}`;
}

export function getAssociationTypeLabel(type) {
    switch (type) {
        case "DAHIRA":
            return "Dahiras";
        case "ETUDIANTE":
            return "Associations étudiantes";
        case "CULTURELLE":
            return "Associations culturelles";
        case "SPORTIVE":
            return "Associations sportives";
        case "HUMANITAIRE":
            return "Humanitaire";
        case "AUTRE":
            return "Autre";
        default:
            return type || "Non précisé";
    }
}

export function getAssociationTypeShortLabel(type) {
    switch (type) {
        case "DAHIRA":
            return "Dahira";
        case "ETUDIANTE":
            return "Étudiante";
        case "CULTURELLE":
            return "Culturelle";
        case "SPORTIVE":
            return "Sportive";
        case "HUMANITAIRE":
            return "Humanitaire";
        case "AUTRE":
            return "Autre";
        default:
            return type || "Non précisé";
    }
}

export function getAssociationTypeDescription(type) {
    switch (type) {
        case "DAHIRA":
            return "Entraide religieuse et collective";
        case "ETUDIANTE":
            return "Accompagnement académique et social";
        case "CULTURELLE":
            return "Culture, évènements et rencontres";
        case "SPORTIVE":
            return "Sport, santé et esprit d'équipe";
        case "HUMANITAIRE":
            return "Actions solidaires et bénévolat";
        case "AUTRE":
            return "Organisations à découvrir";
        default:
            return "";
    }
}

export function getAssociationTypeTone(type) {
    switch (type) {
        case "DAHIRA":
            return "gold";
        case "ETUDIANTE":
            return "teal";
        case "CULTURELLE":
            return "slate";
        case "SPORTIVE":
            return "blue";
        case "HUMANITAIRE":
            return "amber";
        case "AUTRE":
            return "neutral";
        default:
            return "neutral";
    }
}

export function getAssociationTypeClassName(type) {
    switch (type) {
        case "DAHIRA":
            return "bg-[#f7d68e] text-[#785c29]";
        case "ETUDIANTE":
            return "bg-[#dceff2] text-[#00343a]";
        case "CULTURELLE":
            return "bg-[#e7ecf0] text-[#40505a]";
        case "SPORTIVE":
            return "bg-[#d6e4f5] text-[#184f8a]";
        case "HUMANITAIRE":
            return "bg-[#fde8c8] text-[#875100]";
        case "AUTRE":
            return "bg-[#edf3f3] text-[#526062]";
        default:
            return "bg-[#edf3f3] text-[#526062]";
    }
}

export function getAssociationTypeIcon(type) {
    switch (type) {
        case "DAHIRA":
            return "mosque";
        case "ETUDIANTE":
            return "school";
        case "CULTURELLE":
            return "festival";
        case "SPORTIVE":
            return "sports_soccer";
        case "HUMANITAIRE":
            return "volunteer_activism";
        case "AUTRE":
            return "groups";
        default:
            return "groups";
    }
}

export function getAssociationLocationLabel(association) {
    if (!association) {
        return "";
    }

    return [association.city, association.address].filter(Boolean).join(" • ") || "Localisation non renseignée";
}

export function getAssociationAuthorLabel(association) {
    return [association?.creatorFirstName, association?.creatorLastName].filter(Boolean).join(" ").trim() || "Organisateur";
}

export function getAssociationStatusLabel(association) {
    if (!association) {
        return "";
    }

    return association.available ? "Ouverte" : "Fermée";
}

export function buildCreateAssociationPayload(values) {
    return {
        title: values.title.trim(),
        description: values.description.trim(),
        city: values.city.trim(),
        address: values.address.trim() || undefined,
        contactEmail: values.contactEmail.trim() || undefined,
        phone: values.phone.trim() || undefined,
        website: values.website.trim() || undefined,
        logoUrl: values.logoUrl.trim() || undefined,
        associationType: values.associationType,
        available: values.available,
    };
}

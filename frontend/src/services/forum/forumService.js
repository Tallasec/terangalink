import api from "../api";

function normalizeTextValue(value) {
    return typeof value === "string" ? value.trim() : value;
}

function buildForumSearchParams(filters = {}) {
    const params = {
        page: filters.page ?? 0,
        size: filters.size ?? 12,
        sort: filters.sort || "createdAt,desc",
    };

    if (normalizeTextValue(filters.title)) {
        params.title = normalizeTextValue(filters.title);
    }

    if (filters.category) {
        params.category = filters.category;
    }

    return params;
}

function shouldUseSearchEndpoint(filters = {}) {
    return Boolean(normalizeTextValue(filters.title) || filters.category);
}

export async function fetchForumTopics(filters = {}) {
    const params = buildForumSearchParams(filters);
    const endpoint = shouldUseSearchEndpoint(filters) ? "/forum/topics/search" : "/forum/topics";
    const response = await api.get(endpoint, { params });
    return response.data;
}

export async function getForumTopicById(topicId) {
    const response = await api.get(`/forum/topics/${topicId}`);
    return response.data;
}

export async function createForumTopic(payload) {
    const response = await api.post("/forum/topics", payload);
    return response.data;
}

export async function updateForumTopic(topicId, payload) {
    const response = await api.patch(`/forum/topics/${topicId}`, payload);
    return response.data;
}

export async function deleteForumTopic(topicId) {
    await api.delete(`/forum/topics/${topicId}`);
}

export async function fetchForumAnswers(topicId) {
    const response = await api.get(`/forum/topics/${topicId}/answers`);
    return response.data;
}

export async function createForumAnswer(topicId, payload) {
    const response = await api.post(`/forum/topics/${topicId}/answers`, payload);
    return response.data;
}

export async function updateForumAnswer(answerId, payload) {
    const response = await api.patch(`/forum/answers/${answerId}`, payload);
    return response.data;
}

export async function deleteForumAnswer(answerId) {
    await api.delete(`/forum/answers/${answerId}`);
}

export function getForumErrorMessage(error, fallback = "Impossible de charger le forum.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || fallback;
}

export function formatForumDate(dateValue) {
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

export function formatRelativeForumDate(dateValue) {
    if (!dateValue) {
        return "Date inconnue";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "Date inconnue";
    }

    const diffMs = Date.now() - date.getTime();

    if (diffMs < 0) {
        return `le ${formatForumDate(dateValue)}`;
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

    return `le ${formatForumDate(dateValue)}`;
}

export function getForumCategoryLabel(category) {
    switch (category) {
        case "LOGEMENT":
            return "Logement";
        case "ADMINISTRATIF":
            return "Démarches & Visas";
        case "ETUDES":
            return "Études & Concours";
        case "ALTERNANCE":
            return "Alternance";
        case "EMPLOI":
            return "Emploi";
        case "VIE_ETUDIANTE":
            return "Vie & Culture";
        case "EVENEMENT":
            return "Événements";
        case "AUTRE":
            return "Autre";
        default:
            return category || "Non précisé";
    }
}

export function getForumCategoryDescription(category) {
    switch (category) {
        case "LOGEMENT":
            return "Colocations, bailleurs, quartiers";
        case "ADMINISTRATIF":
            return "Campus France, titre de séjour, CAF";
        case "ETUDES":
            return "Licence, master, révisions";
        case "ALTERNANCE":
            return "Contrats, candidatures, entreprises";
        case "EMPLOI":
            return "Jobs étudiants et premiers contrats";
        case "VIE_ETUDIANTE":
            return "Événements, cuisine, entraide";
        case "EVENEMENT":
            return "Rencontres, sorties et bons plans";
        case "AUTRE":
            return "Questions générales";
        default:
            return "";
    }
}

export function getForumCategoryIcon(category) {
    switch (category) {
        case "LOGEMENT":
            return "home";
        case "ADMINISTRATIF":
            return "description";
        case "ETUDES":
            return "school";
        case "ALTERNANCE":
            return "work";
        case "EMPLOI":
            return "badge";
        case "VIE_ETUDIANTE":
            return "groups";
        case "EVENEMENT":
            return "event";
        case "AUTRE":
            return "forum";
        default:
            return "forum";
    }
}

export function getForumCategoryTone(category) {
    switch (category) {
        case "LOGEMENT":
            return "slate";
        case "ADMINISTRATIF":
            return "gold";
        case "ETUDES":
            return "mint";
        case "ALTERNANCE":
            return "blue";
        case "EMPLOI":
            return "amber";
        case "VIE_ETUDIANTE":
            return "sand";
        case "EVENEMENT":
            return "rose";
        case "AUTRE":
            return "neutral";
        default:
            return "neutral";
    }
}

export function getForumCategoryAccentClassName(category) {
    switch (category) {
        case "LOGEMENT":
            return "bg-[#dfecef] text-[#00343a]";
        case "ADMINISTRATIF":
            return "bg-[#ffe7b3] text-[#6f4f00]";
        case "ETUDES":
            return "bg-[#dff3f0] text-[#0a5d54]";
        case "ALTERNANCE":
            return "bg-[#dce9f7] text-[#184f8a]";
        case "EMPLOI":
            return "bg-[#fde8c8] text-[#875100]";
        case "VIE_ETUDIANTE":
            return "bg-[#ece7f7] text-[#53338c]";
        case "EVENEMENT":
            return "bg-[#f9dfe6] text-[#8f2f4f]";
        case "AUTRE":
            return "bg-[#edf3f3] text-[#526062]";
        default:
            return "bg-[#edf3f3] text-[#526062]";
    }
}

export function buildCreateForumTopicPayload(values) {
    return {
        title: values.title.trim(),
        content: values.content.trim(),
        category: values.category,
    };
}

export function buildCreateForumAnswerPayload(values) {
    return {
        content: values.content.trim(),
    };
}

export function getForumTopicExcerpt(topic, maxLength = 180) {
    if (!topic?.content) {
        return "";
    }

    if (topic.content.length <= maxLength) {
        return topic.content;
    }

    return `${topic.content.slice(0, maxLength).trimEnd()}…`;
}

export function getForumAuthorLabel(item) {
    return [item?.authorFirstName, item?.authorLastName].filter(Boolean).join(" ").trim() || "Utilisateur";
}

export function getForumAuthorInitials(item) {
    const firstName = item?.authorFirstName?.trim()?.[0] || "";
    const lastName = item?.authorLastName?.trim()?.[0] || "";
    const initials = `${firstName}${lastName}`.trim();

    return initials || "U";
}


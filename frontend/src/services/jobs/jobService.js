import api from "../api";

function buildSearchParams(filters = {}) {
    const params = {
        page: filters.page ?? 0,
        size: filters.size ?? 12,
        sort: filters.sort || "createdAt,desc",
    };

    if (filters.title?.trim()) {
        params.title = filters.title.trim();
    }

    if (filters.city?.trim()) {
        params.city = filters.city.trim();
    }

    if (filters.companyName?.trim()) {
        params.companyName = filters.companyName.trim();
    }

    if (filters.contractType) {
        params.contractType = filters.contractType;
    }

    if (filters.salaryMin !== "" && filters.salaryMin !== null && filters.salaryMin !== undefined) {
        params.salaryMin = filters.salaryMin;
    }

    if (filters.salaryMax !== "" && filters.salaryMax !== null && filters.salaryMax !== undefined) {
        params.salaryMax = filters.salaryMax;
    }

    if (filters.available !== undefined && filters.available !== null) {
        params.available = filters.available;
    }

    return params;
}

function shouldUseSearchEndpoint(filters = {}) {
    return Boolean(
        filters.title?.trim() ||
            filters.city?.trim() ||
            filters.companyName?.trim() ||
            filters.contractType ||
            filters.salaryMin !== "" ||
            filters.salaryMax !== "" ||
            filters.available !== undefined,
    );
}

export async function fetchJobs(filters = {}) {
    const params = buildSearchParams(filters);
    const endpoint = shouldUseSearchEndpoint(filters) ? "/jobs/search" : "/jobs";
    const response = await api.get(endpoint, { params });
    return response.data;
}

export async function getJobById(jobId) {
    const response = await api.get(`/jobs/${jobId}`);
    return response.data;
}

export async function createJob(payload) {
    const response = await api.post("/jobs", payload);
    return response.data;
}

export async function updateJob(jobId, payload) {
    const response = await api.patch(`/jobs/${jobId}`, payload);
    return response.data;
}

export async function deleteJob(jobId) {
    await api.delete(`/jobs/${jobId}`);
}

export async function applyToJob(jobId, payload) {
    const formData = new FormData();
    formData.append("phoneNumber", payload.phoneNumber);

    if (payload.message?.trim()) {
        formData.append("message", payload.message.trim());
    }

    formData.append("cv", payload.cv);

    const response = await api.post(`/jobs/${jobId}/applications`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    });
    return response.data;
}

export async function getMyJobApplication(jobId) {
    const response = await api.get(`/jobs/${jobId}/applications/me`);
    return response.status === 204 ? null : response.data;
}

export async function getMyJobApplications() {
    const response = await api.get("/job-applications/me");
    return response.data;
}

export async function getJobApplications(jobId) {
    const response = await api.get(`/jobs/${jobId}/applications`);
    return response.data;
}

export function getJobErrorMessage(error, fallback = "Impossible de charger les offres d'emploi.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || fallback;
}

export function formatJobSalary(salary) {
    if (salary === null || salary === undefined || salary === "") {
        return "Salaire non indiqué";
    }

    const normalized = Number(salary);

    if (Number.isNaN(normalized)) {
        return String(salary);
    }

    return new Intl.NumberFormat("fr-FR", {
        style: "currency",
        currency: "EUR",
        maximumFractionDigits: 0,
    }).format(normalized);
}

export function formatJobDate(dateValue) {
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
    }).format(date);
}

export function formatRelativeJobDate(dateValue) {
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

    if (diffDays < 30) {
        return `Publié il y a ${Math.floor(diffDays / 7)} sem`;
    }

    return `Publié le ${formatJobDate(dateValue)}`;
}

export function getJobLocationLabel(job) {
    if (!job) {
        return "";
    }

    return [job.city, job.address].filter(Boolean).join(" • ") || "Localisation non renseignée";
}

export function getJobBadgeTone(job) {
    if (!job?.available) {
        return "neutral";
    }

    switch (job.contractType) {
        case "STAGE":
            return "accent";
        case "ALTERNANCE":
            return "secondary";
        case "CDD":
            return "warm";
        default:
            return "primary";
    }
}

export function getJobBadgeLabel(job) {
    if (!job?.available) {
        return "Indisponible";
    }

    return job.contractType || "Offre";
}

export function getJobApplicationStatusLabel(status) {
    switch (status) {
        case "APPLIED":
            return "Candidature envoyée";
        case "UNDER_REVIEW":
            return "En cours d'examen";
        case "REJECTED":
            return "Refusée";
        case "ACCEPTED":
            return "Acceptée";
        case "WITHDRAWN":
            return "Retirée";
        default:
            return "Statut inconnu";
    }
}

export function getJobApplicationStatusTone(status) {
    switch (status) {
        case "APPLIED":
            return "primary";
        case "UNDER_REVIEW":
            return "accent";
        case "ACCEPTED":
            return "secondary";
        case "REJECTED":
        case "WITHDRAWN":
            return "neutral";
        default:
            return "neutral";
    }
}

export function buildCreateJobPayload(values) {
    return {
        title: values.title.trim(),
        description: values.description.trim() || undefined,
        companyName: values.companyName.trim(),
        city: values.city.trim(),
        address: values.address?.trim() || undefined,
        contractType: values.contractType,
        salary: Number(values.salary),
        available: values.available,
    };
}

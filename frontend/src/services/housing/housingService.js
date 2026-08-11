import api from "../api";
import { parseAvailabilityFilter } from "./housingHelpers";

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

function buildSearchParams(filters = {}) {
    const params = {
        page: filters.page ?? 0,
        size: filters.size ?? 12,
        sort: filters.sort || "createdAt,desc",
    };

    if (filters.city?.trim()) {
        params.city = filters.city.trim();
    }

    if (filters.housingType) {
        params.housingType = filters.housingType;
    }

    const available = parseAvailabilityFilter(filters.available);
    if (available !== undefined) {
        params.available = available;
    }

    if (filters.minPrice !== "" && filters.minPrice !== null && filters.minPrice !== undefined) {
        params.minPrice = filters.minPrice;
    }

    if (filters.maxPrice !== "" && filters.maxPrice !== null && filters.maxPrice !== undefined) {
        params.maxPrice = filters.maxPrice;
    }

    return params;
}

function shouldUseSearchEndpoint(filters = {}) {
    return Boolean(
        filters.city?.trim() ||
            filters.housingType ||
            filters.available ||
            filters.minPrice !== "" ||
            filters.maxPrice !== "",
    );
}

export async function fetchHousings(filters = {}) {
    const params = buildSearchParams(filters);
    const endpoint = shouldUseSearchEndpoint(filters) ? "/housings/search" : "/housings";
    const response = await api.get(endpoint, { params });
    return response.data;
}

export async function getHousingById(housingId) {
    const response = await api.get(`/housings/${housingId}`);
    return response.data;
}

export async function createHousing(payload) {
    const response = await api.post("/housings", payload);
    return response.data;
}

export async function updateHousing(housingId, payload) {
    const response = await api.patch(`/housings/${housingId}`, payload);
    return response.data;
}

export async function uploadHousingImages(housingId, files) {
    const formData = new FormData();

    files.forEach((file) => {
        formData.append("files", file);
    });

    const response = await api.post(`/housings/${housingId}/images`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    });

    return response.data;
}

export async function deleteHousingImage(imageId) {
    await api.delete(`/housing-images/${imageId}`);
}

export async function createHousingReservation(housingId, payload = {}) {
    const response = await api.post(`/housings/${housingId}/reservations`, payload);
    return response.data;
}

export async function getMyHousingReservation(housingId) {
    const response = await api.get(`/housings/${housingId}/reservations/me`, {
        validateStatus: (status) => status === 200 || status === 204,
    });

    return response.status === 200 ? response.data : null;
}

export async function getHousingReservations(housingId) {
    const response = await api.get(`/housings/${housingId}/reservations`);
    return response.data;
}

export async function cancelHousingReservation(reservationId) {
    const response = await api.post(`/housing-reservations/${reservationId}/cancel`);
    return response.data;
}

export function buildCreateHousingPayload(values) {
    return {
        title: values.title.trim(),
        description: values.description.trim() || undefined,
        city: values.city.trim(),
        address: values.address.trim() || undefined,
        price: Number(values.price),
        housingType: values.housingType,
        available: values.available,
    };
}

export function getHousingErrorMessage(error, fallback = "Impossible de charger les logements.") {
    const responseData = error?.response?.data;

    if (!responseData) {
        return fallback;
    }

    return responseData.message || getFirstDetail(responseData.details) || fallback;
}

export const HOUSING_TYPE_OPTIONS = [
    { value: "", label: "Tous les types" },
    { value: "STUDIO", label: "Studio" },
    { value: "ROOM", label: "Chambre" },
    { value: "SHARED_FLAT", label: "Colocation" },
    { value: "APARTMENT", label: "Appartement" },
    { value: "HOUSE", label: "Maison" },
];

export const HOUSING_SORT_OPTIONS = [
    { value: "createdAt,desc", label: "Plus recents" },
    { value: "price,asc", label: "Prix croissant" },
    { value: "price,desc", label: "Prix decroissant" },
    { value: "city,asc", label: "Ville (A-Z)" },
];

export const HOUSING_AVAILABILITY_OPTIONS = [
    { value: "", label: "Tous" },
    { value: "true", label: "Disponible" },
    { value: "false", label: "Non disponible" },
];

export const DEFAULT_HOUSING_FILTERS = {
    city: "",
    housingType: "",
    available: "",
    minPrice: "",
    maxPrice: "",
    sort: "createdAt,desc",
    page: 0,
    size: 12,
};

export const HOUSING_PLACEHOLDER_IMAGE =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuBp-rCg4vJsl9S_xSYvwKWkA5SIfK8Wkh_7-2q2UMAZDMi3ZeDwOjKg1ebi_qNXg6G8kX47QXXCp4OPyABuYJYU5sRg_5I3iWQ5XGwmHA_uFUoxNlFH9cuUnH_PUCRruyKLjmh1HOcti4_k9WPaHp390DJ9lgGtsaKQUJcrmEpLO0AHYC7BQKpVj3BBEbwJ6kGqYKUqiJY_KMRSIEIvJJsAvIlDcMs7Mkg3RMcgYk45lPN7XcdXoI6iiBekBvS_6ms6ZByR_Os04Sk";

const HOUSING_TYPE_LABELS = {
    STUDIO: "Studio",
    ROOM: "Chambre",
    SHARED_FLAT: "Colocation",
    APARTMENT: "Appartement",
    HOUSE: "Maison",
};

const HOUSING_TYPE_BADGE_STYLES = {
    STUDIO: "bg-[#b5ecf5] text-[#001f24]",
    ROOM: "bg-[#fdd798] text-[#271900]",
    SHARED_FLAT: "bg-[#fdd798] text-[#271900]",
    APARTMENT: "bg-[#00343a] text-white",
    HOUSE: "bg-[#00343a] text-white",
};

export function getHousingTypeLabel(housingType) {
    return HOUSING_TYPE_LABELS[housingType] || housingType || "Logement";
}

export function getHousingTypeBadgeClassName(housingType) {
    return HOUSING_TYPE_BADGE_STYLES[housingType] || "bg-[#00343a] text-white";
}

export function formatHousingPrice(price) {
    if (price === null || price === undefined || price === "") {
        return "—";
    }

    const numericPrice = Number(price);

    if (Number.isNaN(numericPrice)) {
        return "—";
    }

    return new Intl.NumberFormat("fr-FR", {
        style: "currency",
        currency: "EUR",
        maximumFractionDigits: 0,
    }).format(numericPrice);
}

export function getOwnerFullName(housing) {
    const parts = [housing?.ownerFirstName, housing?.ownerLastName].filter(Boolean);
    return parts.join(" ") || "Proprietaire";
}

export function getPrimaryImageUrl(housing) {
    return housing?.images?.[0]?.imageUrl || HOUSING_PLACEHOLDER_IMAGE;
}

export function truncateDescription(text, maxLength = 120) {
    if (!text) {
        return "";
    }

    if (text.length <= maxLength) {
        return text;
    }

    return `${text.slice(0, maxLength).trim()}…`;
}

export function parseAvailabilityFilter(value) {
    if (value === "true") {
        return true;
    }

    if (value === "false") {
        return false;
    }

    return undefined;
}

export function isHousingOwner(user, housing) {
    if (!user?.id || !housing?.ownerId) {
        return false;
    }

    return Number(user.id) === Number(housing.ownerId);
}

export function createEmptyHousingForm() {
    return {
        title: "",
        description: "",
        city: "",
        address: "",
        price: "",
        housingType: "STUDIO",
        available: true,
    };
}

export function hasActiveHousingFilters(filters) {
    return Boolean(
        filters.city?.trim() ||
            filters.housingType ||
            filters.available ||
            filters.minPrice !== "" ||
            filters.maxPrice !== "",
    );
}

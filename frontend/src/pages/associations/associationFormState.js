export const DEFAULT_ASSOCIATION_FORM_VALUES = {
    title: "",
    description: "",
    city: "",
    address: "",
    contactEmail: "",
    phone: "",
    website: "",
    logoUrl: "",
    associationType: "",
    available: true,
};

export function createAssociationFormValues(association = null) {
    if (!association) {
        return { ...DEFAULT_ASSOCIATION_FORM_VALUES };
    }

    return {
        title: association.title || "",
        description: association.description || "",
        city: association.city || "",
        address: association.address || "",
        contactEmail: association.contactEmail || "",
        phone: association.phone || "",
        website: association.website || "",
        logoUrl: association.logoUrl || "",
        associationType: association.associationType || "",
        available: Boolean(association.available),
    };
}


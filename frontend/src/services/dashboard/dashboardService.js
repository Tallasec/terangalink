export const dashboardNavigationItems = [
    { label: "Logement", href: "/housing" },
    { label: "Emplois", href: "/jobs" },
    { label: "Groupes", href: "/study-groups" },
    { label: "Communauté", href: "#" },
];

export const dashboardQuickAccessCards = {
    housing: {
        category: "Logement",
        title: "Trouver un logement",
        description:
            "Découvrez des logements étudiants vérifiés et des colocations dans les grandes villes françaises.",
        image:
            "https://lh3.googleusercontent.com/aida-public/AB6AXuBp-rCg4vJsl9S_xSYvwKWkA5SIfK8Wkh_7-2q2UMAZDMi3ZeDwOjKg1ebi_qNXg6G8kX47QXXCp4OPyABuYJYU5sRg_5I3iWQ5XGwmHA_uFUoxNlFH9cuUnH_PUCRruyKLjmh1HOcti4_k9WPaHp390DJ9lgGtsaKQUJcrmEpLO0AHYC7BQKpVj3BBEbwJ6kGqYKUqiJY_KMRSIEIvJJsAvIlDcMs7Mkg3RMcgYk45lPN7XcdXoI6iiBekBvS_6ms6ZByR_Os04Sk",
    },
    jobs: {
        title: "Jobs Étudiants",
        description:
            "Parcourez des opportunités à temps partiel adaptées aux étudiants avec des horaires flexibles.",
        cta: "Explorer les postes",
        href: "/jobs",
        icon: "work",
    },
    groups: {
        title: "Groupes de Révision",
        description:
            "Connectez-vous avec des étudiants sénégalais dans votre domaine pour réviser ensemble.",
        cta: "Trouver des pairs",
        icon: "groups",
        href: "/study-groups",
    },
};

export const dashboardAnnouncements = [
    {
        type: "Annonce",
        tone: "secondary",
        time: "Il y a 2 heures",
        title: "Nouveau partenariat logement à Lyon",
        description:
            "Nous nous sommes associés à trois résidences locales pour offrir des places prioritaires aux étudiants TerangaLink dès ce semestre.",
        icon: "campaign",
        bubbleClassName: "bg-[#ffdea8] text-[#271900]",
        labelClassName: "text-[#755a26]",
    },
    {
        type: "Système",
        tone: "primary",
        time: "Hier",
        title: "Document de visa vérifié",
        description:
            "Votre titre de séjour a été vérifié avec succès. Vous pouvez maintenant accéder aux dossiers complets de logement.",
        icon: "description",
        bubbleClassName: "bg-[#b5ecf5] text-[#001f24]",
        labelClassName: "text-[#00343a]",
    },
];

export const dashboardQuickLinks = [
    "Contacts d'urgence",
    "Guide compte bancaire",
    "Mentorat culturel",
];

export const dashboardFooterColumns = [
    {
        title: "Entreprise",
        links: ["À propos", "Contacter le support"],
    },
    {
        title: "Légal",
        links: ["Conditions d'utilisation", "Politique de confidentialité"],
    },
    {
        title: "Ressources",
        links: ["Guide Étudiant"],
    },
];

export const dashboardFooterDescription =
    "© 2024 TerangaLink. Réinventer l'hospitalité sénégalaise en France. Accompagner les étudiants avec un soutien communautaire et des ressources essentielles.";

export const dashboardHeroCopy = {
    title: "Bonjour !",
    description:
        "Bienvenue sur votre tableau de bord. Votre parcours en France est soutenu par la communauté Teranga. Comment pouvons-nous vous aider aujourd'hui ?",
};

export const dashboardSupportCard = {
    title: "Besoin d'aide ?",
    description:
        "Notre équipe de support est disponible 24/7 pour les membres de la communauté.",
    cta: "Contacter le support",
    image:
        "https://lh3.googleusercontent.com/aida-public/AB6AXkCKY4ZzP4El-oXpXoQSwC7DE0hEW5Wzyx0RpcChsQQ8KiMwZwbNfH1C4oN12izM-xWdUCYtC-oUTfjTTXbR2tL2YCuw1ZOhlfuL_jb_xa9bj_eA1Lhx9yzMwg5JsLXeqvljmDFqbX57ZxtA7UlYUys1V71OCNkvUPhdqiptC_kZx-_FFj9Nh5VFXuhRjglJ7d15cPo-cG1tMwaGikldcJPnwUWWWQIMJ5jDvk7e8B7XXkknj3btnzp1g6nj6W2zT9NkxAkpqXQFmvh-",
};

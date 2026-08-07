import DashboardAnnouncementCard from "./DashboardAnnouncementCard";

import { dashboardAnnouncements } from "../../services/dashboard/dashboardService";

function DashboardAnnouncements() {
    return (
        <section className="lg:col-span-2">
            <div className="mb-6 flex items-center justify-between">
                <h2 className="text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                    Mises à jour récentes de la communauté
                </h2>
                <button
                    className="text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a] hover:underline"
                    type="button"
                >
                    Tout voir
                </button>
            </div>

            <div className="space-y-4">
                {dashboardAnnouncements.map((announcement) => (
                    <DashboardAnnouncementCard
                        key={announcement.title}
                        announcement={announcement}
                    />
                ))}
            </div>
        </section>
    );
}

export default DashboardAnnouncements;

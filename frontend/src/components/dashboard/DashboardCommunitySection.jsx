import DashboardAnnouncements from "./DashboardAnnouncements";
import DashboardSidebar from "./DashboardSidebar";

function DashboardCommunitySection() {
    return (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
            <DashboardAnnouncements />
            <DashboardSidebar />
        </div>
    );
}

export default DashboardCommunitySection;

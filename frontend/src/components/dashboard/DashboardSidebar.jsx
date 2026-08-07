import DashboardQuickLinks from "./DashboardQuickLinks";
import DashboardSupportCard from "./DashboardSupportCard";

function DashboardSidebar() {
    return (
        <aside className="space-y-6">
            <DashboardQuickLinks />
            <DashboardSupportCard />
        </aside>
    );
}

export default DashboardSidebar;

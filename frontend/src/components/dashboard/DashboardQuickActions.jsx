import DashboardProfileCard from "./DashboardProfileCard";
import DashboardGroupsCard from "./DashboardGroupsCard";
import DashboardHousingCard from "./DashboardHousingCard";
import DashboardJobsCard from "./DashboardJobsCard";

function DashboardQuickActions({ user }) {
    return (
        <div className="mb-16 grid grid-cols-1 gap-6 md:grid-cols-12 md:grid-rows-2">
            <DashboardHousingCard />
            <DashboardJobsCard />
            <DashboardGroupsCard />
            <DashboardProfileCard user={user} />
        </div>
    );
}

export default DashboardQuickActions;

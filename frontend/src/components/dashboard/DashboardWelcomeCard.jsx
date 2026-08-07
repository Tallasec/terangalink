import { dashboardHeroCopy } from "../../services/dashboard/dashboardService";
import { getUserFullName } from "../../services/user/userHelpers";

function DashboardWelcomeCard({ user }) {
    const fullName = getUserFullName(user);
    const title = fullName ? `Bonjour, ${fullName} !` : dashboardHeroCopy.title;

    return (
        <section className="mb-12">
            <h1 className="dashboard-welcome mb-2 text-[48px] font-bold leading-[56px] tracking-[-0.02em] text-[#00343a]">
                {title}
            </h1>
            <p className="max-w-2xl text-[18px] leading-[28px] text-[#40484a] text-balance">
                {dashboardHeroCopy.description}
            </p>
            {user?.email ? (
                <p className="mt-4 text-[14px] leading-5 text-[#6a7375]">{user.email}</p>
            ) : null}
        </section>
    );
}

export default DashboardWelcomeCard;

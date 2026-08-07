import DashboardCard from "./DashboardCard";
import MaterialSymbol from "../common/MaterialSymbol";

function DashboardAnnouncementCard({ announcement }) {
    return (
        <DashboardCard className="flex gap-6 rounded-xl border border-[#bfc8ca]/30 bg-white p-6">
            <div
                className={`flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full ${announcement.bubbleClassName}`}
            >
                <MaterialSymbol icon={announcement.icon} />
            </div>
            <div>
                <div className="mb-1 flex items-center gap-2">
                    <span className={`text-[12px] font-semibold leading-4 tracking-[0.05em] ${announcement.labelClassName}`}>
                        {announcement.type}
                    </span>
                    <span className="text-[#bfc8ca]">•</span>
                    <span className="text-[14px] leading-5 text-[#70797a]">
                        {announcement.time}
                    </span>
                </div>
                <h4 className="mb-2 text-[20px] font-medium leading-7 text-[#181c1d]">
                    {announcement.title}
                </h4>
                <p className="text-[16px] leading-6 text-[#40484a]">
                    {announcement.description}
                </p>
            </div>
        </DashboardCard>
    );
}

export default DashboardAnnouncementCard;

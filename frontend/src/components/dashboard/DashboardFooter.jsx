import {
    dashboardFooterColumns,
    dashboardFooterDescription,
} from "../../services/dashboard/dashboardService";

function DashboardFooter() {
    return (
        <footer className="mt-12 border-t border-[#bfc8ca]/30 bg-[#f1f4f5]">
            <div className="mx-auto flex max-w-[1200px] flex-col items-start gap-6 px-4 py-12 md:flex-row md:justify-between md:px-12">
                <div className="max-w-sm">
                    <div className="mb-4 text-[24px] font-semibold leading-8 tracking-[-0.01em] text-[#00343a]">
                        TerangaLink
                    </div>
                    <p className="mb-6 text-[14px] leading-5 text-[#40484a]">
                        {dashboardFooterDescription}
                    </p>
                </div>

                <div className="grid grid-cols-2 gap-12 sm:grid-cols-3">
                    {dashboardFooterColumns.map((column) => (
                        <div key={column.title} className="flex flex-col gap-3">
                            <span className="text-[12px] font-semibold leading-4 tracking-[0.05em] text-[#00343a]">
                                {column.title}
                            </span>
                            {column.links.map((link) => (
                                <a
                                    key={link}
                                    className="text-[14px] leading-5 text-[#40484a] transition-colors hover:text-[#00343a] hover:underline"
                                    href="#"
                                >
                                    {link}
                                </a>
                            ))}
                        </div>
                    ))}
                </div>
            </div>
        </footer>
    );
}

export default DashboardFooter;

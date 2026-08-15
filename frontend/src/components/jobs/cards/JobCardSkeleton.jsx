function JobCardSkeleton({ count = 6 }) {
    const items = Array.from({ length: count });

    return (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
            {items.map((_, index) => (
                <div
                    key={index}
                    className="overflow-hidden rounded-[28px] border border-[#dbe6e6] bg-white shadow-[0px_12px_40px_rgba(0,52,58,0.05)]"
                >
                    <div className="flex items-start justify-between gap-4 p-5">
                        <div className="flex items-center gap-3">
                            <div className="h-12 w-12 rounded-2xl bg-[#edf3f3] animate-pulse" />
                            <div className="space-y-2">
                                <div className="h-4 w-40 rounded-full bg-[#edf3f3] animate-pulse" />
                                <div className="h-3 w-28 rounded-full bg-[#edf3f3] animate-pulse" />
                            </div>
                        </div>
                        <div className="h-8 w-8 rounded-full bg-[#edf3f3] animate-pulse" />
                    </div>

                    <div className="px-5 pb-5">
                        <div className="h-5 w-3/4 rounded-full bg-[#edf3f3] animate-pulse" />
                        <div className="mt-3 h-4 w-full rounded-full bg-[#edf3f3] animate-pulse" />
                        <div className="mt-2 h-4 w-5/6 rounded-full bg-[#edf3f3] animate-pulse" />

                        <div className="mt-5 flex flex-wrap gap-2">
                            <div className="h-8 w-32 rounded-full bg-[#edf3f3] animate-pulse" />
                            <div className="h-8 w-28 rounded-full bg-[#edf3f3] animate-pulse" />
                        </div>

                        <div className="mt-6 flex items-center justify-between border-t border-[#edf3f3] pt-4">
                            <div className="h-3 w-28 rounded-full bg-[#edf3f3] animate-pulse" />
                            <div className="h-10 w-24 rounded-full bg-[#edf3f3] animate-pulse" />
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}

export default JobCardSkeleton;

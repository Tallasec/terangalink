function StudyGroupCardSkeleton({ count = 6 }) {
    return (
        <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
            {Array.from({ length: count }).map((_, index) => (
                <div
                    key={index}
                    className="rounded-[28px] border border-[#dbe6e6] bg-white p-5 shadow-[0px_10px_34px_rgba(0,52,58,0.05)]"
                >
                    <div className="h-4 w-24 animate-pulse rounded-full bg-[#edf3f3]" />
                    <div className="mt-3 h-7 w-4/5 animate-pulse rounded-full bg-[#edf3f3]" />
                    <div className="mt-4 space-y-2">
                        <div className="h-4 w-full animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="h-4 w-5/6 animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="h-4 w-2/3 animate-pulse rounded-full bg-[#edf3f3]" />
                    </div>
                    <div className="mt-5 flex gap-2">
                        <div className="h-8 w-24 animate-pulse rounded-full bg-[#edf3f3]" />
                        <div className="h-8 w-24 animate-pulse rounded-full bg-[#edf3f3]" />
                    </div>
                </div>
            ))}
        </div>
    );
}

export default StudyGroupCardSkeleton;

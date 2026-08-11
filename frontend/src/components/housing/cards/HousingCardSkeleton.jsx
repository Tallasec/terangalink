function HousingCardSkeleton({ count = 6 }) {
    return (
        <div className="grid gap-6 sm:grid-cols-2">
            {Array.from({ length: count }, (_, index) => (
                <div
                    key={index}
                    className="overflow-hidden rounded-2xl border border-[#bfc8ca]/30 bg-white shadow-[0px_4px_20px_rgba(0,0,0,0.04)]"
                >
                    <div className="aspect-[4/3] animate-pulse bg-[#ebeeef]" />
                    <div className="space-y-3 p-5">
                        <div className="h-5 w-3/4 animate-pulse rounded bg-[#ebeeef]" />
                        <div className="h-4 w-1/2 animate-pulse rounded bg-[#ebeeef]" />
                        <div className="h-4 w-full animate-pulse rounded bg-[#ebeeef]" />
                        <div className="h-4 w-5/6 animate-pulse rounded bg-[#ebeeef]" />
                    </div>
                </div>
            ))}
        </div>
    );
}

export default HousingCardSkeleton;

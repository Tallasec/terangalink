function AssociationCardSkeleton({ count = 6 }) {
    return (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
            {Array.from({ length: count }).map((_, index) => (
                <div
                    key={`association-skeleton-${index}`}
                    className="h-[320px] animate-pulse rounded-[28px] bg-white shadow-[0px_10px_34px_rgba(0,52,58,0.05)]"
                />
            ))}
        </div>
    );
}

export default AssociationCardSkeleton;

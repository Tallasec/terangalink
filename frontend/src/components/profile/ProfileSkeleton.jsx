import Loader from "../common/ui/Loader";

function ProfileSkeleton() {
    return (
        <div className="min-h-screen bg-[#f7fafb] px-4 py-8 text-[#181c1d] md:px-12">
            <div className="mx-auto flex max-w-[1200px] items-center gap-3 rounded-full border border-[#bfc8ca]/40 bg-white px-5 py-3 text-[14px] leading-5 text-[#40484a] shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
                <Loader size="sm" />
                Chargement de votre profil...
            </div>

            <div className="mx-auto mt-8 grid max-w-[1200px] gap-6 lg:grid-cols-[0.9fr_1.1fr]">
                <div className="space-y-6">
                    <div className="h-64 animate-pulse rounded-[28px] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.06)]" />
                    <div className="h-56 animate-pulse rounded-[28px] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.06)]" />
                </div>
                <div className="space-y-6">
                    <div className="h-72 animate-pulse rounded-[28px] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.06)]" />
                    <div className="h-56 animate-pulse rounded-[28px] bg-white shadow-[0_24px_80px_rgba(0,52,58,0.06)]" />
                </div>
            </div>
        </div>
    );
}

export default ProfileSkeleton;

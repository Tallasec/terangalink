function StudyGroupPagination({ disabled, page, totalPages, onPageChange }) {
    if (totalPages <= 1) {
        return null;
    }

    return (
        <div className="mt-8 flex items-center justify-between gap-4">
            <button
                className="rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                onClick={() => onPageChange(page - 1)}
                disabled={disabled || page <= 0}
            >
                Page précédente
            </button>

            <p className="text-[14px] text-[#526062]">
                Page {page + 1} sur {totalPages}
            </p>

            <button
                className="rounded-full border border-[#dce7e8] bg-white px-4 py-2 text-[14px] font-semibold text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                onClick={() => onPageChange(page + 1)}
                disabled={disabled || page + 1 >= totalPages}
            >
                Page suivante
            </button>
        </div>
    );
}

export default StudyGroupPagination;

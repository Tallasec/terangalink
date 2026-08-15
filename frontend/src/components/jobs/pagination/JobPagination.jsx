import MaterialSymbol from "../../common/MaterialSymbol";

function JobPagination({ page, totalPages, onPageChange, disabled = false }) {
    if (totalPages <= 1) {
        return null;
    }

    const visiblePages = getVisiblePages(page, totalPages);

    return (
        <nav aria-label="Pagination des emplois" className="mt-10 flex items-center justify-center gap-2">
            <button
                aria-label="Page précédente"
                className="flex h-11 w-11 items-center justify-center rounded-full border border-[#dce7e8] bg-white text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page <= 0}
                onClick={() => onPageChange(page - 1)}
            >
                <MaterialSymbol icon="chevron_left" />
            </button>

            <div className="flex items-center gap-2">
                {visiblePages.map((pageNumber, index) =>
                    pageNumber === "ellipsis" ? (
                        <span key={`ellipsis-${index}`} className="px-2 text-[#839194]">
                            …
                        </span>
                    ) : (
                        <button
                            key={pageNumber}
                            aria-current={pageNumber === page ? "page" : undefined}
                            className={`flex h-11 min-w-11 items-center justify-center rounded-full px-4 text-[14px] font-semibold transition-colors ${
                                pageNumber === page
                                    ? "bg-[#00343a] text-white shadow-[0px_10px_24px_rgba(0,52,58,0.18)]"
                                    : "border border-[#dce7e8] bg-white text-[#526062] hover:border-[#00343a] hover:text-[#00343a]"
                            }`}
                            type="button"
                            disabled={disabled}
                            onClick={() => onPageChange(pageNumber)}
                        >
                            {pageNumber + 1}
                        </button>
                    ),
                )}
            </div>

            <button
                aria-label="Page suivante"
                className="flex h-11 w-11 items-center justify-center rounded-full border border-[#dce7e8] bg-white text-[#526062] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page >= totalPages - 1}
                onClick={() => onPageChange(page + 1)}
            >
                <MaterialSymbol icon="chevron_right" />
            </button>
        </nav>
    );
}

function getVisiblePages(currentPage, totalPages) {
    if (totalPages <= 5) {
        return Array.from({ length: totalPages }, (_, index) => index);
    }

    const pages = new Set([0, totalPages - 1, currentPage]);

    if (currentPage > 0) {
        pages.add(currentPage - 1);
    }

    if (currentPage < totalPages - 1) {
        pages.add(currentPage + 1);
    }

    const sortedPages = [...pages].sort((left, right) => left - right);
    const visiblePages = [];

    sortedPages.forEach((pageNumber, index) => {
        if (index > 0 && pageNumber - sortedPages[index - 1] > 1) {
            visiblePages.push("ellipsis");
        }

        visiblePages.push(pageNumber);
    });

    return visiblePages;
}

export default JobPagination;

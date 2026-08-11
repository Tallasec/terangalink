import MaterialSymbol from "../../common/MaterialSymbol";

function HousingPagination({ page, totalPages, onPageChange, disabled = false }) {
    if (totalPages <= 1) {
        return null;
    }

    const visiblePages = getVisiblePages(page, totalPages);

    return (
        <nav
            aria-label="Pagination des logements"
            className="mt-10 flex items-center justify-center gap-2"
        >
            <button
                aria-label="Page precedente"
                className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#dce7e8] bg-white text-[#40484a] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page <= 0}
                onClick={() => {
                    onPageChange(page - 1);
                }}
            >
                <MaterialSymbol icon="chevron_left" />
            </button>

            {visiblePages.map((pageNumber, index) =>
                pageNumber === "ellipsis" ? (
                    <span
                        key={`ellipsis-${index}`}
                        className="flex h-10 min-w-10 items-center justify-center text-[#70797a]"
                    >
                        …
                    </span>
                ) : (
                    <button
                        key={pageNumber}
                        aria-current={pageNumber === page ? "page" : undefined}
                        className={`flex h-10 min-w-10 items-center justify-center rounded-xl px-3 text-[14px] font-semibold transition-colors ${
                            pageNumber === page
                                ? "bg-[#00343a] text-white"
                                : "border border-[#dce7e8] bg-white text-[#40484a] hover:border-[#00343a] hover:text-[#00343a]"
                        }`}
                        type="button"
                        disabled={disabled}
                        onClick={() => {
                            onPageChange(pageNumber);
                        }}
                    >
                        {pageNumber + 1}
                    </button>
                ),
            )}

            <button
                aria-label="Page suivante"
                className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#dce7e8] bg-white text-[#40484a] transition-colors hover:border-[#00343a] hover:text-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
                type="button"
                disabled={disabled || page >= totalPages - 1}
                onClick={() => {
                    onPageChange(page + 1);
                }}
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

export default HousingPagination;

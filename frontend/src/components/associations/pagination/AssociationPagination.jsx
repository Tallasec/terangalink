import MaterialSymbol from "../../common/MaterialSymbol";

function AssociationPagination({ page, totalPages, disabled = false, onPageChange }) {
    if (!totalPages || totalPages <= 1) {
        return null;
    }

    const pages = buildPages(page, totalPages);

    return (
        <nav className="mt-8 flex items-center justify-center gap-2" aria-label="Pagination des associations">
            <PageButton icon="chevron_left" label="Page précédente" disabled={disabled || page <= 0} onClick={() => onPageChange(page - 1)} />
            {pages.map((item, index) =>
                item === "..." ? (
                    <span key={`ellipsis-${index}`} className="px-2 text-[#526062]">
                        ...
                    </span>
                ) : (
                    <button
                        key={item}
                        className={`flex h-11 min-w-11 items-center justify-center rounded-[14px] border px-4 text-[14px] font-semibold transition-colors ${
                            item === page
                                ? "border-[#00343a] bg-[#00343a] text-white"
                                : "border-[#dbe6e6] bg-white text-[#00343a] hover:border-[#00343a]"
                        }`}
                        type="button"
                        disabled={disabled}
                        onClick={() => onPageChange(item)}
                    >
                        {item + 1}
                    </button>
                ),
            )}
            <PageButton icon="chevron_right" label="Page suivante" disabled={disabled || page >= totalPages - 1} onClick={() => onPageChange(page + 1)} />
        </nav>
    );
}

function PageButton({ icon, label, onClick, disabled }) {
    return (
        <button
            aria-label={label}
            className="flex h-11 w-11 items-center justify-center rounded-[14px] border border-[#dbe6e6] bg-white text-[#00343a] transition-colors hover:border-[#00343a] disabled:cursor-not-allowed disabled:opacity-50"
            type="button"
            disabled={disabled}
            onClick={onClick}
        >
            <MaterialSymbol icon={icon} />
        </button>
    );
}

function buildPages(page, totalPages) {
    const pages = [];
    const totalVisible = 3;

    if (totalPages <= totalVisible) {
        for (let index = 0; index < totalPages; index += 1) {
            pages.push(index);
        }
        return pages;
    }

    pages.push(0);

    if (page > 1) {
        pages.push("...");
    }

    const start = Math.max(1, page - 1);
    const end = Math.min(totalPages - 2, page + 1);

    for (let index = start; index <= end; index += 1) {
        pages.push(index);
    }

    if (page < totalPages - 2) {
        pages.push("...");
    }

    pages.push(totalPages - 1);

    return [...new Set(pages)];
}

export default AssociationPagination;

"use client";

type PaginationControlsProps = {
  currentPage: number;
  totalPages: number;
  itemsOnPage: number;
  pageSize: number;
  totalItems: number;
  onPageChange: (page: number) => void;
  itemLabel?: string;
};

const buildPageRange = (currentPage: number, totalPages: number) => {
  const maxButtons = 5;
  const halfRange = Math.floor(maxButtons / 2);

  let start = Math.max(1, currentPage - halfRange);
  let end = start + maxButtons - 1;

  if (end > totalPages) {
    end = totalPages;
    start = Math.max(1, end - maxButtons + 1);
  }

  return Array.from({ length: end - start + 1 }, (_, idx) => start + idx);
};

export default function PaginationControls({
  currentPage,
  totalPages,
  itemsOnPage,
  pageSize,
  totalItems,
  onPageChange,
  itemLabel = "transactions",
}: PaginationControlsProps) {
  const pages = buildPageRange(currentPage, totalPages);
  const rangeStart = totalItems === 0 ? 0 : (currentPage - 1) * pageSize + 1;
  const rangeEnd =
    totalItems === 0 ? 0 : rangeStart + itemsOnPage - 1;

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 text-sm">
      <div className="flex items-center gap-2">
        <button
          type="button"
          className="rounded-lg border border-(--grey-200) px-4 py-2 text-xs font-semibold uppercase tracking-wide text-(--color-grey-600) transition hover:bg-(--color-purple) hover:text-white hover:border-(--color-purple) active:bg-(--color-purple) active:text-white active:border-(--color-purple) disabled:cursor-not-allowed disabled:opacity-40"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage <= 1}
        >
          Prev
        </button>

        {pages.map((page) => (
          <button
            key={page}
            type="button"
          className={`rounded-lg px-4 py-2 text-xs font-semibold tracking-wide transition ${
            page === currentPage
              ? "bg-black text-white border-black"
              : "border border-(--grey-200) text-(--color-grey-600) hover:bg-(--color-purple) hover:text-white hover:border-(--color-purple) active:bg-(--color-purple) active:text-white active:border-(--color-purple)"
          }`}
            onClick={() => onPageChange(page)}
          >
            {page}
          </button>
        ))}

        <button
          type="button"
          className="rounded-lg border border-(--grey-200) px-4 py-2 text-xs font-semibold uppercase tracking-wide text-(--color-grey-600) transition hover:bg-(--color-purple) hover:text-white hover:border-(--color-purple) active:bg-(--color-purple) active:text-white active:border-(--color-purple) disabled:cursor-not-allowed disabled:opacity-40"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages}
        >
          Next
        </button>
      </div>

      <p className="text-xs text-slate-600">
        Showing {rangeStart} - {rangeEnd} of {totalItems} {itemLabel}
      </p>
    </div>
  );
}

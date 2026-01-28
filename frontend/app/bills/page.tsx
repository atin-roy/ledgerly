"use client";

import { ArrowUpDown, ChevronDown } from "lucide-react";
import {
  useEffect,
  useMemo,
  useRef,
  useState,
  type ChangeEvent,
} from "react";

import CustomSelect from "@/components/ui/CustomSelect";
import PageTitle from "@/components/PageTitle";
import BillCard from "@/components/bills/BillCard";
import PaginationControls from "@/components/transactions/PaginationControls";
import {
  BILL_PAGE_SIZE,
  billRecords,
  billSortOptions,
  formatCurrency,
  getBillPage,
  getBillSummary,
  type BillSortOption,
} from "@/app/bills/data";

export default function BillsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState<BillSortOption>("latest");
  const [page, setPage] = useState(1);
  const [billRowWidth, setBillRowWidth] = useState(0);
  const billListRef = useRef<HTMLDivElement>(null);

  const { data: visibleBills, currentPage, totalItems, totalPages } = useMemo(
    () =>
      getBillPage(billRecords, {
        searchTerm,
        sortBy,
        page,
        pageSize: BILL_PAGE_SIZE,
      }),
    [searchTerm, sortBy, page],
  );

  const summary = useMemo(() => getBillSummary(billRecords), []);

  useEffect(() => {
    if (currentPage !== page) {
      setPage(currentPage);
    }
  }, [currentPage, page]);

  useEffect(() => {
    const node = billListRef.current;
    if (!node || typeof ResizeObserver === "undefined") {
      return undefined;
    }

    const updateWidth = () => {
      const { width } = node.getBoundingClientRect();
      setBillRowWidth(Math.round(width));
    };

    updateWidth();

    const observer = new ResizeObserver(() => {
      updateWidth();
    });

    observer.observe(node);

    return () => {
      observer.disconnect();
    };
  }, []);

  const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
    setPage(1);
  };

  const handleSortChange = (value: BillSortOption) => {
    setSortBy(value);
    setPage(1);
  };

  const handleSortSelection = (label: string) => {
    const option = billSortOptions.find((opt) => opt.label === label);
    if (option) {
      handleSortChange(option.value);
    }
  };

  const currentSortLabel =
    billSortOptions.find((option) => option.value === sortBy)?.label ??
    sortBy;
  const placeholderCount = Math.max(0, BILL_PAGE_SIZE - visibleBills.length);

  return (
    <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <PageTitle title="Recurring Bills" />

        <div className="grid gap-6 lg:grid-cols-[minmax(0,320px)_1fr]">
          <div className="flex flex-col gap-6">
            <div className="rounded-[32px] bg-slate-900 px-6 py-8 shadow-2xl">
              <p className="text-xs uppercase tracking-[0.6em] text-slate-400">
                Total bills
              </p>
              <p className="mt-4 text-4xl font-semibold text-white">
                {formatCurrency(summary.totalAmount)}
              </p>
              <p className="mt-2 text-sm text-slate-300">
                Net monthly commitments
              </p>
            </div>

            <div className="rounded-[32px] bg-white p-6 shadow-xl">
              <p className="text-sm font-semibold uppercase tracking-[0.35em] text-slate-400">
                Summary
              </p>
              <div className="mt-6 space-y-4 text-sm">
                <div className="flex items-center justify-between text-slate-500">
                  <span>Paid bills</span>
                  <span className="font-semibold text-slate-900">
                    {summary.paid.count} ({formatCurrency(summary.paid.amount)})
                  </span>
                </div>
                <div className="flex items-center justify-between text-slate-500">
                  <span>Pending bills</span>
                  <span className="font-semibold text-slate-900">
                    {summary.pending.count} ({formatCurrency(summary.pending.amount)})
                  </span>
                </div>
                <div className="flex items-center justify-between text-rose-600">
                  <span>Overdue bills</span>
                  <span className="font-semibold text-rose-600">
                    {summary.overdue.count} ({formatCurrency(summary.overdue.amount)})
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div className="rounded-[32px] bg-white p-6 shadow-xl">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <div className="relative flex-1 min-w-[220px] max-w-sm">
                <input
                  type="search"
                  placeholder="Search bills"
                  value={searchTerm}
                  onChange={handleSearchChange}
                  className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 pr-12 text-sm text-slate-600 placeholder:text-slate-400 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                />
                <div className="pointer-events-none absolute inset-y-0 right-4 flex items-center text-slate-400">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.6"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="h-4 w-4"
                  >
                    <circle cx="11" cy="11" r="7" />
                    <line x1="17.5" y1="17.5" x2="22" y2="22" />
                  </svg>
                </div>
              </div>

              <div className="flex items-center gap-3 md:hidden">
                <CustomSelect
                  value={currentSortLabel}
                  options={billSortOptions.map((option) => option.label)}
                  onChange={handleSortSelection}
                  icon={<ArrowUpDown className="h-4 w-4" />}
                  ariaLabel="Sort by"
                  isMobile
                  dropdownWidth={billRowWidth || undefined}
                />
              </div>

              <div className="hidden flex-1 items-center gap-3 md:flex">
                <span className="text-xs uppercase tracking-[0.5em] text-slate-400">
                  Sort by
                </span>
                <CustomSelect
                  value={currentSortLabel}
                  options={billSortOptions.map((option) => option.label)}
                  onChange={handleSortSelection}
                  icon={<ChevronDown className="h-4 w-4" />}
                  trailingIcon={<ChevronDown className="h-4 w-4" />}
                  ariaLabel="Sort by"
                  dropdownWidth={billRowWidth || undefined}
                />
              </div>
            </div>

            <div className="mt-6 space-y-4" ref={billListRef}>
              {visibleBills.map((bill) => (
                <BillCard key={bill.id} bill={bill} />
              ))}
              {visibleBills.length === 0 && (
                <p className="rounded-2xl border border-dashed border-slate-200 p-6 text-center text-sm text-slate-500">
                  No bills match your search.
                </p>
              )}
              {Array.from({ length: placeholderCount }).map((_, index) => (
                <div
                  key={`placeholder-${index}`}
                  className="h-[104px] rounded-3xl border border-dashed border-slate-200 bg-slate-50/80"
                  aria-hidden="true"
                />
              ))}
            </div>

            <div className="mt-6">
              <PaginationControls
                currentPage={currentPage}
                totalPages={totalPages}
                itemsOnPage={visibleBills.length}
                pageSize={BILL_PAGE_SIZE}
                totalItems={totalItems}
                onPageChange={setPage}
                itemLabel="bills"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

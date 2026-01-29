"use client";

import { ArrowUpDown, ChevronDown } from "lucide-react";
import {
  useEffect,
  useMemo,
  useState,
  type ChangeEvent,
  type SyntheticEvent,
} from "react";

import CustomSelect from "@/components/ui/CustomSelect";
import PageTitle from "@/components/PageTitle";
import BillCard from "@/components/bills/BillCard";
import PaginationControls from "@/components/transactions/PaginationControls";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import {
  BILL_PAGE_SIZE,
  billSortOptions,
  formatCurrency,
  getBillPage,
  getBillSummary,
  type BillSortOption,
  type Bill,
} from "@/app/bills/data";
import { baseCategories, type TransactionCategory } from "@/app/transactions/data";
import { getBills, createBill, type BillResponse } from "@/lib/services";

const billCategories = baseCategories;
const billFrequencyOptions = ["Weekly", "Bi-Weekly", "Monthly", "Quarterly", "Annual"] as const;

type BillCategory = TransactionCategory;
type BillFrequency = (typeof billFrequencyOptions)[number];

type BillFormValues = {
  title: string;
  amount: string;
  category: BillCategory;
  frequency: BillFrequency;
  description: string;
};

const billModalFields: BillFormValues = {
  title: "",
  amount: "",
  category: billCategories[0],
  frequency: billFrequencyOptions[2],
  description: "",
};

export default function BillsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState<BillSortOption>("latest");
  const [page, setPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState<BillFormValues>(billModalFields);
  const [bills, setBills] = useState<Bill[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadBills();
  }, []);

  const loadBills = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getBills();
      
      // Convert backend format to frontend format
      const convertedBills: Bill[] = data.map((b) => ({
        id: b.id.toString(),
        title: b.name,
        dueLabel: "Monthly",
        nextDue: b.dueDate.split("T")[0],
        amount: b.amount,
        status: b.status.toLowerCase() as any,
        iconLabel: b.name.substring(0, 2).toUpperCase(),
        iconColor: "var(--color-green)",
      }));
      
      setBills(convertedBills);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load bills");
      console.error("Error loading bills:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const { data: visibleBills, currentPage, totalItems, totalPages } = useMemo(
    () =>
      getBillPage(bills, {
        searchTerm,
        sortBy,
        page,
        pageSize: BILL_PAGE_SIZE,
      }),
    [bills, searchTerm, sortBy, page]
  );

  const summary = useMemo(() => getBillSummary(bills), [bills]);

  useEffect(() => {
    if (currentPage !== page) {
      setPage(currentPage);
    }
  }, [currentPage, page]);

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
    billSortOptions.find((option) => option.value === sortBy)?.label ?? sortBy;
  const placeholderCount = Math.max(0, BILL_PAGE_SIZE - visibleBills.length);

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleCategoryChange = (value: string) => {
    setFormValues((prev) => ({ ...prev, category: value as BillCategory }));
  };

  const handleFrequencyChange = (value: string) => {
    setFormValues((prev) => ({ ...prev, frequency: value as BillFrequency }));
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    try {
      await createBill({
        name: formValues.title,
        amount: parseFloat(formValues.amount),
        status: "PENDING",
        dueDate: new Date().toISOString(),
      });
      
      setIsModalOpen(false);
      setFormValues(billModalFields);
      await loadBills();
    } catch (err) {
      console.error("Error creating bill:", err);
      alert("Failed to create bill. Please try again.");
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Recurring Bills" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-gray-600">Loading bills...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Recurring Bills" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={loadBills} className={primaryActionButtonClass}>
              Try Again
            </Button>
          </div>
        </div>
      </div>
    );
  }

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

          <div className="relative">
            <div className="rounded-[32px] bg-white p-6 shadow-xl">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="relative flex-1 min-w-[220px] max-w-[300px]">
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
                  />
                </div>

                <Button
                  variant="ghost"
                  size="sm"
                  className={primaryActionButtonClass}
                  onClick={() => setIsModalOpen(true)}
                >
                  New bill
                </Button>

                <div className="hidden items-center gap-3 md:flex md:justify-end">
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
                    className="w-[180px]"
                  />
                </div>
              </div>

              <div className="mt-6 space-y-4">
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

            {isModalOpen && (
              <div className="absolute inset-0 z-40 flex items-center justify-center rounded-[32px] bg-black/40 px-4 py-8">
                <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl">
                  <div className="mb-4 flex items-center justify-between">
                    <h3 className="text-lg font-semibold text-(--color-grey-900)">
                      New bill
                    </h3>
                    <button
                      type="button"
                      className="text-sm font-semibold text-slate-500 hover:text-slate-700"
                      onClick={() => setIsModalOpen(false)}
                    >
                      Close
                    </button>
                  </div>
                  <form className="mt-2 space-y-4" onSubmit={handleFormSubmit}>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Title
                      </span>
                      <input
                        name="title"
                        value={formValues.title}
                        onChange={handleFormChange}
                        placeholder="Electricity subscription"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Category
                      </span>
                      <CustomSelect
                        value={formValues.category}
                        options={billCategories}
                        onChange={handleCategoryChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Bill category"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Frequency
                      </span>
                      <CustomSelect
                        value={formValues.frequency}
                        options={billFrequencyOptions}
                        onChange={handleFrequencyChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Billing frequency"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Amount
                      </span>
                      <input
                        name="amount"
                        value={formValues.amount}
                        onChange={handleFormChange}
                        placeholder="0.00"
                        type="number"
                        min={0}
                        step="0.01"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Description
                      </span>
                      <input
                        name="description"
                        value={formValues.description}
                        onChange={handleFormChange}
                        placeholder="What the bill covers"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <div className="flex justify-center gap-3 pt-1">
                      <Button
                        variant="ghost"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-rose-600 text-white shadow-lg focus-visible:ring-rose-500 active:translate-y-px active:scale-95 hover:bg-rose-600 hover:text-white hover:shadow-none"
                        onClick={() => setIsModalOpen(false)}
                        type="button"
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="ghost"
                        type="submit"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-emerald-600 text-white shadow-lg focus-visible:ring-emerald-500 active:translate-y-px active:scale-95 hover:bg-emerald-600 hover:text-white hover:shadow-none"
                      >
                        Save
                      </Button>
                    </div>
                  </form>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

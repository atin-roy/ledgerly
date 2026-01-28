"use client";

import { SyntheticEvent, useEffect, useMemo, useState } from "react";
import { ChevronDown } from "lucide-react";

import PageTitle from "@/components/PageTitle";
import PaginationControls from "@/components/transactions/PaginationControls";
import TransactionFilters from "@/components/transactions/TransactionFilters";
import TransactionList from "@/components/transactions/TransactionList";
import CustomSelect from "@/components/ui/CustomSelect";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import {
  getTransactionPage,
  transactionCategoryOptions,
  transactionRecords,
  type TransactionSortOption,
} from "@/app/transactions/data";

const PAGE_SIZE = 10;

const modalFields = {
  recipient: "",
  description: "",
  amount: "",
  category: "",
  type: "expense",
};

const transactionTypes = [
  { label: "Expense", value: "expense" },
  { label: "Income", value: "income" },
];

export default function TransactionsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [category, setCategory] = useState<string>(
    transactionCategoryOptions[0],
  );
  const [sortBy, setSortBy] = useState<TransactionSortOption>("latest");
  const [page, setPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(modalFields);

  const { data, totalItems, totalPages, currentPage } = useMemo(
    () =>
      getTransactionPage(transactionRecords, {
        searchTerm,
        category,
        sortBy,
        page,
        pageSize: PAGE_SIZE,
      }),
    [searchTerm, category, sortBy, page],
  );

  useEffect(() => {
    if (currentPage !== page) {
      setPage(currentPage);
    }
  }, [currentPage, page]);

  const handleSearchChange = (value: string) => {
    setSearchTerm(value);
    setPage(1);
  };

  const handleCategoryChange = (value: string) => {
    setCategory(value);
    setPage(1);
  };

  const handleSortChange = (value: TransactionSortOption) => {
    setSortBy(value);
    setPage(1);
  };

  const handleFormChange = (
    event: SyntheticEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const target = event.target as HTMLInputElement | HTMLSelectElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleFormSubmit = (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsModalOpen(false);
    setFormValues(modalFields);
  };

  const transactionTypeLabel =
    transactionTypes.find((type) => type.value === formValues.type)?.label ??
    transactionTypes[0].label;

  const handleTransactionTypeChange = (label: string) => {
    const option = transactionTypes.find((type) => type.label === label);
    if (option) {
      setFormValues((prev) => ({ ...prev, type: option.value }));
    }
  };

  const transactionButtonClass = primaryActionButtonClass;

  return (
    <div className="min-h-screen bg-beige-100 py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <PageTitle title="Transactions" />
          <Button
            variant="ghost"
            size="sm"
            className={`${transactionButtonClass}`}
            onClick={() => setIsModalOpen(true)}
          >
            New transaction
          </Button>
        </div>

        <div className="relative">
          <section className="space-y-6 rounded-3xl bg-white p-6 shadow-xl">
            <TransactionFilters
              search={searchTerm}
              category={category}
              sortBy={sortBy}
              onSearchChange={handleSearchChange}
              onCategoryChange={handleCategoryChange}
              onSortChange={handleSortChange}
            />

            <div>
              <TransactionList transactions={data} pageSize={PAGE_SIZE} />
            </div>

            <PaginationControls
              currentPage={currentPage}
              totalPages={totalPages}
              itemsOnPage={data.length}
              pageSize={PAGE_SIZE}
              totalItems={totalItems}
              onPageChange={setPage}
            />
          </section>

          {isModalOpen && (
            <div className="absolute inset-0 z-40 flex items-center justify-center rounded-3xl bg-black/40 px-4 py-8">
              <div className="w-full max-w-xl rounded-3xl bg-white p-6 shadow-2xl">
                <div className="mb-4 flex items-center justify-between">
                  <h3 className="text-lg font-semibold text-[var(--color-grey-900)]">
                    New transaction
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
                  <label className="flex flex-col gap-2 text-sm font-semibold text-(--color-grey-600)">
                    <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                      Recipient/Sender
                    </span>
                    <input
                      name="recipient"
                      value={formValues.recipient}
                      onChange={handleFormChange}
                      placeholder="Name"
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
                      placeholder="Add a note"
                      className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                    />
                  </label>
                  <label className="flex flex-col gap-2 text-sm text-slate-600">
                    <span className="text-xs font-semibold uppercase tracking-wide text-(--color-grey-500)">
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
                  <div className="grid gap-4 md:grid-cols-2">
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs font-semibold uppercase tracking-wide text-(--color-grey-500)">
                        Category
                      </span>
                      <input
                        name="category"
                        value={formValues.category}
                        onChange={handleFormChange}
                        placeholder="General"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>

                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs font-semibold uppercase tracking-wide text-(--color-grey-500)">
                        Type
                      </span>
                      <CustomSelect
                        value={transactionTypeLabel}
                        options={transactionTypes.map((type) => type.label)}
                        onChange={handleTransactionTypeChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Transaction type"
                      />
                    </label>
                  </div>
                  <div className="flex justify-center gap-3 pt-1">
                    <Button
                      variant="ghost"
                      className="rounded-2xl px-6 py-3 text-sm font-semibold bg-rose-600 text-white shadow-lg focus-visible:ring-rose-500 active:translate-y-[1px] active:scale-95 hover:bg-rose-600 hover:text-white hover:shadow-none"
                      onClick={() => setIsModalOpen(false)}
                      type="button"
                    >
                      Cancel
                    </Button>
                    <Button
                      variant="ghost"
                      type="submit"
                      className="rounded-2xl px-6 py-3 text-sm font-semibold bg-emerald-600 text-white shadow-lg focus-visible:ring-emerald-500 active:translate-y-[1px] active:scale-95 hover:bg-emerald-600 hover:text-white hover:shadow-none"
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
  );
}

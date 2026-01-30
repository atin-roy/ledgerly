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
  type TransactionSortOption,
  type Transaction,
} from "@/app/transactions/data";
import { getTransactions, getCategories, createTransaction, type TransactionResponse, type CategoryResponse } from "@/lib/services";

const PAGE_SIZE = 10;

const modalFields = {
  recipient: "",
  description: "",
  amount: "",
  category: "",
  type: "expense",
  date: new Date().toISOString().split("T")[0], // Today's date in YYYY-MM-DD format
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
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [transactionsData, categoriesData] = await Promise.all([
        getTransactions(),
        getCategories(),
      ]);
      
      console.log("Transactions data:", transactionsData);
      console.log("Categories data:", categoriesData);
      
      // Ensure data is an array
      const transactionsArray = Array.isArray(transactionsData) ? transactionsData : [];
      const categoriesArray = Array.isArray(categoriesData) ? categoriesData : [];
      
      // Convert backend format to frontend format
      const convertedTransactions: Transaction[] = transactionsArray.map((t) => ({
        id: t.id.toString(),
        recipient: t.partyName || "Unknown",
        description: t.description || "",
        category: t.categoryName as any,
        date: t.date.split("T")[0], // Convert ISO to date only
        amount: Math.abs(t.amount),
        type: t.amount >= 0 ? "income" : "expense",
        badgeColor: "var(--color-green)",
      }));
      
      setTransactions(convertedTransactions);
      setCategories(categoriesArray);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load transactions");
      console.error("Error loading data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const { data, totalItems, totalPages, currentPage } = useMemo(
    () =>
      getTransactionPage(transactions, {
        searchTerm,
        category,
        sortBy,
        page,
        pageSize: PAGE_SIZE,
      }),
    [transactions, searchTerm, category, sortBy, page],
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

  const handleOpenModal = () => {
    // Initialize category with first available category when opening modal
    const initialCategory = categories.length > 0 ? categories[0].name : "";
    setFormValues({ ...modalFields, category: initialCategory });
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    try {
      const category = categories.find(c => c.name === formValues.category);
      if (!category) {
        alert("Please select a valid category");
        return;
      }

      const amount = parseFloat(formValues.amount);
      if (isNaN(amount) || amount <= 0) {
        alert("Please enter a valid amount");
        return;
      }

      // Convert date to ISO format with time
      const dateTime = new Date(formValues.date + "T00:00:00").toISOString();

      await createTransaction({
        amount: formValues.type === "expense" ? -amount : amount,
        date: dateTime,
        categoryId: category.id,
        partyName: formValues.recipient.trim() || undefined,
        description: formValues.description.trim() || undefined,
      });
      
      setIsModalOpen(false);
      setFormValues(modalFields);
      await loadData();
    } catch (err) {
      console.error("Error creating transaction:", err);
      alert("Failed to create transaction. Please try again.");
    }
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

  const handleFormCategoryChange = (categoryName: string) => {
    setFormValues((prev) => ({ ...prev, category: categoryName }));
  };

  const transactionButtonClass = primaryActionButtonClass;

  if (isLoading) {
    return (
      <div className="min-h-screen bg-beige-100 py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Transactions" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-gray-600">Loading transactions...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-beige-100 py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Transactions" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={loadData} className={primaryActionButtonClass}>
              Try Again
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-beige-100 py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <PageTitle title="Transactions" />
          <Button
            variant="ghost"
            size="sm"
            className={`${transactionButtonClass}`}
            onClick={handleOpenModal}
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
                  <label className="flex flex-col gap-2 text-sm text-slate-600">
                    <span className="text-xs font-semibold uppercase tracking-wide text-(--color-grey-500)">
                      Date
                    </span>
                    <input
                      name="date"
                      value={formValues.date}
                      onChange={handleFormChange}
                      type="date"
                      required
                      className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                    />
                  </label>
                  <div className="grid gap-4 md:grid-cols-2">
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs font-semibold uppercase tracking-wide text-(--color-grey-500)">
                        Category
                      </span>
                      <CustomSelect
                        value={formValues.category || (categories.length > 0 ? categories[0].name : "General")}
                        options={categories.map(c => c.name)}
                        onChange={handleFormCategoryChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Transaction category"
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


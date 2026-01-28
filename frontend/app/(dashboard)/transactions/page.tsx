"use client";

import { useEffect, useMemo, useState } from "react";

import PageTitle from "@/components/PageTitle";
import PaginationControls from "@/components/transactions/PaginationControls";
import TransactionFilters from "@/components/transactions/TransactionFilters";
import TransactionList from "@/components/transactions/TransactionList";
import {
  getTransactionPage,
  transactionCategoryOptions,
  transactionRecords,
  type TransactionSortOption,
} from "@/app/transactions/data";

const PAGE_SIZE = 10;

export default function TransactionsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [category, setCategory] = useState<string>(transactionCategoryOptions[0]);
  const [sortBy, setSortBy] = useState<TransactionSortOption>("latest");
  const [page, setPage] = useState(1);

  const { data, totalItems, totalPages, currentPage } = useMemo(
    () =>
      getTransactionPage(transactionRecords, {
        searchTerm,
        category,
        sortBy,
        page,
        pageSize: PAGE_SIZE,
      }),
    [searchTerm, category, sortBy, page]
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

  return (
    <div className="min-h-screen bg-beige-100 py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <div>
          <PageTitle title="Transactions" />
        </div>

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
      </div>
    </div>
  );
}

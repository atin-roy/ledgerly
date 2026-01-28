"use client";

import { ChangeEvent } from "react";
import { ChevronDown, Search } from "lucide-react";

import {
  transactionCategoryOptions,
  transactionSortOptions,
  type TransactionSortOption,
} from "@/app/transactions/data";

type TransactionFiltersProps = {
  search: string;
  category: string;
  sortBy: TransactionSortOption;
  onSearchChange: (value: string) => void;
  onCategoryChange: (value: string) => void;
  onSortChange: (value: TransactionSortOption) => void;
};

export default function TransactionFilters({
  search,
  category,
  sortBy,
  onSearchChange,
  onCategoryChange,
  onSortChange,
}: TransactionFiltersProps) {
  return (
    <div className="grid gap-4 md:grid-cols-[2fr_1fr_1fr] md:items-end">
      <label className="relative flex flex-col gap-2 text-(--color-grey-600)">
        <span className="text-xs font-semibold uppercase tracking-wide">
          Search
        </span>
        <div className="relative">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-(--color-grey-500)" />
          <input
            type="search"
            value={search}
            onChange={(event: ChangeEvent<HTMLInputElement>) =>
              onSearchChange(event.target.value)
            }
            placeholder="Search transaction"
            className="w-full rounded-2xl border border-grey-300 bg-white py-3 pl-10 pr-4 text-sm font-medium text-(--color-grey-900) placeholder:text-(--color-grey-500) focus:border-(--color-green) focus:outline-none"
          />
        </div>
      </label>

      <label className="flex flex-col gap-2 text-(--color-grey-600)">
        <span className="text-xs font-semibold uppercase tracking-wide">
          Sort by
        </span>
        <div className="relative">
          <select
            value={sortBy}
            onChange={(event) =>
              onSortChange(event.target.value as TransactionSortOption)
            }
            className="w-full appearance-none rounded-2xl border border-grey-300 bg-white py-3 px-4 pr-10 text-sm font-medium text-(--color-grey-900) focus:border-(--color-green) focus:outline-none"
          >
          {transactionSortOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
          </select>
          <ChevronDown className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-(--color-grey-500)" />
        </div>
      </label>

      <label className="flex flex-col gap-2 text-(--color-grey-600)">
        <span className="text-xs font-semibold uppercase tracking-wide">
          Category
        </span>
        <div className="relative">
          <select
            value={category}
            onChange={(event) => onCategoryChange(event.target.value)}
            className="w-full appearance-none rounded-2xl border border-grey-300 bg-white py-3 px-4 pr-10 text-sm font-medium text-(--color-grey-900) focus:border-(--color-green) focus:outline-none"
          >
          {transactionCategoryOptions.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
          </select>
          <ChevronDown className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-(--color-grey-500)" />
        </div>
      </label>
    </div>
  );
}

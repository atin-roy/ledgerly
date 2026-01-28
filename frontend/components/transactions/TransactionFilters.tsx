"use client";

import { ChangeEvent } from "react";
import { ArrowUpDown, ChevronDown, Search, Tag } from "lucide-react";

import CustomSelect from "@/components/ui/CustomSelect";
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

const getSortLabel = (sortBy: TransactionSortOption) =>
  transactionSortOptions.find((opt) => opt.value === sortBy)?.label ?? sortBy;

export default function TransactionFilters({
  search,
  category,
  sortBy,
  onSearchChange,
  onCategoryChange,
  onSortChange,
}: TransactionFiltersProps) {
  const handleSortSelection = (label: string) => {
    const option = transactionSortOptions.find((opt) => opt.label === label);
    if (option) onSortChange(option.value);
  };

  return (
    <>
      <div className="relative md:hidden">
        <div className="flex items-center gap-2">
          <div className="relative flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-(--color-grey-600)" />
            <input
              type="search"
              value={search}
              onChange={(event: ChangeEvent<HTMLInputElement>) =>
                onSearchChange(event.target.value)
              }
              placeholder="Search"
              className="w-full rounded-2xl border border-grey-300 bg-white py-3 pl-10 pr-4 text-sm font-medium text-(--color-grey-900) placeholder:text-(--color-grey-500) focus:border-(--color-green) focus:outline-none"
            />
          </div>

          <div className="relative flex-1 max-w-12">
            <CustomSelect
              value={getSortLabel(sortBy)}
              options={transactionSortOptions.map((opt) => opt.label)}
              onChange={handleSortSelection}
              icon={<ArrowUpDown className="h-4 w-4" />}
              ariaLabel="Sort by"
              isMobile
            />
          </div>

          <div className="relative flex-1 max-w-12">
            <CustomSelect
              value={category}
              options={transactionCategoryOptions}
              onChange={onCategoryChange}
              icon={<Tag className="h-4 w-4" />}
              ariaLabel="Filter By"
              isMobile
            />
          </div>
        </div>
      </div>

      <div className="hidden md:grid md:gap-4 md:grid-cols-[2fr_1fr_1fr] md:items-end">
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
          <CustomSelect
            value={getSortLabel(sortBy)}
            options={transactionSortOptions.map((opt) => opt.label)}
            onChange={handleSortSelection}
            icon={<ChevronDown className="h-4 w-4" />}
            trailingIcon={<ChevronDown className="h-4 w-4" />}
            ariaLabel="Sort by"
          />
        </label>

        <label className="flex flex-col gap-2 text-(--color-grey-600)">
          <span className="text-xs font-semibold uppercase tracking-wide">
            Filter By
          </span>
          <CustomSelect
            value={category}
            options={transactionCategoryOptions}
            onChange={onCategoryChange}
            icon={<ChevronDown className="h-4 w-4" />}
            trailingIcon={<ChevronDown className="h-4 w-4" />}
            ariaLabel="Filter By"
          />
        </label>
      </div>
    </>
  );
}

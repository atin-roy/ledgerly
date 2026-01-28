"use client";

import { ChangeEvent, useState, useRef, useEffect } from "react";
import { ArrowUpDown, ChevronDown, Search, Tag } from "lucide-react";

import {
  transactionCategoryOptions,
  transactionSortOptions,
  type TransactionSortOption,
} from "@/app/transactions/data";

// Custom dropdown component for better styling control
interface CustomSelectProps {
  value: string;
  options: readonly string[];
  onChange: (value: string) => void;
  icon: React.ReactNode;
  ariaLabel: string;
  className?: string;
}

function CustomSelect({ value, options, onChange, icon, ariaLabel, className }: CustomSelectProps) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleSelect = (option: string) => {
    onChange(option);
    setIsOpen(false);
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Escape") {
      setIsOpen(false);
    } else if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setIsOpen(!isOpen);
    } else if (event.key === "ArrowDown" && !isOpen) {
      event.preventDefault();
      setIsOpen(true);
    }
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  return (
    <div ref={dropdownRef} className={`relative ${className || ""}`}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={handleKeyDown}
        className="flex h-12 w-full items-center justify-center rounded-2xl border border-grey-300 bg-white px-4 text-(--color-grey-600) hover:border-(--color-green) focus:border-(--color-green) focus:outline-none md:h-auto md:justify-between md:px-4 md:py-3"
        aria-label={ariaLabel}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
      >
        <span className="md:hidden">{icon}</span>
        <span className="hidden text-sm font-medium text-(--color-grey-900) md:block">
          {value}
        </span>
        <ChevronDown className="hidden h-4 w-4 text-(--color-grey-500) md:block" />
      </button>

      {isOpen && (
        <div className="absolute top-full left-0 right-0 z-50 mt-1 rounded-2xl border border-grey-300 bg-white shadow-lg md:w-full overflow-hidden">
          <ul className="" role="listbox">
            {options.map((option, index) => {
              const isFirst = index === 0;
              const isLast = index === options.length - 1;
              const isSelected = option === value;

              let buttonClasses = "w-full text-left text-sm focus:bg-(--color-grey-100) focus:outline-none ";

              // Add hover effect only for unselected items
              if (!isSelected) {
                buttonClasses += "hover:bg-(--color-grey-100) ";
              }

              // Add vertical padding - first and last items extend to edges when selected
              if (isSelected && (isFirst || isLast)) {
                buttonClasses += "py-2 ";
              } else {
                buttonClasses += "py-2 ";
              }

              // Add horizontal padding - selected items extend to edges
              if (isSelected) {
                buttonClasses += "px-0 ";
              } else {
                buttonClasses += "px-4 ";
              }

              // Add border radius for first and last items
              if (isFirst) {
                buttonClasses += "rounded-t-2xl ";
              }
              if (isLast) {
                buttonClasses += "rounded-b-2xl ";
              }

              // Add selection styling
              if (isSelected) {
                buttonClasses += "bg-(--color-green) text-white ";
              } else {
                buttonClasses += "text-(--color-grey-900) ";
              }

              return (
                <li key={option}>
                  <button
                    type="button"
                    onClick={() => handleSelect(option)}
                    className={buttonClasses.trim()}
                    role="option"
                    aria-selected={isSelected}
                  >
                    <span className={isSelected ? "px-4 block" : ""}>{option}</span>
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
}

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
    <>
      {/* Mobile layout */}
      <div className="flex items-center gap-2 md:hidden">
        <div className="relative flex-1">
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

        <CustomSelect
          value={transactionSortOptions.find(opt => opt.value === sortBy)?.label || sortBy}
          options={transactionSortOptions.map(opt => opt.label)}
          onChange={(label) => {
            const option = transactionSortOptions.find(opt => opt.label === label);
            if (option) onSortChange(option.value);
          }}
          icon={<ArrowUpDown className="h-4 w-4" />}
          ariaLabel="Sort by"
        />

        <CustomSelect
          value={category}
          options={transactionCategoryOptions}
          onChange={onCategoryChange}
          icon={<Tag className="h-4 w-4" />}
          ariaLabel="Category"
        />
      </div>

      {/* Desktop layout */}
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
            value={transactionSortOptions.find(opt => opt.value === sortBy)?.label || sortBy}
            options={transactionSortOptions.map(opt => opt.label)}
            onChange={(label) => {
              const option = transactionSortOptions.find(opt => opt.label === label);
              if (option) onSortChange(option.value);
            }}
            icon={<ChevronDown className="h-4 w-4" />}
            ariaLabel="Sort by"
          />
        </label>

        <label className="flex flex-col gap-2 text-(--color-grey-600)">
          <span className="text-xs font-semibold uppercase tracking-wide">
            Category
          </span>
          <CustomSelect
            value={category}
            options={transactionCategoryOptions}
            onChange={onCategoryChange}
            icon={<ChevronDown className="h-4 w-4" />}
            ariaLabel="Category"
          />
        </label>
      </div>
    </>
  );
}

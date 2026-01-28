"use client";

import {
  formatCurrency,
  formatTransactionDate,
  type Transaction,
} from "@/app/transactions/data";

export const TRANSACTION_ROW_HEIGHT = 64;

type TransactionRowProps = {
  transaction: Transaction;
};

export default function TransactionRow({ transaction }: TransactionRowProps) {
  const isIncome = transaction.type === "income";
  const amountClass = isIncome
    ? "text-[var(--color-green)]"
    : "text-[var(--color-red)]";

  return (
    <div
      className="flex items-center justify-between gap-4 px-4 py-4 md:grid md:grid-cols-[2fr_1fr_1fr_1fr] md:items-center md:gap-3 md:py-0 text-sm text-(--color-grey-900)"
      style={{ minHeight: `${TRANSACTION_ROW_HEIGHT}px` }}
    >
      {/* Mobile layout: recipient and category stacked */}
      <div className="flex flex-col gap-1 md:hidden">
        <span className="font-semibold">{transaction.recipient}</span>
        {transaction.description && (
          <span className="text-xs text-(--color-grey-600) truncate max-w-full">
            {transaction.description}
          </span>
        )}
        <span className="text-sm text-(--color-grey-600)">
          {transaction.category}
        </span>
      </div>

      {/* Mobile layout: amount and date stacked */}
      <div className="flex flex-col items-end gap-1 md:hidden">
        <span className={`text-sm font-semibold ${amountClass}`}>
          {isIncome ? "+" : "-"}
          {formatCurrency(transaction.amount)}
        </span>
        <span className="text-sm text-(--color-grey-600)">
          {formatTransactionDate(transaction.date)}
        </span>
      </div>

      {/* Desktop layout */}
      <div className="hidden flex-col gap-1 md:flex">
        <span className="font-semibold">{transaction.recipient}</span>
        {transaction.description && (
          <span className="text-xs text-(--color-grey-600) truncate max-w-full">
            {transaction.description}
          </span>
        )}
      </div>

      <span className="hidden text-sm text-(--color-grey-600) md:block">
        {transaction.category}
      </span>

      <span className="hidden text-sm text-(--color-grey-600) md:block">
        {formatTransactionDate(transaction.date)}
      </span>

      <span className={`hidden text-sm font-semibold ${amountClass} text-right md:block`}>
        {isIncome ? "+" : "-"}
        {formatCurrency(transaction.amount)}
      </span>
    </div>
  );
}

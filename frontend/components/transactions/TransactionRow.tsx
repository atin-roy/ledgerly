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
      className="grid grid-cols-[2fr_1fr_1fr_1fr] items-center gap-3 px-4 text-sm text-(--color-grey-900)"
      style={{ height: `${TRANSACTION_ROW_HEIGHT}px` }}
    >
      <span className="font-semibold">{transaction.recipient}</span>

      <span className="text-sm text-(--color-grey-600)">
        {transaction.category}
      </span>

      <span className="text-sm text-(--color-grey-600)">
        {formatTransactionDate(transaction.date)}
      </span>

      <span className={`text-sm font-semibold ${amountClass} text-right`}>
        {isIncome ? "+" : "-"}
        {formatCurrency(transaction.amount)}
      </span>
    </div>
  );
}

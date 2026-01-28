"use client";

import { type Transaction } from "@/app/transactions/data";

import TransactionRow, {
  TRANSACTION_ROW_HEIGHT,
} from "@/components/transactions/TransactionRow";

type TransactionListProps = {
  transactions: Transaction[];
  pageSize: number;
};

export default function TransactionList({
  transactions,
  pageSize,
}: TransactionListProps) {
  const minBodyHeight = pageSize * TRANSACTION_ROW_HEIGHT;
  const hasTransactions = transactions.length > 0;
  const emptyRowsCount = Math.max(pageSize - transactions.length, 0);

  return (
    <div className="overflow-hidden rounded-2xl border border-(--grey-200) bg-white shadow-sm">
      <div className="grid grid-cols-[2fr_1fr_1fr_1fr] gap-3 border-b border-grey-100 px-4 py-3 text-xs font-semibold uppercase tracking-widest text-(--color-grey-600)">
        <span>Recipient / Sender</span>
        <span className="text-left">Category</span>
        <span className="text-left">Transaction Date</span>
        <span className="text-right">Amount</span>
      </div>
      <div
        className="divide-y divide-grey-100"
        style={{ minHeight: `${minBodyHeight}px` }}
      >
        {hasTransactions ? (
          <>
            {transactions.map((transaction) => (
              <TransactionRow key={transaction.id} transaction={transaction} />
            ))}
            {emptyRowsCount > 0 &&
              Array.from({ length: emptyRowsCount }).map((_, index) => (
                <div
                  key={`empty-row-${index}`}
                  className="px-4"
                  aria-hidden="true"
                  style={{ height: `${TRANSACTION_ROW_HEIGHT}px` }}
                />
              ))}
          </>
        ) : (
          <div className="flex h-full items-center justify-center px-4 text-center text-sm text-(--color-grey-500)">
            No transactions match the selected filters.
          </div>
        )}
      </div>
    </div>
  );
}

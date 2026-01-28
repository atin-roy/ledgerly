import Link from "next/link";
import { Button } from "@/components/ui/button";
import actionButtonClass from "@/components/ui/actionButtonClass";
import {
  formatCurrency as formatBillCurrency,
  formatBillDueDate,
  billRecords,
  BillStatus,
} from "@/app/bills/data";
import {
  baseCategories,
  formatCurrency as formatTransactionCurrency,
  formatTransactionDate,
  transactionRecords,
  TransactionCategory,
  TransactionType,
} from "@/app/transactions/data";
import { budgets, formatBudgetCurrency } from "@/app/budgets/data";
import { formatCurrency as formatPotCurrency, formatPercentage, pots } from "@/app/pots/data";

const statusStyles: Record<BillStatus, string> = {
  pending: "bg-amber-50 text-amber-600",
  paid: "bg-emerald-50 text-emerald-600",
  overdue: "bg-red-50 text-red-600",
  cancelled: "bg-slate-100 text-slate-500",
};

type CategorySlice = {
  label: TransactionCategory;
  value: number;
};

function buildCategoryTotals(type: TransactionType): CategorySlice[] {
  const totals: Record<TransactionCategory, number> = Object.fromEntries(
    baseCategories.map((category) => [category, 0]),
  ) as Record<TransactionCategory, number>;

  transactionRecords.forEach((transaction) => {
    if (transaction.type !== type) {
      return;
    }
    totals[transaction.category] += transaction.amount;
  });

  return baseCategories
    .map((category) => ({ label: category, value: totals[category] }))
    .filter((entry) => entry.value > 0)
    .sort((a, b) => b.value - a.value);
}

export default function OverviewPage() {
  const incomeTotal = transactionRecords
    .filter((transaction) => transaction.type === "income")
    .reduce((total, transaction) => total + transaction.amount, 0);

  const expenseTotal = transactionRecords
    .filter((transaction) => transaction.type === "expense")
    .reduce((total, transaction) => total + transaction.amount, 0);

  const currentBalance = incomeTotal - expenseTotal;

  const recentTransactions = [...transactionRecords]
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 3);

  const upcomingBills = [...billRecords]
    .sort((a, b) => new Date(a.nextDue).getTime() - new Date(b.nextDue).getTime())
    .slice(0, 3);

  const budgetsPreview = budgets.slice(0, 3);
  const potsPreview = pots.slice(0, 3);

  const expenseCategoryBreakdown = buildCategoryTotals("expense").map((slice, idx) => ({
    ...slice,
    color: "var(--color-red)",
  }));

  const incomeCategoryBreakdown = buildCategoryTotals("income").map((slice, idx) => ({
    ...slice,
    color: "var(--color-green)",
  }));

  const incomeSourcesTotal = incomeCategoryBreakdown.reduce(
    (total, slice) => total + slice.value,
    0,
  );
  const expenseSourcesTotal = expenseCategoryBreakdown.reduce(
    (total, slice) => total + slice.value,
    0,
  );

  return (
    <main className="space-y-10 text-(--color-grey-900) pb-12">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.5em] text-(--color-grey-600)">
            Personal snapshot
          </p>
          <h1 className="text-3xl font-semibold text-(--color-grey-900)">Dashboard</h1>
        </div>
      </header>

      <section className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.4em] text-(--color-grey-600)">
            Balance
          </p>
          <p className="mt-2 text-3xl font-semibold text-(--color-grey-900)">
            {formatTransactionCurrency(currentBalance)}
          </p>
          <p className="mt-3 text-sm text-(--color-grey-600)">
            Balance reflects the last 30 transactions.
          </p>
        </div>

        <div className="rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.4em] text-(--color-grey-600)">Income</p>
          <p className="mt-2 text-3xl font-semibold text-(--color-green)">
            {formatTransactionCurrency(incomeTotal)}
          </p>
          <p className="mt-3 text-sm text-(--color-grey-600)">
            Total credited amounts over the full history.
          </p>
        </div>

        <div className="rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.4em] text-(--color-grey-600)">
            Expenses
          </p>
          <p className="mt-2 text-3xl font-semibold text-(--color-red)">
            {formatTransactionCurrency(expenseTotal)}
          </p>
          <p className="mt-3 text-sm text-(--color-grey-600)">
            Sum of every documented payment.
          </p>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-[2fr_1fr]">
        <div className="space-y-4 rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p className="text-xs uppercase tracking-[0.4em] text-(--color-grey-600)">
                Transactions
              </p>
              <h2 className="text-xl font-semibold text-(--color-grey-900)">Recent transactions</h2>
            </div>
            <Button variant="ghost" size="sm" className={actionButtonClass} asChild>
              <Link href="/transactions">New transaction</Link>
            </Button>
          </div>
          <div className="divide-y border-t border-[#f0e8df]">
            {recentTransactions.map((transaction) => {
              const isIncome = transaction.type === "income";
              const amountClass = isIncome ? "text-(--color-green)" : "text-(--color-red)";
              return (
                <div
                  key={transaction.id}
                  className="grid gap-2 py-4 md:grid-cols-[2fr_1fr_1fr_1fr] md:items-center"
                >
                  <div>
                    <p className="text-sm font-semibold text-(--color-grey-900)">
                      {transaction.recipient}
                    </p>
                    {transaction.description && (
                      <p className="text-xs text-(--color-grey-600)">{transaction.description}</p>
                    )}
                  </div>
                  <p className="text-xs text-(--color-grey-600) text-left">{transaction.category}</p>
                  <p className="text-xs text-(--color-grey-600) md:text-right">
                    {formatTransactionDate(transaction.date)}
                  </p>
                  <p className={`text-sm font-semibold ${amountClass} md:text-base md:text-right`}>
                    {isIncome ? "+" : "-"}
                    {formatTransactionCurrency(transaction.amount)}
                  </p>
                </div>
              );
            })}
          </div>
        </div>

        <div className="space-y-4 rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.4em] text-(--color-grey-600)">
                Budgets
              </p>
              <h2 className="text-xl font-semibold text-(--color-grey-900)">Compact view</h2>
            </div>
            <Button variant="ghost" size="sm" className={actionButtonClass} asChild>
              <Link href="/budgets">New budget</Link>
            </Button>
          </div>
          <div className="space-y-4">
            {budgetsPreview.map((budget) => {
              const progress = budget.limit ? (budget.spent / budget.limit) * 100 : 0;
              return (
                <div key={budget.id} className="space-y-1">
                  <div className="flex items-center justify-between text-sm font-semibold text-(--color-grey-900)">
                    <p>{budget.name}</p>
                    <p>
                      {formatBudgetCurrency(budget.spent)} / {formatBudgetCurrency(budget.limit)}
                    </p>
                  </div>
                  <div className="h-1.5 rounded-full bg-[#ede8e1]">
                    <div
                      className="h-full rounded-full"
                      style={{
                        width: `${Math.min(progress, 100)}%`,
                        backgroundColor: budget.color,
                      }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-2">
        <div className="space-y-4 rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.4em] text-(--color-grey-600)">
                Upcoming bills
              </p>
              <h2 className="text-xl font-semibold text-(--color-grey-900)">Next 3</h2>
            </div>
            <Button variant="ghost" size="sm" className={actionButtonClass} asChild>
              <Link href="/bills">New bill</Link>
            </Button>
          </div>
          <div className="space-y-3 rounded-2xl border border-[#f5efe7] bg-beige-100 p-4">
            {upcomingBills.map((bill) => (
              <div key={bill.id} className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-(--color-grey-900)">{bill.title}</p>
                  <p className="text-xs text-(--color-grey-600)">{bill.dueLabel}</p>
                </div>
                <div className="flex flex-col items-end">
                  <p className="text-sm font-semibold text-(--color-grey-900)">
                    {formatBillCurrency(bill.amount)}
                  </p>
                  <p className="text-xs text-(--color-grey-600)">
                    {formatBillDueDate(bill.nextDue)}
                  </p>
                  <span
                    className={`mt-1 rounded-full px-2 py-1 text-[0.6rem] font-semibold ${statusStyles[bill.status]}`}
                  >
                    {bill.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-4 rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.4em] text-(--color-grey-600)">Pots</p>
              <h2 className="text-xl font-semibold text-(--color-grey-900)">Aligned goals</h2>
            </div>
            <Button variant="ghost" size="sm" className={actionButtonClass} asChild>
              <Link href="/pots">New pot</Link>
            </Button>
          </div>
          <div className="space-y-4">
            {potsPreview.map((pot) => {
              const progress = pot.target ? (pot.saved / pot.target) * 100 : 0;
              return (
                <div key={pot.id} className="space-y-1">
                  <div className="flex items-center justify-between text-sm font-semibold text-(--color-grey-900)">
                    <p>{pot.name}</p>
                    <p>{formatPotCurrency(pot.saved)}</p>
                  </div>
                  <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
                    Saved of {formatPotCurrency(pot.target)} · {formatPercentage(progress)}%
                  </p>
                  <div className="h-1.5 rounded-full bg-[#ede8e1]">
                    <div
                      className="h-full rounded-full"
                      style={{
                        width: `${Math.min(progress, 100)}%`,
                        backgroundColor: pot.accentColor,
                      }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="rounded-2xl border border-[#eee7de] bg-white p-6 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.4em] text-(--color-grey-600)">
              Sources
            </p>
            <h2 className="text-xl font-semibold text-(--color-grey-900)">
              Income & expense sources
            </h2>
          </div>
        </div>
        <p className="mt-2 text-sm text-(--color-grey-600)">
          All time breakdown by category.
        </p>
        <div className="mt-6 grid gap-6 md:grid-cols-2">
          <div className="space-y-3 rounded-2xl border border-[#f0e8df] bg-beige-100 p-4">
            <div className="flex items-center justify-between text-sm font-semibold text-(--color-grey-900)">
              <p>Income</p>
              <p>{formatTransactionCurrency(incomeSourcesTotal)}</p>
            </div>
            <div className="space-y-2">
              {incomeCategoryBreakdown.length ? (
                incomeCategoryBreakdown.map((slice) => (
                  <div key={slice.label} className="flex items-center justify-between text-sm text-(--color-grey-600)">
                    <p>{slice.label}</p>
                    <p className="font-semibold text-(--color-grey-900)">
                      {formatTransactionCurrency(slice.value)}
                    </p>
                  </div>
                ))
              ) : (
                <p className="text-xs text-(--color-grey-500)">No income sources yet.</p>
              )}
            </div>
          </div>

          <div className="space-y-3 rounded-2xl border border-[#f0e8df] bg-beige-100 p-4">
            <div className="flex items-center justify-between text-sm font-semibold text-(--color-grey-900)">
              <p>Expenses</p>
              <p>{formatTransactionCurrency(expenseSourcesTotal)}</p>
            </div>
            <div className="space-y-2">
              {expenseCategoryBreakdown.length ? (
                expenseCategoryBreakdown.map((slice) => (
                  <div key={slice.label} className="flex items-center justify-between text-sm text-(--color-grey-600)">
                    <p>{slice.label}</p>
                    <p className="font-semibold text-(--color-grey-900)">
                      {formatTransactionCurrency(slice.value)}
                    </p>
                  </div>
                ))
              ) : (
                <p className="text-xs text-(--color-grey-500)">No expense sources yet.</p>
              )}
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}

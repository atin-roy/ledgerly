"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import actionButtonClass from "@/components/ui/actionButtonClass";
import {
  formatCurrency as formatBillCurrency,
  formatBillDueDate,
  BillStatus,
  type Bill,
} from "@/app/bills/data";
import {
  baseCategories,
  formatCurrency as formatTransactionCurrency,
  formatTransactionDate,
  TransactionCategory,
  TransactionType,
  type Transaction,
} from "@/app/transactions/data";
import { formatBudgetCurrency } from "@/app/budgets/data";
import { formatCurrency as formatPotCurrency, formatPercentage } from "@/app/pots/data";
import { getTransactions, getBudgets, getPots, getBills, getCategories, type TransactionResponse, type BudgetResponse, type PotResponse, type BillResponse, type CategoryResponse } from "@/lib/services";

const statusStyles: Record<BillStatus, string> = {
  pending: "bg-amber-50 text-amber-600",
  paid: "bg-emerald-50 text-emerald-600",
  overdue: "bg-red-50 text-red-600",
  cancelled: "bg-slate-100 text-slate-500",
};

type CategorySlice = {
  label: string;
  value: number;
};

export default function OverviewPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [budgets, setBudgets] = useState<BudgetResponse[]>([]);
  const [pots, setPots] = useState<PotResponse[]>([]);
  const [bills, setBills] = useState<Bill[]>([]);
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
      const [transactionsData, budgetsData, potsData, billsData, categoriesData] = await Promise.all([
        getTransactions(),
        getBudgets(),
        getPots(),
        getBills(),
        getCategories(),
      ]);
      
      // Convert transactions
      const convertedTransactions: Transaction[] = transactionsData.map((t) => ({
        id: t.id.toString(),
        recipient: t.partyName || "Unknown",
        description: "",
        category: t.categoryName as any,
        date: t.date.split("T")[0],
        amount: Math.abs(t.amount),
        type: t.amount >= 0 ? "income" : "expense",
        badgeColor: "var(--color-green)",
      }));
      
      // Convert bills
      const convertedBills: Bill[] = billsData.map((b) => ({
        id: b.id.toString(),
        title: b.name,
        dueLabel: "Monthly",
        nextDue: b.dueDate.split("T")[0],
        amount: b.amount,
        status: b.status.toLowerCase() as any,
        iconLabel: b.name.substring(0, 2).toUpperCase(),
        iconColor: "var(--color-green)",
      }));
      
      setTransactions(convertedTransactions);
      setBudgets(budgetsData);
      setPots(potsData);
      setBills(convertedBills);
      setCategories(categoriesData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load data");
      console.error("Error loading data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
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
        <div className="rounded-2xl border border-[#eee7de] bg-white p-12 shadow-sm text-center">
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </main>
    );
  }

  if (error) {
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
        <div className="rounded-2xl border border-[#eee7de] bg-white p-12 shadow-sm text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <Button onClick={loadData}>Try Again</Button>
        </div>
      </main>
    );
  }

  const incomeTotal = transactions
    .filter((transaction) => transaction.type === "income")
    .reduce((total, transaction) => total + transaction.amount, 0);

  const expenseTotal = transactions
    .filter((transaction) => transaction.type === "expense")
    .reduce((total, transaction) => total + transaction.amount, 0);

  const currentBalance = incomeTotal - expenseTotal;

  const recentTransactions = [...transactions]
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 3);

  const upcomingBills = [...bills]
    .sort((a, b) => new Date(a.nextDue).getTime() - new Date(b.nextDue).getTime())
    .slice(0, 3);

  const budgetsPreview = budgets.slice(0, 3);
  const potsPreview = pots.slice(0, 3);

  const expenseCategoryBreakdown: CategorySlice[] = [];
  const incomeCategoryBreakdown: CategorySlice[] = [];
  
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
            {budgetsPreview.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-4">No budgets yet</p>
            ) : (
              budgetsPreview.map((budget) => {
                const category = categories.find(c => c.id === budget.categoryId);
                const progress = 0; // We'll calculate this later from transactions
                return (
                  <div key={budget.id} className="space-y-1">
                    <div className="flex items-center justify-between text-sm font-semibold text-(--color-grey-900)">
                      <p>{category?.name || "Unknown"}</p>
                      <p>
                        {formatBudgetCurrency(0)} / {formatBudgetCurrency(budget.amount)}
                      </p>
                    </div>
                    <div className="h-1.5 rounded-full bg-[#ede8e1]">
                      <div
                        className="h-full rounded-full"
                        style={{
                          width: `${Math.min(progress, 100)}%`,
                          backgroundColor: "var(--color-green)",
                        }}
                      />
                    </div>
                  </div>
                );
              })
            )}
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
            {potsPreview.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-4">No pots yet</p>
            ) : (
              potsPreview.map((pot) => {
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
                          backgroundColor: "var(--color-green)",
                        }}
                      />
                    </div>
                  </div>
                );
              })
            )}
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

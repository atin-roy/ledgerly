"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  formatCurrency as formatBillCurrency,
  formatBillDueDate,
  BillStatus,
  type Bill,
} from "@/app/bills/data";
import {
  formatCurrency as formatTransactionCurrency,
  formatTransactionDate,
  type Transaction,
} from "@/app/transactions/data";
import { formatBudgetCurrency } from "@/app/budgets/data";
import {
  formatCurrency as formatPotCurrency,
  formatPercentage,
} from "@/app/pots/data";
import {
  getTransactions,
  getBudgets,
  getPots,
  getBills,
  getCategories,
  type BudgetResponse,
  type PotResponse,
  type CategoryResponse,
} from "@/lib/services";
import SealStamp from "@/components/brand/SealStamp";
import styles from "./overview.module.css";

const statusClass: Record<BillStatus, string> = {
  pending: styles.statusPending,
  paid: styles.statusPaid,
  overdue: styles.statusOverdue,
  cancelled: styles.statusCancelled,
};

function normalizeBillStatus(status: string): BillStatus {
  const normalized = status.toLowerCase();
  if (
    normalized === "pending" ||
    normalized === "paid" ||
    normalized === "overdue" ||
    normalized === "cancelled"
  ) {
    return normalized;
  }
  return "pending";
}

type CategorySlice = { label: string; value: number };

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

      const [transactionsData, budgetsData, potsData, billsData, categoriesData] =
        await Promise.all([
          getTransactions(),
          getBudgets(),
          getPots(),
          getBills(),
          getCategories(),
        ]);

      const transactionsArray = Array.isArray(transactionsData)
        ? transactionsData
        : [];
      const budgetsArray = Array.isArray(budgetsData) ? budgetsData : [];
      const potsArray = Array.isArray(potsData) ? potsData : [];
      const billsArray = Array.isArray(billsData) ? billsData : [];
      const categoriesArray = Array.isArray(categoriesData) ? categoriesData : [];

      const convertedTransactions: Transaction[] = transactionsArray.map((t) => ({
        id: t.id.toString(),
        recipient: t.partyName || "Unknown",
        description: "",
        category: t.categoryName,
        categoryId: t.categoryId,
        date: t.date.split("T")[0],
        amount: Math.abs(t.amount),
        type: t.amount >= 0 ? "income" : "expense",
      }));

      const convertedBills: Bill[] = billsArray.map((b) => ({
        id: b.id.toString(),
        title: b.name,
        dueLabel: "Monthly",
        nextDue: b.dueDate.split("T")[0],
        amount: b.amount,
        status: normalizeBillStatus(b.status),
        iconLabel: b.name.substring(0, 2).toUpperCase(),
        iconColor: "var(--brass)",
      }));

      setTransactions(convertedTransactions);
      setBudgets(budgetsArray);
      setPots(potsArray);
      setBills(convertedBills);
      setCategories(categoriesArray);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load data");
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className={styles.page}>
        <p className={styles.eyebrow}>Statement</p>
        <div className={styles.state}>Balancing the books…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        <p className={styles.eyebrow}>Statement</p>
        <div className={styles.state}>
          <p>{error}</p>
          <button type="button" onClick={loadData} className={styles.retry}>
            Try again
          </button>
        </div>
      </div>
    );
  }

  const incomeTotal = transactions
    .filter((t) => t.type === "income")
    .reduce((sum, t) => sum + t.amount, 0);
  const expenseTotal = transactions
    .filter((t) => t.type === "expense")
    .reduce((sum, t) => sum + t.amount, 0);
  const currentBalance = incomeTotal - expenseTotal;

  const recentTransactions = [...transactions]
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 5);

  const upcomingBills = [...bills]
    .sort((a, b) => new Date(a.nextDue).getTime() - new Date(b.nextDue).getTime())
    .slice(0, 3);

  const budgetsPreview = budgets.slice(0, 3);
  const potsPreview = pots.slice(0, 3);

  const categoryTotals = new Map<string, { income: number; expense: number }>();
  transactions.forEach((t) => {
    const existing = categoryTotals.get(t.category) || { income: 0, expense: 0 };
    if (t.type === "income") existing.income += t.amount;
    else existing.expense += t.amount;
    categoryTotals.set(t.category, existing);
  });

  const incomeCategoryBreakdown: CategorySlice[] = [];
  const expenseCategoryBreakdown: CategorySlice[] = [];
  categoryTotals.forEach((totals, category) => {
    if (totals.income > 0)
      incomeCategoryBreakdown.push({ label: category, value: totals.income });
    if (totals.expense > 0)
      expenseCategoryBreakdown.push({ label: category, value: totals.expense });
  });
  incomeCategoryBreakdown.sort((a, b) => b.value - a.value);
  expenseCategoryBreakdown.sort((a, b) => b.value - a.value);

  return (
    <div className={styles.page}>
      {/* --- statement hero --- */}
      <section className={styles.hero}>
        <div>
          <p className={styles.eyebrow}>Statement</p>
          <div className={styles.balance}>
            {formatTransactionCurrency(currentBalance)}
          </div>
          <p className={styles.balanceSub}>
            Current balance across all recorded activity.
          </p>
          <div className={styles.dc}>
            <div className={styles.dcCell}>
              <div className={styles.dcK}>Money out</div>
              <div className={`${styles.dcV} ${styles.debit}`}>
                {formatTransactionCurrency(expenseTotal)}
              </div>
            </div>
            <div className={styles.dcCell}>
              <div className={styles.dcK}>Money in</div>
              <div className={`${styles.dcV} ${styles.credit}`}>
                {formatTransactionCurrency(incomeTotal)}
              </div>
            </div>
            <div className={styles.dcCell}>
              <div className={styles.dcK}>Net</div>
              <div className={styles.dcV}>
                {currentBalance >= 0 ? "+" : ""}
                {formatTransactionCurrency(currentBalance)}
              </div>
            </div>
          </div>
        </div>
        <SealStamp className={styles.seal} />
      </section>

      {/* --- recent transactions ledger sheet --- */}
      <section className={styles.sheet}>
        <div className={styles.sheetHd}>
          <h2 className={styles.sheetTitle}>Recent entries</h2>
          <Link href="/transactions" className={styles.sheetLink}>
            Open ledger
          </Link>
        </div>
        {recentTransactions.length === 0 ? (
          <p className={styles.emptyPaper}>No entries recorded yet.</p>
        ) : (
          <table className={styles.ledger}>
            <thead>
              <tr>
                <th className={`${styles.th} ${styles.thLeft}`}>Date</th>
                <th className={`${styles.th} ${styles.thLeft}`}>Account</th>
                <th className={styles.th}>Debit</th>
                <th className={styles.th}>Credit</th>
              </tr>
            </thead>
            <tbody>
              {recentTransactions.map((t) => {
                const isIncome = t.type === "income";
                return (
                  <tr key={t.id} className={styles.tr}>
                    <td className={`${styles.td} ${styles.tdLeft} ${styles.date}`}>
                      {formatTransactionDate(t.date)}
                    </td>
                    <td className={`${styles.td} ${styles.tdLeft}`}>
                      <span className={styles.acct}>{t.recipient}</span>
                    </td>
                    <td className={`${styles.td} ${styles.debitC}`}>
                      {isIncome ? "—" : formatTransactionCurrency(t.amount)}
                    </td>
                    <td className={`${styles.td} ${styles.creditC}`}>
                      {isIncome ? formatTransactionCurrency(t.amount) : "—"}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </section>

      {/* --- budgets / pots / bills panels --- */}
      <section className={styles.cols}>
        {/* Budgets */}
        <div className={styles.panel}>
          <div className={styles.panelHd}>
            <h3 className={styles.panelTitle}>Budgets</h3>
            <Link href="/budgets" className={styles.panelLink}>
              View all
            </Link>
          </div>
          {budgetsPreview.length === 0 ? (
            <p className={styles.empty}>No budgets yet</p>
          ) : (
            budgetsPreview.map((budget) => {
              const category = categories.find((c) => c.id === budget.categoryId);
              const spent = budget.spent ?? 0;
              const progress =
                budget.amount > 0 ? (spent / budget.amount) * 100 : 0;
              const over = spent > budget.amount;
              return (
                <div key={budget.id} className={styles.row}>
                  <div className={styles.rowLabel}>
                    <b>{category?.name || "Unknown"}</b>
                    <small>
                      {formatBudgetCurrency(spent)} of{" "}
                      {formatBudgetCurrency(budget.amount)}
                      {over ? " · over" : ""}
                    </small>
                  </div>
                  <span
                    className={`${styles.meter} ${over ? styles.meterOver : ""}`}
                  >
                    <i
                      className={styles.meterFill}
                      style={{ width: `${Math.min(progress, 100)}%` }}
                    />
                  </span>
                </div>
              );
            })
          )}
        </div>

        {/* Pots */}
        <div className={styles.panel}>
          <div className={styles.panelHd}>
            <h3 className={styles.panelTitle}>Pots</h3>
            <Link href="/pots" className={styles.panelLink}>
              View all
            </Link>
          </div>
          {potsPreview.length === 0 ? (
            <p className={styles.empty}>No pots yet</p>
          ) : (
            potsPreview.map((pot) => {
              const progress = pot.target ? (pot.saved / pot.target) * 100 : 0;
              return (
                <div key={pot.id} className={styles.row}>
                  <div className={styles.rowLabel}>
                    <b>{pot.name}</b>
                    <small>goal {formatPotCurrency(pot.target)}</small>
                  </div>
                  <div className={styles.amt}>
                    {formatPotCurrency(pot.saved)}
                    <span className={styles.amtDim}>
                      {" "}
                      · {formatPercentage(progress)}%
                    </span>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Bills */}
        <div className={styles.panel}>
          <div className={styles.panelHd}>
            <h3 className={styles.panelTitle}>Bills</h3>
            <Link href="/bills" className={styles.panelLink}>
              View all
            </Link>
          </div>
          {upcomingBills.length === 0 ? (
            <p className={styles.empty}>No bills yet</p>
          ) : (
            upcomingBills.map((bill) => (
              <div key={bill.id} className={styles.row}>
                <div className={styles.rowLabel}>
                  <b>{bill.title}</b>
                  <small>{formatBillDueDate(bill.nextDue)}</small>
                  <span
                    className={`${styles.status} ${statusClass[bill.status]}`}
                  >
                    {bill.status}
                  </span>
                </div>
                <div className={styles.amt}>
                  {formatBillCurrency(bill.amount)}
                </div>
              </div>
            ))
          )}
        </div>
      </section>

      {/* --- sources --- */}
      <section className={styles.sources}>
        <p className={styles.sectionEyebrow}>Sources</p>
        <h2 className={styles.sectionTitle}>Income &amp; expenses by category</h2>
        <p className={styles.sourcesSub}>All-time breakdown.</p>
        <div className={styles.sourcesGrid}>
          <div className={styles.panel}>
            <div className={styles.sourceTotal}>
              <span>Income</span>
              <span className={styles.amt}>
                {formatTransactionCurrency(incomeTotal)}
              </span>
            </div>
            {incomeCategoryBreakdown.length ? (
              incomeCategoryBreakdown.map((slice) => (
                <div key={slice.label} className={styles.row}>
                  <div className={styles.rowLabel}>
                    <b>{slice.label}</b>
                  </div>
                  <div className={styles.amt}>
                    {formatTransactionCurrency(slice.value)}
                  </div>
                </div>
              ))
            ) : (
              <p className={styles.empty}>No income yet.</p>
            )}
          </div>

          <div className={styles.panel}>
            <div className={styles.sourceTotal}>
              <span>Expenses</span>
              <span className={styles.amt}>
                {formatTransactionCurrency(expenseTotal)}
              </span>
            </div>
            {expenseCategoryBreakdown.length ? (
              expenseCategoryBreakdown.map((slice) => (
                <div key={slice.label} className={styles.row}>
                  <div className={styles.rowLabel}>
                    <b>{slice.label}</b>
                  </div>
                  <div className={styles.amt}>
                    {formatTransactionCurrency(slice.value)}
                  </div>
                </div>
              ))
            ) : (
              <p className={styles.empty}>No expenses yet.</p>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}

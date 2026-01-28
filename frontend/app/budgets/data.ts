export interface BudgetSummaryBreakdown {
  label: string;
  spent: number;
  limit: number;
  color: string;
}

export interface BudgetSummaryData {
  title: string;
  description: string;
  totalLimit: number;
  totalSpent: number;
  totalRemaining: number;
  breakdown: BudgetSummaryBreakdown[];
}

export interface BudgetRecentSpending {
  id: string;
  label: string;
  amount: number;
  date: string; // ISO
}

export interface BudgetData {
  id: string;
  name: string;
  limit: number;
  spent: number;
  color: string;
  recentSpending: BudgetRecentSpending[];
}

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
});

export function formatBudgetCurrency(value: number) {
  return currencyFormatter.format(value);
}

const dateOptions: Intl.DateTimeFormatOptions = {
  month: "short",
  day: "2-digit",
};

export function formatBudgetDate(date: string) {
  return new Date(date).toLocaleDateString("en-US", dateOptions);
}

export const budgetSummary: BudgetSummaryData = {
  title: "Budget Summary",
  description:
    "Track your allowances in one place and make sure the right categories stay on track.",
  totalLimit: 1150,
  totalSpent: 612.5,
  totalRemaining: 537.5,
  breakdown: [
    { label: "Dining Out", spent: 133.75, limit: 150, color: "var(--color-orange)" },
    { label: "Entertainment", spent: 65, limit: 250, color: "var(--color-purple)" },
    { label: "Bills", spent: 315, limit: 600, color: "var(--color-cyan)" },
    { label: "Personal Care", spent: 98.75, limit: 150, color: "var(--color-purple-soft)" },
  ],
};

export const budgets: BudgetData[] = [
  {
    id: "budget-entertainment",
    name: "Entertainment",
    limit: 250,
    spent: 65,
    color: "var(--color-purple)",
    recentSpending: [
      { id: "ent-1", label: "Movie tickets", amount: 23, date: "2024-08-11" },
      { id: "ent-2", label: "Arcade night", amount: 32, date: "2024-08-07" },
      { id: "ent-3", label: "Concert stream", amount: 10, date: "2024-08-03" },
    ],
  },
  {
    id: "budget-bills",
    name: "Bills",
    limit: 600,
    spent: 315,
    color: "var(--color-cyan)",
    recentSpending: [
      { id: "bill-1", label: "Utilities", amount: 90.25, date: "2024-08-05" },
      { id: "bill-2", label: "Spark Electric", amount: 100, date: "2024-08-02" },
      { id: "bill-3", label: "Water services", amount: 51.4, date: "2024-07-29" },
    ],
  },
  {
    id: "budget-dining",
    name: "Dining Out",
    limit: 150,
    spent: 133.75,
    color: "var(--color-orange)",
    recentSpending: [
      { id: "dine-1", label: "Savory Bites Bistro", amount: 55.5, date: "2024-08-19" },
      { id: "dine-2", label: "Lunch w/ team", amount: 32.5, date: "2024-08-13" },
      { id: "dine-3", label: "Cafe catch-up", amount: 45.75, date: "2024-08-10" },
    ],
  },
  {
    id: "budget-care",
    name: "Personal Care",
    limit: 150,
    spent: 98.75,
    color: "var(--color-purple-soft)",
    recentSpending: [
      { id: "care-1", label: "Wellness package", amount: 30, date: "2024-08-03" },
      { id: "care-2", label: "Skin essentials", amount: 28.75, date: "2024-07-28" },
      { id: "care-3", label: "Massage", amount: 40, date: "2024-07-15" },
    ],
  },
];

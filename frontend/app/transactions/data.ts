export type TransactionType = "income" | "expense";

export type TransactionSortOption =
  | "latest"
  | "oldest"
  | "amount-high-to-low"
  | "amount-low-to-high";

export const transactionSortOptions: {
  value: TransactionSortOption;
  label: string;
}[] = [
  { value: "latest", label: "Latest" },
  { value: "oldest", label: "Oldest" },
  { value: "amount-high-to-low", label: "Amount (High to Low)" },
  { value: "amount-low-to-high", label: "Amount (Low to High)" },
];

export interface Transaction {
  id: string;
  recipient: string;
  description?: string;
  category: string;
  categoryId: number;
  date: string; // ISO date
  amount: number;
  type: TransactionType;
}

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
});

export function formatCurrency(value: number) {
  return currencyFormatter.format(value);
}

const dateOptions: Intl.DateTimeFormatOptions = {
  day: "2-digit",
  month: "short",
  year: "numeric",
};

export function formatTransactionDate(date: string) {
  return new Date(date).toLocaleDateString("en-US", dateOptions);
}

/** Today as yyyy-mm-dd in the user's own timezone, not UTC. */
export function localToday() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

const monthLabelFormatter = new Intl.DateTimeFormat("en-US", {
  month: "long",
  year: "numeric",
});

/** Distinct months present in the records, newest first, for the filter. */
export function getMonthOptions(records: Transaction[]) {
  const months = [...new Set(records.map((tx) => tx.date.slice(0, 7)))].sort(
    (a, b) => b.localeCompare(a),
  );
  return [
    { value: "all", label: "All months" },
    ...months.map((m) => ({
      value: m,
      label: monthLabelFormatter.format(new Date(`${m}-01T00:00:00`)),
    })),
  ];
}

export interface TransactionQuery {
  searchTerm?: string;
  category?: string;
  month?: string; // "all" or "yyyy-MM"
  sortBy?: TransactionSortOption;
  page?: number;
  pageSize?: number;
}

export interface TransactionPage {
  data: Transaction[];
  totalPages: number;
  totalItems: number;
  currentPage: number;
}

function applySort(transactions: Transaction[], sortBy: TransactionSortOption) {
  const sorted = [...transactions];

  switch (sortBy) {
    case "oldest":
      sorted.sort(
        (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime(),
      );
      break;
    case "amount-high-to-low":
      sorted.sort((a, b) => b.amount - a.amount);
      break;
    case "amount-low-to-high":
      sorted.sort((a, b) => a.amount - b.amount);
      break;
    default:
      sorted.sort(
        (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
      );
  }

  return sorted;
}

export function getTransactionPage(
  records: Transaction[],
  {
    searchTerm = "",
    category = "All Transactions",
    month = "all",
    sortBy = "latest",
    page = 1,
    pageSize = 6,
  }: TransactionQuery = {},
): TransactionPage {
  const normalizedSearch = searchTerm.trim().toLowerCase();

  const filtered = records
    .filter((tx) => {
      if (category === "Income" && tx.type !== "income") {
        return false;
      }
      if (category === "Expense" && tx.type !== "expense") {
        return false;
      }
      if (category !== "All Transactions" && category !== "Income" && category !== "Expense" && tx.category !== category) {
        return false;
      }

      if (month !== "all" && !tx.date.startsWith(month)) {
        return false;
      }

      if (!normalizedSearch) {
        return true;
      }

      return (
        tx.recipient.toLowerCase().includes(normalizedSearch) ||
        tx.category.toLowerCase().includes(normalizedSearch) ||
        (tx.description ?? "").toLowerCase().includes(normalizedSearch)
      );
    })
    .map((tx) => tx);

  const sorted = applySort(filtered, sortBy);

  const totalItems = sorted.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const safePage = Math.min(Math.max(1, page), totalPages);
  const startIndex = (safePage - 1) * pageSize;

  return {
    data: sorted.slice(startIndex, startIndex + pageSize),
    totalItems,
    totalPages,
    currentPage: safePage,
  };
}

export type TransactionType = "income" | "expense";

export const baseCategories = [
  "General",
  "Dining Out",
  "Groceries",
  "Entertainment",
  "Utilities",
  "Travel",
] as const;

export type TransactionCategory = string;

export const transactionCategoryOptions = [
  "All Transactions",
  "Income",
  "Expense",
  ...baseCategories,
] as const;

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
  category: TransactionCategory;
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

export interface TransactionQuery {
  searchTerm?: string;
  category?: string;
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

      if (!normalizedSearch) {
        return true;
      }

      return (
        tx.recipient.toLowerCase().includes(normalizedSearch) ||
        tx.category.toLowerCase().includes(normalizedSearch)
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

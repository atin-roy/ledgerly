export type TransactionType = "income" | "expense";

export const baseCategories = [
  "General",
  "Dining Out",
  "Groceries",
  "Entertainment",
  "Utilities",
  "Travel",
] as const;

export type TransactionCategory = (typeof baseCategories)[number];

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
  date: string; // ISO date
  amount: number;
  type: TransactionType;
  badgeColor: string;
}

export const transactionRecords: Transaction[] = [
  {
    id: "txn-1",
    recipient: "Emma Richardson",
    description: "Freelance design project payment",
    category: "General",
    date: "2024-08-19",
    amount: 75.5,
    type: "income",
    badgeColor: "var(--color-green)",
  },
  {
    id: "txn-2",
    recipient: "Savory Bites Bistro",
    description: "Dinner with colleagues after work",
    category: "Dining Out",
    date: "2024-08-19",
    amount: 55.5,
    type: "expense",
    badgeColor: "var(--color-orange)",
  },
  {
    id: "txn-3",
    recipient: "Daniel Carter",
    category: "General",
    date: "2024-08-18",
    amount: 42.3,
    type: "expense",
    badgeColor: "var(--color-cyan)",
  },
  {
    id: "txn-4",
    recipient: "Sun Park",
    description: "Monthly allowance from parents",
    category: "General",
    date: "2024-08-17",
    amount: 120,
    type: "income",
    badgeColor: "var(--color-yellow)",
  },
  {
    id: "txn-5",
    recipient: "Urban Services Hub",
    category: "General",
    date: "2024-08-17",
    amount: 65,
    type: "expense",
    badgeColor: "var(--color-grey-600)",
  },
  {
    id: "txn-6",
    recipient: "Liam Hughes",
    description:
      "Refund for returned electronics item that was defective upon arrival",
    category: "Groceries",
    date: "2024-08-15",
    amount: 65.75,
    type: "income",
    badgeColor: "var(--color-green)",
  },
  {
    id: "txn-7",
    recipient: "Lily Ramirez",
    category: "General",
    date: "2024-08-14",
    amount: 50,
    type: "income",
    badgeColor: "var(--color-purple)",
  },
  {
    id: "txn-8",
    recipient: "Ethan Clark",
    category: "Dining Out",
    date: "2024-08-13",
    amount: 32.5,
    type: "expense",
    badgeColor: "var(--color-red)",
  },
  {
    id: "txn-9",
    recipient: "James Thompson",
    description: "Movie ticket for evening show",
    category: "Entertainment",
    date: "2024-08-11",
    amount: 5,
    type: "expense",
    badgeColor: "var(--color-purple-soft)",
  },
  {
    id: "txn-10",
    recipient: "Pixel Playground",
    category: "Entertainment",
    date: "2024-08-11",
    amount: 10,
    type: "expense",
    badgeColor: "var(--color-purple)",
  },
  {
    id: "txn-11",
    recipient: "Penny Market",
    category: "Groceries",
    date: "2024-08-10",
    amount: 82,
    type: "expense",
    badgeColor: "var(--color-turquoise)",
  },
  {
    id: "txn-12",
    recipient: "Airline Rewards",
    category: "Travel",
    date: "2024-08-08",
    amount: 200,
    type: "income",
    badgeColor: "var(--color-blue)",
  },
  {
    id: "txn-13",
    recipient: "Solstice Co-Op",
    description: "Monthly electricity bill payment",
    category: "Utilities",
    date: "2024-08-05",
    amount: 90.25,
    type: "expense",
    badgeColor: "var(--color-brown)",
  },
  {
    id: "txn-14",
    recipient: "Harbor Fitness",
    category: "General",
    date: "2024-08-03",
    amount: 40,
    type: "expense",
    badgeColor: "var(--color-cyan)",
  },
];

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

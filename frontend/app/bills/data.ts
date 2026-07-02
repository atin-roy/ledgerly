export type BillStatus = "pending" | "paid" | "overdue" | "cancelled";

export interface Bill {
  id: string;
  title: string;
  nextDue: string;
  amount: number;
  status: BillStatus;
}

export const billSortOptions = [
  { value: "latest", label: "Latest Due" },
  { value: "soonest", label: "Due Soonest" },
  { value: "amount-high-to-low", label: "Amount (High to Low)" },
  { value: "amount-low-to-high", label: "Amount (Low to High)" },
] as const;

export type BillSortOption = (typeof billSortOptions)[number]["value"];

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
});

const dateFormatter = new Intl.DateTimeFormat("en-US", {
  month: "short",
  day: "numeric",
});

export function formatCurrency(value: number) {
  return currencyFormatter.format(value);
}

export function formatBillDueDate(value: string) {
  return dateFormatter.format(new Date(value));
}

export interface BillSummarySection {
  count: number;
  amount: number;
}

export interface BillSummary {
  totalAmount: number;
  paid: BillSummarySection;
  pending: BillSummarySection;
  overdue: BillSummarySection;
}

export function getBillSummary(records: Bill[]): BillSummary {
  return records.reduce<BillSummary>(
    (summary, bill) => {
      summary.totalAmount += bill.amount;

      if (bill.status === "paid") {
        summary.paid.count += 1;
        summary.paid.amount += bill.amount;
      }

      if (bill.status === "pending") {
        summary.pending.count += 1;
        summary.pending.amount += bill.amount;
      }

      if (bill.status === "overdue") {
        summary.overdue.count += 1;
        summary.overdue.amount += bill.amount;
      }

      return summary;
    },
    {
      totalAmount: 0,
      paid: { count: 0, amount: 0 },
      pending: { count: 0, amount: 0 },
      overdue: { count: 0, amount: 0 },
    },
  );
}

export interface BillQuery {
  searchTerm?: string;
  sortBy?: BillSortOption;
}

function applySort(records: Bill[], sortBy: BillSortOption) {
  const sorted = [...records];

  switch (sortBy) {
    case "soonest":
      sorted.sort(
        (a, b) =>
          new Date(a.nextDue).getTime() - new Date(b.nextDue).getTime(),
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
        (a, b) =>
          new Date(b.nextDue).getTime() - new Date(a.nextDue).getTime(),
      );
      break;
  }

  return sorted;
}

export function filterAndSortBills(
  records: Bill[],
  { searchTerm = "", sortBy = "latest" }: BillQuery = {},
) {
  const normalizedSearch = searchTerm.trim().toLowerCase();

  const filtered = records.filter((bill) => {
    if (!normalizedSearch) {
      return true;
    }

    return bill.title.toLowerCase().includes(normalizedSearch);
  });

  return applySort(filtered, sortBy);
}

export const BILL_PAGE_SIZE = 8;

export interface BillPage {
  data: Bill[];
  totalItems: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface BillPageQuery extends BillQuery {
  page?: number;
  pageSize?: number;
}

export function getBillPage(
  records: Bill[],
  { page = 1, pageSize = BILL_PAGE_SIZE, ...query }: BillPageQuery = {},
): BillPage {
  const filtered = filterAndSortBills(records, query);
  const totalItems = filtered.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const safePage = Math.min(Math.max(1, page), totalPages);
  const startIndex = (safePage - 1) * pageSize;

  return {
    data: filtered.slice(startIndex, startIndex + pageSize),
    totalItems,
    totalPages,
    currentPage: safePage,
    pageSize,
  };
}

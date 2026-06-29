import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Transactions | Ledgerly",
  description: "View, add, and manage all your income and expense transactions.",
};

export default function TransactionsLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

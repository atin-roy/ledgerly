import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Budgets | Ledgerly",
  description: "Set spending limits and track how much you've used across categories.",
};

export default function BudgetsLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

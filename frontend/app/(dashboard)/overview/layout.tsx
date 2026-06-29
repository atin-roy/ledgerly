import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Overview | Ledgerly",
  description: "Your financial overview — balances, recent transactions, and spending at a glance.",
};

export default function OverviewLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

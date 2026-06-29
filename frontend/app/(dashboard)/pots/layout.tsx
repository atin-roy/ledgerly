import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Pots | Ledgerly",
  description: "Save towards goals with spending pots.",
};

export default function PotsLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

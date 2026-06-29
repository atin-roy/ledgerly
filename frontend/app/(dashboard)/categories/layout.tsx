import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Categories | Ledgerly",
  description: "Organise your transactions with custom categories.",
};

export default function CategoriesLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Admin | Ledgerly",
  description: "Search registered accounts.",
};

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}

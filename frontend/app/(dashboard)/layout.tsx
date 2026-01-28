import Sidebar from "@/components/Sidebar";
import type { ReactNode } from "react";

export default function DashboardLayout({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <div className="min-h-screen bg-[var(--beige-100)]">
      <div className="flex min-h-screen">
        <Sidebar />
        <main className="flex-1 overflow-hidden bg-[var(--beige-100)] px-4 py-10 sm:px-6 lg:px-8">
          {children}
        </main>
      </div>
    </div>
  );
}

import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Create Account | Ledgerly",
  description: "Create your free Ledgerly account and start tracking your finances.",
};

export default function RegisterLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

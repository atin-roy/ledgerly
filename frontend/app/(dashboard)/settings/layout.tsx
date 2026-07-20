import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Settings | Ledgerly",
  description: "Manage your account.",
};

export default function SettingsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}

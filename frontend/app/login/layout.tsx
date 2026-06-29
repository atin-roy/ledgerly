import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Sign In | Ledgerly",
  description: "Sign in to your Ledgerly account to manage your finances.",
};

export default function LoginLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}

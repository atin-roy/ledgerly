"use client";

import Link from "next/link";
import { useRouter, usePathname } from "next/navigation";
import {
  type LucideIcon,
  CreditCard,
  Home,
  LogOut,
  PieChart,
  PiggyBank,
  Repeat,
} from "lucide-react";
import { clearAuthTokens } from "@/lib/auth";

export type NavItem = {
  label: string;
  href: string;
  Icon: LucideIcon;
};

export const navItems: NavItem[] = [
  { label: "Dashboard", href: "/overview", Icon: Home },
  { label: "Transactions", href: "/transactions", Icon: Repeat },
  { label: "Budgets", href: "/budgets", Icon: PieChart },
  { label: "Pots", href: "/pots", Icon: PiggyBank },
  { label: "Recurring bills", href: "/bills", Icon: CreditCard },
];

const classNames = (...classes: Array<string | undefined>) =>
  classes.filter(Boolean).join(" ");

export default function Sidebar() {
  const pathname = usePathname() ?? "/";
  const router = useRouter();

  const handleSignOut = () => {
    clearAuthTokens();
    router.replace("/login");
  };

  return (
    <aside className="hidden min-h-screen w-80 flex-col bg-[#1f2126] px-6 py-8 text-sm text-white shadow-xl lg:flex">
      <div className="flex h-full flex-col justify-between">
        <div className="space-y-10">
          <div>
            <p className="text-2xl font-semibold uppercase tracking-[0.6em] text-white">
              ledgerly
            </p>
            <p className="text-xs font-semibold uppercase tracking-[0.5em] text-slate-400">
              personal finance
            </p>
          </div>
          <nav className="space-y-1">
            {navItems.map(({ label, href, Icon }) => {
              const isActive =
                pathname === href || pathname.startsWith(href + "/");
              return (
                <Link
                  key={label}
                  href={href}
                  className={classNames(
                    "flex items-center gap-3 rounded-2xl px-4 py-3 transition",
                    isActive
                      ? "bg-slate-900 text-white shadow-inner"
                      : "text-slate-300 hover:bg-white/10 hover:text-white",
                  )}
                >
                  <Icon className="h-4 w-4" />
                  <span className="font-semibold text-sm">{label}</span>
                </Link>
              );
            })}
          </nav>
        </div>
        <div className="mt-6 mb-16">
          <button
            type="button"
            onClick={handleSignOut}
            className="flex w-full items-center justify-center gap-2 rounded-2xl border border-white/10 px-4 py-3 text-sm font-semibold uppercase tracking-[0.4em] text-slate-300 transition hover:bg-white/10 hover:text-white"
          >
            <LogOut className="h-4 w-4 text-slate-300" />
            <span>Sign Out</span>
          </button>
        </div>
      </div>
    </aside>
  );
}

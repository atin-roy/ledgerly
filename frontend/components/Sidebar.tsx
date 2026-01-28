"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  ArrowLeft,
  CreditCard,
  Home,
  ListChecks,
  PieChart,
  PiggyBank,
  Repeat,
} from "lucide-react";

const navItems = [
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

  return (
    <aside className="flex h-full min-h-screen w-72 flex-col justify-between bg-[#1f2126] px-6 py-8 text-sm text-white shadow-xl">
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
            const isActive = pathname === href || pathname.startsWith(href + "/");
            return (
              <Link
                key={label}
                href={href}
                className={classNames(
                  "flex items-center gap-3 rounded-2xl px-4 py-3 transition",
                  isActive
                    ? "bg-slate-900 text-white shadow-inner"
                    : "text-slate-300 hover:bg-white/10 hover:text-white"
                )}
              >
                <Icon className="h-4 w-4" />
                <span className="font-semibold text-sm">{label}</span>
              </Link>
            );
          })}
        </nav>
      </div>

      <button
        type="button"
        className="mt-6 flex items-center justify-between rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-xs font-semibold uppercase tracking-[0.4em] text-white transition hover:border-white/30 hover:bg-white/10"
      >
        <span>Minimize Menu</span>
        <ArrowLeft className="h-3.5 w-3.5" />
      </button>
    </aside>
  );
}

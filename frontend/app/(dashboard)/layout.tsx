"use client";

import Link from "next/link";
import Sidebar, { navItems } from "@/components/Sidebar";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";

const classNames = (...classes: Array<string | undefined>) =>
  classes.filter(Boolean).join(" ");

function MobileNav() {
  const pathname = usePathname() ?? "/";

  return (
    <nav className="fixed inset-x-0 bottom-0 border-t border-slate-800 bg-[#1f2126] text-white shadow-lg lg:hidden">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-2 px-6 py-2">
        {navItems.map(({ label, href, Icon }) => {
          const isActive = pathname === href || pathname.startsWith(href + "/");
          return (
            <Link
              key={label}
              href={href}
              className={classNames(
                "flex flex-col items-center gap-1 rounded-2xl px-3 py-2 text-xs font-semibold uppercase tracking-[0.25em] transition",
                isActive
                  ? "text-white"
                  : "text-slate-400 hover:text-white/90"
              )}
            >
              <Icon className="h-5 w-5" />
              <span className="leading-none text-[0.55rem]">{label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}

export default function DashboardLayout({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <div className="min-h-screen bg-[var(--beige-100)]">
      <div className="flex min-h-screen">
        <Sidebar />
        <main className="flex-1 overflow-hidden bg-[var(--beige-100)] px-4 py-10 sm:px-6 lg:px-8 lg:pb-0 pb-[96px]">
          {children}
        </main>
      </div>
      <MobileNav />
    </div>
  );
}

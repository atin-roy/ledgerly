"use client";

import Link from "next/link";
import Sidebar, { navItems } from "@/components/Sidebar";
import { usePathname, useRouter } from "next/navigation";
import { type MouseEvent, useRef } from "react";
import { clearAuthTokens } from "@/lib/auth";
import type { ReactNode } from "react";

const classNames = (...classes: Array<string | undefined>) =>
  classes.filter(Boolean).join(" ");

const LONG_PRESS_DURATION = 600;

function MobileNav() {
  const pathname = usePathname() ?? "/";
  const router = useRouter();
  const timerRef = useRef<ReturnType<typeof window.setTimeout> | null>(null);
  const longPressTriggered = useRef(false);

  const dashboardItem = navItems.find((item) => item.label === "Dashboard");
  const otherItems = navItems.filter((item) => item.label !== "Dashboard");
  const splitPoint = Math.ceil(otherItems.length / 2);
  const leftItems = otherItems.slice(0, splitPoint);
  const rightItems = otherItems.slice(splitPoint);
  const mobileNavItems = dashboardItem
    ? [...leftItems, dashboardItem, ...rightItems]
    : navItems;

  const clearTimer = () => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  };

  const handleSignOut = () => {
    clearAuthTokens();
    router.replace("/login");
  };

  const onLongPress = () => {
    longPressTriggered.current = true;
    clearTimer();
    handleSignOut();
  };

  const startLongPress = () => {
    clearTimer();
    longPressTriggered.current = false;
    timerRef.current = window.setTimeout(onLongPress, LONG_PRESS_DURATION);
  };

  const cancelLongPress = () => {
    clearTimer();
  };

  const handleDashboardClick = (event: MouseEvent<HTMLAnchorElement>) => {
    if (longPressTriggered.current) {
      event.preventDefault();
      longPressTriggered.current = false;
    }
  };

  return (
    <nav className="fixed inset-x-0 bottom-0 border-t border-slate-800 bg-[#1f2126] text-white shadow-lg lg:hidden">
      <div className="mx-auto px-6 py-2">
        <div
          className="grid w-full items-center gap-2 text-center"
          style={{
            gridTemplateColumns: `repeat(${mobileNavItems.length}, minmax(0, 1fr))`,
          }}
        >
          {mobileNavItems.map(({ label, href, Icon }) => {
            const isActive = pathname === href || pathname.startsWith(href + "/");
            const isDashboard = label === "Dashboard";
            return (
              <Link
                key={label}
                href={href}
                className={classNames(
                  "flex flex-col items-center gap-1 rounded-2xl px-3 py-2 transition",
                  isActive ? "text-white" : "text-slate-400 hover:text-white/90",
                )}
                onPointerDown={isDashboard ? startLongPress : undefined}
                onPointerUp={isDashboard ? cancelLongPress : undefined}
                onPointerLeave={isDashboard ? cancelLongPress : undefined}
                onPointerCancel={isDashboard ? cancelLongPress : undefined}
                onClick={isDashboard ? handleDashboardClick : undefined}
              >
                <Icon className="h-5 w-5" />
                <span className="sr-only">{label}</span>
              </Link>
            );
          })}
        </div>
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

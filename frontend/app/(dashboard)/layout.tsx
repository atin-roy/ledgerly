"use client";

import Link from "next/link";
import Sidebar, { navItems } from "@/components/Sidebar";
import { usePathname, useRouter } from "next/navigation";
import { type MouseEvent, useRef, useState, useEffect } from "react";
import { clearAuthTokens, isAuthenticated } from "@/lib/auth";
import type { ReactNode } from "react";

const classNames = (...classes: Array<string | undefined>) =>
  classes.filter(Boolean).join(" ");

const LONG_PRESS_DURATION = 600;

function MobileNav() {
  const pathname = usePathname() ?? "/";
  const router = useRouter();
  const timerRef = useRef<number | null>(null);
  const longPressTriggered = useRef(false);
  const [showSignOutPrompt, setShowSignOutPrompt] = useState(false);

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
    setShowSignOutPrompt(true);
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

  const closePrompt = () => {
    setShowSignOutPrompt(false);
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
      {showSignOutPrompt && (
        <div className="fixed inset-0 z-20 flex items-end justify-center bg-black/30 lg:hidden">
          <div className="mb-24 w-full px-6">
            <div className="rounded-2xl bg-[#1f2126] px-5 py-4 text-center text-white shadow-lg">
              <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                Hold detected
              </p>
              <p className="mt-1 text-xs text-slate-400">
                Release to reveal sign out.
              </p>
              <div className="mt-3 flex flex-col gap-2">
                <button
                  type="button"
                  onClick={() => {
                    handleSignOut();
                    closePrompt();
                  }}
                  className="rounded-xl bg-[#f87171] px-4 py-3 text-sm font-semibold uppercase tracking-[0.3em] text-white transition hover:bg-[#f87171]/90"
                >
                  Sign Out
                </button>
                <button
                  type="button"
                  onClick={closePrompt}
                  className="rounded-xl border border-white/30 px-4 py-3 text-sm font-semibold uppercase tracking-[0.3em] text-white transition hover:border-white/60"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
}

export default function DashboardLayout({
  children,
}: {
  children: ReactNode;
}) {
  const router = useRouter();
  const [isChecking, setIsChecking] = useState(true);

  useEffect(() => {
    // Check authentication on mount
    if (typeof window !== "undefined") {
      const authenticated = isAuthenticated();
      console.log("🔒 Dashboard Auth Check:", authenticated);
      
      if (!authenticated) {
        console.log("❌ Not authenticated, redirecting to login...");
        router.replace("/login");
      } else {
        console.log("✅ Authenticated, loading dashboard");
        setIsChecking(false);
      }
    }
  }, [router]);

  if (isChecking) {
    return (
      <div className="min-h-screen bg-beige-100 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-beige-100">
      <div className="flex min-h-screen">
        <Sidebar />
        <main className="flex-1 overflow-hidden bg-beige-100 px-4 py-10 sm:px-6 lg:px-8 lg:pb-0 pb-[96px]">
          {children}
        </main>
      </div>
      <MobileNav />
    </div>
  );
}

"use client";

import Link from "next/link";
import Sidebar, { navItems } from "@/components/Sidebar";
import { usePathname, useRouter } from "next/navigation";
import { type MouseEvent, useRef, useState, useEffect } from "react";
import { clearAuthTokens, isAuthenticated } from "@/lib/auth";
import type { ReactNode } from "react";
import styles from "./dashboard.module.css";

const LONG_PRESS_DURATION = 600;

function MobileNav() {
  const pathname = usePathname() ?? "/";
  const router = useRouter();
  const timerRef = useRef<number | null>(null);
  const longPressTriggered = useRef(false);
  const [showSignOutPrompt, setShowSignOutPrompt] = useState(false);

  const overviewItem = navItems.find((item) => item.label === "Overview");
  const otherItems = navItems.filter((item) => item.label !== "Overview");
  const splitPoint = Math.ceil(otherItems.length / 2);
  const leftItems = otherItems.slice(0, splitPoint);
  const rightItems = otherItems.slice(splitPoint);
  const mobileNavItems = overviewItem
    ? [...leftItems, overviewItem, ...rightItems]
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

  const handleOverviewClick = (event: MouseEvent<HTMLAnchorElement>) => {
    if (longPressTriggered.current) {
      event.preventDefault();
      longPressTriggered.current = false;
    }
  };

  const closePrompt = () => {
    setShowSignOutPrompt(false);
  };

  return (
    <nav className={styles.mobileNav}>
      <div className={styles.mobileInner}>
        <div
          className={styles.mobileGrid}
          style={{
            gridTemplateColumns: `repeat(${mobileNavItems.length}, minmax(0, 1fr))`,
          }}
        >
          {mobileNavItems.map(({ label, href, Icon }) => {
            const isActive =
              pathname === href || pathname.startsWith(href + "/");
            const isOverview = label === "Overview";
            return (
              <Link
                key={label}
                href={href}
                aria-current={isActive ? "page" : undefined}
                className={
                  isActive
                    ? `${styles.mobileItem} ${styles.mobileItemActive}`
                    : styles.mobileItem
                }
                onPointerDown={isOverview ? startLongPress : undefined}
                onPointerUp={isOverview ? cancelLongPress : undefined}
                onPointerLeave={isOverview ? cancelLongPress : undefined}
                onPointerCancel={isOverview ? cancelLongPress : undefined}
                onClick={isOverview ? handleOverviewClick : undefined}
              >
                <Icon className={styles.mobileIcon} aria-hidden />
                <span className="sr-only">{label}</span>
              </Link>
            );
          })}
        </div>
      </div>
      {showSignOutPrompt && (
        <div className={styles.promptOverlay}>
          <div className={styles.promptWrap}>
            <div className={styles.promptCard}>
              <p className={styles.promptKicker}>Hold detected</p>
              <p className={styles.promptText}>Release to reveal sign out.</p>
              <div className={styles.promptActions}>
                <button
                  type="button"
                  onClick={() => {
                    handleSignOut();
                    closePrompt();
                  }}
                  className={styles.promptSignout}
                >
                  Sign out
                </button>
                <button
                  type="button"
                  onClick={closePrompt}
                  className={styles.promptCancel}
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
  const [isChecking] = useState(() =>
    typeof window === "undefined" ? true : !isAuthenticated(),
  );

  useEffect(() => {
    if (typeof window !== "undefined" && !isAuthenticated()) {
      router.replace("/login");
    }
  }, [router]);

  if (isChecking) {
    return (
      <div className={styles.loading}>
        <p>Loading…</p>
      </div>
    );
  }

  return (
    <div className={styles.shell}>
      <div className={styles.row}>
        <Sidebar />
        <main className={styles.main}>{children}</main>
      </div>
      <MobileNav />
    </div>
  );
}

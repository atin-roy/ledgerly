"use client";

import Link from "next/link";
import { useRouter, usePathname } from "next/navigation";
import {
  type LucideIcon,
  CreditCard,
  LayoutGrid,
  LogOut,
  PieChart,
  PiggyBank,
  Repeat,
  Settings,
  Tag,
} from "lucide-react";
import { clearAuthTokens } from "@/lib/auth";
import WordmarkStamp from "@/components/brand/WordmarkStamp";
import styles from "./Sidebar.module.css";

export type NavItem = {
  label: string;
  href: string;
  Icon: LucideIcon;
};

export const navItems: NavItem[] = [
  { label: "Overview", href: "/overview", Icon: LayoutGrid },
  { label: "Transactions", href: "/transactions", Icon: Repeat },
  { label: "Categories", href: "/categories", Icon: Tag },
  { label: "Budgets", href: "/budgets", Icon: PieChart },
  { label: "Pots", href: "/pots", Icon: PiggyBank },
  { label: "Bills", href: "/bills", Icon: CreditCard },
  { label: "Settings", href: "/settings", Icon: Settings },
];

export default function Sidebar() {
  const pathname = usePathname() ?? "/";
  const router = useRouter();

  const handleSignOut = () => {
    clearAuthTokens();
    router.replace("/login");
  };

  return (
    <aside className={styles.aside}>
      <div className={styles.inner}>
        <div>
          <div className={styles.brand}>
            <WordmarkStamp className={styles.wordmark} />
          </div>
          <nav className={styles.nav}>
            {navItems.map(({ label, href, Icon }) => {
              const isActive =
                pathname === href || pathname.startsWith(href + "/");
              return (
                <Link
                  key={label}
                  href={href}
                  aria-current={isActive ? "page" : undefined}
                  className={
                    isActive
                      ? `${styles.navItem} ${styles.navItemActive}`
                      : styles.navItem
                  }
                >
                  <Icon className={styles.icon} aria-hidden />
                  <span className={styles.label}>{label}</span>
                </Link>
              );
            })}
          </nav>
        </div>
        <div className={styles.bottom}>
          <button
            type="button"
            onClick={handleSignOut}
            className={styles.signout}
          >
            <LogOut className={styles.icon} aria-hidden />
            <span>Sign out</span>
          </button>
        </div>
      </div>
    </aside>
  );
}

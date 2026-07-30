import Link from "next/link";

import WordmarkStamp from "@/components/brand/WordmarkStamp";
import styles from "./marketing.module.css";

export default function MarketingNav() {
  return (
    <nav className={styles.nav} aria-label="Main">
      <Link href="/" aria-label="Ledgerly home">
        <WordmarkStamp className={styles.wordmark} />
      </Link>
      <div className={styles.navLinks}>
        <Link
          href="/about"
          className={`${styles.navLink} ${styles.navOptional}`}
        >
          About
        </Link>
        <Link href="/login" className={styles.navLink}>
          Sign in
        </Link>
        <Link href="/register" className={styles.navCta}>
          Open an account
        </Link>
      </div>
    </nav>
  );
}

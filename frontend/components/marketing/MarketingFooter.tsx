import Link from "next/link";

import WordmarkStamp from "@/components/brand/WordmarkStamp";
import styles from "./marketing.module.css";

export default function MarketingFooter() {
  return (
    <footer className={styles.footer}>
      <div className={styles.footGrid}>
        <div className={styles.footBrand}>
          <WordmarkStamp className={styles.footWordmark} />
          <p className={styles.footTag}>
            A personal finance tracker built like a bookkeeper&rsquo;s ledger —
            entries, running balances, allotments, and reserves. No ads, no bank
            linking, no dark patterns.
          </p>
        </div>
        <div>
          <p className={styles.footHead}>Product</p>
          <ul className={styles.footList}>
            <li>
              <Link href="/register">Open an account</Link>
            </li>
            <li>
              <Link href="/login">Sign in</Link>
            </li>
            <li>
              <a
                href="https://github.com/atin-roy/ledgerly"
                target="_blank"
                rel="noopener noreferrer"
              >
                Source on GitHub
              </a>
            </li>
          </ul>
        </div>
        <div>
          <p className={styles.footHead}>Company</p>
          <ul className={styles.footList}>
            <li>
              <Link href="/about">About</Link>
            </li>
            <li>
              <Link href="/privacy">Privacy</Link>
            </li>
            <li>
              <Link href="/terms">Terms</Link>
            </li>
          </ul>
        </div>
      </div>
      <hr className={styles.footRule} />
      <div className={styles.footBottom}>
        <p className={styles.footNote}>
          © 2026 Ledgerly · built by{" "}
          <a
            href="https://atinroy.dev"
            target="_blank"
            rel="noopener noreferrer"
          >
            Atin Roy
          </a>
        </p>
        <p className={styles.footStack}>
          Next.js · TypeScript · Spring Boot · PostgreSQL
        </p>
      </div>
    </footer>
  );
}

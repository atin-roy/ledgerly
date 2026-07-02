import Link from "next/link";
import { ArrowRight } from "lucide-react";

import RedirectIfAuthenticated from "@/components/auth/RedirectIfAuthenticated";
import WordmarkStamp from "@/components/brand/WordmarkStamp";
import SealStamp from "@/components/brand/SealStamp";
import styles from "./home.module.css";

const principles = [
  {
    title: "A running balance, always",
    body: "Every entry carries the account forward. You always know the figure standing at the foot of the page — not just a pile of transactions.",
  },
  {
    title: "Debit and credit, plainly",
    body: "Money out in red, money in green, set in two columns like a proper statement. No mystery pie charts — just the figures.",
  },
  {
    title: "Allotments and reserves",
    body: "Budgets are monthly allotments you draw down. Pots are reserves, set aside and kept out of the running balance.",
  },
];

export default function HomePage() {
  return (
    <div className={styles.landing}>
      <RedirectIfAuthenticated />

      <div className={styles.inner}>
        <nav className={styles.nav}>
          <WordmarkStamp className={styles.wordmark} />
          <div className={styles.navLinks}>
            <Link href="/login" className={styles.navLink}>
              Sign in
            </Link>
            <Link href="/register" className={styles.navCta}>
              Open an account
            </Link>
          </div>
        </nav>

        <section className={styles.hero}>
          <div>
            <p className={styles.eyebrow}>Personal bookkeeping</p>
            <h1 className={styles.h1}>
              Keep your own <em>books</em>.
            </h1>
            <p className={styles.lede}>
              Ledgerly is a personal finance tracker built like a bookkeeper&rsquo;s
              ledger. Record each entry, watch the balance carry forward, and see
              exactly where the money stands.
            </p>
            <div className={styles.ctas}>
              <Link href="/register" className={styles.btnPrimary}>
                Open an account
                <ArrowRight size={16} aria-hidden />
              </Link>
              <Link href="/login" className={styles.btnGhost}>
                Sign in
              </Link>
            </div>
          </div>

          <div className={styles.sheetWrap}>
            <div className={styles.sheet}>
              <div className={styles.sheetHd}>
                <span className={styles.sheetTitle}>General Ledger</span>
                <span className={styles.sheetMeta}>Mar 2026</span>
              </div>
              <table className={styles.ledger}>
                <thead>
                  <tr>
                    <th className={`${styles.lth} ${styles.lthLeft}`}>Date</th>
                    <th className={`${styles.lth} ${styles.lthLeft}`}>
                      Particulars
                    </th>
                    <th className={styles.lth}>Debit</th>
                    <th className={styles.lth}>Credit</th>
                    <th className={styles.lth}>Balance</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lDate}`}>
                      Mar 01
                    </td>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lParty}`}>
                      Salary
                    </td>
                    <td className={`${styles.ltd} ${styles.lDash}`}>—</td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lCredit}`}>
                      ₹82,000
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹82,000
                    </td>
                  </tr>
                  <tr>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lDate}`}>
                      Mar 03
                    </td>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lParty}`}>
                      Blue Tokai Coffee
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lDebit}`}>
                      ₹460
                    </td>
                    <td className={`${styles.ltd} ${styles.lDash}`}>—</td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹81,540
                    </td>
                  </tr>
                  <tr>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lDate}`}>
                      Mar 05
                    </td>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lParty}`}>
                      Rent — March
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lDebit}`}>
                      ₹24,000
                    </td>
                    <td className={`${styles.ltd} ${styles.lDash}`}>—</td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹57,540
                    </td>
                  </tr>
                  <tr>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lDate}`}>
                      Mar 08
                    </td>
                    <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lParty}`}>
                      Freelance invoice
                    </td>
                    <td className={`${styles.ltd} ${styles.lDash}`}>—</td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lCredit}`}>
                      ₹15,000
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹72,540
                    </td>
                  </tr>
                </tbody>
                <tfoot className={styles.foot}>
                  <tr>
                    <td
                      className={`${styles.ltd} ${styles.ltdLeft}`}
                      colSpan={4}
                    >
                      <span className={styles.footLabel}>Balance carried</span>
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹72,540
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
            <SealStamp className={styles.seal} />
          </div>
        </section>

        <section className={styles.principles}>
          {principles.map((p, i) => (
            <div key={p.title} className={styles.principle}>
              <span className={styles.pNum}>
                {String(i + 1).padStart(2, "0")}
              </span>
              <h2 className={styles.pTitle}>{p.title}</h2>
              <p className={styles.pBody}>{p.body}</p>
            </div>
          ))}
        </section>

        <section className={styles.band}>
          <h2 className={styles.bandTitle}>Open the ledger.</h2>
          <p className={styles.bandSub}>
            Free to start. Your books, kept in order.
          </p>
          <Link href="/register" className={styles.bandBtn}>
            Open an account
            <ArrowRight size={16} aria-hidden />
          </Link>
        </section>

        <footer className={styles.footer}>
          <div className={styles.footRow}>
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
      </div>
    </div>
  );
}

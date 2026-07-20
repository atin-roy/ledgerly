import Link from "next/link";
import { ArrowRight } from "lucide-react";

import RedirectIfAuthenticated from "@/components/auth/RedirectIfAuthenticated";
import MarketingNav from "@/components/marketing/MarketingNav";
import MarketingFooter from "@/components/marketing/MarketingFooter";
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

const ledgerRows = [
  { date: "Mar 01", party: "Salary", debit: null, credit: "₹82,000", bal: "₹82,000" },
  { date: "Mar 03", party: "Blue Tokai Coffee", debit: "₹460", credit: null, bal: "₹81,540" },
  { date: "Mar 05", party: "Rent — March", debit: "₹24,000", credit: null, bal: "₹57,540" },
  { date: "Mar 08", party: "Freelance invoice", debit: null, credit: "₹15,000", bal: "₹72,540" },
];

const bills = [
  { name: "Rent", due: "1st of the month", amount: "₹24,000", state: "paid" },
  { name: "Electricity", due: "12th of the month", amount: "₹1,850", state: "due" },
  { name: "Internet", due: "18th of the month", amount: "₹999", state: "upcoming" },
];

export default function HomePage() {
  return (
    <div className={styles.landing}>
      <RedirectIfAuthenticated />

      <div className={styles.inner}>
        <MarketingNav />

        <section className={styles.hero}>
          <div>
            <p className={styles.eyebrow}>Personal bookkeeping</p>
            <h1 className={styles.h1}>
              Keep your own <em>books</em>.
            </h1>
            <p className={styles.lede}>
              Ledgerly is a personal finance tracker built like a
              bookkeeper&rsquo;s ledger. Record each entry, watch the balance
              carry forward, and see exactly where the money stands.
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
            <ul className={styles.proofs}>
              <li>Free to use</li>
              <li>Open source</li>
              <li>No bank credentials, ever</li>
            </ul>
          </div>

          <div className={styles.sheetWrap}>
            <div className={styles.sheet}>
              <div className={styles.sheetHd}>
                <span className={styles.sheetTitle}>General Ledger</span>
                <span className={styles.sheetMeta}>Folio 12 · Mar 2026</span>
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
                  {ledgerRows.map((r) => (
                    <tr key={r.date}>
                      <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lDate}`}>
                        {r.date}
                      </td>
                      <td className={`${styles.ltd} ${styles.ltdLeft} ${styles.lParty}`}>
                        {r.party}
                      </td>
                      <td
                        className={`${styles.ltd} ${
                          r.debit ? `${styles.lFig} ${styles.lDebit}` : styles.lDash
                        }`}
                      >
                        {r.debit ?? "—"}
                      </td>
                      <td
                        className={`${styles.ltd} ${
                          r.credit ? `${styles.lFig} ${styles.lCredit}` : styles.lDash
                        }`}
                      >
                        {r.credit ?? "—"}
                      </td>
                      <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                        {r.bal}
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot className={styles.foot}>
                  <tr>
                    <td className={`${styles.ltd} ${styles.ltdLeft}`} colSpan={4}>
                      <span className={styles.footLabel}>Balance carried</span>
                    </td>
                    <td className={`${styles.ltd} ${styles.lFig} ${styles.lBal}`}>
                      ₹72,540
                    </td>
                  </tr>
                </tfoot>
              </table>
              <SealStamp className={styles.seal} />
            </div>
          </div>
        </section>

        <section className={styles.section} aria-labelledby="principles-hd">
          <div className={styles.sectionHd}>
            <span className={styles.folio}>Folio 01</span>
            <h2 id="principles-hd" className={styles.sectionTitle}>
              The method
            </h2>
          </div>
          <div className={styles.principles}>
            {principles.map((p, i) => (
              <div key={p.title} className={styles.principle}>
                <span className={styles.pNum}>
                  {String(i + 1).padStart(2, "0")}
                </span>
                <h3 className={styles.pTitle}>{p.title}</h3>
                <p className={styles.pBody}>{p.body}</p>
              </div>
            ))}
          </div>
        </section>

        <section className={styles.section} aria-labelledby="inside-hd">
          <div className={styles.sectionHd}>
            <span className={styles.folio}>Folio 02</span>
            <h2 id="inside-hd" className={styles.sectionTitle}>
              Inside the books
            </h2>
          </div>

          <div className={styles.vignettes}>
            <div className={styles.vignette}>
              <div className={styles.vChrome}>
                <span className={styles.vLabel}>Budgets</span>
                <span className={styles.vMeta}>March allotment</span>
              </div>
              <div className={styles.vBody}>
                <div className={styles.allot}>
                  <div className={styles.allotRow}>
                    <span className={styles.allotName}>Groceries</span>
                    <span className={`${styles.allotFig} num`}>
                      ₹6,420 <span className={styles.allotOf}>of ₹9,000</span>
                    </span>
                  </div>
                  <div
                    className={styles.allotBar}
                    role="img"
                    aria-label="Groceries: 71% of allotment drawn"
                  >
                    <div className={styles.allotFill} style={{ width: "71%" }} />
                  </div>
                  <div className={styles.allotRow}>
                    <span className={styles.allotName}>Eating out</span>
                    <span className={`${styles.allotFig} num`}>
                      ₹3,940 <span className={styles.allotOf}>of ₹4,000</span>
                    </span>
                  </div>
                  <div
                    className={styles.allotBar}
                    role="img"
                    aria-label="Eating out: 98% of allotment drawn"
                  >
                    <div
                      className={`${styles.allotFill} ${styles.allotHot}`}
                      style={{ width: "98%" }}
                    />
                  </div>
                  <div className={styles.allotRow}>
                    <span className={styles.allotName}>Transport</span>
                    <span className={`${styles.allotFig} num`}>
                      ₹1,150 <span className={styles.allotOf}>of ₹3,000</span>
                    </span>
                  </div>
                  <div
                    className={styles.allotBar}
                    role="img"
                    aria-label="Transport: 38% of allotment drawn"
                  >
                    <div className={styles.allotFill} style={{ width: "38%" }} />
                  </div>
                </div>
              </div>
              <p className={styles.vCaption}>
                Allotments draw down as you spend. The rule fills; you stop.
              </p>
            </div>

            <div className={styles.vignette}>
              <div className={styles.vChrome}>
                <span className={styles.vLabel}>Pots</span>
                <span className={styles.vMeta}>Reserves</span>
              </div>
              <div className={styles.vBody}>
                <div className={styles.pot}>
                  <p className={styles.potName}>Emergency fund</p>
                  <p className={`${styles.potFig} num`}>₹1,20,000</p>
                  <p className={styles.potTarget}>
                    of a ₹2,00,000 target · set aside, out of the balance
                  </p>
                </div>
              </div>
              <p className={styles.vCaption}>
                Reserves sit apart from the running balance until you draw them.
              </p>
            </div>

            <div className={styles.vignette}>
              <div className={styles.vChrome}>
                <span className={styles.vLabel}>Bills</span>
                <span className={styles.vMeta}>Payables</span>
              </div>
              <div className={styles.vBody}>
                <ul className={styles.billList}>
                  {bills.map((b) => (
                    <li key={b.name} className={styles.bill}>
                      <div>
                        <p className={styles.billName}>{b.name}</p>
                        <p className={styles.billDue}>{b.due}</p>
                      </div>
                      <div className={styles.billRight}>
                        <span className={`${styles.billAmt} num`}>{b.amount}</span>
                        <span
                          className={`${styles.billState} ${
                            b.state === "paid"
                              ? styles.billPaid
                              : b.state === "due"
                                ? styles.billDueNow
                                : styles.billUpcoming
                          }`}
                        >
                          {b.state}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
              <p className={styles.vCaption}>
                A payables schedule, so nothing lands on the account unannounced.
              </p>
            </div>
          </div>
        </section>

        <section className={styles.band}>
          <div className={styles.bandSealWrap} aria-hidden>
            <SealStamp className={styles.bandSeal} />
          </div>
          <h2 className={styles.bandTitle}>Open the ledger.</h2>
          <p className={styles.bandSub}>
            Free to start. Your books, kept in order.
          </p>
          <Link href="/register" className={styles.bandBtn}>
            Open an account
            <ArrowRight size={16} aria-hidden />
          </Link>
        </section>

        <MarketingFooter />
      </div>
    </div>
  );
}

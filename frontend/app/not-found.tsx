import Link from "next/link";

import MarketingNav from "@/components/marketing/MarketingNav";
import MarketingFooter from "@/components/marketing/MarketingFooter";
import styles from "@/components/marketing/marketing.module.css";

export default function NotFound() {
  return (
    <div className={styles.prosePage}>
      <div className={styles.proseInner}>
        <MarketingNav />
        <main className={styles.proseMain}>
          <p className={styles.proseEyebrow}>Folio not found</p>
          <h1 className={styles.proseTitle}>
            This page isn&rsquo;t in the books.
          </h1>
          <p>
            The entry you&rsquo;re looking for was never recorded, or it has
            been struck from the ledger. Check the address, or head back to a
            page that balances.
          </p>
          <p>
            <Link href="/">Return to the front page</Link> ·{" "}
            <Link href="/login">Sign in</Link>
          </p>
        </main>
        <MarketingFooter />
      </div>
    </div>
  );
}

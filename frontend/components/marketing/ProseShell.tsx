import type { ReactNode } from "react";

import MarketingNav from "./MarketingNav";
import MarketingFooter from "./MarketingFooter";
import styles from "./marketing.module.css";

export default function ProseShell({
  eyebrow,
  title,
  updated,
  children,
}: {
  eyebrow: string;
  title: string;
  updated?: string;
  children: ReactNode;
}) {
  return (
    <div className={styles.prosePage}>
      <div className={styles.proseInner}>
        <MarketingNav />
        <main className={styles.proseMain}>
          <p className={styles.proseEyebrow}>{eyebrow}</p>
          <h1 className={styles.proseTitle}>{title}</h1>
          {updated ? (
            <p className={styles.proseUpdated}>Last entered: {updated}</p>
          ) : null}
          {children}
        </main>
        <MarketingFooter />
      </div>
    </div>
  );
}

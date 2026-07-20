import type { Metadata } from "next";
import Link from "next/link";

import ProseShell from "@/components/marketing/ProseShell";

export const metadata: Metadata = {
  title: "Privacy — Ledgerly",
  description:
    "What Ledgerly stores, what it never collects, and how to take your data out.",
};

export default function PrivacyPage() {
  return (
    <ProseShell
      eyebrow="The fine print"
      title="Privacy policy"
      updated="20 July 2026"
    >
      <p>
        Ledgerly is a personal bookkeeping tool. The short version of this
        policy: what you write in your ledger is yours, it is stored so the
        app can show it back to you, and it is not used for anything else.
      </p>

      <h2>What is collected</h2>
      <ul>
        <li>
          <strong>Account details</strong> — your name, email address, and a
          password. The password is stored only as a salted BCrypt hash; no
          one, including the operator, can read it.
        </li>
        <li>
          <strong>Your entries</strong> — the transactions, budgets, pots, and
          bills you record. These exist solely so the application can display
          your books to you.
        </li>
      </ul>

      <h2>What is never collected</h2>
      <ul>
        <li>
          <strong>Bank credentials.</strong> Ledgerly has no bank integration
          and will never ask for online-banking logins, card numbers, or
          account numbers.
        </li>
        <li>
          <strong>Trackers.</strong> No advertising pixels, no third-party
          analytics, no fingerprinting. The only cookies/storage used are the
          ones that keep you signed in.
        </li>
      </ul>

      <h2>Where it lives</h2>
      <p>
        Data is stored in a PostgreSQL database on a server operated by the
        developer, reachable only over HTTPS. The database is not exposed to
        the public internet; only the application can talk to it.
      </p>

      <h2>Who sees it</h2>
      <p>
        Nobody, in the ordinary course. Your data is not sold, shared, rented,
        or used to train anything. The operator has administrative access to
        the server for maintenance and will not read your entries except where
        strictly required to debug a problem you have reported.
      </p>

      <h2>Taking your data out</h2>
      <p>
        You can delete individual entries at any time from within the app. To
        have your account and everything in it erased, contact the developer
        via{" "}
        <a href="https://atinroy.dev" target="_blank" rel="noopener noreferrer">
          atinroy.dev
        </a>{" "}
        and it will be removed from the live database.
      </p>

      <h2>Changes</h2>
      <p>
        If this policy changes, the &ldquo;last entered&rdquo; date above
        changes with it. Because the{" "}
        <a
          href="https://github.com/atin-roy/ledgerly"
          target="_blank"
          rel="noopener noreferrer"
        >
          source is public
        </a>
        , the history of this page is auditable like everything else.
      </p>
      <p>
        Questions? Start at the <Link href="/about">about page</Link>.
      </p>
    </ProseShell>
  );
}

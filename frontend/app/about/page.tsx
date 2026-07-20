import type { Metadata } from "next";
import Link from "next/link";

import ProseShell from "@/components/marketing/ProseShell";

export const metadata: Metadata = {
  title: "About — Ledgerly",
  description:
    "Why Ledgerly exists: a personal finance tracker built like a bookkeeper's ledger, by one person, in the open.",
};

export default function AboutPage() {
  return (
    <ProseShell eyebrow="The story" title="About Ledgerly">
        <h2>Why a ledger?</h2>
        <p>
          Most budgeting apps want to gamify your money — streaks, confetti,
          pie charts in brand colors. Ledgerly goes the other way. For a few
          hundred years, people kept perfectly good track of money with a bound
          book, two columns, and a running balance. That discipline still
          works; it just needed a login screen.
        </p>
        <p>
          So Ledgerly behaves like the book: every transaction is an entry,
          money out sits in the debit column in red, money in sits in the
          credit column in green, and the balance carries forward so you always
          know the figure at the foot of the page. Budgets are monthly
          allotments you draw down. Pots are reserves, set aside and kept out
          of the running balance — the digital version of an envelope in a
          drawer.
        </p>

        <h2>Who builds it</h2>
        <p>
          Ledgerly is designed, built, and run by{" "}
          <a href="https://atinroy.dev" target="_blank" rel="noopener noreferrer">
            Atin Roy
          </a>{" "}
          — one person, end to end: the Spring Boot API, the Next.js frontend,
          the PostgreSQL schema, and the server it all runs on. It is a real,
          working product and also a working demonstration of how I like to
          build software: typed contracts, migrations under version control,
          CI/CD on every push, and no telemetry I wouldn&rsquo;t want pointed
          at me.
        </p>
        <p>
          The entire codebase is public. If you&rsquo;d rather audit than
          trust, the books are open:{" "}
          <a
            href="https://github.com/atin-roy/ledgerly"
            target="_blank"
            rel="noopener noreferrer"
          >
            github.com/atin-roy/ledgerly
          </a>
          .
        </p>

        <h2>What it deliberately doesn&rsquo;t do</h2>
        <ul>
          <li>
            <strong>No bank linking.</strong> Ledgerly never asks for bank
            credentials and never talks to your bank. You enter what you spend
            — that friction is the point; it&rsquo;s what makes you notice.
          </li>
          <li>
            <strong>No ads, no analytics, no resale.</strong> Your entries
            exist to show you your balance, nothing else.
          </li>
          <li>
            <strong>No advice.</strong> Ledgerly shows you the figures. What
            you do about them is your business.
          </li>
        </ul>

        <h2>The stack, for the curious</h2>
        <p>
          Spring Boot 4 (Java) behind the <code>/api</code> path, Next.js 16
          with CSS Modules in front, PostgreSQL with Flyway migrations
          underneath, all shipped by GitHub Actions to a Linux VPS behind
          Caddy. Details and decisions live in the repository&rsquo;s
          documentation.
        </p>
        <p>
          Ready to keep your own books?{" "}
          <Link href="/register">Open an account</Link> — it&rsquo;s free.
        </p>
    </ProseShell>
  );
}

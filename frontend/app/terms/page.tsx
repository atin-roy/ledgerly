import type { Metadata } from "next";
import Link from "next/link";

import ProseShell from "@/components/marketing/ProseShell";

export const metadata: Metadata = {
  title: "Terms — Ledgerly",
  description: "The terms of using Ledgerly, in plain language.",
};

export default function TermsPage() {
  return (
    <ProseShell
      eyebrow="The fine print"
      title="Terms of use"
      updated="20 July 2026"
    >
      <p>
        These terms are deliberately short. Ledgerly is a free personal
        bookkeeping tool built and run by one developer; using it is an
        agreement between you and{" "}
        <a href="https://atinroy.dev" target="_blank" rel="noopener noreferrer">
          Atin Roy
        </a>
        .
      </p>

      <h2>The service</h2>
      <p>
        Ledgerly lets you record personal financial entries and view balances,
        allotments, and reserves calculated from them. It is provided free of
        charge, as is, with no guarantee of uninterrupted availability. It is
        a personal project run with care, not a bank.
      </p>

      <h2>Your account</h2>
      <ul>
        <li>
          You are responsible for keeping your password to yourself. Choose a
          good one; it protects your books.
        </li>
        <li>
          Your entries are yours. Ledgerly claims no ownership of anything you
          record.
        </li>
        <li>
          You may close your account at any time — see the{" "}
          <Link href="/privacy">privacy policy</Link> for how data removal
          works.
        </li>
      </ul>

      <h2>Fair use</h2>
      <p>
        Don&rsquo;t attempt to break into other people&rsquo;s accounts,
        overload the service, or use it to store anything unlawful. Accounts
        doing so may be removed without notice.
      </p>

      <h2>Not financial advice</h2>
      <p>
        Ledgerly shows you figures you entered. Nothing in the application is
        investment, tax, accounting, or legal advice. Decisions you make from
        your books are your own.
      </p>

      <h2>No warranty</h2>
      <p>
        The service is provided &ldquo;as is&rdquo; and &ldquo;as
        available&rdquo;, without warranties of any kind. To the maximum
        extent permitted by law, the developer is not liable for losses
        arising from use of the service — keep independent records of anything
        you cannot afford to lose.
      </p>

      <h2>Changes</h2>
      <p>
        These terms may be revised; the date above tracks the latest entry.
        Continued use after a change means you accept the revised terms.
      </p>
    </ProseShell>
  );
}

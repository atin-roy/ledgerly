"use client";

import { useState, type SyntheticEvent } from "react";
import { useRouter } from "next/navigation";
import { KeyRound, ShieldAlert } from "lucide-react";

import { changePassword, deleteAccount } from "@/lib/services";
import { extractApiError } from "@/lib/apiClient";
import { clearAuthTokens, getStoredUser } from "@/lib/auth";
import styles from "./settings.module.css";

export default function SettingsPage() {
  const router = useRouter();
  const user = getStoredUser();

  const [pw, setPw] = useState({ current: "", next: "", confirm: "" });
  const [pwError, setPwError] = useState<string | null>(null);
  const [pwDone, setPwDone] = useState(false);
  const [pwBusy, setPwBusy] = useState(false);

  const [delPassword, setDelPassword] = useState("");
  const [delError, setDelError] = useState<string | null>(null);
  const [delArmed, setDelArmed] = useState(false);
  const [delBusy, setDelBusy] = useState(false);

  const handlePasswordSubmit = async (e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setPwDone(false);
    if (pw.next.length < 8) {
      setPwError("New password must be at least 8 characters.");
      return;
    }
    if (pw.next !== pw.confirm) {
      setPwError("New password and confirmation do not match.");
      return;
    }
    setPwBusy(true);
    setPwError(null);
    try {
      await changePassword({
        currentPassword: pw.current,
        newPassword: pw.next,
      });
      setPw({ current: "", next: "", confirm: "" });
      setPwDone(true);
    } catch (err) {
      setPwError(extractApiError(err));
    } finally {
      setPwBusy(false);
    }
  };

  const handleDelete = async (e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setDelBusy(true);
    setDelError(null);
    try {
      await deleteAccount(delPassword);
      clearAuthTokens();
      router.replace("/login");
    } catch (err) {
      setDelError(extractApiError(err));
      setDelBusy(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.head}>
        <div>
          <p className={styles.eyebrow}>The office</p>
          <h1 className={styles.title}>Settings</h1>
          <p className={styles.subtitle}>
            {user?.email
              ? `Signed in as ${user.email}.`
              : "Manage your account."}
          </p>
        </div>
      </div>

      <section className={styles.card}>
        <div className={styles.cardHd}>
          <KeyRound size={16} aria-hidden />
          <h2 className={styles.cardTitle}>Change password</h2>
        </div>
        <form className={styles.form} onSubmit={handlePasswordSubmit}>
          <div className={styles.field}>
            <label className={styles.label} htmlFor="pw-current">
              Current password
            </label>
            <input
              id="pw-current"
              type="password"
              className={styles.input}
              value={pw.current}
              onChange={(e) => setPw({ ...pw, current: e.target.value })}
              autoComplete="current-password"
              required
            />
          </div>
          <div className={styles.fieldRow}>
            <div className={styles.field}>
              <label className={styles.label} htmlFor="pw-next">
                New password
              </label>
              <input
                id="pw-next"
                type="password"
                className={styles.input}
                value={pw.next}
                onChange={(e) => setPw({ ...pw, next: e.target.value })}
                autoComplete="new-password"
                minLength={8}
                required
              />
            </div>
            <div className={styles.field}>
              <label className={styles.label} htmlFor="pw-confirm">
                Confirm new password
              </label>
              <input
                id="pw-confirm"
                type="password"
                className={styles.input}
                value={pw.confirm}
                onChange={(e) => setPw({ ...pw, confirm: e.target.value })}
                autoComplete="new-password"
                minLength={8}
                required
              />
            </div>
          </div>
          {pwError ? (
            <p className={styles.error} role="alert">
              {pwError}
            </p>
          ) : null}
          {pwDone ? (
            <p className={styles.success} role="status">
              Password changed. It takes effect from your next sign-in.
            </p>
          ) : null}
          <div className={styles.actions}>
            <button
              type="submit"
              className={styles.btnPrimary}
              disabled={pwBusy}
            >
              {pwBusy ? "Saving…" : "Change password"}
            </button>
          </div>
        </form>
      </section>

      <section className={`${styles.card} ${styles.cardDanger}`}>
        <div className={styles.cardHd}>
          <ShieldAlert size={16} aria-hidden />
          <h2 className={styles.cardTitle}>Close the books</h2>
        </div>
        <p className={styles.dangerText}>
          Deleting your account erases every entry, budget, pot, and bill —
          permanently. Export your ledger to CSV first if you want a copy.
        </p>
        {!delArmed ? (
          <div className={styles.actions}>
            <button
              type="button"
              className={styles.btnDangerGhost}
              onClick={() => setDelArmed(true)}
            >
              Delete account…
            </button>
          </div>
        ) : (
          <form className={styles.form} onSubmit={handleDelete}>
            <div className={styles.field}>
              <label className={styles.label} htmlFor="del-password">
                Confirm with your current password
              </label>
              <input
                id="del-password"
                type="password"
                className={styles.input}
                value={delPassword}
                onChange={(e) => setDelPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>
            {delError ? (
              <p className={styles.error} role="alert">
                {delError}
              </p>
            ) : null}
            <div className={styles.actions}>
              <button
                type="button"
                className={styles.btnGhost}
                onClick={() => {
                  setDelArmed(false);
                  setDelPassword("");
                  setDelError(null);
                }}
                disabled={delBusy}
              >
                Keep my account
              </button>
              <button
                type="submit"
                className={styles.btnDanger}
                disabled={delBusy || delPassword.length === 0}
              >
                {delBusy ? "Deleting…" : "Delete everything"}
              </button>
            </div>
          </form>
        )}
      </section>
    </div>
  );
}

"use client";

import { useEffect, useState } from "react";
import { Search } from "lucide-react";

import { searchUsers, type AdminUser } from "@/lib/services";
import { extractApiError } from "@/lib/apiClient";
import styles from "./admin.module.css";

export default function AdminPage() {
  const [query, setQuery] = useState("");
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const timer = window.setTimeout(async () => {
      setLoading(true);
      try {
        const result = await searchUsers(query);
        if (!cancelled) {
          setUsers(result);
          setError(null);
        }
      } catch (err) {
        if (!cancelled) setError(extractApiError(err));
      } finally {
        if (!cancelled) setLoading(false);
      }
    }, 250);
    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [query]);

  return (
    <div className={styles.page}>
      <div className={styles.head}>
        <p className={styles.eyebrow}>The back office</p>
        <h1 className={styles.title}>Accounts</h1>
        <p className={styles.subtitle}>
          Search registered accounts by email. Shows up to 50 matches.
        </p>
      </div>

      <label className={styles.searchWrap}>
        <Search size={16} aria-hidden />
        <span className="sr-only">Search by email</span>
        <input
          type="search"
          className={styles.search}
          placeholder="Search by email…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          autoFocus
        />
      </label>

      {error ? (
        <p className={styles.error} role="alert">
          {error}
        </p>
      ) : (
        <div className={styles.tableWrap}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Email</th>
                <th>Username</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td className={styles.email}>{u.email}</td>
                  <td>{u.username}</td>
                  <td className={styles.role}>{u.role}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {!loading && users.length === 0 ? (
            <p className={styles.empty}>No accounts match.</p>
          ) : null}
        </div>
      )}
    </div>
  );
}

import LoginCard from "@/components/login/LoginCard";
import RedirectIfAuthenticated from "@/components/auth/RedirectIfAuthenticated";
import WordmarkStamp from "@/components/brand/WordmarkStamp";
import SealStamp from "@/components/brand/SealStamp";
import styles from "@/components/auth/auth.module.css";

export default function LoginPage() {
  return (
    <main className={styles.page}>
      <RedirectIfAuthenticated />
      <div className={styles.book}>
        <aside className={styles.brand}>
          <div className={styles.brandTop}>
            <WordmarkStamp className={styles.brandWordmark} />
          </div>
          <div>
            <h2 className={styles.brandTitle}>Welcome back to your books.</h2>
            <p className={styles.brandText}>
              Pick up where you left off — the running balance is exactly where
              you left it.
            </p>
          </div>
          <SealStamp className={styles.brandSeal} />
        </aside>
        <section className={styles.form}>
          <LoginCard />
        </section>
      </div>
    </main>
  );
}

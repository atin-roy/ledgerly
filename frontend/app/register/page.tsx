import SignUpCard from "@/components/register/SignUpCard";
import RedirectIfAuthenticated from "@/components/auth/RedirectIfAuthenticated";
import WordmarkStamp from "@/components/brand/WordmarkStamp";
import SealStamp from "@/components/brand/SealStamp";
import styles from "@/components/auth/auth.module.css";

export default function RegisterPage() {
  return (
    <main className={styles.page}>
      <RedirectIfAuthenticated />
      <div className={styles.book}>
        <aside className={styles.brand}>
          <div className={styles.brandTop}>
            <WordmarkStamp className={styles.brandWordmark} />
          </div>
          <div>
            <h2 className={styles.brandTitle}>
              Open an account, start your ledger.
            </h2>
            <p className={styles.brandText}>
              Track transactions, set monthly allotments, and put money aside in
              reserves — all in one honest ledger.
            </p>
          </div>
          <SealStamp className={styles.brandSeal} />
        </aside>
        <section className={styles.form}>
          <SignUpCard />
        </section>
      </div>
    </main>
  );
}

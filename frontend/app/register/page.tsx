import Image from "next/image";
import illustration from "@/public/images/register-illustration.svg";
import SignUpCard from "@/components/register/SignUpCard";

export default function RegisterPage() {
  return (
    <main className="flex min-h-screen w-full items-center justify-center bg-amber-50 px-4 py-12">
      <div className="register-book flex w-full max-w-6xl flex-col overflow-hidden rounded-3xl md:flex-row">
        <div className="register-side register-image relative flex-1 overflow-hidden">
          <div className="relative h-full w-full overflow-hidden bg-slate-900">
            <Image
              src={illustration}
              alt="Register illustration"
              className="h-full w-full object-cover"
              priority
              sizes="(min-width: 1024px) 50vw, 100vw"
            />
            <div className="pointer-events-none absolute inset-0 bg-gradient-to-b from-transparent via-black/20 to-black/70" />
            <div className="pointer-events-none absolute left-6 top-6 text-sm font-semibold uppercase tracking-[0.5em] text-white/90">
              ledgerly
            </div>
            <div className="pointer-events-none absolute bottom-6 left-6 right-6 space-y-3 text-white">
              <p className="text-xl font-semibold leading-tight md:text-2xl">
                Keep track of your money and save for your future
              </p>
              <p className="text-base leading-relaxed text-white/80">
                Ledgerly puts you in control of your spending. Track
                transactions, set budgets, and add to savings pots easily.
              </p>
            </div>
          </div>
        </div>
        <div className="register-side register-card flex-1 p-6 md:p-10">
          <SignUpCard />
        </div>
      </div>
    </main>
  );
}

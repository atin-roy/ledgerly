import PageTitle from "@/components/PageTitle";
import PotCard from "@/components/pots/PotCard";
import { formatCurrency, formatPercentage, pots } from "@/app/pots/data";

export default function PotsPage() {
  const totalSaved = pots.reduce((acc, pot) => acc + pot.saved, 0);
  const totalTarget = pots.reduce((acc, pot) => acc + pot.target, 0);
  const rawProgress = totalTarget ? (totalSaved / totalTarget) * 100 : 0;
  const progressWidth = Math.min(Math.max(rawProgress, 0), 100);
  const progressLabel = `${formatPercentage(rawProgress)}%`;

  const renderPotActions = () => (
    <div className="flex flex-wrap gap-3">
      <button
        type="button"
        className="rounded-full border border-[var(--color-grey-300)] px-4 py-2 text-xs font-semibold uppercase tracking-[0.3em] text-[var(--color-grey-600)] transition hover:border-[var(--color-grey-500)] hover:text-[var(--color-grey-900)]"
      >
        + Add Money
      </button>
      <button
        type="button"
        className="rounded-full border border-[var(--color-grey-300)] px-4 py-2 text-xs font-semibold uppercase tracking-[0.3em] text-[var(--color-grey-600)] transition hover:border-[var(--color-grey-500)] hover:text-[var(--color-grey-900)]"
      >
        Withdraw
      </button>
    </div>
  );

  return (
    <div className="min-h-screen bg-beige-100 pt-12 pb-24">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <PageTitle title="Pots" />

        <section className="space-y-6 pb-12">
          <div className="rounded-3xl bg-white p-6 shadow-xl">
            <div className="flex flex-col gap-6 sm:flex-row sm:items-end sm:justify-between">
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
                  Total saved
                </p>
                <p className="text-3xl font-semibold text-(--color-grey-900)">
                  {formatCurrency(totalSaved)}
                </p>
                <p className="mt-1 text-sm text-(--color-grey-600)">
                  {pots.length} active pots • Target {formatCurrency(totalTarget)}
                </p>
              </div>
              <div className="text-right">
                <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
                  Progress
                </p>
                <p className="text-3xl font-semibold text-(--color-grey-900)">
                  {progressLabel}
                </p>
              </div>
            </div>
            <div className="mt-4 h-2 overflow-hidden rounded-full bg-(--color-beige-300)">
              <div
                className="h-full rounded-full transition-all duration-300 ease-in-out"
                style={{
                  width: `${progressWidth}%`,
                  backgroundColor: "var(--color-green)",
                }}
              />
            </div>
          </div>

          <div className="grid gap-6 md:grid-cols-2">
            {pots.map((pot) => (
              <PotCard
                key={pot.id}
                name={pot.name}
                saved={pot.saved}
                target={pot.target}
                accentColor={pot.accentColor}
                description={pot.description}
                actions={renderPotActions()}
              />
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}

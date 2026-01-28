import {
  formatBudgetCurrency,
  type BudgetSummaryBreakdown,
} from "@/app/budgets/data";

interface BudgetSummaryProps {
  title: string;
  description?: string;
  totalLimit: number;
  totalSpent: number;
  totalRemaining: number;
  breakdown: BudgetSummaryBreakdown[];
}

export default function BudgetSummary({
  title,
  description,
  totalLimit,
  totalSpent,
  totalRemaining,
  breakdown,
}: BudgetSummaryProps) {
  const progressPercent = totalLimit
    ? Math.min(100, Math.round((totalSpent / totalLimit) * 100))
    : 0;

  return (
    <div className="flex flex-col gap-6 rounded-3xl bg-white p-8 shadow-xl">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h2 className="text-3xl font-semibold text-(--color-grey-900)">
            {title}
          </h2>
          {description ? (
            <p className="mt-1 text-sm text-(--color-grey-600)">{description}</p>
          ) : null}
        </div>
        <span className="text-sm uppercase tracking-[0.2em] text-(--color-grey-600)">
          {progressPercent}% used
        </span>
      </div>

      <div className="h-3 overflow-hidden rounded-full bg-(--color-beige-300)">
        <div
          className="h-full rounded-full bg-(--color-green)"
          style={{ width: `${progressPercent}%` }}
        />
      </div>

      <div className="grid grid-cols-2 gap-4 text-sm text-(--color-grey-600)">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-400)">
            Spent
          </p>
          <p className="text-3xl font-semibold text-(--color-grey-900)">
            {formatBudgetCurrency(totalSpent)}
          </p>
        </div>
        <div className="text-right">
          <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-400)">
            Remaining
          </p>
          <p className="text-3xl font-semibold text-(--color-grey-900)">
            {formatBudgetCurrency(totalRemaining)}
          </p>
        </div>
      </div>

      <div className="flex items-center justify-between rounded-2xl border border-(--color-beige-300) bg-(--color-beige-100) px-5 py-4 text-sm">
        <p className="text-sm font-semibold text-(--color-grey-900)">Guardrail</p>
        <p className="text-sm text-(--color-grey-600)">
          {formatBudgetCurrency(totalLimit)} total
        </p>
      </div>

      <div className="space-y-3">
        <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
          Budget breakdown
        </p>
        <div className="grid gap-3 md:grid-cols-2">
          {breakdown.map((item) => (
            <div
              key={item.label}
              className="flex items-center justify-between rounded-2xl border border-(--color-beige-300) bg-grey-100/60 px-4 py-3"
            >
              <div className="flex items-center gap-3">
                <span
                  className="inline-flex h-2.5 w-2.5 rounded-full"
                  style={{ backgroundColor: item.color }}
                />
                <div>
                  <p className="text-sm font-semibold text-(--color-grey-900)">
                    {item.label}
                  </p>
                  <p className="text-xs text-(--color-grey-600)">
                    {formatBudgetCurrency(item.spent)} spent
                  </p>
                </div>
              </div>
              <p className="text-sm font-semibold text-(--color-grey-900)">
                {formatBudgetCurrency(item.limit)}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

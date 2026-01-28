import {
  formatBudgetCurrency,
  formatBudgetDate,
  type BudgetRecentSpending,
} from "@/app/budgets/data";

interface BudgetCardProps {
  name: string;
  limit: number;
  spent: number;
  color: string;
  recentSpending: BudgetRecentSpending[];
}

export default function BudgetCard({
  name,
  limit,
  spent,
  color,
  recentSpending,
}: BudgetCardProps) {
  const remaining = Math.max(limit - spent, 0);
  const percent = limit ? Math.min(100, Math.round((spent / limit) * 100)) : 0;

  return (
    <div className="flex flex-col gap-5 rounded-3xl bg-white p-6 shadow-lg">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-lg font-semibold text-(--color-grey-900)">{name}</p>
          <p className="text-sm text-(--color-grey-600)">
            Monthly limit {formatBudgetCurrency(limit)}
          </p>
        </div>
        <span className="text-xs font-semibold uppercase tracking-[0.3em] text-(--color-grey-600)">
          {percent}% used
        </span>
      </div>

      <div className="h-3 overflow-hidden rounded-full bg-(--color-beige-300)">
        <div
          className="h-full rounded-full"
          style={{ width: `${percent}%`, backgroundColor: color }}
        />
      </div>

      <div className="grid grid-cols-2 gap-4 text-sm text-(--color-grey-600)">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-400)">
            Spent
          </p>
          <p className="text-lg font-semibold text-(--color-grey-900)">
            {formatBudgetCurrency(spent)}
          </p>
        </div>
        <div className="text-right">
          <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-400)">
            Remaining
          </p>
          <p className="text-lg font-semibold text-(--color-grey-900)">
            {formatBudgetCurrency(remaining)}
          </p>
        </div>
      </div>

      <div className="space-y-3">
        <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
          Latest spending
        </p>
        <ul className="space-y-3">
          {recentSpending.map((entry) => (
            <li key={entry.id} className="flex items-center justify-between gap-4">
              <div className="flex flex-col">
                <p className="text-sm font-semibold text-(--color-grey-900)">
                  {entry.label}
                </p>
                <p className="text-xs text-(--color-grey-600)">
                  {formatBudgetDate(entry.date)}
                </p>
              </div>
              <span className="text-sm font-semibold text-(--color-grey-900)">
                -{formatBudgetCurrency(entry.amount)}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

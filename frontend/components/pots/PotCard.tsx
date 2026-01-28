import type { ReactNode } from "react";

import { formatCurrency, formatPercentage } from "@/app/pots/data";

interface PotCardProps {
  name: string;
  saved: number;
  target: number;
  accentColor?: string;
  description?: string;
  actions?: ReactNode;
}

const DEFAULT_COLOR = "var(--color-purple)";

export default function PotCard({
  name,
  saved,
  target,
  accentColor,
  description,
  actions,
}: PotCardProps) {
  const normalizedAccent = accentColor ?? DEFAULT_COLOR;
  const savedCurrency = formatCurrency(saved);
  const targetCurrency = formatCurrency(target);
  const rawPercent = target ? (saved / target) * 100 : 0;
  const progressValue = Number.isFinite(rawPercent) ? rawPercent : 0;
  const widthPercent = Math.min(Math.max(progressValue, 0), 100);
  const percentLabel = `${formatPercentage(progressValue)}%`;

  return (
    <article className="flex flex-col gap-6 rounded-3xl bg-white p-6 shadow-xl">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-center gap-3">
          <span
            className="h-3 w-3 rounded-full"
            style={{ backgroundColor: normalizedAccent }}
          />
          <p className="text-lg font-semibold text-(--color-grey-900)">{name}</p>
        </div>
        <span className="text-xs font-semibold uppercase tracking-[0.3em] text-(--color-grey-600)">
          {percentLabel} completed
        </span>
      </div>
      {description && (
        <p className="text-sm text-(--color-grey-600)">{description}</p>
      )}
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
          Total Saved
        </p>
        <p className="text-3xl font-semibold text-(--color-grey-900)">
          {savedCurrency}
        </p>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-(--color-beige-300)">
        <div
          className="h-full rounded-full transition-all duration-300 ease-in-out"
          style={{
            width: `${widthPercent}%`,
            backgroundColor: normalizedAccent,
          }}
        />
      </div>
      <div className="flex items-center justify-between text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
        <span>{percentLabel} of target</span>
        <span>Target of {targetCurrency}</span>
      </div>
      {actions ? <div className="mt-4">{actions}</div> : null}
    </article>
  );
}

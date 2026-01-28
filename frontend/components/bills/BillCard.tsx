import {
  Bill,
  BillStatus,
  formatBillDueDate,
  formatCurrency,
} from "@/app/bills/data";

const statusLabel: Record<BillStatus, string> = {
  paid: "Paid",
  pending: "Pending",
  overdue: "Overdue",
  cancelled: "Cancelled",
};

const statusDotColor: Record<BillStatus, string> = {
  paid: "var(--color-green)",
  pending: "var(--color-yellow)",
  overdue: "var(--color-red)",
  cancelled: "var(--color-grey-300)",
};

const statusTextColor: Record<BillStatus, string> = {
  paid: "text-emerald-600",
  pending: "text-slate-500",
  overdue: "text-rose-600",
  cancelled: "text-slate-500",
};

export default function BillCard({ bill }: { bill: Bill }) {
  return (
    <article className="flex items-center justify-between gap-4 rounded-3xl border border-slate-100 bg-white px-4 py-4 shadow-[0_20px_40px_rgba(15,22,42,0.08)]">
      <div className="flex flex-1 flex-col gap-1">
        <p className="text-base font-semibold text-slate-900">
          {bill.title}
        </p>
        <span className="flex items-center gap-1 text-sm">
          <span
            className="h-2 w-2 rounded-full"
            style={{ backgroundColor: statusDotColor[bill.status] }}
          />
          <span className={statusTextColor[bill.status]}>
            {statusLabel[bill.status]}
          </span>
        </span>
      </div>
      <div className="flex flex-col items-end text-right">
        <span className="text-lg font-semibold text-slate-900">
          {formatCurrency(bill.amount)}
        </span>
        <span className="text-xs uppercase tracking-wide text-slate-400">
          {formatBillDueDate(bill.nextDue)}
        </span>
      </div>
    </article>
  );
}

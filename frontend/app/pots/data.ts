const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
});

const percentFormatter = new Intl.NumberFormat("en-US", {
  maximumFractionDigits: 2,
});

export function formatCurrency(value: number) {
  return currencyFormatter.format(value);
}

export function formatPercentage(value: number) {
  if (!Number.isFinite(value)) {
    return "0";
  }

  const clamped = Math.min(Math.max(value, 0), 100);
  return percentFormatter.format(clamped);
}

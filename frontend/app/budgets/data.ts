const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
});

export function formatBudgetCurrency(value: number) {
  return currencyFormatter.format(value);
}

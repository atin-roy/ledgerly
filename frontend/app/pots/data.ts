export interface Pot {
  id: string;
  name: string;
  saved: number;
  target: number;
  accentColor: string;
  description?: string;
}

export const pots: Pot[] = [
  {
    id: "pot-1",
    name: "Savings",
    saved: 159,
    target: 2000,
    accentColor: "var(--color-green)",
    description: "Emergency cash bucket for future surprises.",
  },
  {
    id: "pot-2",
    name: "Concert Ticket",
    saved: 110,
    target: 150,
    accentColor: "var(--color-purple)",
    description: "Front row seats to the summer show.",
  },
  {
    id: "pot-3",
    name: "Gift",
    saved: 40,
    target: 60,
    accentColor: "var(--color-cyan)",
    description: "Holiday presents for friends and family.",
  },
  {
    id: "pot-4",
    name: "New Laptop",
    saved: 10,
    target: 1000,
    accentColor: "var(--color-gold)",
    description: "Upgrade hardware for design workstreams.",
  },
  {
    id: "pot-5",
    name: "Holiday",
    saved: 531,
    target: 1440,
    accentColor: "var(--color-purple-soft)",
    description: "European getaway in the spring.",
  },
];

const currencyFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
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

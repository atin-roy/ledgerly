interface CurrentBalanceProps {
  balance: number;
  currency?: string;
  className?: string;
}

export const CurrentBalance: React.FC<CurrentBalanceProps> = ({
  balance,
  currency = "USD",
  className = "",
}) => {
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(amount);
  };

  const isPositive = balance >= 0;

  return (
    <div
      className={`bg-white dark:bg-zinc-900 rounded-xl border border-zinc-200 dark:border-zinc-800 p-6 shadow-sm hover:shadow-md transition-shadow ${className}`}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div
            className={`w-10 h-10 rounded-full flex items-center justify-center ${
              isPositive
                ? "bg-green-100 dark:bg-green-900/20"
                : "bg-red-100 dark:bg-red-900/20"
            }`}
          >
            <svg
              className={`w-5 h-5 ${
                isPositive
                  ? "text-green-600 dark:text-green-400"
                  : "text-red-600 dark:text-red-400"
              }`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isPositive ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6"
                />
              )}
            </svg>
          </div>
          <div>
            <p className="text-sm font-medium text-zinc-500 dark:text-zinc-400">
              Current Balance
            </p>
            <p
              className={`text-2xl font-bold ${
                isPositive
                  ? "text-green-600 dark:text-green-400"
                  : "text-red-600 dark:text-red-400"
              }`}
            >
              {formatCurrency(balance)}
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-1">
          <span
            className={`text-sm font-medium px-2 py-1 rounded-full ${
              isPositive
                ? "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
                : "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            }`}
          >
            {isPositive ? "+" : ""}
            {(balance * 0.05).toFixed(2)}%
          </span>
        </div>
      </div>

      <div className="mt-4 pt-4 border-t border-zinc-100 dark:border-zinc-800">
        <div className="flex justify-between text-sm text-zinc-500 dark:text-zinc-400">
          <span>Last updated</span>
          <span>{new Date().toLocaleDateString()}</span>
        </div>
      </div>
    </div>
  );
};

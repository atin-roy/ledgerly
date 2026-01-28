import BudgetCard from "@/components/budgets/BudgetCard";
import BudgetSummary from "@/components/budgets/BudgetSummary";
import PageTitle from "@/components/PageTitle";
import { budgetSummary, budgets } from "@/app/budgets/data";

export default function BudgetsPage() {
  return (
    <div className="min-h-screen bg-beige-100 py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <PageTitle title="Budgets" />

        <section className="space-y-6 pb-12">
          <BudgetSummary
            title={budgetSummary.title}
            description={budgetSummary.description}
            totalLimit={budgetSummary.totalLimit}
            totalSpent={budgetSummary.totalSpent}
            totalRemaining={budgetSummary.totalRemaining}
            breakdown={budgetSummary.breakdown}
          />

          <div className="grid gap-6 md:grid-cols-2">
            {budgets.map((budget) => (
              <BudgetCard
                key={budget.id}
                name={budget.name}
                limit={budget.limit}
                spent={budget.spent}
                color={budget.color}
                recentSpending={budget.recentSpending}
              />
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}

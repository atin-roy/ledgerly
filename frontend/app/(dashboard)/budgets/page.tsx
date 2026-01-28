 "use client";

import { SyntheticEvent, useState } from "react";
import { ChevronDown } from "lucide-react";

import BudgetCard from "@/components/budgets/BudgetCard";
import BudgetSummary from "@/components/budgets/BudgetSummary";
import PageTitle from "@/components/PageTitle";
import CustomSelect from "@/components/ui/CustomSelect";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import { transactionCategoryOptions } from "@/app/transactions/data";
import { budgetSummary, budgets } from "@/app/budgets/data";

const budgetCategories = transactionCategoryOptions.filter(
  (category) => category !== "All Transactions",
);

const budgetModalFields = {
  category: budgetCategories[0],
  limit: "",
  description: "",
};

export default function BudgetsPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(budgetModalFields);

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleCategoryChange = (category: string) => {
    setFormValues((prev) => ({ ...prev, category }));
  };

  const handleFormSubmit = (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsModalOpen(false);
    setFormValues(budgetModalFields);
  };

  return (
    <div className="min-h-screen bg-beige-100 py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <PageTitle title="Budgets" />
          <Button
            variant="ghost"
            size="sm"
            className={primaryActionButtonClass}
            onClick={() => setIsModalOpen(true)}
          >
            New budget
          </Button>
        </div>

        <div className="relative">
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

          {isModalOpen && (
            <div className="absolute inset-0 z-40 bg-black/40 px-4">
              <div className="flex justify-center pt-40">
                <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl">
                  <div className="mb-4 flex items-center justify-between">
                    <h3 className="text-lg font-semibold text-[var(--color-grey-900)]">
                      New budget
                    </h3>
                    <button
                      type="button"
                      className="text-sm font-semibold text-slate-500 hover:text-slate-700"
                      onClick={() => setIsModalOpen(false)}
                    >
                      Close
                    </button>
                  </div>
                  <form className="mt-2 space-y-4" onSubmit={handleFormSubmit}>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Category
                      </span>
                      <CustomSelect
                        value={formValues.category}
                        options={budgetCategories}
                        onChange={handleCategoryChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Budget category"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Limit
                      </span>
                      <input
                        name="limit"
                        value={formValues.limit}
                        onChange={handleFormChange}
                        placeholder="0.00"
                        type="number"
                        min={0}
                        step="0.01"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Description
                      </span>
                      <input
                        name="description"
                        value={formValues.description}
                        onChange={handleFormChange}
                        placeholder="Add notes about this goal"
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <div className="flex justify-center gap-3 pt-1">
                      <Button
                        variant="ghost"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-rose-600 text-white shadow-lg focus-visible:ring-rose-500 active:translate-y-[1px] active:scale-95 hover:bg-rose-600 hover:text-white hover:shadow-none"
                        onClick={() => setIsModalOpen(false)}
                        type="button"
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="ghost"
                        type="submit"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-emerald-600 text-white shadow-lg focus-visible:ring-emerald-500 active:translate-y-[1px] active:scale-95 hover:bg-emerald-600 hover:text-white hover:shadow-none"
                      >
                        Save
                      </Button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

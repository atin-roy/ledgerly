 "use client";

import { SyntheticEvent, useState, useEffect, type MouseEvent } from "react";
import { ChevronDown } from "lucide-react";

import BudgetCard from "@/components/budgets/BudgetCard";
import BudgetSummary from "@/components/budgets/BudgetSummary";
import PageTitle from "@/components/PageTitle";
import CustomSelect from "@/components/ui/CustomSelect";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import { transactionCategoryOptions } from "@/app/transactions/data";
import { getBudgets, getCategories, createBudget, deleteBudget, type BudgetResponse, type CategoryResponse } from "@/lib/services";
import ContextMenu, { createEditMenuItem, createDeleteMenuItem } from "@/components/ui/ContextMenu";
import ConfirmDialog from "@/components/ui/ConfirmDialog";

const budgetCategories = transactionCategoryOptions.filter(
  (category) => category !== "All Transactions",
);

const budgetModalFields = {
  category: budgetCategories[0],
  limit: "",
};

export default function BudgetsPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(budgetModalFields);
  const [budgets, setBudgets] = useState<BudgetResponse[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{ x: number; y: number; budget: BudgetResponse } | null>(null);
  const [confirmDialog, setConfirmDialog] = useState<{ isOpen: boolean; budgetId?: number; message?: string }>({ isOpen: false });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [budgetsData, categoriesData] = await Promise.all([
        getBudgets(),
        getCategories(),
      ]);
      
      console.log("Budgets data:", budgetsData);
      console.log("Categories data:", categoriesData);
      
      // Ensure data is an array
      const budgetsArray = Array.isArray(budgetsData) ? budgetsData : [];
      const categoriesArray = Array.isArray(categoriesData) ? categoriesData : [];
      
      setBudgets(budgetsArray);
      setCategories(categoriesArray);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load budgets");
      console.error("Error loading data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleCategoryChange = (category: string) => {
    setFormValues((prev) => ({
      ...prev,
      category: category as (typeof budgetCategories)[number],
    }));
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    try {
      const category = categories.find(c => c.name === formValues.category);
      if (!category) {
        setConfirmDialog({ isOpen: true, message: "Please select a valid category" });
        return;
      }

      await createBudget({
        amount: parseFloat(formValues.limit),
        categoryId: category.id,
      });
      
      setIsModalOpen(false);
      setFormValues(budgetModalFields);
      await loadData();
    } catch (err) {
      console.error("Error creating budget:", err);
      setConfirmDialog({ isOpen: true, message: "Failed to create budget. Please try again." });
    }
  };

  const handleDelete = async (budgetId: number) => {
    try {
      await deleteBudget(budgetId);
      await loadData();
    } catch (err) {
      console.error("Error deleting budget:", err);
      setConfirmDialog({ isOpen: true, message: "Failed to delete budget. Please try again." });
    }
  };

  const handleContextMenu = (event: MouseEvent<HTMLDivElement>, budget: BudgetResponse) => {
    event.preventDefault();
    setContextMenu({
      x: event.clientX,
      y: event.clientY,
      budget,
    });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-beige-100 py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Budgets" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-gray-600">Loading budgets...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-beige-100 py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Budgets" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={loadData} className={primaryActionButtonClass}>
              Try Again
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const totalLimit = budgets.reduce((sum, b) => sum + b.amount, 0);
  const totalSpent = 0; // We'll need to calculate this from transactions
  const totalRemaining = totalLimit - totalSpent;

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
              title="Budget Overview"
              description="Track your spending limits"
              totalLimit={totalLimit}
              totalSpent={totalSpent}
              totalRemaining={totalRemaining}
              breakdown={[]}
            />

            <div className="grid gap-6 md:grid-cols-2">
              {budgets.length === 0 ? (
                <div className="col-span-2 rounded-3xl border border-dashed border-slate-300 p-12 text-center">
                  <p className="text-slate-600 mb-4">No budgets yet. Create your first budget!</p>
                  <Button
                    variant="ghost"
                    size="sm"
                    className={primaryActionButtonClass}
                    onClick={() => setIsModalOpen(true)}
                  >
                    Create First Budget
                  </Button>
                </div>
              ) : (
                budgets.map((budget) => {
                  const category = categories.find(c => c.id === budget.categoryId);
                  return (
                    <div
                      key={budget.id}
                      onContextMenu={(e) => handleContextMenu(e, budget)}
                      className="cursor-context-menu"
                    >
                      <BudgetCard
                        name={category?.name || "Unknown"}
                        limit={budget.amount}
                        spent={0}
                        color="var(--color-green)"
                        recentSpending={[]}
                      />
                    </div>
                  );
                })
              )}
            </div>
          </section>

          {isModalOpen && (
            <div className="absolute inset-x-0 top-0 bottom-12 z-40 rounded-3xl bg-black/40 px-4">
              <div className="flex justify-center pt-40">
                <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl">
                  <div className="mb-4 flex items-center justify-between">
                    <h3 className="text-lg font-semibold text-(--color-grey-900)">
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
                        required
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <div className="flex justify-center gap-3 pt-1">
                      <Button
                        variant="ghost"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-rose-600 text-white shadow-lg focus-visible:ring-rose-500 active:translate-y-px active:scale-95 hover:bg-rose-600 hover:text-white hover:shadow-none"
                        onClick={() => setIsModalOpen(false)}
                        type="button"
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="ghost"
                        type="submit"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-emerald-600 text-white shadow-lg focus-visible:ring-emerald-500 active:translate-y-px active:scale-95 hover:bg-emerald-600 hover:text-white hover:shadow-none"
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

      {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          items={[
            createEditMenuItem(() => {
              // TODO: Implement edit functionality
              setConfirmDialog({ isOpen: true, message: "Edit budget functionality coming soon!" });
            }),
            createDeleteMenuItem(() => {
              setConfirmDialog({
                isOpen: true,
                budgetId: contextMenu.budget.id,
                message: "Are you sure you want to delete this budget? This action cannot be undone.",
              });
            }),
          ]}
          onClose={() => setContextMenu(null)}
        />
      )}

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        title={confirmDialog.budgetId ? "Delete Budget" : "Notice"}
        message={confirmDialog.message || ""}
        confirmLabel={confirmDialog.budgetId ? "Delete" : "OK"}
        cancelLabel={confirmDialog.budgetId ? "Cancel" : "Close"}
        variant={confirmDialog.budgetId ? "danger" : "default"}
        onConfirm={() => {
          if (confirmDialog.budgetId) {
            handleDelete(confirmDialog.budgetId);
          }
        }}
        onCancel={() => setConfirmDialog({ isOpen: false })}
      />
    </div>
  );
}

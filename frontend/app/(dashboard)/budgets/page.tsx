"use client";

import { SyntheticEvent, useEffect, useState, type MouseEvent } from "react";
import {
  ChevronDown,
  MoreHorizontal,
  Pencil,
  Plus,
  Trash2,
  X,
} from "lucide-react";

import { formatBudgetCurrency } from "@/app/budgets/data";
import {
  getBudgets,
  getCategories,
  createBudget,
  updateBudget,
  deleteBudget,
  type BudgetResponse,
  type CategoryResponse,
} from "@/lib/services";
import styles from "./budgets.module.css";

const emptyForm = { category: "", limit: "" };

export default function BudgetsPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(emptyForm);
  const [editingBudget, setEditingBudget] = useState<BudgetResponse | null>(
    null,
  );
  const [budgets, setBudgets] = useState<BudgetResponse[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    budget: BudgetResponse;
  } | null>(null);

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
      setBudgets(Array.isArray(budgetsData) ? budgetsData : []);
      setCategories(Array.isArray(categoriesData) ? categoriesData : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load budgets");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!contextMenu) return;
    const close = () => setContextMenu(null);
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && close();
    const id = setTimeout(() => {
      document.addEventListener("mousedown", close);
      document.addEventListener("keydown", onKey);
      window.addEventListener("scroll", close, true);
    }, 0);
    return () => {
      clearTimeout(id);
      document.removeEventListener("mousedown", close);
      document.removeEventListener("keydown", onKey);
      window.removeEventListener("scroll", close, true);
    };
  }, [contextMenu]);

  const openNew = () => {
    setEditingBudget(null);
    setFormValues({ category: categories[0]?.name ?? "", limit: "" });
    setIsModalOpen(true);
  };

  const handleEdit = (budget: BudgetResponse) => {
    const category = categories.find((c) => c.id === budget.categoryId);
    setFormValues({
      category: category?.name ?? "",
      limit: budget.amount.toString(),
    });
    setEditingBudget(budget);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingBudget(null);
    setFormValues(emptyForm);
  };

  const handleFormChange = (
    event: SyntheticEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const target = event.target as HTMLInputElement | HTMLSelectElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      const category = categories.find((c) => c.name === formValues.category);
      if (!category) {
        alert("Please select a valid category");
        return;
      }
      const amount = parseFloat(formValues.limit);
      if (isNaN(amount) || amount <= 0) {
        alert("Please enter a valid limit");
        return;
      }

      if (editingBudget) {
        await updateBudget(editingBudget.id, {
          amount,
          categoryId: category.id,
        });
      } else {
        await createBudget({ amount, categoryId: category.id });
      }
      closeModal();
      await loadData();
    } catch (err) {
      console.error("Error saving budget:", err);
      alert("Failed to save budget. Please try again.");
    }
  };

  const handleDelete = async (budgetId: number) => {
    if (!confirm("Delete this allotment? This action cannot be undone.")) {
      return;
    }
    try {
      await deleteBudget(budgetId);
      await loadData();
    } catch (err) {
      console.error("Error deleting budget:", err);
      alert("Failed to delete budget. Please try again.");
    }
  };

  const openContextMenu = (
    event: MouseEvent<HTMLElement>,
    budget: BudgetResponse,
  ) => {
    event.preventDefault();
    setContextMenu({
      x: Math.min(event.clientX, window.innerWidth - 176),
      y: Math.min(event.clientY, window.innerHeight - 96),
      budget,
    });
  };

  const totalLimit = budgets.reduce((sum, b) => sum + b.amount, 0);
  const totalSpent = budgets.reduce((sum, b) => sum + b.spent, 0);
  const totalRemaining = totalLimit - totalSpent;

  const masthead = (
    <div className={styles.head}>
      <div>
        <p className={styles.eyebrow}>Allotments</p>
        <h1 className={styles.title}>Budgets</h1>
        <p className={styles.subtitle}>
          Spending limits set against each category this month.
        </p>
      </div>
      <button type="button" className={styles.newBtn} onClick={openNew}>
        <Plus size={15} aria-hidden />
        New budget
      </button>
    </div>
  );

  if (isLoading) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>Totalling the allotments…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>
          <p>{error}</p>
          <button type="button" onClick={loadData} className={styles.retry}>
            Try again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      {masthead}

      <div className={styles.summary}>
        <div className={styles.sumCell}>
          <div className={styles.sumK}>Total allotted</div>
          <div className={styles.sumV}>{formatBudgetCurrency(totalLimit)}</div>
        </div>
        <div className={styles.sumCell}>
          <div className={styles.sumK}>Spent</div>
          <div className={`${styles.sumV} ${styles.sumSpent}`}>
            {formatBudgetCurrency(totalSpent)}
          </div>
        </div>
        <div className={styles.sumCell}>
          <div className={styles.sumK}>Remaining</div>
          <div
            className={`${styles.sumV} ${
              totalRemaining < 0 ? styles.sumOver : styles.sumLeft
            }`}
          >
            {formatBudgetCurrency(totalRemaining)}
          </div>
        </div>
      </div>

      <section className={styles.sheet}>
        <div className={styles.sheetHd}>
          <h2 className={styles.sheetTitle}>Allotment Ledger</h2>
          <span className={styles.sheetMeta}>
            {budgets.length} {budgets.length === 1 ? "budget" : "budgets"}
          </span>
        </div>

        {budgets.length === 0 ? (
          <div className={styles.emptyPaper}>
            <p>No allotments set. Add one to start tracking a category.</p>
            <button type="button" className={styles.emptyBtn} onClick={openNew}>
              <Plus size={14} aria-hidden />
              New budget
            </button>
          </div>
        ) : (
          <div className={styles.scroll}>
            <table className={styles.ledger}>
              <thead>
                <tr>
                  <th className={`${styles.th} ${styles.thLeft}`}>Category</th>
                  <th className={styles.th}>Allotted</th>
                  <th className={styles.th}>Spent</th>
                  <th className={styles.th}>Remaining</th>
                  <th className={styles.th}>Used</th>
                  <th className={styles.th} aria-label="Actions" />
                </tr>
              </thead>
              <tbody>
                {budgets.map((budget) => {
                  const category = categories.find(
                    (c) => c.id === budget.categoryId,
                  );
                  const spent = budget.spent ?? 0;
                  const remaining = budget.amount - spent;
                  const pct =
                    budget.amount > 0 ? (spent / budget.amount) * 100 : 0;
                  const over = spent > budget.amount;
                  return (
                    <tr
                      key={budget.id}
                      className={styles.tr}
                      onContextMenu={(e) => openContextMenu(e, budget)}
                    >
                      <td className={`${styles.td} ${styles.tdLeft}`}>
                        <span className={styles.category}>
                          {category?.name ?? "Unknown"}
                        </span>
                      </td>
                      <td className={`${styles.td} ${styles.figure}`}>
                        {formatBudgetCurrency(budget.amount)}
                      </td>
                      <td
                        className={`${styles.td} ${styles.figure} ${styles.spentFig}`}
                      >
                        {formatBudgetCurrency(spent)}
                      </td>
                      <td
                        className={`${styles.td} ${styles.figure} ${styles.leftFig} ${
                          remaining < 0 ? styles.leftNeg : ""
                        }`}
                      >
                        {formatBudgetCurrency(remaining)}
                      </td>
                      <td className={styles.td}>
                        <span
                          className={`${styles.usage} ${
                            over ? styles.usageOver : ""
                          }`}
                        >
                          <span
                            className={`${styles.bar} ${
                              over ? styles.barOver : ""
                            }`}
                          >
                            <span
                              className={styles.barFill}
                              style={{ width: `${Math.min(pct, 100)}%` }}
                            />
                          </span>
                          <span className={styles.usagePct}>
                            {Math.round(pct)}%{over ? " · over" : ""}
                          </span>
                        </span>
                      </td>
                      <td className={styles.td}>
                        <button
                          type="button"
                          className={styles.rowActions}
                          onClick={(e) => openContextMenu(e, budget)}
                          aria-label={`Actions for ${category?.name ?? "budget"}`}
                        >
                          <MoreHorizontal size={16} aria-hidden />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
              <tfoot className={styles.foot}>
                <tr>
                  <td className={`${styles.td} ${styles.tdLeft}`}>
                    <span className={styles.footLabel}>Totals</span>
                  </td>
                  <td className={`${styles.td} ${styles.footFig}`}>
                    {formatBudgetCurrency(totalLimit)}
                  </td>
                  <td className={`${styles.td} ${styles.footFig} ${styles.spentFig}`}>
                    {formatBudgetCurrency(totalSpent)}
                  </td>
                  <td
                    className={`${styles.td} ${styles.footFig} ${
                      totalRemaining < 0 ? styles.leftNeg : styles.leftFig
                    }`}
                  >
                    {formatBudgetCurrency(totalRemaining)}
                  </td>
                  <td className={styles.td} />
                  <td className={styles.td} />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </section>

      {contextMenu && (
        <div
          className={styles.menu}
          style={{ left: contextMenu.x, top: contextMenu.y }}
          onMouseDown={(e) => e.stopPropagation()}
        >
          <button
            type="button"
            className={styles.menuItem}
            onClick={() => {
              handleEdit(contextMenu.budget);
              setContextMenu(null);
            }}
          >
            <Pencil size={15} aria-hidden />
            Edit budget
          </button>
          <button
            type="button"
            className={`${styles.menuItem} ${styles.menuDanger}`}
            onClick={() => {
              handleDelete(contextMenu.budget.id);
              setContextMenu(null);
            }}
          >
            <Trash2 size={15} aria-hidden />
            Delete budget
          </button>
        </div>
      )}

      {isModalOpen && (
        <div
          className={styles.overlay}
          onMouseDown={(e) => {
            if (e.target === e.currentTarget) closeModal();
          }}
        >
          <div className={styles.modal} role="dialog" aria-modal="true">
            <div className={styles.modalHd}>
              <h3 className={styles.modalTitle}>
                {editingBudget ? "Edit budget" : "New budget"}
              </h3>
              <button
                type="button"
                className={styles.modalClose}
                onClick={closeModal}
                aria-label="Close"
              >
                <X size={18} aria-hidden />
              </button>
            </div>
            <form className={styles.form} onSubmit={handleFormSubmit}>
              <div className={styles.field}>
                <label className={styles.label} htmlFor="budget-category">
                  Category
                </label>
                <div className={styles.selectWrap}>
                  <select
                    id="budget-category"
                    name="category"
                    className={styles.select}
                    value={formValues.category}
                    onChange={handleFormChange}
                  >
                    {categories.length === 0 ? (
                      <option value="">No categories</option>
                    ) : (
                      categories.map((c) => (
                        <option key={c.id} value={c.name}>
                          {c.name}
                        </option>
                      ))
                    )}
                  </select>
                  <ChevronDown className={styles.selectChevron} aria-hidden />
                </div>
              </div>

              <div className={styles.field}>
                <label className={styles.label} htmlFor="budget-limit">
                  Monthly limit
                </label>
                <input
                  id="budget-limit"
                  name="limit"
                  className={`${styles.input} ${styles.inputNum}`}
                  value={formValues.limit}
                  onChange={handleFormChange}
                  placeholder="0.00"
                  type="number"
                  min={0}
                  step="0.01"
                  required
                />
              </div>

              <div className={styles.modalActions}>
                <button
                  type="button"
                  className={styles.btnGhost}
                  onClick={closeModal}
                >
                  Cancel
                </button>
                <button type="submit" className={styles.btnPrimary}>
                  {editingBudget ? "Save changes" : "Set allotment"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

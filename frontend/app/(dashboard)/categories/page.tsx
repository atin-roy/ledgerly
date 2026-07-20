"use client";

import {
  SyntheticEvent,
  useEffect,
  useRef,
  useMemo,
  useState,
  type MouseEvent,
} from "react";
import { MoreHorizontal, Pencil, Plus, Search, Trash2, X } from "lucide-react";

import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  type CategoryResponse,
} from "@/lib/services";
import styles from "./categories.module.css";

type CategoryType = "INCOME" | "EXPENSE";

const emptyForm: { name: string; type: CategoryType } = {
  name: "",
  type: "EXPENSE",
};

export default function CategoriesPage() {
  const [searchTerm, setSearchTerm] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(emptyForm);
  const [editingCategory, setEditingCategory] =
    useState<CategoryResponse | null>(null);

  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    category: CategoryResponse;
  } | null>(null);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getCategories();
      setCategories(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load categories");
    } finally {
      setIsLoading(false);
    }
  };

  const filtered = useMemo(() => {
    const q = searchTerm.trim().toLowerCase();
    if (!q) return categories;
    return categories.filter((c) => c.name.toLowerCase().includes(q));
  }, [categories, searchTerm]);

  const incomeAccounts = filtered.filter((c) => c.type === "INCOME");
  const expenseAccounts = filtered.filter((c) => c.type === "EXPENSE");
  const incomeTotal = categories.filter((c) => c.type === "INCOME").length;
  const expenseTotal = categories.filter((c) => c.type === "EXPENSE").length;

  // Close the context menu on any outside interaction.
  const menuRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!contextMenu) return;
    const close = () => setContextMenu(null);
    // Guard by target: React delegates events from `document` in the app
    // router, so a mousedown on a menu item reaches this listener too and
    // stopPropagation on the menu cannot block it.
    const onDown = (e: Event) => {
      if (e.target instanceof Node && menuRef.current?.contains(e.target)) {
        return;
      }
      close();
    };
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && close();
    const id = setTimeout(() => {
      document.addEventListener("mousedown", onDown);
      document.addEventListener("keydown", onKey);
      window.addEventListener("scroll", close, true);
    }, 0);
    return () => {
      clearTimeout(id);
      document.removeEventListener("mousedown", onDown);
      document.removeEventListener("keydown", onKey);
      window.removeEventListener("scroll", close, true);
    };
  }, [contextMenu]);

  const handleOpenNew = () => {
    setEditingCategory(null);
    setFormValues(emptyForm);
    setIsModalOpen(true);
  };

  const handleEdit = (category: CategoryResponse) => {
    setEditingCategory(category);
    setFormValues({ name: category.name, type: category.type });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingCategory(null);
    setFormValues(emptyForm);
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      const name = formValues.name.trim();
      if (!name) {
        alert("Please enter an account name");
        return;
      }
      if (editingCategory) {
        await updateCategory(editingCategory.id, { name, type: formValues.type });
      } else {
        await createCategory({ name, type: formValues.type });
      }
      closeModal();
      await loadCategories();
    } catch (err) {
      console.error("Error saving category:", err);
      alert("Failed to save account. Please try again.");
    }
  };

  const handleDelete = async (categoryId: number) => {
    if (
      !confirm(
        "Delete this account? Entries posted to it may be affected. This cannot be undone.",
      )
    ) {
      return;
    }
    try {
      await deleteCategory(categoryId);
      await loadCategories();
    } catch (err) {
      console.error("Error deleting category:", err);
      alert("Failed to delete account. It may be in use by transactions.");
    }
  };

  const openContextMenu = (
    event: MouseEvent<HTMLElement>,
    category: CategoryResponse,
  ) => {
    event.preventDefault();
    setContextMenu({
      x: Math.min(event.clientX, window.innerWidth - 176),
      y: Math.min(event.clientY, window.innerHeight - 96),
      category,
    });
  };

  const masthead = (
    <div className={styles.head}>
      <div>
        <p className={styles.eyebrow}>The Ledger</p>
        <h1 className={styles.title}>Chart of Accounts</h1>
        <p className={styles.subtitle}>
          The accounts every entry is posted against.
        </p>
      </div>
      <button type="button" className={styles.newBtn} onClick={handleOpenNew}>
        <Plus size={15} aria-hidden />
        New account
      </button>
    </div>
  );

  if (isLoading) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>Opening the chart…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>
          <p>{error}</p>
          <button type="button" onClick={loadCategories} className={styles.retry}>
            Try again
          </button>
        </div>
      </div>
    );
  }

  const renderFolio = (
    accounts: CategoryResponse[],
    variant: "credit" | "debit",
  ) => {
    const isCredit = variant === "credit";
    return (
      <section
        className={`${styles.folio} ${
          isCredit ? styles.folioCredit : styles.folioDebit
        }`}
      >
        <div className={styles.folioHd}>
          <h2 className={styles.folioTitle}>
            {isCredit ? "Income accounts" : "Expense accounts"}
          </h2>
          <span className={styles.folioMeta}>
            {isCredit ? incomeTotal : expenseTotal}
          </span>
        </div>
        {accounts.length === 0 ? (
          <p className={styles.folioEmpty}>
            {searchTerm.trim()
              ? "No accounts match your search."
              : isCredit
                ? "No income accounts yet."
                : "No expense accounts yet."}
          </p>
        ) : (
          accounts.map((category) => (
            <div
              key={category.id}
              className={styles.row}
              onContextMenu={(e) => openContextMenu(e, category)}
            >
              <div className={styles.rowMain}>
                <span
                  className={`${styles.mark} ${
                    isCredit ? styles.markCredit : styles.markDebit
                  }`}
                >
                  {isCredit ? "Cr" : "Dr"}
                </span>
                <span className={styles.accountName}>{category.name}</span>
              </div>
              <button
                type="button"
                className={styles.rowActions}
                onClick={(e) => openContextMenu(e, category)}
                aria-label={`Actions for ${category.name}`}
              >
                <MoreHorizontal size={16} aria-hidden />
              </button>
            </div>
          ))
        )}
      </section>
    );
  };

  return (
    <div className={styles.page}>
      {masthead}

      <div className={styles.summary}>
        <div className={styles.sumCell}>
          <p className={styles.sumK}>Total accounts</p>
          <p className={styles.sumV}>{categories.length}</p>
        </div>
        <div className={`${styles.sumCell} ${styles.sumCredit}`}>
          <p className={styles.sumK}>Income</p>
          <p className={styles.sumV}>{incomeTotal}</p>
        </div>
        <div className={`${styles.sumCell} ${styles.sumDebit}`}>
          <p className={styles.sumK}>Expense</p>
          <p className={styles.sumV}>{expenseTotal}</p>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.search}>
          <Search className={styles.searchIcon} aria-hidden />
          <input
            className={styles.searchInput}
            type="search"
            placeholder="Search accounts"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            aria-label="Search accounts"
          />
        </div>
      </div>

      <div className={styles.folios}>
        {renderFolio(incomeAccounts, "credit")}
        {renderFolio(expenseAccounts, "debit")}
      </div>

      {contextMenu && (
        <div
          ref={menuRef}
          className={styles.menu}
          style={{ left: contextMenu.x, top: contextMenu.y }}
          onMouseDown={(e) => e.stopPropagation()}
        >
          <button
            type="button"
            className={styles.menuItem}
            onClick={() => {
              handleEdit(contextMenu.category);
              setContextMenu(null);
            }}
          >
            <Pencil size={15} aria-hidden />
            Edit account
          </button>
          <button
            type="button"
            className={`${styles.menuItem} ${styles.menuDanger}`}
            onClick={() => {
              handleDelete(contextMenu.category.id);
              setContextMenu(null);
            }}
          >
            <Trash2 size={15} aria-hidden />
            Delete account
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
                {editingCategory ? "Edit account" : "New account"}
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
                <label className={styles.label} htmlFor="cat-name">
                  Account name
                </label>
                <input
                  id="cat-name"
                  name="name"
                  className={styles.input}
                  value={formValues.name}
                  onChange={(e) =>
                    setFormValues((prev) => ({ ...prev, name: e.target.value }))
                  }
                  placeholder="e.g. Groceries, Salary"
                  required
                  autoFocus
                />
              </div>

              <div className={styles.field}>
                <span className={styles.label}>Type</span>
                <div className={styles.toggle}>
                  <button
                    type="button"
                    className={`${styles.toggleBtn} ${
                      formValues.type === "INCOME"
                        ? styles.toggleCreditActive
                        : ""
                    }`}
                    onClick={() =>
                      setFormValues((prev) => ({ ...prev, type: "INCOME" }))
                    }
                  >
                    Income · Cr
                  </button>
                  <button
                    type="button"
                    className={`${styles.toggleBtn} ${
                      formValues.type === "EXPENSE"
                        ? styles.toggleDebitActive
                        : ""
                    }`}
                    onClick={() =>
                      setFormValues((prev) => ({ ...prev, type: "EXPENSE" }))
                    }
                  >
                    Expense · Dr
                  </button>
                </div>
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
                  {editingCategory ? "Save changes" : "Add account"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

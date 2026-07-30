"use client";

import {
  SyntheticEvent,
  useEffect,
  useRef,
  useMemo,
  useState,
  type MouseEvent,
} from "react";
import {
  CircleAlert,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  Download,
  MoreHorizontal,
  Pencil,
  Plus,
  Search,
  Trash2,
  X,
} from "lucide-react";

import {
  formatCurrency,
  formatTransactionDate,
  getMonthOptions,
  getTransactionPage,
  localToday,
  transactionSortOptions,
  type TransactionSortOption,
  type Transaction,
} from "@/app/transactions/data";
import {
  getTransactions,
  getCategories,
  createTransaction,
  updateTransaction,
  deleteTransaction,
  getParties,
  type CategoryResponse,
  type PartyResponse,
} from "@/lib/services";
import { downloadCsv, toCsv } from "@/lib/csv";
import { extractApiError } from "@/lib/apiClient";
import styles from "./transactions.module.css";

const PAGE_SIZE = 12;

const emptyForm = {
  recipient: "",
  description: "",
  amount: "",
  category: "",
  type: "expense",
  date: "",
};

const transactionTypes = [
  { label: "Expense", value: "expense" },
  { label: "Income", value: "income" },
];

/** Signed contribution of an entry to the running account balance. */
function signedAmount(t: Transaction) {
  return t.type === "income" ? t.amount : -t.amount;
}

export default function TransactionsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [category, setCategory] = useState("All Transactions");
  const [month, setMonth] = useState("all");
  const [sortBy, setSortBy] = useState<TransactionSortOption>("latest");
  const [page, setPage] = useState(1);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(emptyForm);
  const [formError, setFormError] = useState<string | null>(null);
  const [editingTransaction, setEditingTransaction] =
    useState<Transaction | null>(null);

  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [parties, setParties] = useState<PartyResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    transaction: Transaction;
  } | null>(null);
  const [transactionToDelete, setTransactionToDelete] =
    useState<Transaction | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const deleteDialogRef = useRef<HTMLDialogElement | null>(null);
  const cancelDeleteRef = useRef<HTMLButtonElement | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [transactionsData, categoriesData, partiesData] = await Promise.all([
        getTransactions(),
        getCategories(),
        // parties feed the datalist; an empty list just means no suggestions
        getParties().catch(() => []),
      ]);

      const transactionsArray = Array.isArray(transactionsData)
        ? transactionsData
        : [];
      const categoriesArray = Array.isArray(categoriesData)
        ? categoriesData
        : [];

      const convertedTransactions: Transaction[] = transactionsArray.map(
        (t) => ({
          id: t.id.toString(),
          recipient: t.partyName || "Unknown",
          description: t.description || "",
          category: t.categoryName,
          categoryId: t.categoryId,
          date: t.date.split("T")[0],
          amount: Math.abs(t.amount),
          type: t.amount >= 0 ? "income" : "expense",
        }),
      );

      setTransactions(convertedTransactions);
      setCategories(categoriesArray);
      setParties(Array.isArray(partiesData) ? partiesData : []);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to load transactions",
      );
    } finally {
      setIsLoading(false);
    }
  };

  // Running account balance after each entry, computed over the full set in
  // chronological order — this is what makes the sheet a ledger, not a list.
  const balanceById = useMemo(() => {
    const chrono = [...transactions].sort((a, b) => {
      const byDate = new Date(a.date).getTime() - new Date(b.date).getTime();
      if (byDate !== 0) return byDate;
      return a.id.localeCompare(b.id, undefined, { numeric: true });
    });
    const map = new Map<string, number>();
    let balance = 0;
    for (const entry of chrono) {
      balance += signedAmount(entry);
      map.set(entry.id, balance);
    }
    return map;
  }, [transactions]);

  const accountBalance = useMemo(
    () => transactions.reduce((sum, t) => sum + signedAmount(t), 0),
    [transactions],
  );

  const categoryFilterOptions = useMemo(
    () => [
      "All Transactions",
      "Income",
      "Expense",
      ...categories.map((c) => c.name),
    ],
    [categories],
  );

  const monthOptions = useMemo(
    () => getMonthOptions(transactions),
    [transactions],
  );

  // if the selected month vanishes (e.g. its last entry was deleted), fall back
  useEffect(() => {
    if (month !== "all" && !monthOptions.some((o) => o.value === month)) {
      setMonth("all");
    }
  }, [month, monthOptions]);

  const { data, totalItems, totalPages, currentPage } = useMemo(
    () =>
      getTransactionPage(transactions, {
        searchTerm,
        category,
        month,
        sortBy,
        page,
        pageSize: PAGE_SIZE,
      }),
    [transactions, searchTerm, category, month, sortBy, page],
  );

  useEffect(() => {
    if (currentPage !== page) {
      setPage(currentPage);
    }
  }, [currentPage, page]);

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

  useEffect(() => {
    const dialog = deleteDialogRef.current;
    if (!dialog) return;

    if (transactionToDelete && !dialog.open) {
      dialog.showModal();
      cancelDeleteRef.current?.focus();
    } else if (!transactionToDelete && dialog.open) {
      dialog.close();
    }
  }, [transactionToDelete]);

  const pageDebit = data
    .filter((t) => t.type === "expense")
    .reduce((sum, t) => sum + t.amount, 0);
  const pageCredit = data
    .filter((t) => t.type === "income")
    .reduce((sum, t) => sum + t.amount, 0);

  const handleOpenNew = () => {
    setEditingTransaction(null);
    setFormValues({
      ...emptyForm,
      date: localToday(),
      category: categories.length > 0 ? categories[0].name : "",
    });
    setIsModalOpen(true);
  };

  const handleEdit = (transaction: Transaction) => {
    const categoryName =
      categories.find((c) => c.id === transaction.categoryId)?.name ??
      transaction.category;
    setFormValues({
      recipient: transaction.recipient,
      description: transaction.description || "",
      amount: transaction.amount.toString(),
      category: categoryName,
      type: transaction.type,
      date: transaction.date,
    });
    setEditingTransaction(transaction);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingTransaction(null);
    setFormValues(emptyForm);
    setFormError(null);
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
      const selectedCategory = categories.find(
        (c) => c.name === formValues.category,
      );
      if (!selectedCategory) {
        setFormError("Pick a category for this entry.");
        return;
      }

      const amount = parseFloat(formValues.amount);
      if (isNaN(amount) || amount <= 0) {
        setFormError("Enter an amount greater than zero.");
        return;
      }

      // send the local wall-clock date as-is; converting to UTC shifts the
      // entry to the previous day for anyone east of Greenwich
      const dateTime = formValues.date + "T00:00:00";
      const payload = {
        amount: formValues.type === "expense" ? -amount : amount,
        date: dateTime,
        categoryId: selectedCategory.id,
        partyName: formValues.recipient.trim() || undefined,
        description: formValues.description.trim() || undefined,
      };

      if (editingTransaction) {
        await updateTransaction(parseInt(editingTransaction.id), payload);
      } else {
        await createTransaction(payload);
      }

      closeModal();
      await loadData();
    } catch (err) {
      console.error("Error saving transaction:", err);
      setFormError(extractApiError(err));
    }
  };

  const closeDeleteDialog = () => {
    if (isDeleting) return;
    setTransactionToDelete(null);
    setDeleteError(null);
  };

  const handleDelete = async () => {
    if (!transactionToDelete) return;

    try {
      setIsDeleting(true);
      setDeleteError(null);
      await deleteTransaction(parseInt(transactionToDelete.id));
      setTransactionToDelete(null);
      await loadData();
    } catch (err) {
      console.error("Error deleting transaction:", err);
      setDeleteError("The entry could not be removed. Please try again.");
    } finally {
      setIsDeleting(false);
    }
  };

  const openContextMenu = (
    event: MouseEvent<HTMLElement>,
    transaction: Transaction,
  ) => {
    event.preventDefault();
    setContextMenu({
      x: Math.min(event.clientX, window.innerWidth - 176),
      y: Math.min(event.clientY, window.innerHeight - 96),
      transaction,
    });
  };

  const handleExportCsv = () => {
    // the filtered view, all pages, in the same order as on screen
    const { data: rows } = getTransactionPage(transactions, {
      searchTerm,
      category,
      month,
      sortBy,
      page: 1,
      pageSize: Math.max(transactions.length, 1),
    });
    const csv = toCsv([
      ["Date", "Party", "Note", "Category", "Debit", "Credit", "Balance"],
      ...rows.map((t) => [
        t.date,
        t.recipient,
        t.description || "",
        t.category,
        t.type === "expense" ? t.amount.toFixed(2) : "",
        t.type === "income" ? t.amount.toFixed(2) : "",
        (balanceById.get(t.id) ?? 0).toFixed(2),
      ]),
    ]);
    downloadCsv(`ledgerly-transactions-${localToday()}.csv`, csv);
  };

  const masthead = (
    <div className={styles.head}>
      <div>
        <p className={styles.eyebrow}>The Ledger</p>
        <h1 className={styles.title}>Transactions</h1>
        <p className={styles.subtitle}>
          Every entry recorded in order, with a running balance.
        </p>
      </div>
      <div className={styles.headActions}>
        <button
          type="button"
          className={styles.exportBtn}
          onClick={handleExportCsv}
          disabled={transactions.length === 0}
        >
          <Download size={15} aria-hidden />
          Export CSV
        </button>
        <button type="button" className={styles.newBtn} onClick={handleOpenNew}>
          <Plus size={15} aria-hidden />
          New entry
        </button>
      </div>
    </div>
  );

  if (isLoading) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>Opening the ledger…</div>
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

      <div className={styles.toolbar}>
        <div className={styles.search}>
          <Search className={styles.searchIcon} aria-hidden />
          <input
            className={styles.searchInput}
            type="search"
            placeholder="Search party, note, or category"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPage(1);
            }}
            aria-label="Search entries"
          />
        </div>

        <div className={styles.selectWrap}>
          <select
            className={styles.select}
            value={category}
            onChange={(e) => {
              setCategory(e.target.value);
              setPage(1);
            }}
            aria-label="Filter by category"
          >
            {categoryFilterOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
          <ChevronDown className={styles.selectChevron} aria-hidden />
        </div>

        <div className={styles.selectWrap}>
          <select
            className={styles.select}
            value={month}
            onChange={(e) => {
              setMonth(e.target.value);
              setPage(1);
            }}
            aria-label="Filter by month"
          >
            {monthOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <ChevronDown className={styles.selectChevron} aria-hidden />
        </div>

        <div className={styles.selectWrap}>
          <select
            className={styles.select}
            value={sortBy}
            onChange={(e) => {
              setSortBy(e.target.value as TransactionSortOption);
              setPage(1);
            }}
            aria-label="Sort entries"
          >
            {transactionSortOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <ChevronDown className={styles.selectChevron} aria-hidden />
        </div>
      </div>

      <section className={styles.sheet}>
        <div className={styles.sheetHd}>
          <h2 className={styles.sheetTitle}>General Ledger</h2>
          <span className={styles.sheetMeta}>
            {totalItems} {totalItems === 1 ? "entry" : "entries"}
          </span>
        </div>

        {data.length === 0 ? (
          <p className={styles.emptyPaper}>
            {transactions.length === 0
              ? "No entries recorded yet. Add your first to open the ledger."
              : "No entries match this view."}
          </p>
        ) : (
          <div className={styles.scroll}>
            <table className={styles.ledger}>
              <thead>
                <tr>
                  <th className={`${styles.th} ${styles.thLeft}`}>Date</th>
                  <th className={`${styles.th} ${styles.thLeft}`}>
                    Particulars
                  </th>
                  <th className={styles.th}>Debit</th>
                  <th className={styles.th}>Credit</th>
                  <th className={styles.th}>Balance</th>
                  <th className={styles.th} aria-label="Actions" />
                </tr>
              </thead>
              <tbody>
                {data.map((t) => {
                  const isIncome = t.type === "income";
                  const balance = balanceById.get(t.id) ?? 0;
                  return (
                    <tr
                      key={t.id}
                      className={styles.tr}
                      onContextMenu={(e) => openContextMenu(e, t)}
                    >
                      <td className={`${styles.td} ${styles.tdLeft} ${styles.date}`}>
                        {formatTransactionDate(t.date)}
                      </td>
                      <td className={`${styles.td} ${styles.tdLeft}`}>
                        <span className={styles.particulars}>
                          <span className={styles.party}>{t.recipient}</span>
                          {t.description ? (
                            <span className={styles.note}>{t.description}</span>
                          ) : null}
                          <span className={styles.cat}>{t.category}</span>
                        </span>
                      </td>
                      <td className={`${styles.td} ${styles.figure}`}>
                        {isIncome ? (
                          <span className={styles.dash}>—</span>
                        ) : (
                          <span className={styles.debitFig}>
                            {formatCurrency(t.amount)}
                          </span>
                        )}
                      </td>
                      <td className={`${styles.td} ${styles.figure}`}>
                        {isIncome ? (
                          <span className={styles.creditFig}>
                            {formatCurrency(t.amount)}
                          </span>
                        ) : (
                          <span className={styles.dash}>—</span>
                        )}
                      </td>
                      <td
                        className={`${styles.td} ${styles.figure} ${styles.balanceFig} ${
                          balance < 0 ? styles.balanceNeg : ""
                        }`}
                      >
                        {formatCurrency(balance)}
                      </td>
                      <td className={styles.td}>
                        <button
                          type="button"
                          className={styles.rowActions}
                          onClick={(e) => openContextMenu(e, t)}
                          aria-label={`Actions for ${t.recipient}`}
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
                  <td className={`${styles.td} ${styles.tdLeft}`} colSpan={2}>
                    <span className={styles.footLabel}>Page totals</span>
                  </td>
                  <td className={`${styles.td} ${styles.footFig} ${styles.debitFig}`}>
                    {formatCurrency(pageDebit)}
                  </td>
                  <td className={`${styles.td} ${styles.footFig} ${styles.creditFig}`}>
                    {formatCurrency(pageCredit)}
                  </td>
                  <td
                    className={`${styles.td} ${styles.footFig} ${
                      accountBalance < 0 ? styles.balanceNeg : ""
                    }`}
                  >
                    {formatCurrency(accountBalance)}
                  </td>
                  <td className={styles.td} />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </section>

      {totalItems > 0 && (
        <div className={styles.pager}>
          <p className={styles.pagerInfo}>
            Page <b>{currentPage}</b> of <b>{totalPages}</b> · showing{" "}
            <b>{data.length}</b> of <b>{totalItems}</b>
          </p>
          <div className={styles.pagerBtns}>
            <button
              type="button"
              className={styles.pageBtn}
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={currentPage <= 1}
            >
              <ChevronLeft size={15} aria-hidden />
              Prev
            </button>
            <button
              type="button"
              className={styles.pageBtn}
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage >= totalPages}
            >
              Next
              <ChevronRight size={15} aria-hidden />
            </button>
          </div>
        </div>
      )}

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
              handleEdit(contextMenu.transaction);
              setContextMenu(null);
            }}
          >
            <Pencil size={15} aria-hidden />
            Edit entry
          </button>
          <button
            type="button"
            className={`${styles.menuItem} ${styles.menuDanger}`}
            onClick={() => {
              setTransactionToDelete(contextMenu.transaction);
              setContextMenu(null);
            }}
          >
            <Trash2 size={15} aria-hidden />
            Delete entry
          </button>
        </div>
      )}

      <dialog
        ref={deleteDialogRef}
        className={styles.confirmDialog}
        aria-labelledby="delete-entry-title"
        aria-describedby="delete-entry-description"
        onCancel={(event) => {
          event.preventDefault();
          closeDeleteDialog();
        }}
        onClose={() => {
          setTransactionToDelete(null);
          setDeleteError(null);
        }}
        onMouseDown={(event) => {
          if (event.target === event.currentTarget) closeDeleteDialog();
        }}
      >
        <div className={styles.confirmBody}>
          <div className={styles.confirmIcon} aria-hidden>
            <CircleAlert size={22} strokeWidth={1.7} />
          </div>
          <p className={styles.confirmKicker}>Permanent action</p>
          <h3 id="delete-entry-title" className={styles.confirmTitle}>
            Remove this entry?
          </h3>
          <p id="delete-entry-description" className={styles.confirmCopy}>
            This will permanently remove the entry and recalculate your running
            balance.
          </p>

          {transactionToDelete ? (
            <div className={styles.confirmEntry}>
              <div>
                <strong className={styles.confirmParty}>
                  {transactionToDelete.recipient}
                </strong>
                <span className={styles.confirmMeta}>
                  {formatTransactionDate(transactionToDelete.date)} ·{" "}
                  {transactionToDelete.category}
                </span>
              </div>
              <span
                className={`${styles.confirmAmount} ${
                  transactionToDelete.type === "expense"
                    ? styles.confirmDebit
                    : styles.confirmCredit
                }`}
              >
                {transactionToDelete.type === "expense" ? "−" : "+"}
                {formatCurrency(transactionToDelete.amount)}
              </span>
            </div>
          ) : null}

          {deleteError ? (
            <p className={styles.confirmError} role="alert">
              {deleteError}
            </p>
          ) : null}

          <div className={styles.confirmActions}>
            <button
              ref={cancelDeleteRef}
              type="button"
              className={styles.confirmCancel}
              onClick={closeDeleteDialog}
              disabled={isDeleting}
            >
              Keep entry
            </button>
            <button
              type="button"
              className={styles.confirmDelete}
              onClick={handleDelete}
              disabled={isDeleting}
            >
              <Trash2 size={15} aria-hidden />
              {isDeleting ? "Removing…" : "Delete entry"}
            </button>
          </div>
        </div>
      </dialog>

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
                {editingTransaction ? "Edit entry" : "New entry"}
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
                <label className={styles.label} htmlFor="tx-recipient">
                  Party
                </label>
                <input
                  id="tx-recipient"
                  name="recipient"
                  className={styles.input}
                  value={formValues.recipient}
                  onChange={handleFormChange}
                  placeholder="Who it was with"
                  list="party-options"
                  autoComplete="off"
                />
                <datalist id="party-options">
                  {parties.map((p) => (
                    <option key={p.id} value={p.name} />
                  ))}
                </datalist>
              </div>

              <div className={styles.field}>
                <label className={styles.label} htmlFor="tx-description">
                  Note
                </label>
                <input
                  id="tx-description"
                  name="description"
                  className={styles.input}
                  value={formValues.description}
                  onChange={handleFormChange}
                  placeholder="Optional detail"
                />
              </div>

              <div className={styles.fieldRow}>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="tx-amount">
                    Amount
                  </label>
                  <input
                    id="tx-amount"
                    name="amount"
                    className={`${styles.input} ${styles.inputNum}`}
                    value={formValues.amount}
                    onChange={handleFormChange}
                    placeholder="0.00"
                    type="number"
                    min={0}
                    step="0.01"
                  />
                </div>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="tx-date">
                    Date
                  </label>
                  <input
                    id="tx-date"
                    name="date"
                    className={styles.input}
                    value={formValues.date}
                    onChange={handleFormChange}
                    type="date"
                    required
                  />
                </div>
              </div>

              <div className={styles.fieldRow}>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="tx-category">
                    Category
                  </label>
                  <div className={styles.selectWrap}>
                    <select
                      id="tx-category"
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
                  <label className={styles.label} htmlFor="tx-type">
                    Type
                  </label>
                  <div className={styles.selectWrap}>
                    <select
                      id="tx-type"
                      name="type"
                      className={styles.select}
                      value={formValues.type}
                      onChange={handleFormChange}
                    >
                      {transactionTypes.map((type) => (
                        <option key={type.value} value={type.value}>
                          {type.label}
                        </option>
                      ))}
                    </select>
                    <ChevronDown className={styles.selectChevron} aria-hidden />
                  </div>
                </div>
              </div>

              {formError ? (
                <p className={styles.formError} role="alert">
                  {formError}
                </p>
              ) : null}

              <div className={styles.modalActions}>
                <button
                  type="button"
                  className={styles.btnGhost}
                  onClick={closeModal}
                >
                  Cancel
                </button>
                <button type="submit" className={styles.btnPrimary}>
                  {editingTransaction ? "Save changes" : "Record entry"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

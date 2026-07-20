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
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  MoreHorizontal,
  Pencil,
  Plus,
  Search,
  Trash2,
  X,
} from "lucide-react";

import {
  BILL_PAGE_SIZE,
  billSortOptions,
  formatCurrency,
  formatBillDueDate,
  getBillPage,
  getBillSummary,
  type BillSortOption,
  type Bill,
  type BillStatus,
} from "@/app/bills/data";
import { getBills, createBill, updateBill, deleteBill } from "@/lib/services";
import styles from "./bills.module.css";

const emptyForm = {
  title: "",
  amount: "",
  date: new Date().toISOString().split("T")[0],
  status: "pending" as BillStatus,
};

const billStatuses: { label: string; value: BillStatus }[] = [
  { label: "Pending", value: "pending" },
  { label: "Paid", value: "paid" },
  { label: "Overdue", value: "overdue" },
  { label: "Cancelled", value: "cancelled" },
];

const stampClass: Record<BillStatus, string> = {
  pending: styles.stampPending,
  paid: styles.stampPaid,
  overdue: styles.stampOverdue,
  cancelled: styles.stampCancelled,
};

function normalizeBillStatus(status: string): BillStatus {
  const normalized = status.toLowerCase();
  if (
    normalized === "pending" ||
    normalized === "paid" ||
    normalized === "overdue" ||
    normalized === "cancelled"
  ) {
    return normalized;
  }
  return "pending";
}

export default function BillsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState<BillSortOption>("soonest");
  const [page, setPage] = useState(1);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(emptyForm);
  const [editingBill, setEditingBill] = useState<Bill | null>(null);

  const [bills, setBills] = useState<Bill[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    bill: Bill;
  } | null>(null);

  useEffect(() => {
    loadBills();
  }, []);

  const loadBills = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getBills();
      const billsArray = Array.isArray(data) ? data : [];
      const converted: Bill[] = billsArray.map((b) => ({
        id: b.id.toString(),
        title: b.name,
        nextDue: b.dueDate.split("T")[0],
        amount: b.amount,
        status: normalizeBillStatus(b.status),
      }));
      setBills(converted);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load bills");
    } finally {
      setIsLoading(false);
    }
  };

  const summary = useMemo(() => getBillSummary(bills), [bills]);
  const outstanding = summary.pending.amount + summary.overdue.amount;

  const { data, totalItems, totalPages, currentPage } = useMemo(
    () =>
      getBillPage(bills, {
        searchTerm,
        sortBy,
        page,
        pageSize: BILL_PAGE_SIZE,
      }),
    [bills, searchTerm, sortBy, page],
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

  const pageTotal = data.reduce((sum, b) => sum + b.amount, 0);

  const handleOpenNew = () => {
    setEditingBill(null);
    setFormValues({ ...emptyForm, date: new Date().toISOString().split("T")[0] });
    setIsModalOpen(true);
  };

  const handleEdit = (bill: Bill) => {
    setFormValues({
      title: bill.title,
      amount: bill.amount.toString(),
      date: bill.nextDue,
      status: bill.status,
    });
    setEditingBill(bill);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingBill(null);
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
      const amount = parseFloat(formValues.amount);
      if (isNaN(amount) || amount <= 0) {
        alert("Please enter a valid amount");
        return;
      }
      const dueDate = new Date(formValues.date + "T00:00:00").toISOString();
      const payload = {
        name: formValues.title.trim(),
        amount,
        status: formValues.status.toUpperCase(),
        dueDate,
      };

      if (editingBill) {
        await updateBill(parseInt(editingBill.id), payload);
      } else {
        await createBill(payload);
      }

      closeModal();
      await loadBills();
    } catch (err) {
      console.error("Error saving bill:", err);
      alert("Failed to save bill. Please try again.");
    }
  };

  const handleDelete = async (billId: number) => {
    if (!confirm("Remove this bill from the schedule? This cannot be undone.")) {
      return;
    }
    try {
      await deleteBill(billId);
      await loadBills();
    } catch (err) {
      console.error("Error deleting bill:", err);
      alert("Failed to delete bill. Please try again.");
    }
  };

  const openContextMenu = (event: MouseEvent<HTMLElement>, bill: Bill) => {
    event.preventDefault();
    setContextMenu({
      x: Math.min(event.clientX, window.innerWidth - 176),
      y: Math.min(event.clientY, window.innerHeight - 96),
      bill,
    });
  };

  const masthead = (
    <div className={styles.head}>
      <div>
        <p className={styles.eyebrow}>The Ledger</p>
        <h1 className={styles.title}>Recurring Bills</h1>
        <p className={styles.subtitle}>
          A schedule of what&rsquo;s due, and what&rsquo;s been settled.
        </p>
      </div>
      <button type="button" className={styles.newBtn} onClick={handleOpenNew}>
        <Plus size={15} aria-hidden />
        New bill
      </button>
    </div>
  );

  if (isLoading) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>Pulling the schedule…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>
          <p>{error}</p>
          <button type="button" onClick={loadBills} className={styles.retry}>
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
          <p className={styles.sumK}>Outstanding</p>
          <p className={styles.sumV}>{formatCurrency(outstanding)}</p>
          <p className={styles.sumSub}>Pending &amp; overdue</p>
        </div>
        <div className={styles.sumCell}>
          <p className={styles.sumK}>Paid</p>
          <p className={styles.sumV}>{formatCurrency(summary.paid.amount)}</p>
          <p className={styles.sumSub}>{summary.paid.count} settled</p>
        </div>
        <div className={styles.sumCell}>
          <p className={styles.sumK}>Pending</p>
          <p className={styles.sumV}>{formatCurrency(summary.pending.amount)}</p>
          <p className={styles.sumSub}>{summary.pending.count} awaiting</p>
        </div>
        <div className={`${styles.sumCell} ${styles.sumOverdue}`}>
          <p className={styles.sumK}>Overdue</p>
          <p className={styles.sumV}>{formatCurrency(summary.overdue.amount)}</p>
          <p className={styles.sumSub}>{summary.overdue.count} past due</p>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.search}>
          <Search className={styles.searchIcon} aria-hidden />
          <input
            className={styles.searchInput}
            type="search"
            placeholder="Search by payee"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPage(1);
            }}
            aria-label="Search bills"
          />
        </div>

        <div className={styles.selectWrap}>
          <select
            className={styles.select}
            value={sortBy}
            onChange={(e) => {
              setSortBy(e.target.value as BillSortOption);
              setPage(1);
            }}
            aria-label="Sort bills"
          >
            {billSortOptions.map((option) => (
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
          <h2 className={styles.sheetTitle}>Schedule of Payables</h2>
          <span className={styles.sheetMeta}>
            {totalItems} {totalItems === 1 ? "bill" : "bills"}
          </span>
        </div>

        {data.length === 0 ? (
          <p className={styles.emptyPaper}>
            {bills.length === 0
              ? "No bills scheduled yet. Add your first to start the register."
              : "No bills match this view."}
          </p>
        ) : (
          <div className={styles.scroll}>
            <table className={styles.ledger}>
              <thead>
                <tr>
                  <th className={`${styles.th} ${styles.thLeft}`}>Due</th>
                  <th className={`${styles.th} ${styles.thLeft}`}>Payee</th>
                  <th className={`${styles.th} ${styles.thLeft}`}>Status</th>
                  <th className={styles.th}>Amount</th>
                  <th className={styles.th} aria-label="Actions" />
                </tr>
              </thead>
              <tbody>
                {data.map((bill) => (
                  <tr
                    key={bill.id}
                    className={styles.tr}
                    onContextMenu={(e) => openContextMenu(e, bill)}
                  >
                    <td className={`${styles.td} ${styles.tdLeft} ${styles.date}`}>
                      {formatBillDueDate(bill.nextDue)}
                    </td>
                    <td className={`${styles.td} ${styles.tdLeft}`}>
                      <span className={styles.payee}>{bill.title}</span>
                    </td>
                    <td className={`${styles.td} ${styles.tdLeft}`}>
                      <span className={`${styles.stamp} ${stampClass[bill.status]}`}>
                        {bill.status}
                      </span>
                    </td>
                    <td className={`${styles.td} ${styles.figure}`}>
                      {formatCurrency(bill.amount)}
                    </td>
                    <td className={styles.td}>
                      <button
                        type="button"
                        className={styles.rowActions}
                        onClick={(e) => openContextMenu(e, bill)}
                        aria-label={`Actions for ${bill.title}`}
                      >
                        <MoreHorizontal size={16} aria-hidden />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot className={styles.foot}>
                <tr>
                  <td className={`${styles.td} ${styles.tdLeft}`} colSpan={3}>
                    <span className={styles.footLabel}>Page total</span>
                  </td>
                  <td className={`${styles.td} ${styles.figure} ${styles.footFig}`}>
                    {formatCurrency(pageTotal)}
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
              handleEdit(contextMenu.bill);
              setContextMenu(null);
            }}
          >
            <Pencil size={15} aria-hidden />
            Edit bill
          </button>
          <button
            type="button"
            className={`${styles.menuItem} ${styles.menuDanger}`}
            onClick={() => {
              handleDelete(parseInt(contextMenu.bill.id));
              setContextMenu(null);
            }}
          >
            <Trash2 size={15} aria-hidden />
            Delete bill
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
                {editingBill ? "Edit bill" : "New bill"}
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
                <label className={styles.label} htmlFor="bill-title">
                  Payee
                </label>
                <input
                  id="bill-title"
                  name="title"
                  className={styles.input}
                  value={formValues.title}
                  onChange={handleFormChange}
                  placeholder="Who it's paid to"
                  required
                />
              </div>

              <div className={styles.fieldRow}>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="bill-amount">
                    Amount
                  </label>
                  <input
                    id="bill-amount"
                    name="amount"
                    className={`${styles.input} ${styles.inputNum}`}
                    value={formValues.amount}
                    onChange={handleFormChange}
                    placeholder="0.00"
                    type="number"
                    min={0}
                    step="0.01"
                    required
                  />
                </div>
                <div className={styles.field}>
                  <label className={styles.label} htmlFor="bill-date">
                    Due date
                  </label>
                  <input
                    id="bill-date"
                    name="date"
                    className={styles.input}
                    value={formValues.date}
                    onChange={handleFormChange}
                    type="date"
                    required
                  />
                </div>
              </div>

              <div className={styles.field}>
                <label className={styles.label} htmlFor="bill-status">
                  Status
                </label>
                <div className={styles.selectWrap}>
                  <select
                    id="bill-status"
                    name="status"
                    className={styles.select}
                    value={formValues.status}
                    onChange={handleFormChange}
                  >
                    {billStatuses.map((s) => (
                      <option key={s.value} value={s.value}>
                        {s.label}
                      </option>
                    ))}
                  </select>
                  <ChevronDown className={styles.selectChevron} aria-hidden />
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
                  {editingBill ? "Save changes" : "Add bill"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

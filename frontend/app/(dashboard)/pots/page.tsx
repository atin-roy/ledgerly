"use client";

import { SyntheticEvent, useEffect, useState, type MouseEvent } from "react";
import {
  Minus,
  MoreHorizontal,
  Pencil,
  Plus,
  Trash2,
  X,
} from "lucide-react";

import { formatCurrency, formatPercentage } from "@/app/pots/data";
import {
  getPots,
  createPot,
  updatePot,
  deletePot,
  type PotResponse,
} from "@/lib/services";
import styles from "./pots.module.css";

const emptyForm = { name: "", target: "", description: "" };

type MoneyModal = {
  isOpen: boolean;
  pot: PotResponse | null;
  type: "add" | "withdraw";
};

export default function PotsPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(emptyForm);
  const [editingPot, setEditingPot] = useState<PotResponse | null>(null);
  const [pots, setPots] = useState<PotResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    pot: PotResponse;
  } | null>(null);
  const [moneyModal, setMoneyModal] = useState<MoneyModal>({
    isOpen: false,
    pot: null,
    type: "add",
  });
  const [moneyAmount, setMoneyAmount] = useState("");

  useEffect(() => {
    loadPots();
  }, []);

  const loadPots = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getPots();
      setPots(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load pots");
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

  const totalSaved = pots.reduce((acc, pot) => acc + pot.saved, 0);
  const totalTarget = pots.reduce((acc, pot) => acc + pot.target, 0);
  const rawProgress = totalTarget ? (totalSaved / totalTarget) * 100 : 0;
  const progressWidth = Math.min(Math.max(rawProgress, 0), 100);

  const openNew = () => {
    setEditingPot(null);
    setFormValues(emptyForm);
    setIsModalOpen(true);
  };

  const handleEdit = (pot: PotResponse) => {
    setFormValues({
      name: pot.name,
      target: pot.target.toString(),
      description: "",
    });
    setEditingPot(pot);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingPot(null);
    setFormValues(emptyForm);
  };

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      const target = parseFloat(formValues.target);
      if (!formValues.name.trim()) {
        alert("Please give the reserve a name");
        return;
      }
      if (isNaN(target) || target <= 0) {
        alert("Please enter a valid target");
        return;
      }

      if (editingPot) {
        await updatePot(editingPot.id, { name: formValues.name.trim(), target });
      } else {
        await createPot({ name: formValues.name.trim(), target, saved: 0 });
      }
      closeModal();
      await loadPots();
    } catch (err) {
      console.error("Error saving pot:", err);
      alert("Failed to save pot. Please try again.");
    }
  };

  const openMoney = (pot: PotResponse, type: "add" | "withdraw") => {
    setMoneyModal({ isOpen: true, pot, type });
    setMoneyAmount("");
  };

  const closeMoney = () => {
    setMoneyModal({ isOpen: false, pot: null, type: "add" });
    setMoneyAmount("");
  };

  const handleMoneyOperation = async () => {
    if (!moneyModal.pot) return;
    const amount = parseFloat(moneyAmount);
    if (isNaN(amount) || amount <= 0) {
      alert("Please enter a valid amount");
      return;
    }
    try {
      const newSaved =
        moneyModal.type === "add"
          ? moneyModal.pot.saved + amount
          : Math.max(0, moneyModal.pot.saved - amount);
      await updatePot(moneyModal.pot.id, { saved: newSaved });
      closeMoney();
      await loadPots();
    } catch (err) {
      console.error("Error updating pot:", err);
      alert(`Failed to ${moneyModal.type} money. Please try again.`);
    }
  };

  const handleDelete = async (potId: number) => {
    if (!confirm("Delete this reserve? This action cannot be undone.")) {
      return;
    }
    try {
      await deletePot(potId);
      await loadPots();
    } catch (err) {
      console.error("Error deleting pot:", err);
      alert("Failed to delete pot. Please try again.");
    }
  };

  const openContextMenu = (
    event: MouseEvent<HTMLElement>,
    pot: PotResponse,
  ) => {
    event.preventDefault();
    setContextMenu({
      x: Math.min(event.clientX, window.innerWidth - 176),
      y: Math.min(event.clientY, window.innerHeight - 96),
      pot,
    });
  };

  const masthead = (
    <div className={styles.head}>
      <div>
        <p className={styles.eyebrow}>Reserves</p>
        <h1 className={styles.title}>Pots</h1>
        <p className={styles.subtitle}>
          Money set aside toward a goal, kept out of the running balance.
        </p>
      </div>
      <button type="button" className={styles.newBtn} onClick={openNew}>
        <Plus size={15} aria-hidden />
        New pot
      </button>
    </div>
  );

  if (isLoading) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>Counting the reserves…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        {masthead}
        <div className={styles.state}>
          <p>{error}</p>
          <button type="button" onClick={loadPots} className={styles.retry}>
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
        <div className={styles.sumRow}>
          <div>
            <div className={styles.sumK}>Total set aside</div>
            <div className={styles.sumV}>{formatCurrency(totalSaved)}</div>
            <div className={styles.sumSub}>
              {pots.length} {pots.length === 1 ? "reserve" : "reserves"} ·
              target {formatCurrency(totalTarget)}
            </div>
          </div>
          <div className={styles.sumPct}>
            <div className={styles.sumK}>Progress</div>
            <div className={styles.sumV}>{formatPercentage(rawProgress)}%</div>
          </div>
        </div>
        <div className={styles.summaryBar}>
          <span
            className={styles.summaryFill}
            style={{ width: `${progressWidth}%` }}
          />
        </div>
      </div>

      {pots.length === 0 ? (
        <div className={styles.empty}>
          <p>No reserves yet. Open one to start setting money aside.</p>
          <button type="button" className={styles.emptyBtn} onClick={openNew}>
            <Plus size={14} aria-hidden />
            New pot
          </button>
        </div>
      ) : (
        <div className={styles.grid}>
          {pots.map((pot) => {
            const pct = pot.target ? (pot.saved / pot.target) * 100 : 0;
            const full = pot.saved >= pot.target && pot.target > 0;
            return (
              <article
                key={pot.id}
                className={styles.card}
                onContextMenu={(e) => openContextMenu(e, pot)}
              >
                <div className={styles.cardHd}>
                  <h3 className={styles.cardName}>{pot.name}</h3>
                  <button
                    type="button"
                    className={styles.cardActions}
                    onClick={(e) => openContextMenu(e, pot)}
                    aria-label={`Actions for ${pot.name}`}
                  >
                    <MoreHorizontal size={16} aria-hidden />
                  </button>
                </div>

                <div>
                  <span className={styles.saved}>
                    {formatCurrency(pot.saved)}
                  </span>
                  <span className={styles.savedOf}>
                    of {formatCurrency(pot.target)}
                  </span>
                  {full ? <span className={styles.done}>Funded</span> : null}
                </div>

                <div className={`${styles.bar} ${full ? styles.barFull : ""}`}>
                  <span
                    className={styles.barFill}
                    style={{ width: `${Math.min(pct, 100)}%` }}
                  />
                </div>
                <div className={styles.barMeta}>
                  <span>Saved</span>
                  <span className={styles.barPct}>
                    {formatPercentage(pct)}%
                  </span>
                </div>

                <div className={styles.cardBtns}>
                  <button
                    type="button"
                    className={styles.cardBtn}
                    onClick={() => openMoney(pot, "add")}
                  >
                    <Plus size={13} aria-hidden />
                    Add
                  </button>
                  <button
                    type="button"
                    className={`${styles.cardBtn} ${styles.cardBtnWithdraw}`}
                    onClick={() => openMoney(pot, "withdraw")}
                  >
                    <Minus size={13} aria-hidden />
                    Withdraw
                  </button>
                </div>
              </article>
            );
          })}
        </div>
      )}

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
              handleEdit(contextMenu.pot);
              setContextMenu(null);
            }}
          >
            <Pencil size={15} aria-hidden />
            Edit pot
          </button>
          <button
            type="button"
            className={`${styles.menuItem} ${styles.menuDanger}`}
            onClick={() => {
              handleDelete(contextMenu.pot.id);
              setContextMenu(null);
            }}
          >
            <Trash2 size={15} aria-hidden />
            Delete pot
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
                {editingPot ? "Edit pot" : "New pot"}
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
                <label className={styles.label} htmlFor="pot-name">
                  Name
                </label>
                <input
                  id="pot-name"
                  name="name"
                  className={styles.input}
                  value={formValues.name}
                  onChange={handleFormChange}
                  placeholder="What you're saving for"
                />
              </div>
              <div className={styles.field}>
                <label className={styles.label} htmlFor="pot-target">
                  Target
                </label>
                <input
                  id="pot-target"
                  name="target"
                  className={`${styles.input} ${styles.inputNum}`}
                  value={formValues.target}
                  onChange={handleFormChange}
                  placeholder="0.00"
                  type="number"
                  min={0}
                  step="0.01"
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
                  {editingPot ? "Save changes" : "Open reserve"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {moneyModal.isOpen && moneyModal.pot && (
        <div
          className={styles.overlay}
          onMouseDown={(e) => {
            if (e.target === e.currentTarget) closeMoney();
          }}
        >
          <div className={styles.modal} role="dialog" aria-modal="true">
            <div className={styles.modalHd}>
              <h3 className={styles.modalTitle}>
                {moneyModal.type === "add" ? "Add to reserve" : "Withdraw"}
              </h3>
              <button
                type="button"
                className={styles.modalClose}
                onClick={closeMoney}
                aria-label="Close"
              >
                <X size={18} aria-hidden />
              </button>
            </div>
            <p className={styles.modalSub}>
              {moneyModal.type === "add" ? "Adding to" : "Withdrawing from"}{" "}
              <b>{moneyModal.pot.name}</b>
            </p>
            <form
              className={styles.form}
              onSubmit={(e) => {
                e.preventDefault();
                handleMoneyOperation();
              }}
            >
              <div className={styles.field}>
                <label className={styles.label} htmlFor="pot-amount">
                  Amount
                </label>
                <input
                  id="pot-amount"
                  className={`${styles.input} ${styles.inputNum}`}
                  value={moneyAmount}
                  onChange={(e) => setMoneyAmount(e.target.value)}
                  placeholder="0.00"
                  type="number"
                  min={0}
                  step="0.01"
                  autoFocus
                />
              </div>
              <div className={styles.modalActions}>
                <button
                  type="button"
                  className={styles.btnGhost}
                  onClick={closeMoney}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className={
                    moneyModal.type === "add"
                      ? styles.btnPrimary
                      : styles.btnDanger
                  }
                >
                  {moneyModal.type === "add" ? "Add" : "Withdraw"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

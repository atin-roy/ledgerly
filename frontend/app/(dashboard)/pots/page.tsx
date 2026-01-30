"use client";

import { SyntheticEvent, useState, useEffect, type MouseEvent } from "react";
import { ChevronDown } from "lucide-react";

import PageTitle from "@/components/PageTitle";
import PotCard from "@/components/pots/PotCard";
import CustomSelect from "@/components/ui/CustomSelect";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import { formatCurrency, formatPercentage } from "@/app/pots/data";
import { getPots, createPot, updatePot, deletePot, type PotResponse } from "@/lib/services";
import ContextMenu, { createEditMenuItem, createDeleteMenuItem } from "@/components/ui/ContextMenu";
import ConfirmDialog from "@/components/ui/ConfirmDialog";

const potTypeOptions = ["Travel", "Emergency Fund", "Home Improvement"];

const potModalFields = {
  name: "",
  target: "",
  category: potTypeOptions[0],
  description: "",
};

export default function PotsPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState(potModalFields);
  const [pots, setPots] = useState<PotResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [contextMenu, setContextMenu] = useState<{ x: number; y: number; pot: PotResponse } | null>(null);
  const [confirmDialog, setConfirmDialog] = useState<{ isOpen: boolean; potId?: number; message?: string }>({ isOpen: false });
  const [moneyModal, setMoneyModal] = useState<{ isOpen: boolean; pot: PotResponse | null; type: 'add' | 'withdraw' }>({ isOpen: false, pot: null, type: 'add' });
  const [moneyAmount, setMoneyAmount] = useState("");

  useEffect(() => {
    loadPots();
  }, []);

  const loadPots = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getPots();
      
      console.log("Pots data:", data);
      
      // Ensure data is an array
      const potsArray = Array.isArray(data) ? data : [];
      setPots(potsArray);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load pots");
      console.error("Error loading pots:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const totalSaved = pots.reduce((acc, pot) => acc + pot.saved, 0);
  const totalTarget = pots.reduce((acc, pot) => acc + pot.target, 0);
  const rawProgress = totalTarget ? (totalSaved / totalTarget) * 100 : 0;
  const progressWidth = Math.min(Math.max(rawProgress, 0), 100);
  const progressLabel = `${formatPercentage(rawProgress)}%`;

  const renderPotActions = (pot: PotResponse) => (
    <div className="flex flex-wrap gap-3">
      <button
        type="button"
        onClick={() => setMoneyModal({ isOpen: true, pot, type: 'add' })}
        className="rounded-full border border-[var(--color-grey-300)] px-4 py-2 text-xs font-semibold uppercase tracking-[0.3em] text-[var(--color-grey-600)] transition hover:border-[var(--color-grey-500)] hover:text-[var(--color-grey-900)]"
      >
        + Add Money
      </button>
      <button
        type="button"
        onClick={() => setMoneyModal({ isOpen: true, pot, type: 'withdraw' })}
        className="rounded-full border border-[var(--color-grey-300)] px-4 py-2 text-xs font-semibold uppercase tracking-[0.3em] text-[var(--color-grey-600)] transition hover:border-[var(--color-grey-500)] hover:text-[var(--color-grey-900)]"
      >
        Withdraw
      </button>
    </div>
  );

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleCategoryChange = (category: string) => {
    setFormValues((prev) => ({ ...prev, category }));
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    try {
      await createPot({
        name: formValues.name,
        target: parseFloat(formValues.target),
        saved: 0,
      });
      
      setIsModalOpen(false);
      setFormValues(potModalFields);
      await loadPots(); // Reload pots after creation
    } catch (err) {
      console.error("Error creating pot:", err);
      setConfirmDialog({ isOpen: true, message: "Failed to create pot. Please try again." });
    }
  };

  const handleMoneyOperation = async () => {
    if (!moneyModal.pot || !moneyAmount) return;

    try {
      const amount = parseFloat(moneyAmount);
      const newSaved = moneyModal.type === 'add' 
        ? moneyModal.pot.saved + amount 
        : Math.max(0, moneyModal.pot.saved - amount);

      await updatePot(moneyModal.pot.id, { saved: newSaved });
      setMoneyModal({ isOpen: false, pot: null, type: 'add' });
      setMoneyAmount("");
      await loadPots();
    } catch (err) {
      console.error("Error updating pot:", err);
      setConfirmDialog({ isOpen: true, message: `Failed to ${moneyModal.type} money. Please try again.` });
    }
  };

  const handleDelete = async (potId: number) => {
    try {
      await deletePot(potId);
      await loadPots();
    } catch (err) {
      console.error("Error deleting pot:", err);
      setConfirmDialog({ isOpen: true, message: "Failed to delete pot. Please try again." });
    }
  };

  const handleContextMenu = (event: MouseEvent<HTMLDivElement>, pot: PotResponse) => {
    event.preventDefault();
    setContextMenu({
      x: event.clientX,
      y: event.clientY,
      pot,
    });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-beige-100 pt-12 pb-24">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Pots" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-gray-600">Loading pots...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-beige-100 pt-12 pb-24">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Pots" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={loadPots} className={primaryActionButtonClass}>
              Try Again
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-beige-100 pt-12 pb-24">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <PageTitle title="Pots" />
          <Button
            variant="ghost"
            size="sm"
            className={primaryActionButtonClass}
            onClick={() => setIsModalOpen(true)}
          >
            New pot
          </Button>
        </div>

        <div className="relative">
          <section className="space-y-6 pb-12">
            <div className="rounded-3xl bg-white p-6 shadow-xl">
              <div className="flex flex-col gap-6 sm:flex-row sm:items-end sm:justify-between">
                <div>
                  <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
                    Total saved
                  </p>
                  <p className="text-3xl font-semibold text-(--color-grey-900)">
                    {formatCurrency(totalSaved)}
                  </p>
                  <p className="mt-1 text-sm text-(--color-grey-600)">
                    {pots.length} active pots • Target {formatCurrency(totalTarget)}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-xs uppercase tracking-[0.3em] text-(--color-grey-600)">
                    Progress
                  </p>
                  <p className="text-3xl font-semibold text-(--color-grey-900)">
                    {progressLabel}
                  </p>
                </div>
              </div>
              <div className="mt-4 h-2 overflow-hidden rounded-full bg-(--color-beige-300)">
                <div
                  className="h-full rounded-full transition-all duration-300 ease-in-out"
                  style={{
                    width: `${progressWidth}%`,
                    backgroundColor: "var(--color-green)",
                  }}
                />
              </div>
            </div>

            <div className="grid gap-6 md:grid-cols-2">
              {pots.length === 0 ? (
                <div className="col-span-2 rounded-3xl border border-dashed border-slate-300 p-12 text-center">
                  <p className="text-slate-600 mb-4">No pots yet. Create your first savings goal!</p>
                  <Button
                    variant="ghost"
                    size="sm"
                    className={primaryActionButtonClass}
                    onClick={() => setIsModalOpen(true)}
                  >
                    Create First Pot
                  </Button>
                </div>
              ) : (
                pots.map((pot) => (
                  <div
                    key={pot.id}
                    onContextMenu={(e) => handleContextMenu(e, pot)}
                    className="cursor-context-menu"
                  >
                    <PotCard
                      name={pot.name}
                      saved={pot.saved}
                      target={pot.target}
                      accentColor="var(--color-green)"
                      description=""
                      actions={renderPotActions(pot)}
                    />
                  </div>
                ))
              )}
            </div>
          </section>

          {isModalOpen && (
            <div className="absolute inset-x-0 top-0 bottom-12 z-40 rounded-3xl bg-black/40 px-4">
              <div className="flex justify-center pt-40">
                <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl">
                <div className="mb-4 flex items-center justify-between">
                  <h3 className="text-lg font-semibold text-[var(--color-grey-900)]">
                    New pot
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
                      Name
                    </span>
                    <input
                      name="name"
                      value={formValues.name}
                      onChange={handleFormChange}
                      placeholder="Goal title"
                      className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                    />
                  </label>
                  <label className="flex flex-col gap-2 text-sm text-slate-600">
                    <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                      Category
                    </span>
                    <CustomSelect
                      value={formValues.category}
                      options={potTypeOptions}
                      onChange={handleCategoryChange}
                      icon={<ChevronDown className="h-4 w-4" />}
                      trailingIcon={<ChevronDown className="h-4 w-4" />}
                      ariaLabel="Pot category"
                    />
                  </label>
                  <label className="flex flex-col gap-2 text-sm text-slate-600">
                    <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                      Target
                    </span>
                    <input
                      name="target"
                      value={formValues.target}
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
                      placeholder="Why you're saving"
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

      {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          items={[
            createEditMenuItem(() => {
              // TODO: Implement edit functionality
              setConfirmDialog({ isOpen: true, message: "Edit pot functionality coming soon!" });
            }),
            createDeleteMenuItem(() => {
              setConfirmDialog({
                isOpen: true,
                potId: contextMenu.pot.id,
                message: "Are you sure you want to delete this pot? This action cannot be undone.",
              });
            }),
          ]}
          onClose={() => setContextMenu(null)}
        />
      )}

      {moneyModal.isOpen && moneyModal.pot && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black/40" onClick={() => setMoneyModal({ isOpen: false, pot: null, type: 'add' })} />
          <div className="relative w-full max-w-md rounded-3xl bg-white p-6 shadow-2xl mx-4">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">
              {moneyModal.type === 'add' ? 'Add Money' : 'Withdraw Money'}
            </h3>
            <p className="text-sm text-slate-600 mb-4">
              {moneyModal.type === 'add' ? 'Add money to' : 'Withdraw money from'} <strong>{moneyModal.pot.name}</strong>
            </p>
            <input
              type="number"
              value={moneyAmount}
              onChange={(e) => setMoneyAmount(e.target.value)}
              placeholder="0.00"
              min={0}
              step="0.01"
              className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200 mb-6"
            />
            <div className="flex justify-end gap-3">
              <Button
                variant="ghost"
                onClick={() => {
                  setMoneyModal({ isOpen: false, pot: null, type: 'add' });
                  setMoneyAmount("");
                }}
                className="rounded-2xl px-6 py-3 text-sm font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200"
              >
                Cancel
              </Button>
              <Button
                variant="ghost"
                onClick={handleMoneyOperation}
                className={`rounded-2xl px-6 py-3 text-sm font-semibold text-white shadow-lg ${
                  moneyModal.type === 'add' 
                    ? 'bg-emerald-600 hover:bg-emerald-700' 
                    : 'bg-rose-600 hover:bg-rose-700'
                }`}
              >
                {moneyModal.type === 'add' ? 'Add' : 'Withdraw'}
              </Button>
            </div>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        title={confirmDialog.potId ? "Delete Pot" : "Notice"}
        message={confirmDialog.message || ""}
        confirmLabel={confirmDialog.potId ? "Delete" : "OK"}
        cancelLabel={confirmDialog.potId ? "Cancel" : "Close"}
        variant={confirmDialog.potId ? "danger" : "default"}
        onConfirm={() => {
          if (confirmDialog.potId) {
            handleDelete(confirmDialog.potId);
          }
        }}
        onCancel={() => setConfirmDialog({ isOpen: false })}
      />
    </div>
  );
}

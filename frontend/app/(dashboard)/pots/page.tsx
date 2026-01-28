"use client";

import { SyntheticEvent, useState } from "react";
import { ChevronDown } from "lucide-react";

import PageTitle from "@/components/PageTitle";
import PotCard from "@/components/pots/PotCard";
import CustomSelect from "@/components/ui/CustomSelect";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import { formatCurrency, formatPercentage, pots } from "@/app/pots/data";

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

  const totalSaved = pots.reduce((acc, pot) => acc + pot.saved, 0);
  const totalTarget = pots.reduce((acc, pot) => acc + pot.target, 0);
  const rawProgress = totalTarget ? (totalSaved / totalTarget) * 100 : 0;
  const progressWidth = Math.min(Math.max(rawProgress, 0), 100);
  const progressLabel = `${formatPercentage(rawProgress)}%`;

  const renderPotActions = () => (
    <div className="flex flex-wrap gap-3">
      <button
        type="button"
        className="rounded-full border border-[var(--color-grey-300)] px-4 py-2 text-xs font-semibold uppercase tracking-[0.3em] text-[var(--color-grey-600)] transition hover:border-[var(--color-grey-500)] hover:text-[var(--color-grey-900)]"
      >
        + Add Money
      </button>
      <button
        type="button"
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

  const handleFormSubmit = (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsModalOpen(false);
    setFormValues(potModalFields);
  };

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
              {pots.map((pot) => (
                <PotCard
                  key={pot.id}
                  name={pot.name}
                  saved={pot.saved}
                  target={pot.target}
                  accentColor={pot.accentColor}
                  description={pot.description}
                  actions={renderPotActions()}
                />
              ))}
            </div>
          </section>

          {isModalOpen && (
            <div className="absolute inset-0 z-40 rounded-3xl bg-black/40 px-4">
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
    </div>
  );
}

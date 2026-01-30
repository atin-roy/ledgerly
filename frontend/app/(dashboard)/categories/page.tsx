"use client";

import { ArrowUpDown, ChevronDown } from "lucide-react";
import {
  useEffect,
  useMemo,
  useState,
  type ChangeEvent,
  type SyntheticEvent,
} from "react";

import CustomSelect from "@/components/ui/CustomSelect";
import PageTitle from "@/components/PageTitle";
import { Button } from "@/components/ui/button";
import primaryActionButtonClass from "@/components/ui/primaryActionButtonClass";
import { getCategories, createCategory, updateCategory, deleteCategory, type CategoryResponse } from "@/lib/services";

const categoryTypeOptions = ["INCOME", "EXPENSE"] as const;
type CategoryType = typeof categoryTypeOptions[number];

type CategoryFormValues = {
  name: string;
  type: CategoryType;
};

const categoryModalFields: CategoryFormValues = {
  name: "",
  type: "EXPENSE",
};

export default function CategoriesPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formValues, setFormValues] = useState<CategoryFormValues>(categoryModalFields);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingCategory, setEditingCategory] = useState<CategoryResponse | null>(null);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getCategories();
      
      console.log("Categories data:", data);
      
      // Ensure data is an array
      const categoriesArray = Array.isArray(data) ? data : [];
      setCategories(categoriesArray);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load categories");
      console.error("Error loading categories:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const filteredCategories = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();
    if (!normalizedSearch) return categories;
    
    return categories.filter((cat) =>
      cat.name.toLowerCase().includes(normalizedSearch)
    );
  }, [categories, searchTerm]);

  const incomeCategories = filteredCategories.filter(c => c.type === "INCOME");
  const expenseCategories = filteredCategories.filter(c => c.type === "EXPENSE");

  const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleFormChange = (event: SyntheticEvent<HTMLInputElement>) => {
    const target = event.target as HTMLInputElement;
    setFormValues((prev) => ({ ...prev, [target.name]: target.value }));
  };

  const handleTypeChange = (value: string) => {
    setFormValues((prev) => ({ ...prev, type: value as CategoryType }));
  };

  const handleOpenModal = (category?: CategoryResponse) => {
    if (category) {
      setEditingCategory(category);
      setFormValues({ name: category.name, type: category.type });
    } else {
      setEditingCategory(null);
      setFormValues(categoryModalFields);
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingCategory(null);
    setFormValues(categoryModalFields);
  };

  const handleFormSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    try {
      if (editingCategory) {
        await updateCategory(editingCategory.id, {
          name: formValues.name,
          type: formValues.type,
        });
      } else {
        await createCategory({
          name: formValues.name,
          type: formValues.type,
        });
      }
      
      handleCloseModal();
      await loadCategories();
    } catch (err) {
      console.error("Error saving category:", err);
      alert("Failed to save category. Please try again.");
    }
  };

  const handleDelete = async (categoryId: number) => {
    if (!confirm("Are you sure you want to delete this category? This action cannot be undone.")) {
      return;
    }

    try {
      await deleteCategory(categoryId);
      await loadCategories();
    } catch (err) {
      console.error("Error deleting category:", err);
      alert("Failed to delete category. It may be in use by transactions.");
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Categories" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-gray-600">Loading categories...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
          <PageTitle title="Categories" />
          <div className="rounded-3xl bg-white p-12 shadow-xl text-center">
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={loadCategories} className={primaryActionButtonClass}>
              Try Again
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-beige-100 py-10 sm:py-12">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-4 sm:px-6 lg:px-8">
        <PageTitle title="Categories" />

        <div className="grid gap-6 lg:grid-cols-[minmax(0,320px)_1fr]">
          <div className="flex flex-col gap-6">
            <div className="rounded-[32px] bg-slate-900 px-6 py-8 shadow-2xl">
              <p className="text-xs uppercase tracking-[0.6em] text-slate-400">
                Total categories
              </p>
              <p className="mt-4 text-4xl font-semibold text-white">
                {categories.length}
              </p>
              <p className="mt-2 text-sm text-slate-300">
                Organize your finances
              </p>
            </div>

            <div className="rounded-[32px] bg-white p-6 shadow-xl">
              <p className="text-sm font-semibold uppercase tracking-[0.35em] text-slate-400">
                Summary
              </p>
              <div className="mt-6 space-y-4 text-sm">
                <div className="flex items-center justify-between text-slate-500">
                  <span>Income categories</span>
                  <span className="font-semibold text-slate-900">
                    {incomeCategories.length}
                  </span>
                </div>
                <div className="flex items-center justify-between text-slate-500">
                  <span>Expense categories</span>
                  <span className="font-semibold text-slate-900">
                    {expenseCategories.length}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div className="relative">
            <div className="rounded-[32px] bg-white p-6 shadow-xl">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="relative flex-1 min-w-[220px] max-w-[300px]">
                  <input
                    type="search"
                    placeholder="Search categories"
                    value={searchTerm}
                    onChange={handleSearchChange}
                    className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 pr-12 text-sm text-slate-600 placeholder:text-slate-400 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                  />
                  <div className="pointer-events-none absolute inset-y-0 right-4 flex items-center text-slate-400">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.6"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      className="h-4 w-4"
                    >
                      <circle cx="11" cy="11" r="7" />
                      <line x1="17.5" y1="17.5" x2="22" y2="22" />
                    </svg>
                  </div>
                </div>

                <Button
                  variant="ghost"
                  size="sm"
                  className={primaryActionButtonClass}
                  onClick={() => handleOpenModal()}
                >
                  New category
                </Button>
              </div>

              <div className="mt-6 space-y-6">
                {/* Income Categories */}
                <div>
                  <h3 className="text-sm font-semibold uppercase tracking-[0.3em] text-emerald-600 mb-3">
                    Income
                  </h3>
                  <div className="space-y-2">
                    {incomeCategories.length === 0 ? (
                      <p className="rounded-2xl border border-dashed border-slate-200 p-4 text-center text-sm text-slate-500">
                        No income categories yet.
                      </p>
                    ) : (
                      incomeCategories.map((category) => (
                        <div
                          key={category.id}
                          className="flex items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 hover:bg-slate-100 transition"
                        >
                          <span className="font-semibold text-slate-900">{category.name}</span>
                          <div className="flex gap-2">
                            <button
                              onClick={() => handleOpenModal(category)}
                              className="rounded-lg px-3 py-1 text-xs font-semibold text-slate-600 hover:bg-slate-200 transition"
                            >
                              Edit
                            </button>
                            <button
                              onClick={() => handleDelete(category.id)}
                              className="rounded-lg px-3 py-1 text-xs font-semibold text-rose-600 hover:bg-rose-50 transition"
                            >
                              Delete
                            </button>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>

                {/* Expense Categories */}
                <div>
                  <h3 className="text-sm font-semibold uppercase tracking-[0.3em] text-rose-600 mb-3">
                    Expenses
                  </h3>
                  <div className="space-y-2">
                    {expenseCategories.length === 0 ? (
                      <p className="rounded-2xl border border-dashed border-slate-200 p-4 text-center text-sm text-slate-500">
                        No expense categories yet.
                      </p>
                    ) : (
                      expenseCategories.map((category) => (
                        <div
                          key={category.id}
                          className="flex items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 hover:bg-slate-100 transition"
                        >
                          <span className="font-semibold text-slate-900">{category.name}</span>
                          <div className="flex gap-2">
                            <button
                              onClick={() => handleOpenModal(category)}
                              className="rounded-lg px-3 py-1 text-xs font-semibold text-slate-600 hover:bg-slate-200 transition"
                            >
                              Edit
                            </button>
                            <button
                              onClick={() => handleDelete(category.id)}
                              className="rounded-lg px-3 py-1 text-xs font-semibold text-rose-600 hover:bg-rose-50 transition"
                            >
                              Delete
                            </button>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>

            {isModalOpen && (
              <div className="absolute inset-0 z-40 flex items-center justify-center rounded-[32px] bg-black/40 px-4 py-8">
                <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl">
                  <div className="mb-4 flex items-center justify-between">
                    <h3 className="text-lg font-semibold text-[var(--color-grey-900)]">
                      {editingCategory ? "Edit category" : "New category"}
                    </h3>
                    <button
                      type="button"
                      className="text-sm font-semibold text-slate-500 hover:text-slate-700"
                      onClick={handleCloseModal}
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
                        placeholder="Category name"
                        required
                        className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-slate-300 focus:outline-none focus:ring-2 focus:ring-slate-200"
                      />
                    </label>
                    <label className="flex flex-col gap-2 text-sm text-slate-600">
                      <span className="text-xs uppercase tracking-wide text-(--color-grey-500)">
                        Type
                      </span>
                      <CustomSelect
                        value={formValues.type}
                        options={categoryTypeOptions}
                        onChange={handleTypeChange}
                        icon={<ChevronDown className="h-4 w-4" />}
                        trailingIcon={<ChevronDown className="h-4 w-4" />}
                        ariaLabel="Category type"
                      />
                    </label>
                    <div className="flex justify-center gap-3 pt-1">
                      <Button
                        variant="ghost"
                        className="rounded-2xl px-6 py-3 text-sm font-semibold bg-rose-600 text-white shadow-lg focus-visible:ring-rose-500 active:translate-y-px active:scale-95 hover:bg-rose-600 hover:text-white hover:shadow-none"
                        onClick={handleCloseModal}
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
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

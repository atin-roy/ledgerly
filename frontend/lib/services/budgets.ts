import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface BudgetResponse {
  id: number;
  amount: number;
  categoryId: number;
}

export interface CreateBudgetRequest {
  amount: number;
  categoryId: number;
}

export interface UpdateBudgetRequest {
  amount?: number;
  categoryId?: number;
}

export async function getBudgets(): Promise<BudgetResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BudgetResponse[]>(`/users/${userId}/budgets`);
}

export async function getBudget(budgetId: number): Promise<BudgetResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BudgetResponse>(`/users/${userId}/budgets/${budgetId}`);
}

export async function createBudget(data: CreateBudgetRequest): Promise<BudgetResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BudgetResponse>(`/users/${userId}/budgets`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateBudget(
  budgetId: number,
  data: UpdateBudgetRequest
): Promise<BudgetResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BudgetResponse>(`/users/${userId}/budgets/${budgetId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteBudget(budgetId: number): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<void>(`/users/${userId}/budgets/${budgetId}`, {
    method: "DELETE",
  });
}

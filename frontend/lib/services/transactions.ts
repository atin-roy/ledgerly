import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface TransactionResponse {
  id: number;
  amount: number;
  date: string; // ISO date string
  categoryName: string;
  partyName: string;
}

export interface CreateTransactionRequest {
  amount: number;
  categoryId: number;
  partyId?: number;
  partyName?: string;
}

export interface UpdateTransactionRequest {
  amount?: number;
  categoryId?: number;
  partyId?: number;
}

export async function getTransactions(): Promise<TransactionResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<TransactionResponse[]>(`/users/${userId}/transactions`);
}

export async function getTransaction(transactionId: number): Promise<TransactionResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<TransactionResponse>(`/users/${userId}/transactions/${transactionId}`);
}

export async function createTransaction(
  data: CreateTransactionRequest
): Promise<TransactionResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<TransactionResponse>(`/users/${userId}/transactions`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateTransaction(
  transactionId: number,
  data: UpdateTransactionRequest
): Promise<TransactionResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<TransactionResponse>(`/users/${userId}/transactions/${transactionId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteTransaction(transactionId: number): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<void>(`/users/${userId}/transactions/${transactionId}`, {
    method: "DELETE",
  });
}

import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface BillResponse {
  id: number;
  name: string;
  amount: number;
  status: string;
  dueDate: string; // ISO date string
}

export interface CreateBillRequest {
  name: string;
  amount: number;
  status: string;
  dueDate: string;
}

export interface UpdateBillRequest {
  name?: string;
  amount?: number;
  status?: string;
  dueDate?: string;
}

export async function getBills(): Promise<BillResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BillResponse[]>(`/users/${userId}/bills`);
}

export async function getBill(billId: number): Promise<BillResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BillResponse>(`/users/${userId}/bills/${billId}`);
}

export async function createBill(data: CreateBillRequest): Promise<BillResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BillResponse>(`/users/${userId}/bills`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateBill(
  billId: number,
  data: UpdateBillRequest
): Promise<BillResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<BillResponse>(`/users/${userId}/bills/${billId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteBill(billId: number): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<void>(`/users/${userId}/bills/${billId}`, {
    method: "DELETE",
  });
}

export async function countBillsByStatus(status: string): Promise<number> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<number>(`/users/${userId}/bills/count?status=${status}`);
}

export async function sumBillsByStatus(status: string): Promise<number> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<number>(`/users/${userId}/bills/sum?status=${status}`);
}

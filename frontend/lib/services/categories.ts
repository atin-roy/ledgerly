import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface CategoryResponse {
  id: number;
  name: string;
  type: "INCOME" | "EXPENSE";
}

export interface CreateCategoryRequest {
  name: string;
  type: "INCOME" | "EXPENSE";
}

export interface UpdateCategoryRequest {
  name?: string;
  type?: "INCOME" | "EXPENSE";
}

export async function getCategories(): Promise<CategoryResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<CategoryResponse[]>(`/users/${userId}/categories`);
}

export async function getCategory(categoryId: number): Promise<CategoryResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<CategoryResponse>(`/users/${userId}/categories/${categoryId}`);
}

export async function createCategory(data: CreateCategoryRequest): Promise<CategoryResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<CategoryResponse>(`/users/${userId}/categories`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateCategory(
  categoryId: number,
  data: UpdateCategoryRequest
): Promise<CategoryResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<CategoryResponse>(`/users/${userId}/categories/${categoryId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteCategory(categoryId: number): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<void>(`/users/${userId}/categories/${categoryId}`, {
    method: "DELETE",
  });
}

"use client";
import { useState, type ChangeEvent, type FormEvent } from "react";
import { useRouter } from "next/navigation";

import { AUTH_LOGIN_URL } from "@/lib/api";
import { persistAuthTokens, AuthResponse } from "@/lib/auth";

interface FormData {
  email: string;
  password: string;
}

interface FormErrors {
  email?: string;
  password?: string;
  general?: string;
}

interface LoginCardProps {
  apiUrl?: string;
}

export default function LoginCard({
  apiUrl = AUTH_LOGIN_URL,
}: LoginCardProps) {
  const [formData, setFormData] = useState<FormData>({
    email: "",
    password: "",
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Please enter a valid email address";
    }

    if (!formData.password) {
      newErrors.password = "Password is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);
    setErrors({});

    try {
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: formData.email.trim().toLowerCase(),
          password: formData.password,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData?.message || `Unable to login (status ${response.status})`,
        );
      }

      const result = (await response.json()) as AuthResponse;
      persistAuthTokens(result);
      router.replace("/overview");
    } catch (error) {
      console.error("Login error:", error);
      setErrors({
        general:
          error instanceof Error
            ? error.message
            : "An unexpected error occurred",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section className="flex h-full min-h-[420px] w-full flex-col gap-4 rounded-3xl text-grey-900 justify-center md:rounded-3xl">
      <div className="space-y-2 p-8">
        <h1 className="text-4xl font-bold leading-tight">Login</h1>
        <p className="text-base text-gray-600">Welcome back to Ledgerly</p>
      </div>

      {errors.general && (
        <div className="mx-8 rounded-lg bg-red-50 p-3 text-red-700 border border-red-200">
          {errors.general}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-4 w-full px-8"
        noValidate
      >
        <div>
          <label
            htmlFor="email"
            className="text-sm font-semibold uppercase tracking-wide text-grey-900"
          >
            Email
          </label>
          <input
            id="email"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleInputChange}
            className={`rounded-xl border p-3 text-base w-full ${
              errors.email
                ? "border-red-500 focus:border-red-500"
                : "border-gray-300"
            }`}
            placeholder="Enter your email"
          />
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{errors.email}</p>
          )}
        </div>

        <div>
          <label
            htmlFor="password"
            className="text-sm font-semibold uppercase tracking-wide text-grey-900"
          >
            Password
          </label>
          <input
            id="password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleInputChange}
            className={`rounded-xl border p-3 text-base w-full ${
              errors.password
                ? "border-red-500 focus:border-red-500"
                : "border-gray-300"
            }`}
            placeholder="Enter your password"
          />
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="mt-2 rounded-full bg-[#277c78] px-6 py-3 text-white transition hover:bg-[#277c78]/90 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? "Logging in..." : "Login"}
        </button>
      </form>
      <div className="h-8" />
    </section>
  );
}
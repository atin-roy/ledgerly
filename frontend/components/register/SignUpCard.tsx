"use client";
import { useState, type ChangeEvent, type FormEvent } from "react";
import { useRouter } from "next/navigation";

import { AUTH_REGISTER_URL } from "@/lib/api";
import { persistAuthTokens, AuthResponse } from "@/lib/auth";

interface FormData {
  name: string;
  email: string;
  password: string;
}

interface FormErrors {
  name?: string;
  email?: string;
  password?: string;
  general?: string;
}

interface SignUpCardProps {
  apiUrl?: string;
}

export default function SignUpCard({
  apiUrl = AUTH_REGISTER_URL,
}: SignUpCardProps) {
  const [formData, setFormData] = useState<FormData>({
    name: "",
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    // Name validation
    if (!formData.name.trim()) {
      newErrors.name = "Name is required";
    } else if (formData.name.trim().length < 2) {
      newErrors.name = "Name must be at least 2 characters";
    }

    // Email validation
    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Please enter a valid email address";
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 8) {
      newErrors.password = "Password must be at least 8 characters";
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(formData.password)) {
      newErrors.password =
        "Password must contain at least one uppercase letter, one lowercase letter, and one number";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Clear error when user starts typing
    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({
        ...prev,
        [name]: undefined,
      }));
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
      console.log(`📝 Attempting signup to: ${apiUrl}`);
      
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: formData.name.trim(),
          email: formData.email.trim().toLowerCase(),
          password: formData.password,
        }),
      });

      if (!response.ok) {
        console.error("❌ Signup failed with status:", response.status);
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`,
        );
      }

      const result = (await response.json()) as AuthResponse;

      console.log("✅ Signup successful, persisting tokens");
      persistAuthTokens(result);
      // Reset form on success
      setFormData({ name: "", email: "", password: "" });
      console.log("✅ Redirecting to dashboard");
      router.replace("/overview");
    } catch (error) {
      console.error("❌ Signup error:", error);
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
        <h1 className="text-4xl font-bold leading-tight">Sign Up</h1>
        <p className="text-base text-gray-600">
          Create your Ledgerly account today
        </p>
      </div>

      {errors.general && (
        <div className="mx-8 rounded-lg bg-red-50 p-3 text-red-700 border border-red-200">
          {errors.general}
        </div>
      )}

      <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-full px-8">
        <div>
          <label
            htmlFor="name"
            className="text-sm font-semibold uppercase tracking-wide text-grey-900"
          >
            Name
          </label>
          <input
            id="name"
            name="name"
            type="text"
            value={formData.name}
            onChange={handleInputChange}
            className={`rounded-xl border p-3 text-base w-full ${
              errors.name
                ? "border-red-500 focus:border-red-500"
                : "border-gray-300"
            }`}
            placeholder="Enter your full name"
          />
          {errors.name && (
            <p className="mt-1 text-sm text-red-600">{errors.name}</p>
          )}
        </div>

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
            placeholder="Enter your email address"
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
            placeholder="Create a strong password"
          />
          <p className="text-xs text-gray-500 mt-1">
            Password must be at least 8 characters with uppercase, lowercase,
            and number.
          </p>
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={isLoading}
          style={{ backgroundColor: "var(--color-green)" }}
          className="mt-2 rounded-full px-6 py-3 text-white transition hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? "Creating Account..." : "Sign Up"}
        </button>
      </form>
      <div className="h-8" />
    </section>
  );
}

"use client";
import { useState, type ChangeEvent, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

import { AUTH_LOGIN_URL } from "@/lib/api";
import { persistAuthTokens, AuthResponse } from "@/lib/auth";
import styles from "@/components/auth/auth.module.css";

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
        const errorData = await response.json().catch(() => ({})) as { message?: string };
        throw new Error(
          errorData?.message || `Unable to login (status ${response.status})`,
        );
      }

      const result = (await response.json()) as AuthResponse;
      persistAuthTokens(result);
      router.replace("/overview");
    } catch (error) {
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
    <>
      <p className={styles.eyebrow}>The Ledger</p>
      <h1 className={styles.heading}>Sign in</h1>
      <p className={styles.sub}>Welcome back to Ledgerly.</p>

      {errors.general && (
        <div className={styles.general}>
          <div className={styles.generalBox}>{errors.general}</div>
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate>
        <div className={styles.fields}>
          <div className={styles.field}>
            <label htmlFor="email" className={styles.label}>
              Email
            </label>
            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleInputChange}
              className={`${styles.input} ${errors.email ? styles.inputError : ""}`}
              placeholder="you@example.com"
            />
            {errors.email && (
              <p className={styles.fieldError}>{errors.email}</p>
            )}
          </div>

          <div className={styles.field}>
            <label htmlFor="password" className={styles.label}>
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleInputChange}
              className={`${styles.input} ${errors.password ? styles.inputError : ""}`}
              placeholder="Enter your password"
            />
            {errors.password && (
              <p className={styles.fieldError}>{errors.password}</p>
            )}
          </div>
        </div>

        <button type="submit" disabled={isLoading} className={styles.submit}>
          {isLoading ? "Signing in…" : "Sign in"}
        </button>
      </form>

      <p className={styles.alt}>
        New to Ledgerly?{" "}
        <Link href="/register" className={styles.altLink}>
          Open an account
        </Link>
      </p>
    </>
  );
}
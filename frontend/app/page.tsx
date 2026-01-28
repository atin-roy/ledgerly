import Link from "next/link";
import Image from "next/image";
import illustration from "@/public/register-illustration.svg";
import {
  Database,
  ShieldCheck,
  ArrowRight,
  Lock,
  Code,
  Zap,
  LogIn,
  UserPlus,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function HomePage() {
  return (
    <div className="min-h-screen bg-amber-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-amber-50 py-20 sm:py-32">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 gap-12 lg:grid-cols-2 lg:gap-16">
            <div className="flex flex-col justify-center">
              <h1 className="text-4xl font-bold tracking-tight text-[var(--color-grey-900)] sm:text-6xl">
                Master Your Money,
                <br />
                <span className="text-[var(--color-green)]">Effortlessly</span>
              </h1>
              <p className="mt-6 text-lg leading-8 text-[var(--color-grey-600)]">
                Take control of your finances with Ledgerly&apos;s intuitive
                dashboard. Track expenses, manage budgets, and gain insights
                into your spending habits with powerful analytics and real-time
                reporting.
              </p>
              <div className="mt-8 flex flex-col gap-4 sm:flex-row">
                <Link href="/register">
                  <Button size="lg" className="text-lg font-bold">
                    <UserPlus className="h-5 w-5" strokeWidth={3} />
                    Sign Up
                    <ArrowRight className="h-5 w-5" strokeWidth={3} />
                  </Button>
                </Link>
                <Link href="/login">
                  <Button
                    size="lg"
                    variant="secondary"
                    className="bg-[var(--color-blue)] text-white hover:bg-[var(--color-blue)]/90 hover:text-white text-lg font-bold"
                  >
                    <LogIn className="h-5 w-5" strokeWidth={3} />
                    Sign In
                  </Button>
                </Link>
              </div>
            </div>
            <div className="flex items-center justify-center">
              <div className="relative w-full max-w-3xl overflow-hidden rounded-2xl">
                <Image
                  src={illustration}
                  alt="Financial management illustration showing money tracking and budget planning"
                  className="h-full w-full object-contain"
                  priority
                  sizes="(min-width: 1280px) 720px, (min-width: 1024px) 560px, 100vw"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Value Props */}
      <section className="py-20 sm:py-32 bg-amber-50">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-3xl font-bold tracking-tight text-[var(--color-grey-900)] sm:text-4xl">
              Why Choose Ledgerly?
            </h2>
            <p className="mt-4 text-lg text-[var(--color-grey-600)]">
              Everything you need to manage your finances with confidence
            </p>
          </div>
          <div className="mt-16 grid grid-cols-1 gap-8 md:grid-cols-3">
            <Card className="border-0 bg-white shadow-lg hover:shadow-xl transition-shadow">
              <CardHeader className="text-center">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-[var(--color-green)]/10 mx-auto">
                  <Zap className="h-6 w-6 text-[var(--color-green)]" />
                </div>
                <CardTitle className="text-[var(--color-grey-900)]">
                  Server-Side Excellence
                </CardTitle>
                <CardDescription className="text-[var(--color-grey-600)]">
                  Powered by Next.js App Router for sub-second page loads and
                  optimized Core Web Vitals.
                </CardDescription>
              </CardHeader>
            </Card>

            <Card className="border-0 bg-white shadow-lg hover:shadow-xl transition-shadow">
              <CardHeader className="text-center">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-[var(--color-blue)]/10 mx-auto">
                  <ShieldCheck className="h-6 w-6 text-[var(--color-blue)]" />
                </div>
                <CardTitle className="text-[var(--color-grey-900)]">
                  Type-Safe Reliability
                </CardTitle>
                <CardDescription className="text-[var(--color-grey-600)]">
                  End-to-end TypeScript integration ensuring data integrity from
                  the UI to the API layer.
                </CardDescription>
              </CardHeader>
            </Card>

            <Card className="border-0 bg-white shadow-lg hover:shadow-xl transition-shadow">
              <CardHeader className="text-center">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-[var(--color-purple)]/10 mx-auto">
                  <Database className="h-6 w-6 text-[var(--color-purple)]" />
                </div>
                <CardTitle className="text-[var(--color-grey-900)]">
                  Robust Data Layer
                </CardTitle>
                <CardDescription className="text-[var(--color-grey-600)]">
                  Backed by Spring Boot and PostgreSQL for ACID-compliant
                  financial transaction handling.
                </CardDescription>
              </CardHeader>
            </Card>
          </div>
        </div>
      </section>

      {/* Architecture & Privacy */}
      <section className="py-16 bg-beige-100">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <p className="text-sm font-medium text-[var(--color-grey-600)] uppercase tracking-wide">
              Built with privacy and performance in mind
            </p>
            <div className="mt-8 grid grid-cols-1 gap-8 sm:grid-cols-3">
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center mb-3">
                  <Lock className="h-8 w-8 text-[var(--color-green)]" />
                </div>
                <p className="text-lg font-bold text-[var(--color-grey-900)]">
                  Privacy First
                </p>
                <p className="mt-2 text-[var(--color-grey-600)] text-center">
                  Your data never leaves your device unencrypted.
                </p>
              </div>
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center mb-3">
                  <Code className="h-8 w-8 text-[var(--color-blue)]" />
                </div>
                <p className="text-lg font-bold text-[var(--color-grey-900)]">
                  Transparent Code
                </p>
                <p className="mt-2 text-[var(--color-grey-600)] text-center">
                  Built on open standards. No hidden trackers.
                </p>
              </div>
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center mb-3">
                  <Zap className="h-8 w-8 text-[var(--color-purple)]" />
                </div>
                <p className="text-lg font-bold text-[var(--color-grey-900)]">
                  Blazing Fast
                </p>
                <p className="mt-2 text-[var(--color-grey-600)] text-center">
                  Zero-latency UI powered by Next.js.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Final CTA */}
      <section className="py-20 sm:py-32 bg-grey-900">
        <div className="mx-auto max-w-2xl px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold tracking-tight text-white sm:text-4xl">
            Ready to Take Control?
          </h2>
          <p className="mt-4 text-lg text-grey-300">
            A personal finance tracker built for speed, privacy, and technical
            transparency.
          </p>
          <div className="mt-8">
            <Link href="/register">
              <Button size="lg" className="text-lg font-bold">
                Launch Ledgerly
                <ArrowRight className="ml-2 h-4 w-4" strokeWidth={3} />
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-beige-500 bg-amber-50 py-12">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col items-center space-y-4">
            <div className="text-[var(--color-grey-600)]">
              © 2026 Ledgerly. Made with ❤️ by{" "}
              <a
                href="https://atinroy.dev"
                target="_blank"
                rel="noopener noreferrer"
                className="hover:text-[var(--color-grey-900)] transition-colors"
              >
                Atin Roy
              </a>
              .
            </div>
            <div className="text-sm text-[var(--color-grey-600)]">
              Built on Next.js and Spring Boot
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

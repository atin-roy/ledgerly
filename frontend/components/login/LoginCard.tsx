export default function LoginCard() {
  return (
    <section className="flex h-full min-h-[420px] w-full flex-col gap-4 rounded-3xl text-grey-900 justify-center md:rounded-3xl">
      <div className="space-y-2 p-8">
        <h1 className="text-4xl font-bold leading-tight">Login</h1>
        <p className="text-base text-gray-600">
          Welcome back to Ledgerly
        </p>
      </div>
      <form className="flex flex-col gap-4 w-full px-8">
        <label
          htmlFor="email"
          className="text-sm font-semibold uppercase tracking-wide text-grey-900"
        >
          Email
        </label>
        <input
          id="email"
          type="email"
          className="rounded-xl border border-gray-300 p-3 text-base"
        />
        <label
          htmlFor="password"
          className="text-sm font-semibold uppercase tracking-wide text-grey-900"
        >
          Password
        </label>
        <input
          id="password"
          type="password"
          className="rounded-xl border border-gray-300 p-3 text-base"
        />
        <button
          type="submit"
          className="mt-2 rounded-full bg-[var(--color-green)] px-6 py-3 text-white transition hover:bg-[var(--color-green)]/90"
        >
          Login
        </button>
      </form>
      <div className="h-8" />
    </section>
  );
}
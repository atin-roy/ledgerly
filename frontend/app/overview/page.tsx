import CurrentBalance from "@/components/CurrentBalance";

export default function OverviewPage() {
  return (
    <div className="min-h-screen bg-zinc-50 p-8 dark:bg-black">
      <h1 className="mb-8 text-3xl font-semibold tracking-tight text-black dark:text-zinc-50">
        Overview
      </h1>
      <CurrentBalance />
    </div>
  );
}

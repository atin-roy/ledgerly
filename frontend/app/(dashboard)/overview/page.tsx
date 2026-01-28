import MoneyCard from "@/components/MoneyCard";

export default function OverviewPage() {
  const balance = "69,4267.00";

  return (
    <main className="min-h-screen p-8 bg-[var(--beige-100)]">
      <h1 className="mb-8 text-3xl font-semibold tracking-tight text-black md:text-4xl]">
        Overview
      </h1>
      <MoneyCard title={"Current Balance"} number={balance} colorscheme={"dark"} />
      <MoneyCard title={"Income"} number={"1000"} colorscheme={"light"} />
      <MoneyCard title={"Expenses"} number={"1000"} colorscheme={"light"} />
    </main>
  );
}

interface CardProps {
  title: string;
  number: string;
  colorscheme?: "dark" | "light";
}
export default function MoneyCard(
  { title, number, colorscheme = "light" }: CardProps
) {
  const cardType = {
    "dark": {
      bgVar: "--color-black",
      textVar: "--white",
      numberColor: "--white",
    },
    "light": {
      bgVar: "--white",
      textVar: "--grey-600",
      numberColor: "--color-black",
    },
  }
  const { bgVar, textVar, numberColor } = cardType[colorscheme];

  return (
    <article
      className="rounded-lg p-4 shadow-md max-w-85 w-full mx-auto flex flex-col gap-4"
      style={{
        backgroundColor: `var(${bgVar})`,
        color: `var(${textVar})`,
      }}
    >
      <h2 className="text-2">{title}</h2>
      <p
        className="text-1 font-bold"
        style={{ color: `var(${numberColor})` }}
      >
        ₹{number}
      </p>
    </article>
  );
}

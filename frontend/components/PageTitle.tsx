export default function PageTitle({title}: {title: string}) {
  return (
    <h1 className="mb-8 text-3xl font-semibold tracking-tight text-black md:text-4xl]">
      {title}
    </h1>
  );
}
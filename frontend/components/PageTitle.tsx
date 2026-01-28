export default function PageTitle({title}: {title: string}) {
  return (
    <h1 className="mb-2 ml-2 text-3xl font-semibold tracking-tight text-black md:mb-8 md:ml-0 md:text-4xl">
      {title}
    </h1>
  );
}
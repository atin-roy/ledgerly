import Link from "next/link";

export default function Footer() {
  return (
    <footer className="bg-beige-100 py-4 md:py-6">
      <div className="container mx-auto px-4 md:px-6 text-center">
        <p className="text-grey-600 text-xs md:text-sm">
          Made with ❤️ by{" "}
          <Link
            href="https://atinroy.dev"
            target="_blank"
            rel="noopener noreferrer"
            className="text-green hover:text-green/80 font-medium transition-colors duration-200"
          >
            Atin Roy
          </Link>
        </p>
      </div>
    </footer>
  );
}
import type { Metadata } from "next";
import { Zilla_Slab, IBM_Plex_Mono, Public_Sans } from "next/font/google";
import "./globals.css";

const display = Zilla_Slab({
  variable: "--font-display",
  weight: ["500", "600", "700"],
  subsets: ["latin"],
});

const mono = IBM_Plex_Mono({
  variable: "--font-mono",
  weight: ["400", "500", "600"],
  subsets: ["latin"],
});

const body = Public_Sans({
  variable: "--font-body",
  weight: ["400", "500", "600", "700"],
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Ledgerly - Personal Finance Tracker",
  description:
    "Track your expenses, manage your budget, and take control of your finances with Ledgerly.",
  icons: {
    icon: "/icon.svg",
    shortcut: "/favicon.ico",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${display.variable} ${mono.variable} ${body.variable}`}
      >
        <div className="flex flex-col min-h-screen md:h-svh">{children}</div>
      </body>
    </html>
  );
}

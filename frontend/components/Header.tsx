"use client";
import { useState } from "react";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserPlus, faSignInAlt, faBars, faTimes } from "@fortawesome/free-solid-svg-icons";

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="bg-beige-100 relative z-50 border-b-2 border-grey-300">
      <div className="container mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          {/* Logo and Title */}
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 md:w-12 md:h-12 bg-green rounded-xl flex items-center justify-center shadow-md">
              <svg className="w-6 h-6 md:w-8 md:h-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14,2 14,8 20,8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10,9 9,9 8,10"></polyline>
              </svg>
            </div>
            <h1 className="text-xl md:text-2xl font-bold text-grey-900 tracking-tight">
              Ledgerly
            </h1>
          </div>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-4">
            <Link
              href="/register"
              className="inline-flex items-center px-6 py-2 min-w-24 justify-center bg-green text-white font-semibold rounded-lg hover:bg-opacity-90 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-md transition-all duration-150 shadow-md text-sm"
            >
              <FontAwesomeIcon icon={faUserPlus} className="w-4 h-4 mr-2" />
              Register
            </Link>
            <Link
              href="/login"
              className="inline-flex items-center px-6 py-2 min-w-24 justify-center bg-blue text-white font-semibold rounded-lg hover:bg-opacity-90 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-md transition-all duration-150 shadow-md text-sm"
            >
              <FontAwesomeIcon icon={faSignInAlt} className="w-4 h-4 mr-2" />
              Login
            </Link>
          </nav>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="md:hidden p-2 text-grey-900 hover:bg-grey-100 rounded-lg transition-colors"
            aria-label="Toggle menu"
          >
            <FontAwesomeIcon icon={isMenuOpen ? faTimes : faBars} className="w-6 h-6" />
          </button>
        </div>
      </div>

      {/* Mobile Navigation Modal Overlay */}
      {isMenuOpen && (
        <div
          className="fixed inset-0 md:hidden z-40"
          onClick={() => setIsMenuOpen(false)}
        />
      )}

      {/* Mobile Navigation Modal */}
      <div
        className={`fixed top-0 left-0 right-0 bg-beige-100 md:hidden transition-all duration-300 ease-in-out z-50 rounded-b-2xl shadow-lg ${
          isMenuOpen
            ? "opacity-100 translate-y-0 pointer-events-auto"
            : "opacity-0 -translate-y-full pointer-events-none"
        }`}
        style={{
          marginTop: "calc(56px + 1rem)", // Account for header height
        }}
      >
        <nav className="flex flex-col space-y-3 p-4 w-4/5 mx-auto">
          <Link
            href="/register"
            className="inline-flex items-center justify-center w-full px-6 py-3 bg-green text-white font-semibold rounded-lg hover:bg-opacity-90 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-md transition-all duration-150 shadow-md text-sm"
            onClick={() => setIsMenuOpen(false)}
          >
            <FontAwesomeIcon icon={faUserPlus} className="w-4 h-4 mr-2" />
            Register
          </Link>
          <Link
            href="/login"
            className="inline-flex items-center justify-center w-full px-6 py-3 bg-blue text-white font-semibold rounded-lg hover:bg-opacity-90 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-md transition-all duration-150 shadow-md text-sm"
            onClick={() => setIsMenuOpen(false)}
          >
            <FontAwesomeIcon icon={faSignInAlt} className="w-4 h-4 mr-2" />
            Login
          </Link>
        </nav>
      </div>
    </header>
  );
}
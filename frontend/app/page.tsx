"use client";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChartBar,
  faPiggyBank,
  faChartLine,
} from "@fortawesome/free-solid-svg-icons";

export default function Home() {
  return (
    <div className="bg-beige-100 h-full md:flex md:items-center md:justify-center">
      <div className="container mx-auto px-4 md:px-6 py-8 md:py-4">
        {/* Hero Section */}
        <div className="text-center mb-6 md:mb-4">
          <div className="flex justify-center mb-4 md:mb-4">
            <div className="w-14 h-14 md:w-16 md:h-16 bg-green rounded-2xl flex items-center justify-center shadow-lg">
              <svg
                className="w-8 h-8 md:w-10 md:h-10 text-white"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14,2 14,8 20,8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10,9 9,9 8,10"></polyline>
              </svg>
            </div>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold text-grey-900 mb-3 tracking-tight">
            Welcome to Ledgerly
          </h1>
          <p className="text-sm md:text-base text-grey-600 max-w-2xl mx-auto leading-relaxed px-4">
            Take control of your finances with our intuitive personal finance
            tracker. Monitor your expenses, track your income, and achieve
            your financial goals.
          </p>
        </div>

        {/* Features Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-5 mb-6 md:mb-4">
          <div className="bg-white rounded-xl p-5 shadow-sm border border-grey-100">
            <div className="w-10 h-10 bg-green rounded-lg flex items-center justify-center mb-2">
              <FontAwesomeIcon
                icon={faChartBar}
                className="w-5 h-5 text-white"
              />
            </div>
            <h3 className="text-base font-semibold text-grey-900 mb-2">
              Track Expenses
            </h3>
            <p className="text-sm text-grey-600">
              Categorize and monitor your spending patterns to understand
              where your money goes.
            </p>
          </div>

          <div className="bg-white rounded-xl p-5 shadow-sm border border-grey-100">
            <div className="w-10 h-10 bg-blue rounded-lg flex items-center justify-center mb-2">
              <FontAwesomeIcon
                icon={faPiggyBank}
                className="w-5 h-5 text-white"
              />
            </div>
            <h3 className="text-base font-semibold text-grey-900 mb-2">
              Budget Planning
            </h3>
            <p className="text-sm text-grey-600">
              Set budgets for different categories and get insights on your
              financial health.
            </p>
          </div>

          <div className="bg-white rounded-xl p-5 shadow-sm border border-grey-100">
            <div className="w-10 h-10 bg-purple rounded-lg flex items-center justify-center mb-2">
              <FontAwesomeIcon
                icon={faChartLine}
                className="w-5 h-5 text-white"
              />
            </div>
            <h3 className="text-base font-semibold text-grey-900 mb-2">
              Financial Insights
            </h3>
            <p className="text-sm text-grey-600">
              Get detailed reports and analytics to make informed financial
              decisions.
            </p>
          </div>
        </div>

        {/* CTA Section */}
        <div className="text-center pb-2 md:pb-0">
          <Link
            href="/register"
            className="inline-flex items-center px-6 py-2.5 bg-purple text-white font-semibold rounded-lg hover:bg-opacity-90 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-md transition-all duration-150 shadow-lg text-sm"
          >
            <FontAwesomeIcon icon={faChartLine} className="w-4 h-4 mr-2" />
            Get Started
          </Link>
          <p className="mt-2 text-grey-600 text-sm">
            Start tracking your finances today
          </p>
        </div>
        </div>
    </div>
  );
}

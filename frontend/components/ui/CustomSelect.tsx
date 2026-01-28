"use client";

import { useEffect, useRef, useState, type CSSProperties, type ReactNode } from "react";

type CustomSelectProps = {
  value: string;
  options: readonly string[];
  onChange: (value: string) => void;
  icon: ReactNode;
  ariaLabel: string;
  className?: string;
  isMobile?: boolean;
  trailingIcon?: ReactNode;
  dropdownWidth?: number;
};

export default function CustomSelect({
  value,
  options,
  onChange,
  icon,
  ariaLabel,
  className,
  isMobile = false,
  trailingIcon,
  dropdownWidth,
}: CustomSelectProps) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleSelect = (option: string) => {
    onChange(option);
    setIsOpen(false);
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Escape") {
      setIsOpen(false);
    } else if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setIsOpen(!isOpen);
    } else if (event.key === "ArrowDown" && !isOpen) {
      event.preventDefault();
      setIsOpen(true);
    }
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  const dropdownStyle =
    dropdownWidth !== undefined
      ? ({
          width: `${dropdownWidth}px`,
          ...(isMobile
            ? {
                left: "50%",
                transform: "translateX(-50%)",
              }
            : undefined),
        } as CSSProperties)
      : undefined;

  return (
    <div ref={dropdownRef} className={`relative ${className ?? ""}`}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={handleKeyDown}
        className="flex h-12 w-full items-center justify-center rounded-2xl border border-grey-300 bg-white px-4 text-(--color-grey-600) hover:border-(--color-green) focus:border-(--color-green) focus:outline-none md:h-auto md:justify-between md:px-4 md:py-3"
        aria-label={ariaLabel}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
      >
        <span className="md:hidden">{icon}</span>
        <span className="hidden text-sm font-medium text-(--color-grey-900) md:block">
          {value}
        </span>
        {trailingIcon && (
          <span className="hidden items-center text-(--color-grey-500) md:flex">
            {trailingIcon}
          </span>
        )}
      </button>

      {isOpen && (
        <div
          className={`${
            isMobile ? "absolute top-full z-50 mt-2" : "absolute top-full left-0 z-50 mt-1"
          } rounded-2xl border border-grey-300 bg-white shadow-lg overflow-hidden`}
          style={dropdownStyle}
        >
          <ul role="listbox">
            {options.map((option, index) => {
              const isFirst = index === 0;
              const isLast = index === options.length - 1;
              const isSelected = option === value;

              let buttonClasses =
                "w-full text-left text-sm focus:bg-(--color-grey-100) focus:outline-none ";

              if (!isSelected) {
                buttonClasses += "hover:bg-(--color-grey-100) ";
              }

              buttonClasses += "py-2 ";
              buttonClasses += isSelected ? "px-0 " : "px-4 ";

              if (isFirst) {
                buttonClasses += "rounded-t-2xl ";
              }
              if (isLast) {
                buttonClasses += "rounded-b-2xl ";
              }

              if (isSelected) {
                buttonClasses += "bg-(--color-green) text-white ";
              } else {
                buttonClasses += "text-(--color-grey-900) ";
              }

              return (
                <li key={option}>
                  <button
                    type="button"
                    onClick={() => handleSelect(option)}
                    className={buttonClasses.trim()}
                    role="option"
                    aria-selected={isSelected}
                  >
                    <span className={isSelected ? "px-4 block" : ""}>
                      {option}
                    </span>
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
}

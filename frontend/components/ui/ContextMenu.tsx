"use client";

import { useEffect, useRef, type ReactNode } from "react";
import { Edit2, Trash2 } from "lucide-react";

export type ContextMenuItem = {
  label: string;
  icon?: ReactNode;
  onClick: () => void;
  variant?: "default" | "danger";
};

type ContextMenuProps = {
  x: number;
  y: number;
  items: ContextMenuItem[];
  onClose: () => void;
};

export default function ContextMenu({ x, y, items, onClose }: ContextMenuProps) {
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        onClose();
      }
    };

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    // Add listeners after a small delay to prevent immediate closing
    const timeoutId = setTimeout(() => {
      document.addEventListener("mousedown", handleClickOutside);
      document.addEventListener("keydown", handleEscape);
    }, 100);

    return () => {
      clearTimeout(timeoutId);
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [onClose]);

  // Adjust position to prevent overflow
  useEffect(() => {
    if (menuRef.current) {
      const rect = menuRef.current.getBoundingClientRect();
      const viewportWidth = window.innerWidth;
      const viewportHeight = window.innerHeight;

      let adjustedX = x;
      let adjustedY = y;

      // Adjust horizontal position
      if (x + rect.width > viewportWidth) {
        adjustedX = viewportWidth - rect.width - 10;
      }

      // Adjust vertical position
      if (y + rect.height > viewportHeight) {
        adjustedY = viewportHeight - rect.height - 10;
      }

      menuRef.current.style.left = `${adjustedX}px`;
      menuRef.current.style.top = `${adjustedY}px`;
    }
  }, [x, y]);

  return (
    <div
      ref={menuRef}
      className="fixed z-50 min-w-[180px] rounded-2xl bg-white shadow-2xl border border-slate-200 py-2 animate-in fade-in-0 zoom-in-95"
      style={{ left: x, top: y }}
    >
      {items.map((item, index) => (
        <button
          key={index}
          onClick={() => {
            item.onClick();
            onClose();
          }}
          className={`w-full flex items-center gap-3 px-4 py-2.5 text-sm font-medium transition-colors ${
            item.variant === "danger"
              ? "text-rose-600 hover:bg-rose-50"
              : "text-slate-700 hover:bg-slate-100"
          }`}
        >
          {item.icon && <span className="flex-shrink-0">{item.icon}</span>}
          <span>{item.label}</span>
        </button>
      ))}
    </div>
  );
}

// Predefined menu item creators for common actions
export const createEditMenuItem = (onEdit: () => void): ContextMenuItem => ({
  label: "Edit",
  icon: <Edit2 className="h-4 w-4" />,
  onClick: onEdit,
  variant: "default",
});

export const createDeleteMenuItem = (onDelete: () => void): ContextMenuItem => ({
  label: "Delete",
  icon: <Trash2 className="h-4 w-4" />,
  onClick: onDelete,
  variant: "danger",
});

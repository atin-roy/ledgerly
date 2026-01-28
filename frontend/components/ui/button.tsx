import * as React from "react"
import { Slot } from "@radix-ui/react-slot"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium ring-offset-white transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 dark:ring-offset-neutral-950",
  {
    variants: {
      variant: {
        default: "bg-emerald-600 text-white hover:bg-emerald-700 hover:shadow-lg active:scale-95 focus-visible:ring-emerald-500 dark:bg-emerald-600 dark:hover:bg-emerald-700",
        destructive:
          "bg-rose-600 text-white hover:bg-rose-700 hover:shadow-lg active:scale-95 focus-visible:ring-rose-500 dark:bg-rose-600 dark:hover:bg-rose-700",
        outline:
          "border-2 border-emerald-600 bg-white text-emerald-600 hover:bg-emerald-50 hover:shadow-md active:scale-95 focus-visible:ring-emerald-500 dark:border-emerald-500 dark:bg-neutral-950 dark:text-emerald-400 dark:hover:bg-emerald-950",
        secondary:
          "bg-slate-200 text-slate-900 hover:bg-slate-300 hover:shadow-md active:scale-95 focus-visible:ring-slate-400 dark:bg-slate-700 dark:text-slate-50 dark:hover:bg-slate-600",
        ghost: "text-slate-700 font-medium hover:text-slate-900 hover:bg-slate-200 hover:shadow-sm active:scale-95 dark:text-slate-50 dark:hover:bg-slate-800",
        link: "text-emerald-600 underline-offset-4 hover:underline hover:text-emerald-700 dark:text-emerald-400 dark:hover:text-emerald-300",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 rounded-md px-3",
        lg: "h-11 rounded-md px-8",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, asChild = false, ...props }, ref) => {
    const Comp = asChild ? Slot : "button"
    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        {...props}
      />
    )
  }
)
Button.displayName = "Button"

export { Button, buttonVariants }
